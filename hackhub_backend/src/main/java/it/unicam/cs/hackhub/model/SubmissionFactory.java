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
 * Factory abstraction for creating {@link Submission} instances.
 *
 * <p>
 * Implementations encapsulate the instantiation logic of specific
 * submission types, allowing the client to remain decoupled from
 * concrete {@code Submission} subclasses.
 * </p>
 */
public interface SubmissionFactory {

    /**
     * Creates a new {@link Submission} associated with the specified
     * {@link Team} and {@link Hackathon}.
     *
     * <p>
     * The {@code source} parameter represents an external reference
     * to the submitted content (e.g., repository URL, file path,
     * external identifier), whose interpretation depends on the
     * concrete factory implementation.
     * </p>
     *
     * @param team       non-null team submitting the project
     * @param hackathon  non-null hackathon context
     * @param source     non-null reference identifying the submitted content
     * @return a newly created {@link Submission} instance
     * @throws NullPointerException if required parameters are null
     */
    Submission create(Team team, Hackathon hackathon, String source);
}