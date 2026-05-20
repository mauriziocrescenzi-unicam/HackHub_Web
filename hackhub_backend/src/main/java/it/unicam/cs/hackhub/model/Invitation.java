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
package it.unicam.cs.hackhub.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * JPA entity representing an invitation sent by a {@link Team}
 * to an {@link Account}.
 *
 * <p>
 * An invitation models the request for an account to join a team
 * and maintains its lifecycle state ({@link InvitationState}),
 * creation date, and involved parties.
 * </p>
 */
@Entity
@Table(name = "invitations")
@Getter
public class Invitation {

    /**
     * Primary key of the invitation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitation")
    private Long idInvitation;

    /**
     * Current state of the invitation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private InvitationState state;

    /**
     * Date when the invitation was created.
     */
    @Column(nullable = false)
    private LocalDate invitationDate;

    /**
     * The account that is being invited.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_account_id", nullable = false)
    private Account invitedAccount;

    /**
     * The team that sent the invitation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inviting_team_id", nullable = false)
    private Team invitingTeam;

    /**
     * Protected no-argument constructor required by JPA.
     */
    protected Invitation() {
    }

    /**
     * Creates a new invitation for the specified account and team.
     *
     * <p>
     * The invitation date is automatically set to the current date,
     * and the initial state is {@link InvitationState#PENDING}.
     * </p>
     *
     * @param invitedAccount the account being invited
     * @param invitingTeam   the team sending the invitation
     * @throws IllegalArgumentException if {@code invitedAccount} or {@code invitingTeam} is null
     */
    public Invitation(Account invitedAccount, Team invitingTeam) {

        if (invitedAccount == null || invitingTeam == null) {
            throw new IllegalArgumentException("Account and Team cannot be null");
        }

        this.invitedAccount = invitedAccount;
        this.invitingTeam = invitingTeam;
        this.invitationDate = LocalDate.now();
        this.state = InvitationState.PENDING;
    }
}

