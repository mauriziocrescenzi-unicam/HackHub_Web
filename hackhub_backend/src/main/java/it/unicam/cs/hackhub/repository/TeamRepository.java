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
package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing persistence operations related to {@link Team} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations such as creation,
 * retrieval, update, and deletion of teams.
 * </p>
 *
 * <p>
 * Also defines query methods for retrieving teams based on leader or member accounts,
 * supporting team management and participation workflows within hackathons.
 * </p>
 */
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Retrieves the {@link Team} led by the account identified by the given idAccount.
     *
     * @param idAccount the unique identifier of the leader account
     * @return an {@link Optional} containing the team led by the specified account,
     *         or empty if the account does not lead any team
     */
    Optional<Team> findByLeaderIdAccount(Long idAccount);

    /**
     * Checks whether there exists a team that includes the account identified by the given idAccount as a member.
     *
     * @param idAccount the unique identifier of the account
     * @return true if the account is a member of at least one team, false otherwise
     */
    boolean existsByMembersIdAccount(Long idAccount);

    /**
     * Checks whether there exists a team led by the account identified by the given idAccount.
     *
     * @param idAccount the unique identifier of the account
     * @return true if the account is the leader of a team, false otherwise
     */
    boolean existsByLeaderIdAccount(Long idAccount);

    /**
     * Retrieves the {@link Team} that includes the account identified by the given idAccount as a member.
     *
     * @param idAccount the unique identifier of the account
     * @return an {@link Optional} containing the team the account belongs to,
     *         or empty if the account is not a member of any team
     */
    Optional<Team> findByMembersIdAccount(Long idAccount);
}
