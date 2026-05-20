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

import it.unicam.cs.hackhub.DTO.*;
import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Team;
import it.unicam.cs.hackhub.service.AccountService;
import it.unicam.cs.hackhub.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller that exposes authentication endpoints.
 * <p>
 * Provides operations for user registration and authentication.
 * All business logic is delegated to the {@link AccountService}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final TeamService teamService;

    /**
     * Creates a new {@code AuthController}.
     * <p>
     * The {@link AccountService} dependency is injected by Spring
     * and used to perform registration and login operations.
     *
     * @param accountService the service handling account-related logic
     */
    public AuthController(AccountService accountService, TeamService teamService) {
        this.accountService = accountService;
        this.teamService = teamService;
    }

    /**
     * Handles user registration.
     * <p>
     * Receives a {@link RegisterRequest}, creates a new account,
     * and automatically performs authentication using the provided
     * credentials. If successful, returns a {@link LoginResponse}
     * containing authentication details (e.g., token).
     *
     * @param req the registration data transfer object
     * @return a {@link ResponseEntity} containing the {@link LoginResponse}
     *         with HTTP status 200 (OK)
     */
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody RegisterRequest req) {
        accountService.register(req);
        LoginRequest loginRequest = new LoginRequest(req.email(), req.password());
        LoginResponse response = accountService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Authenticates an existing user.
     * <p>
     * Validates the credentials contained in the {@link LoginRequest}.
     * On successful authentication, returns a {@link LoginResponse}
     * typically containing a JWT or equivalent authentication token.
     *
     * @param req the login data transfer object containing credentials
     * @return a {@link ResponseEntity} containing the {@link LoginResponse}
     *         with HTTP status 200 (OK)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse response = accountService.login(req);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the authenticated user's account information.
     *
     * <p>This endpoint returns the details of the currently authenticated user,
     * including the associated team if present. The user is identified via the
     * {@link Authentication} object provided by Spring Security.</p>
     *
     * @param auth the authentication object containing the authenticated user's details;
     *             must not be {@code null}. The user's email is extracted from this object.
     *
     * @return a {@link ResponseEntity} containing an {@link AccountResponse} representing
     *         the authenticated user's account and related team information.
     *
     * @throws IllegalArgumentException if no account is found for the authenticated email.
     */
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getMe(Authentication auth) {
        String email = auth.getName();
        Account account = accountService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Optional<Team> team = teamService.findMemberById(account.getIdAccount());

        return ResponseEntity.ok(
                AccountResponse.fromEntity(account, team)
        );
    }
}