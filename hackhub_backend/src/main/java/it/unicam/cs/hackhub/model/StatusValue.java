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
/**
 * Enumeration representing the lifecycle phases of a {@link Hackathon}.
 *
 * <p>
 * The hackathon progresses through the following states:
 * </p>
 * <ul>
 *     <li>{@link #REGISTRATION} – Registration phase; teams can enroll.</li>
 *     <li>{@link #ONGOING} – Active phase; the hackathon is in progress.</li>
 *     <li>{@link #EVALUATION} – Post-event phase; submissions are evaluated.</li>
 *     <li>{@link #COMPLETED} – Final phase; the event is closed and results finalized.</li>
 * </ul>
 */
public enum StatusValue {
    /**
     * Registration is open for the hackathon.
     */
    REGISTRATION,

    /**
     * The hackathon is currently running.
     */
    ONGOING,

    /**
     * The hackathon has ended and submissions are no longer editable.
     * Judges evaluate the submitted projects and assign scores.
     */
    EVALUATION,

    /**
     * The hackathon is definitively closed and a winner has been declared.
     */
    COMPLETED
}