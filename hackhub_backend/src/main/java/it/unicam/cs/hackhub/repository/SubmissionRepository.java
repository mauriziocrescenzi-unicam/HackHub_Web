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
import it.unicam.cs.hackhub.model.Submission;
import it.unicam.cs.hackhub.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing persistence operations related to {@link Submission} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations such as creation,
 * retrieval, update, and deletion of submissions.
 * </p>
 *
 * <p>
 * Also defines query methods for retrieving submissions associated with specific hackathons
 * or teams, supporting the submission management workflow of the application.
 * </p>
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * Retrieves all {@link Submission} entities associated with the hackathon identified by the given idHackathon.
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return the list of submissions belonging to the specified hackathon;
     *         an empty list if no submissions exist
     */
    List<Submission> findByHackathonIdHackathon(Long idHackathon);

    /**
     * Retrieves the {@link Submission} associated with the specified team and hackathon.
     *
     * <p>
     * This method allows verification of whether a team has already submitted a project
     * for a given hackathon.
     * </p>
     *
     * @param t the team associated with the submission
     * @param h the hackathon associated with the submission
     * @return an {@link Optional} containing the matching submission,
     *         or empty if no submission exists for the specified team and hackathon
     */
    Optional<Submission> findByTeamAndHackathon(Team t, Hackathon h);

    /**
     * Counts the number of hackathons in which the specified team has participated.
     *
     * <p>This query counts distinct hackathon identifiers referenced by
     * {@link Submission} entries belonging to the given team. A team is considered
     * to have "played" a hackathon if it has submitted at least one project for it.</p>
     *
     * @param teamId the unique identifier of the team; must not be {@code null}.
     * @return the number of distinct hackathons played by the team.
     */
    @Query("""
        SELECT COUNT(DISTINCT s.hackathon.idHackathon)
        FROM Submission s
        WHERE s.team.idTeam = :teamId
        """)
    int countPlayed(@Param("teamId") Long teamId);

    /**
     * Counts the number of hackathons won by the specified team.
     *
     * <p>A hackathon is considered won if the team's submission has the
     * highest score among all submissions for that hackathon. The query
     * compares each submission's score with the maximum score recorded
     * for the same hackathon.</p>
     *
     * @param teamId the unique identifier of the team; must not be {@code null}.
     * @return the number of hackathons won by the team.
     */
    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.team.idTeam = :teamId
        AND s.evaluation.score = (
            SELECT MAX(s2.evaluation.score)
            FROM Submission s2
            WHERE s2.hackathon.idHackathon = s.hackathon.idHackathon
        )
        """)
    int countWins(@Param("teamId") Long teamId);

    /**
     * Counts the number of podium finishes achieved by the specified team.
     *
     * <p>A podium finish is defined as placing within the top three submissions
     * of a hackathon based on score. This query determines ranking by counting
     * how many submissions have a higher score for each hackathon. If fewer
     * than three submissions outperform the team's submission, the team is
     * considered to have reached the podium.</p>
     *
     * @param teamId the unique identifier of the team; must not be {@code null}.
     * @return the number of podium finishes (1st, 2nd, or 3rd position) achieved by the team.
     */
    @Query("""
        SELECT COUNT(s)
        FROM Submission s
        WHERE s.team.idTeam = :teamId
        AND (
            SELECT COUNT(s2)
            FROM Submission s2
            WHERE s2.hackathon.idHackathon = s.hackathon.idHackathon
            AND s2.evaluation.score > s.evaluation.score
        ) < 3
        """)
    int countPodiums(@Param("teamId") Long teamId);
}