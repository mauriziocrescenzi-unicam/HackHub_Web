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

import java.time.LocalDateTime;

/**
 * Abstract JPA entity representing a generic submission
 * associated with a {@link Team} and a {@link Hackathon}.
 *
 * <p>
 * A {@code Submission} models a deliverable produced by a team
 * and supports evaluation through an embedded {@link Evaluation}.
 * </p>
 *
 * <p>
 * Concrete subclasses define the specific type of deliverable
 * (e.g., GitHub repositories, file uploads, or other content sources).
 * </p>
 */
@Entity
@Table(name = "submissions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "submission_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    protected Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    protected Hackathon hackathon;

    @Column(nullable = false)
    protected LocalDateTime submittedAt;

    @Embedded
    protected Evaluation evaluation;

    /**
     * Determines whether the submission can be modified.
     *
     * <p>
     * A submission is editable only when the associated hackathon
     * is in the {@link StatusValue#ONGOING} phase.
     * </p>
     *
     * @return {@code true} if editable, {@code false} otherwise
     */
    public boolean isEditable() {
        return hackathon.getStatusValue() == StatusValue.ONGOING;
    }

    /**
     * Indicates whether the submission has been evaluated.
     *
     * @return {@code true} if an {@link Evaluation} has been assigned,
     *         {@code false} otherwise
     */
    public boolean isEvaluated() {
        return evaluation != null;
    }

    /**
     * Assigns an {@link Evaluation} to this submission.
     *
     * <p>
     * An evaluation can be assigned only once.
     * </p>
     *
     * @param evaluation non-null evaluation instance
     * @throws NullPointerException  if {@code evaluation} is null
     * @throws IllegalStateException if the submission has already been evaluated
     */

    public void setEvaluation(Evaluation evaluation) {

        if (evaluation == null) {
            throw new NullPointerException("Evaluation cannot be null");
        }

        if (this.evaluation != null) {
            throw new IllegalStateException("Submission already evaluated");
        }

        this.evaluation = evaluation;
    }
    /**
     * Returns a stable, immutable reference identifying
     * the submitted content.
     *
     * <p>
     * The reference must uniquely identify the deliverable
     * and must not change over time
     * (e.g., commit hash, checksum, permanent URL).
     * </p>
     *
     * @return immutable reference string
     */
    public abstract String getImmutableReference();

}