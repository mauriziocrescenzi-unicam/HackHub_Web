/*
 * Copyright © 2025 Victoria Coacci, Daniel Duda, Fattori Filippo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the “Software”), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.DTO.*;
import it.unicam.cs.hackhub.model.*;
import it.unicam.cs.hackhub.model.Activable;
import it.unicam.cs.hackhub.repository.AccountRepository;
import it.unicam.cs.hackhub.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer component responsible for managing {@link Account} entities and account-related workflows.
 *
 * <p>
 * Provides operations for account retrieval, registration, authentication (JWT login),
 * profile updates, and administrative enable/disable management.
 * </p>
 *
 * <p>
 * Implements {@link Activable} to support account activation state management and enforces
 * authorization constraints through {@link PreAuthorize} annotations.
 * </p>
 */
@Service
@Transactional
public class AccountService implements Activable<Long> {

    /** Service used to manage hackathons. */
    private final HackathonService hackathonService;

    /** Service used to manage teams. */
    private final TeamService teamService;

    /**
     * Repository used to access and manage {@link Account} entities
     * within the persistence layer.
     */
    private final AccountRepository accountRepository;

    /** Encoder used to securely hash and verify user passwords. */
    private final PasswordEncoder encoder;

    /** Utility component responsible for generating and validating
     * JSON Web Tokens (JWT) used for authentication and authorization.
     */
    private final JwtUtil jwt;

    /**
     * Global application-level secret used to "pepper" passwords before
     * they are processed by the {@link PasswordEncoder}.
     */
    private final String pepper;
    /**
     * Creates a new {@code AccountService} with the required dependencies.
     *
     * <p>
     * {@link Lazy} injection is used for {@link HackathonService} and {@link TeamService}
     * to avoid circular dependencies between services.
     * </p>
     *
     * @param accountRepository the repository used to access and persist accounts
     * @param hackathonService the service used to verify staff assignments in hackathons
     * @param teamService the service used to manage team membership and leadership constraints
     * @param encoder the password encoder used for password hashing and verification
     * @param jwt the utility used to generate JWT tokens during authentication
     * @param pepper the secret used to "pepper" passwords
     */
    public AccountService(AccountRepository accountRepository,
                          @Lazy HackathonService hackathonService,
                          @Lazy TeamService teamService,
                          PasswordEncoder encoder,
                          JwtUtil jwt,
                          @Value("${app.security.password-pepper}") String pepper){
        this.accountRepository = accountRepository;
        this.hackathonService = hackathonService;
        this.teamService = teamService;
        this.encoder = encoder;
        this.jwt = jwt;
        this.pepper=pepper;
    }

