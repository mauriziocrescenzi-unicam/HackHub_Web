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
package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.GitHubSubmission;
import it.unicam.cs.hackhub.model.Submission;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Data Transfer Object (DTO) representing a hackathon submission.
 * <p>
 * Encapsulates submission metadata, evaluation details (if present),
 * and contextual information about the related team and hackathon.
 *
 * @param id                the unique identifier of the submission
 * @param idTeam            the unique identifier of the team
 * @param team              the name of the team that submitted the project
 * @param hackathon         the name of the related hackathon
 * @param submittedAt       the date and time when the submission was created
 * @param immutableReference an immutable reference to the submitted project
 *                          (e.g., commit SHA, file hash, etc.)
 * @param writtenJudgment   the written evaluation provided by staff (if evaluated)
 * @param score             the numeric score assigned to the submission (if evaluated)
 */
public record SubmissionResponse(
        Long id,
        Long idTeam,
        String team,
        String hackathon,
        LocalDateTime submittedAt,
        Optional<String> repositoryUrl,
        String immutableReference,
        String writtenJudgment,
        Double score,
        boolean teamDisabled
) {
    /**
     * Converts a {@link Submission} entity into a {@link SubmissionResponse}.
     * <p>
     * Safely handles potential {@code null} values for nested objects,
     * including evaluation, team, and hackathon.
     *
     * @param s the {@link Submission} entity to convert
     * @return a corresponding {@link SubmissionResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static SubmissionResponse fromEntity(Submission s) {
        if (s == null) {
            return null;
        }

        String judgment = null;
        Double score = null;

        if (s.getEvaluation() != null) {
            judgment = s.getEvaluation().writtenJudgment();
            score = s.getEvaluation().score();
        }

        Optional<String> repositoryUrl = s instanceof GitHubSubmission gh
                ? Optional.of(gh.getRepositoryUrl())
                : Optional.empty();

        boolean teamDisabled = false;
        if (s.getHackathon() != null && s.getTeam() != null) {
            teamDisabled = s.getHackathon().getTeams()
                    .getOrDefault(s.getTeam(), false);
        }

        return new SubmissionResponse(
                s.getIdSubmission(),
                s.getTeam() != null ? s.getTeam().getIdTeam() : null,
                s.getTeam() != null ? s.getTeam().getName() : null,

                s.getHackathon() != null ? s.getHackathon().getName() : null,

                s.getSubmittedAt(),
                repositoryUrl,
                s.getImmutableReference(),

                judgment,
                score,
                teamDisabled
        );
    }
}
