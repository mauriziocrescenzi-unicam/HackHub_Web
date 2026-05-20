package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.Account;


/**
 * Represents a response object containing essential information
 * about a team member participating in a hackathon.
 *
 * <p>This record is used as a DTO (Data Transfer Object) to expose
 * only the necessary fields of a {@link Account} entity when returning
 * team member information from the API.</p>
 *
 * @param idTeamMember the unique identifier of the team member.
 * @param nickname     the display name or nickname of the team member.
 * @param email        the email address associated with the team member account.
 */
public record TeamMemberResponse(
        Long idTeamMember,
        String nickname,
        String email
) {
    /**
     * Converts an {@link Account} entity into a {@link TeamMemberResponse}.
     *
     * <p>If the given account is {@code null}, the method returns {@code null}
     * to avoid throwing a {@link NullPointerException} and to maintain
     * compatibility with optional mappings.</p>
     *
     * @param account the {@link Account} entity to convert; may be {@code null}.
     * @return a {@link TeamMemberResponse} representing the given account,
     *         or {@code null} if the input is {@code null}.
     */
    public static TeamMemberResponse fromEntity(Account account) {
        if(account == null) return null;
        return new TeamMemberResponse(account.getIdAccount(), account.getNickname(),
                account.getEmail());
    }
}