    /**
     * Retrieves the account with the specified identifier.
     *
     * <p>
     * This method returns the {@link Account} associated with the given identifier.
     * If no account exists with the specified idAccount, an exception is thrown.
     * </p>
     *
     * @param idAccount the unique identifier of the account
     * @return the matching {@link Account}
     * @throws IllegalArgumentException if no account with the specified identifier exists
     */
    public Account getAccount(Long idAccount) {
        return accountRepository.findById(idAccount)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));
    }

    /**
     * Retrieves all accounts.
     *
     * @return the list of all {@link Account} entities
     */
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Modifies the profile information of an existing account.
     * <p>
     * This method allows updating the account nickname and, optionally,
     * changing the account password. The password is updated only if both
     * the old and new passwords are provided and non-empty.
     *
     * @param nickname    the new nickname to set (optional)
     * @param oldPassword the current password (required to change password)
     * @param newPassword the new password to set
     * @return the updated {@code Account}
     *
     * @throws IllegalArgumentException if the account does not exist
     * @throws IllegalArgumentException if the old password is incorrect
     *                                  or the new password is invalid
     */
    @PreAuthorize("hasAnyRole('STAFF','USER')")
    public Account modifyProfile(String name,
                                 String surname,
                                 String email,
                                 String nickname,
                                 String oldPassword,
                                 String newPassword) {
        if(name == null || surname == null)
            throw new IllegalArgumentException("Name and surname cannot be null");

        if(email == null)
            throw new IllegalArgumentException("Email cannot be null");


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account =findByEmail(auth.getName()).get() ;


        account.setName(name);
        account.setSurname(surname);
        account.setEmail(email);
        account.setNickname(nickname);

        if (!(oldPassword == null || newPassword == null)) {
            if (!(newPassword.isEmpty() || oldPassword.isEmpty())){
                if (!encoder.matches(oldPassword +pepper,account.getPassword()))
                    throw new IllegalArgumentException("Old password not valid");
                account.setPassword(encoder.encode(newPassword+pepper));
            }
        }



        return accountRepository.save(account);
    }

    /**
     * Disables or enables an account identified by the given idAccount, according to business rules based on the account role.
     *
     * <p>
     * For {@link Role#STAFF} accounts, disabling is allowed only if the staff member is not currently assigned
     * to any hackathon.
     * </p>
     *
     * <p>
     * For {@link Role#USER} accounts, disabling is constrained by team membership rules (e.g., leader management).
     * </p>
     *
     * @param id the unique identifier of the account to update
     * @param disabled the new disabled state to set
     * @throws IllegalArgumentException if the role is not eligible for this operation or constraints are violated
     */
    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void setDisabled(Long id, boolean disabled) {
        Account account = getAccount(id);

        switch (account.getRole()){
            case USER -> disabledUser(account,disabled);
            case STAFF -> disabledStaff(account,disabled);
            default -> throw new IllegalArgumentException("Invalid role to disable account");

        }

        accountRepository.save(account);
    }

    /**
     * Disables or enables a staff account, provided that the staff member is not
     * currently active in any hackathon.
     *
     * <p>If the staff member is involved in an ongoing hackathon, the operation is
     * not allowed and an {@link IllegalArgumentException} is thrown.</p>
     *
     * @param account  the {@link Account} to update; must not be {@code null}.
     * @param disabled whether the account should be marked as disabled.
     *
     * @throws IllegalArgumentException if the staff member is currently active in a hackathon.
     */
    private void disabledStaff(Account account, boolean disabled) {
        if(hackathonService.findStaffById(account.getIdAccount()))
            throw new  IllegalArgumentException("Staff is active in a hackathon, cannot be " +
                    "managed");
        account.setDisabled(disabled);
    }

    /**
     * Disables or enables a user account, taking into consideration the user's
     * membership within a team.
     *
     * <p>If the user belongs to a team, the method checks whether the user is the
     * current team leader. If the user is the leader and the team has multiple
     * members, leadership is removed via {@code teamService.removeLeader()} before
     * disabling the account.</p>
     *
     * <p>If the user does not belong to any team, the disabling operation is applied
     * directly. In all cases, the {@link Account#setDisabled(boolean)} flag is set
     * at the end of the process.</p>
     *
     * @param account  the {@link Account} to update; must not be {@code null}.
     * @param disabled whether the account should be marked as disabled.
     */
    private void disabledUser(Account account, boolean disabled) {
        teamService.findMemberById(account.getIdAccount())
                .ifPresentOrElse(team -> {
                    if (team.isLeaderAndNotAlone(account)) {
                        teamService.removeLeader();
                    }
                }, () -> {
                    account.setDisabled(disabled);
                });

        account.setDisabled(disabled);
    }

    /**
     * Registers a new account using the provided registration request.
     *
     * <p>
     * Validates email and nickname uniqueness, verifies the requested role (USER or STAFF),
     * and persists the created account with an encoded password.
     * </p>
     *
     * @param req the registration request containing user data
     * @throws IllegalArgumentException if the email or nickname is already used, or the role is invalid
     */
    public void register(RegisterRequest req) {
        if (accountRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email already used");

        if (accountRepository.existsByNickname(req.nickname()))
            throw new IllegalArgumentException("Nickname already used");

        Role role;
        try {
            role = Role.valueOf(req.role().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Role must be USER or STAFF");
        }

        if (role != Role.USER && role != Role.STAFF)
            throw new IllegalArgumentException("Role must be USER or STAFF");

        Account account = new Account(
                req.name(),
                req.surname(),
                req.email(),
                role,
                req.nickname(),
                encoder.encode(req.password()+pepper)
        );

        accountRepository.save(account);
    }

    /**
     * Authenticates an account using the provided credentials and returns a JWT token if successful.
     *
     * <p>
     * Verifies that the account exists, is not disabled, and that the provided password matches
     * the stored encoded password. On success, generates a JWT token that encodes the user identity
     * and role.
     * </p>
     *
     * @param req the login request containing email and password
     * @return a {@link LoginResponse} containing the generated JWT token
     * @throws IllegalArgumentException if the credentials are invalid
     * @throws IllegalStateException if the account is disabled
     */
    public LoginResponse login(LoginRequest req) {
        Account account = accountRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Bad credentials"));

        if (account.isDisabled()) {
            throw new IllegalStateException("Account is disabled");
        }

        if (!encoder.matches(req.password()+pepper, account.getPassword()))
            throw new IllegalArgumentException("Bad credentials");

        String token = jwt.generateToken(account.getEmail(), account.getRole().name());

        return new LoginResponse(token);
    }

    /**
     * Retrieves the account associated with the given email, if present.
     *
     * @param email the email used to search the account
     * @return an {@link Optional} containing the matching account, or empty if not found
     */
    public Optional<Account> findByEmail(String email){
        return accountRepository.findByEmail(email);
    }
    @PreAuthorize("hasAnyRole('STAFF')")
    public List<Account> getStaff(){
        return accountRepository.findByRole(Role.STAFF);
    }
}

