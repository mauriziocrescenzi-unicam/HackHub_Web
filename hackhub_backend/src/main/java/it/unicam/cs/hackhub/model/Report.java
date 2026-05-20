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

import java.time.LocalDateTime;

/**
 * JPA entity representing a report issued by a mentor
 * concerning a {@link Team} within a specific {@link Hackathon}.
 *
 * <p>
 * A report captures the context (team and hackathon),
 * the issuing mentor, a detailed description of the issue,
 * a categorized reason, and the creation timestamp.
 * </p>
 */

@Entity
@Table(name = "reports")
@Getter
public class Report {

    /**
     * Primary key of the report.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_report")
    private Long idReport;

    /**
     * The team being reported.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /**
     * The hackathon where the report was issued.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    /**
     * The mentor who created the report.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Account mentor;

    /**
     * Detailed description of the issue.
     */
    @Column(nullable = false, length = 2000)
    private String description;

    /**
     * Reason category or short motivation.
     */
    @Column(nullable = false, length = 500)
    private String reason;

    /**
     * Timestamp of report creation.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Protected constructor required by JPA.
     */
    protected Report() {
    }

    /**
     * Creates a new report.
     *
     * <p>
     * The report timestamp is automatically set to the current date-time.
     * </p>
     *
     * @param team        the reported team
     * @param hackathon   the hackathon context
     * @param mentor      the mentor issuing the report
     * @param description detailed description of the issue
     * @param reason      short reason or category
     * @throws NullPointerException if any parameter is null
     *                              or if {@code description} or {@code reason} is blank
     */
    public Report(Team team,
                  Hackathon hackathon,
                  Account mentor,
                  String description,
                  String reason) {

        setTeam(team);
        setHackathon(hackathon);
        setMentor(mentor);
        setDescription(description);
        setReason(reason);
        this.date = LocalDateTime.now();
    }

    private void setReason(String reason) {
        if (reason == null || reason.isBlank())
            throw new NullPointerException("Reason cannot be null or blank");
        this.reason = reason;
    }

    private void setDescription(String description) {
        if (description == null || description.isBlank())
            throw new NullPointerException("Description cannot be null or blank");
        this.description = description;
    }

    private void setMentor(Account mentor) {
        if (mentor == null)
            throw new NullPointerException("Mentor cannot be null");
        this.mentor = mentor;
    }

    private void setHackathon(Hackathon hackathon) {
        if (hackathon == null)
            throw new NullPointerException("Hackathon cannot be null");
        this.hackathon = hackathon;
    }

    private void setTeam(Team team) {
        if (team == null)
            throw new NullPointerException("Team cannot be null");
        this.team = team;
    }
}
