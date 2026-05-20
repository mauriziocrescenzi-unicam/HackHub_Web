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

import it.unicam.cs.hackhub.DTO.InvitationResponse;
import it.unicam.cs.hackhub.service.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing team invitations.
 * <p>
 * Exposes endpoints to:
 * <ul>
 *     <li>Send invitations to join a team</li>
 *     <li>Retrieve invitations addressed to the current user</li>
 *     <li>Accept or decline an invitation</li>
 * </ul>
 * Business logic is delegated to the {@link InvitationService}.
 */
@RestController
@RequestMapping("/api/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * Creates a new {@code InvitationController}.
     *
     * @param invitationService the service responsible for invitation management
     */
    public InvitationController(InvitationService  invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Sends an invitation to an account.
     * <p>
     * The request body must contain:
     * <ul>
     *     <li>{@code idAccount} – the identifier of the account to invite</li>
     * </ul>
     *
     * @param payload a map containing the required invitation parameters
     * @return a {@link ResponseEntity} with HTTP status 201 (Created)
     *         and a confirmation message
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> inviteAccount(
            @RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        invitationService.inviteAccount(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Invitation sent successfully."));
    }

    /**
     * Retrieves all invitations addressed to the current user.
     *
     * @return a {@link ResponseEntity} containing a list of
     *         {@link InvitationResponse} objects with HTTP status 200 (OK)
     */
    @GetMapping("/user")
    public ResponseEntity<List<InvitationResponse>> viewInvitations() {
        return ResponseEntity.ok(invitationService.getAllInvitations().stream()
                .map(InvitationResponse::fromEntity)
                .toList());
    }

    /**
     * Processes a response to an invitation.
     * <p>
     * Depending on the value of {@code accept}, the invitation
     * is either accepted or declined.
     *
     * @param id     the identifier of the invitation
     * @param accept {@code true} to accept the invitation,
     *               {@code false} to decline it
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     *         and a confirmation message
     */
    @PatchMapping("/{id}/response")
    public ResponseEntity<InvitationResponse> invitationResponse(
            @PathVariable Long id,
            @RequestParam boolean accept) {

        /*InvitationResponse invitation =
                InvitationResponse.fromEntity(invitationService.invitationResponse(id, accept));
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invitation " + (accept ? "accepted" : "refused") + " successfully.");
        if (accept)
            body.put("idTeam", invita);*/

        return ResponseEntity.ok(InvitationResponse.fromEntity(invitationService.invitationResponse(id, accept)));
    }
}