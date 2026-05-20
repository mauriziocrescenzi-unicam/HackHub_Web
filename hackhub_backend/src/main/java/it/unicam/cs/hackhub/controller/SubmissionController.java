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
package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.DTO.SubmissionResponse;
import it.unicam.cs.hackhub.model.Submission;
import it.unicam.cs.hackhub.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing hackathon submissions.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>Submitting and updating projects</li>
 *     <li>Retrieving submissions (staff or team-restricted access)</li>
 *     <li>Evaluating submissions</li>
 *     <li>Proclaiming the winner of a hackathon</li>
 * </ul>
 * All business logic is delegated to the {@link SubmissionService}.
 */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * Creates a new {@code SubmissionController}.
     *
     * @param submissionService the service responsible for submission management
     */
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * Submits a new project for a specific hackathon.
     * <p>
     * The request body must contain:
     * <ul>
     *     <li>{@code idHackathon} – identifier of the hackathon</li>
     *     <li>{@code type} – submission type (e.g., GitHub, file, etc.)</li>
     *     <li>{@code source} – project source reference</li>
     * </ul>
     *
     * @param payload a map containing submission parameters
     * @return a {@link ResponseEntity} with HTTP status 201 (Created)
     *         and a confirmation message
     */
    @PostMapping
    public ResponseEntity<String> submitProject(@RequestBody Map<String,
            Object> payload) {
        Long idHackathon = ((Number) payload.get("idHackathon")).longValue();
        submissionService.submit(
                idHackathon,
                (String) payload.get("type"),
                (String) payload.get("source")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Submission submitted " +
                "successfully.");
    }

    /**
     * Updates an existing submission.
     * <p>
     * Typically used to refresh submission data (e.g., retrieving updated
     * metadata from external sources such as GitHub).
     *
     * @param id the identifier of the submission
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSubmission(@PathVariable Long id) {
        submissionService.update(id);
        return ResponseEntity.ok("Submission updated successfully.");
    }

    /**
     * Retrieves a submission for staff members (requires staff validation).
     */
    @GetMapping("/{id}/staff")
    public ResponseEntity<SubmissionResponse> getSubmissionForStaff(@PathVariable Long id) {
        Submission s= submissionService.getSubmissionStaff(id);
        if(s == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(SubmissionResponse.fromEntity(s));
    }

    /**
     * Retrieves a submission for staff members.
     * <p>
     * Access validation is performed at the service layer.
     *
     * @param id the submission identifier
     * @return a {@link ResponseEntity} containing the {@link SubmissionResponse}
     *         if found (200 OK), or 404 (Not Found) otherwise
     */
    @GetMapping("/{id}/team")
    public ResponseEntity<SubmissionResponse> getSubmissionForTeamMember(
            @PathVariable Long id) {
        Submission s= submissionService.getSubmissionTeamMembers(id);
        if(s == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(SubmissionResponse.fromEntity(s));
    }

    /**
     * Retrieves all submissions for a specific hackathon.
     * Restricted to staff members assigned to the hackathon.
     *
     * @param idHackathon the hackathon identifier
     * @return a {@link ResponseEntity} containing a list of {@link SubmissionResponse}
     */
    @GetMapping("/hackathons/{idHackathon}/staff")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsForHackathon(@PathVariable Long idHackathon) {
        List<Submission> submissions = submissionService.getSubmissionsByHackathonStaff(idHackathon);

        List<SubmissionResponse> responseList = submissions.stream()
                .map(SubmissionResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(responseList);
    }
    /**
     * Evaluates a submission by assigning a score and written judgment.
     *
     * @param id      the submission identifier
     * @param payload a map containing:
     *                <ul>
     *                    <li>{@code writtenJudgment} – textual evaluation</li>
     *                    <li>{@code score} – numeric score assigned</li>
     *                </ul>
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @PostMapping("/{id}/evaluation")
    public ResponseEntity<String> evaluateSubmission(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        submissionService.evaluateSubmission(
                id,
                (String) payload.get("writtenJudgment"),
                ((Number) payload.get("score")).doubleValue()
        );
        return ResponseEntity.ok("Evaluation submitted.");
    }

    /**
     * Determines the winning team of a hackathon.
     * <p>
     * The winner is computed based on submitted evaluations.
     *
     * @param idHackathon the identifier of the hackathon
     * @return if a winner is determined (200 OK),
     *         or 404 (Not Found) if no winner can be proclaimed
     */
    @PostMapping("/winner/hackathons/{idHackathon}")
    public ResponseEntity<String> proclaimersWinner(@PathVariable Long idHackathon){
        submissionService.proclamationWinner(idHackathon);
        return ResponseEntity.ok("Winner proclamated successfully.");
    }
}