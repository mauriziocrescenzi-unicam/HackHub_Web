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

import it.unicam.cs.hackhub.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing persistence operations related to {@link Hackathon} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations such as creation,
 * retrieval, update, and deletion of hackathon instances.
 * </p>
 *
 * <p>
 * Additionally defines custom query methods to support application-specific requirements,
 * such as verifying whether an account belongs to a hackathon staff and retrieving
 * hackathons associated with a specific team.
 * </p>
 */
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {

    /**
     * Determines whether the account identified by the given idAccount is assigned to the staff
     * of at least one hackathon.
     *
     * <p>
     * An account is considered part of the staff if it appears as:
     * organizer, mentor, or judge in any {@link Hackathon}.
     * </p>
     *
     * @param idAccount the unique identifier of the account to check
     * @return {@code true} if the account is assigned to the staff of at least one hackathon;
     *         {@code false} otherwise
     */
    @Query("SELECT COUNT(h) > 0 FROM Hackathon h " +
            "LEFT JOIN h.staff.mentors m " +
            "LEFT JOIN h.staff.judge j " +
            "WHERE h.staff.organizer.idAccount = :id " +
            "OR m.idAccount = :id " +
            "OR j.idAccount = :id")
    boolean isAccountInStaff(@Param("id") Long idAccount);

    /**
     * Retrieves all hackathons in which the team identified by the given idAccount is registered.
     *
     * <p>
     * Returns the list of {@link Hackathon} entities associated with the team whose identifier
     * matches {@code idTeam}.
     * </p>
     *
     * @param idTeam the unique identifier of the team
     * @return the list of hackathons the specified team participates in; an empty list if none
     */
    @Query("SELECT h FROM Hackathon h JOIN h.teams t WHERE KEY(t).idTeam = :idTeam")
    List<Hackathon> findByTeamId(@Param("idTeam") Long idTeam);

    /**
     * Retrieves all {@link Hackathon} instances in which the specified account
     * is involved in any role or association.
     *
     * <p>This query returns distinct hackathons where the user participates as:</p>
     * <ul>
     *     <li>organizer,</li>
     *     <li>judge,</li>
     *     <li>mentor,</li>
     *     <li>team leader,</li>
     *     <li>team member.</li>
     * </ul>
     *
     * <p>The method uses multiple JOIN operations to detect any type of direct or
     * indirect participation of the account inside the hackathon structure, and
     * returns a deduplicated list of results via {@code SELECT DISTINCT}.</p>
     *
     * @param idAccount the unique identifier of the account whose hackathons
     *                  should be retrieved; must not be {@code null}.
     *
     * @return a list of distinct {@link Hackathon} entities in which the user
     *         is involved. The list may be empty but is never {@code null}.
     */
    @Query("""
    SELECT DISTINCT h
    FROM Hackathon h

    LEFT JOIN h.staff s
    LEFT JOIN s.mentors m

    LEFT JOIN h.teams tMap
    LEFT JOIN KEY(tMap) t
    LEFT JOIN t.members mem

    WHERE
       s.organizer.idAccount = :id
    OR s.judge.idAccount = :id
    OR m.idAccount = :id
    OR t.leader.idAccount = :id
    OR mem.idAccount = :id
    """)
    List<Hackathon> findMyHackathons(@Param("id") Long idAccount);
}
