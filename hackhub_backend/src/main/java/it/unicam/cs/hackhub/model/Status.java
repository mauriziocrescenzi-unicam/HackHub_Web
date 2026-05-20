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
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Embeddable value object representing the lifecycle
 * state of a {@link Hackathon}.
 *
 * <p>
 * A {@code Status} encapsulates the temporal boundaries
 * of the hackathon (start and end dates) and whether it
 * has been forcibly terminated.
 * </p>
 *
 * <p>
 * This object is embedded within {@link Hackathon}
 * and does not possess its own identity.
 * </p>
 */
@Embeddable
@Getter
public class Status {

    /**
     * Indicates whether the hackathon has been forcibly completed.
     */
    @Column(nullable = false)
    private boolean terminated;

    /**
     * Start date of the hackathon.
     */
    @Column(nullable = false)
    private LocalDateTime startDate;

    /**
     * End date of the hackathon.
     */
    @Column(nullable = false)
    private LocalDateTime endDate;

    /**
     * Protected constructor required by JPA.
     */
    protected Status() {
    }

    /**
     * Creates a lifecycle status with the specified time boundaries.
     *
     * @param startDate non-null start date of the hackathon
     * @param endDate   non-null end date of the hackathon
     * @throws NullPointerException     if {@code startDate} or {@code endDate} is null
     * @throws IllegalArgumentException if {@code endDate} precedes {@code startDate}
     */
    public Status(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null)
            throw new NullPointerException("Dates cannot be null");

        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("End date cannot precede start date");

        this.startDate = startDate;
        this.endDate = endDate;
        this.terminated = false;
    }

    /**
     * Computes the current {@link StatusValue} based on
     * the current system time and termination flag.
     *
     * <p>
     * The lifecycle is determined as follows:
     * </p>
     * <ul>
     *     <li>{@code COMPLETED} if forcibly terminated</li>
     *     <li>{@code REGISTRATION} if current time is before start date</li>
     *     <li>{@code ONGOING} if current time is between start and end dates</li>
     *     <li>{@code EVALUATION} if current time is after end date</li>
     * </ul>
     *
     * @return the current lifecycle state
     */
    public StatusValue getStatus() {
        if (terminated)
            return StatusValue.COMPLETED;

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate))
            return StatusValue.REGISTRATION;

        if (now.isBefore(endDate))
            return StatusValue.ONGOING;

        return StatusValue.EVALUATION;
    }

    /**
     * Forces the hackathon into the {@link StatusValue#COMPLETED} state,
     * regardless of temporal boundaries.
     */
    public void completeHackathon() {
        this.terminated = true;
    }
}
