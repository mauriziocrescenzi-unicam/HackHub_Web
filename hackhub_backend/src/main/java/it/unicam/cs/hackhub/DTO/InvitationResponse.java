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
package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.Invitation;
import it.unicam.cs.hackhub.model.InvitationState;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an invitation.
 * <p>
 * This record is used to expose invitation details through REST endpoints,
 * including invitation metadata, invited account information,
 * and the team that issued the invitation.
 *
 * @param idInvitation        the unique identifier of the invitation
 * @param state               the current {@link InvitationState} of the invitation
 * @param invitationDate      the date when the invitation was created
 * @param idInvitedAccount    the identifier of the invited account
 * @param invitedAccountEmail the email of the invited account
 * @param idInvitingTeam      the identifier of the team that sent the invitation
 * @param invitingTeamName    the name of the team that sent the invitation
 */
public record InvitationResponse(
        Long idInvitation,
        InvitationState state,
        LocalDate invitationDate,

        Long idInvitedAccount,
        String invitedAccountEmail,

        Long idInvitingTeam,
        String invitingTeamName
) {

    /**
     * Converts an {@link Invitation} entity into an {@link InvitationResponse}.
     * <p>
     * Safely handles potential {@code null} values for related entities
     * (invited account and inviting team).
     *
     * @param invitation the {@link Invitation} entity to convert
     * @return a corresponding {@link InvitationResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static InvitationResponse fromEntity(Invitation invitation) {
            if (invitation == null) {
                return null;
            }

            return new InvitationResponse(
                    invitation.getIdInvitation(),
                    invitation.getState(),
                    invitation.getInvitationDate(),

                    invitation.getInvitedAccount() != null ? invitation.getInvitedAccount().getIdAccount() : null,
                    invitation.getInvitedAccount() != null ? invitation.getInvitedAccount().getEmail() : null,

                    invitation.getInvitingTeam() != null ? invitation.getInvitingTeam().getIdTeam() : null,
                    invitation.getInvitingTeam() != null ? invitation.getInvitingTeam().getName() : null
            );
        }
}
