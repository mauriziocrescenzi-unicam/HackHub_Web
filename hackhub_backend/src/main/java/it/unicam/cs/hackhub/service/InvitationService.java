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

import it.unicam.cs.hackhub.model.*;
import it.unicam.cs.hackhub.repository.InvitationRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

/**
 * Service layer component responsible for managing {@link Invitation} entities and invitation workflows.
 *
 * <p>
 * Handles inviting user accounts to join teams and processing invitation responses (accept/decline),
 * enforcing authorization constraints and domain rules (e.g., team eligibility and membership constraints).
 * </p>
 */
@Service
@Transactional
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamService teamService;
    private final AccountService accountService;
    private final HackathonService hackathonService;

    /**
     * Creates a new {@code InvitationService} with the required dependencies.
     *
     * <p>
     * {@link Lazy} injection is used for {@link HackathonService} to prevent circular dependencies
     * between services.
     * </p>
     *
     * @param invitationRepository the repository used to access and persist invitations
     * @param teamService the service used to retrieve teams and manage team membership
     * @param accountService the service used to retrieve and validate accounts
     * @param hackathonService the service used to verify hackathon-related constraints for invitations
     */
    public InvitationService(InvitationRepository invitationRepository,
                             TeamService teamService,
                             AccountService accountService,
                             @Lazy HackathonService hackathonService) {
        this.invitationRepository = invitationRepository;
        this.teamService = teamService;
        this.accountService = accountService;
        this.hackathonService = hackathonService;
    }

    /**
     * Sends an invitation to the account identified by {@code idAccount} to join the authenticated leader's team.
     *
     * <p>
     * The authenticated user must be the leader of a team. An invitation can be sent only to accounts with
     * {@link Role#USER}. The operation is denied if the invited account already belongs to a team or if the leader's
     * team is currently registered to a hackathon (as determined by {@link HackathonService#getHackathonsByTeam(Long)}).
     * </p>
     *
     * @param email the email of the account to invite
     * @throws IllegalArgumentException if the authenticated user is not leader of any team, or if the invited account
     *                                  already belongs to a team
     * @throws NullPointerException if the invited account does not exist or cannot be invited, or if the leader's team
     *                              is registered to at least one hackathon
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public void inviteAccount(String email) {
        Account account = accountService.findByEmail(email)
                .orElseThrow(() ->  new IllegalArgumentException("Account not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Team team = teamService.findTeamByLeader(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));
        if (account == null || !account.getRole().equals(Role.USER))
            throw new NullPointerException("Account doesn't exist or can't be invited");

        if (!hackathonService.getHackathonsByTeam(team.getIdTeam()).isEmpty())
            throw new NullPointerException("Team subscribed to a non-terminated hackathon");

        if (teamService.isAccountInTeam(account))
            throw new IllegalArgumentException("Account is already in a team");

        Invitation invitation = new Invitation(account, team);
        invitationRepository.save(invitation);
    }

    /**
     * Processes the response to the invitation identified by {@code idInvitation}.
     *
     * <p>
     * Only the invited account is allowed to respond to the invitation. If {@code response} is {@code true}, the invited
     * account is added to the inviting team and the invitation state is set to {@link InvitationState#ACCEPTED}.
     * Otherwise, the invitation state is set to {@link InvitationState#REFUSED}.
     * </p>
     *
     * @param idInvitation the unique identifier of the invitation
     * @param response {@code true} to accept the invitation; {@code false} to refuse it
     * @throws NullPointerException if the invitation does not exist
     * @throws IllegalStateException if the invitation has already been processed
     * @throws AccessDeniedException if the authenticated user is not the invited account
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public Invitation invitationResponse(Long idInvitation, boolean response) {
        Invitation invitation = getInvitation(idInvitation);

        if (invitation == null) {
            throw new NullPointerException("Invitation doesn't exist");
        }

        if (invitation.getState() != InvitationState.PENDING && invitation.getState() != null) {
            throw new IllegalStateException("Invitation has already been processed");
        }

        Account account = invitation.getInvitedAccount();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!account.getEmail().equals(auth.getName()))
            throw new AccessDeniedException("You are not allowed to respond this " +
                    "invitation");

        if (response) {
            Team team = invitation.getInvitingTeam();
            teamService.addMember(account, team);
            invitation.setState(InvitationState.ACCEPTED);
        }
        else
            invitation.setState(InvitationState.REFUSED);

        invitationRepository.save(invitation);
        return invitation;
    }

    /**
     * Retrieves the invitation with the specified identifier.
     *
     * @param idInvitation the unique identifier of the invitation
     * @return the matching {@link Invitation} if found; {@code null} otherwise
     */
    private Invitation getInvitation(Long idInvitation) {
        return invitationRepository.findById(idInvitation)
                .orElse(null);
    }

    /**
     * Retrieves all invitations addressed to the authenticated user.
     *
     * @return the list of invitations addressed to the authenticated account; an empty list if none exist
     * @throws IllegalArgumentException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public List<Invitation> getAllInvitations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Account account = accountService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User doesn't exist"));

        return invitationRepository.findByInvitedAccountIdAccount(account.getIdAccount());
    }

    /**
     * Retrieves the ID of the team associated with a given invitation.
     *
     * <p>The method looks up the invitation using its unique identifier.
     * If the invitation exists, the ID of the inviting team is returned.
     * If no invitation is found, the method returns {@code null}.</p>
     *
     * @param id the unique identifier of the invitation to look up; must not be {@code null}.
     *
     * @return the ID of the inviting team if the invitation exists; otherwise {@code null}.
     */
    public Long getTeamIdByInvitation(Long id) {
        return invitationRepository.findById(id)
                .map(invitation -> invitation.getInvitingTeam().getIdTeam())
                .orElse(null);
    }
}
