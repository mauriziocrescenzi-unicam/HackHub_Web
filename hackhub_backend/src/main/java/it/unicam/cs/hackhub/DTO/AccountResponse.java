package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Team;

import java.util.Optional;

/**
 * Data Transfer Object (DTO) representing a user account for REST responses.
 * <p>
 * Exposes only non-sensitive fields.
 *
 * @param idAccount the account ID
 * @param name      the first name
 * @param surname   the last name
 * @param nickname  the account nickname
 * @param email     the account email
 * @param role      the role of the account
 * @param disabled  whether the account is disabled
 * @param idTeam  the team ID
 */
public record AccountResponse(
        Long idAccount,
        String name,
        String surname,
        String nickname,
        String email,
        String role,
        boolean disabled,
        Long idTeam,
        String teamName
) {

    /**
     * Converts an {@link Account} entity into an {@link AccountResponse}.
     *
     * @param account the account entity
     * @param team the team associated to the account
     * @return the corresponding DTO, or {@code null} if {@code account} is null
     */
    public static AccountResponse fromEntity(Account account, Optional<Team> team) {
        if (account == null) return null;

        return new AccountResponse(
                account.getIdAccount(),
                account.getName(),
                account.getSurname(),
                account.getNickname(),
                account.getEmail(),
                account.getRole().name(),
                account.isDisabled(),
                team.<Long>map(Team::getIdTeam).orElse(null),
                team.<String>map(Team::getName).orElse(null)
        );
    }
}