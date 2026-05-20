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
package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.DTO.AccountResponse;
import it.unicam.cs.hackhub.DTO.MentorResponse;
import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Team;
import it.unicam.cs.hackhub.service.AccountService;
import it.unicam.cs.hackhub.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller responsible for managing {@link Account} resources.
 * <p>
 * Exposes endpoints for retrieving accounts, updating profile information,
 * and managing account activation status.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TeamService teamService;

    /**
     * Creates a new controller instance with the required {@link AccountService}.
     *
     * @param accountService the service layer component handling account logic
     */
    public AccountController(AccountService accountService, TeamService teamService) {
        this.accountService = accountService;
        this.teamService = teamService;
    }

    /**
     * Retrieves all registered accounts.
     *
     * @return a {@link ResponseEntity} containing the list of accounts
     *         with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<Account> accounts = accountService.getAccounts();
        return ResponseEntity.ok(accounts.stream()
                .map(a -> AccountResponse.fromEntity(a, teamService.findMemberById(a.getIdAccount())))
                .toList());
    }

    /**
     * Retrieves a specific account by its identifier.
     *
     * @param id the unique identifier of the account
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *             <li>the requested account with status 200 (OK), if found</li>
     *             <li>status 404 (Not Found), if no account exists with the given idAccount</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccount(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    /**
     * Updates the profile information of the currently authenticated account.
     * <p>
     * The request body must contain:
     * <ul>
     *     <li>{@code nickname}</li>
     *     <li>{@code oldPassword}</li>
     *     <li>{@code newPassword}</li>
     * </ul>
     *
     * @param payload a map containing the profile update parameters
     * @return a {@link ResponseEntity} containing the updated {@link Account}
     *         with HTTP status 200 (OK)
     */
    @PutMapping("/profile")
    public ResponseEntity<AccountResponse> updateProfile(
            @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String surname = payload.get("surname");
        String email = payload.get("email");
        String nickname = payload.get("nickname");
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        Account updatedAccount = accountService.modifyProfile(name, surname,
                email, nickname, oldPassword, newPassword);

        Optional<Team> team = teamService.findMemberById(updatedAccount.getIdAccount());

        return ResponseEntity.ok(
                AccountResponse.fromEntity(updatedAccount, team)
        );
    }

    /**
     * Enables or disables an account.
     *
     * @param id       the unique identifier of the account
     * @param disabled {@code true} to disable the account,
     *                 {@code false} to activate it
     * @return a {@link ResponseEntity} containing a confirmation message
     *         with HTTP status 200 (OK)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> manageAccount(
            @PathVariable Long id,
            @RequestParam boolean disabled) {

        accountService.setDisabled(id, disabled);
        String status = disabled ? "disabled" : "active";
        return ResponseEntity.ok(Map.of("message", "Account " + id + " " + status));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<MentorResponse>> getStaff(){
        return  ResponseEntity.ok (accountService.getStaff().stream().map(MentorResponse::fromEntity).toList());
    }
}