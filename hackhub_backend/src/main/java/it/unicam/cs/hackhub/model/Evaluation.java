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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Immutable value object representing an evaluation assigned to a submission.
 *
 * <p>
 * An {@code Evaluation} encapsulates:
 * </p>
 * <ul>
 *     <li>A qualitative written judgment</li>
 *     <li>A quantitative score within the range [0, 10]</li>
 * </ul>
 *
 * <p>
 * This type is marked as {@link Embeddable} and is designed to be embedded
 * within a parent entity (e.g., {@code Submission}) as part of its persistent state.
 * Being a record, it is immutable and validated upon construction.
 * </p>
 */
@Embeddable
public record Evaluation(

        /**
         * Textual qualitative assessment provided by a judge.
         */
        @Column(length = 1000)
        String writtenJudgment,

        /**
         * Numerical score assigned to the submission.
         * Must be between 0 and 10.
         */
        double score

) {

    /**
     * Canonical constructor enforcing validation constraints.
     *
     * @param writtenJudgment a non-null, non-blank textual evaluation
     * @param score           a numeric value in the inclusive range [0, 10]
     * @throws IllegalArgumentException if the judgment is null/blank
     *                                  or the score is outside the valid range
     */
    public Evaluation {

        if (writtenJudgment == null || writtenJudgment.isBlank()) {
            throw new IllegalArgumentException(
                    "Written judgment must not be null or blank"
            );
        }

        if (score < 0 || score > 10.0) {
            throw new IllegalArgumentException(
                    "Score must be between 0 and 10.0"
            );
        }
    }
}
