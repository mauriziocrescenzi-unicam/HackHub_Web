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

import it.unicam.cs.hackhub.DTO.ReportRequest;
import it.unicam.cs.hackhub.DTO.ReportResponse;
import it.unicam.cs.hackhub.DTO.SubmissionResponse;
import it.unicam.cs.hackhub.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for report handling and team moderation.
 * <p>
 * Provides endpoints to:
 * <ul>
 *     <li>Submit reports against teams participating in a hackathon</li>
 *     <li>Enable or disable teams as a moderation action</li>
 * </ul>
 * Business logic is delegated to the {@link ReportService}.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * Creates a new {@code ReportController}.
     *
     * @param reportService the service responsible for report management
     */
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Submits a report against a team.
     * <p>
     * The request body must contain:
     * <ul>
     *     <li>{@code idTeam} – identifier of the reported team</li>
     *     <li>{@code idHackathon} – identifier of the related hackathon</li>
     *     <li>{@code description} – detailed explanation of the issue</li>
     *     <li>{@code reason} – short reason/category of the report</li>
     * </ul>
     *
     * @param request report parameters
     * @return a {@link ResponseEntity} with HTTP status 201 (Created)
     *         and a confirmation message
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> reportTeam(@RequestBody ReportRequest request) {
        reportService.reportTeam(
                request.idTeam(),
                request.idHackathon(),
                request.description(),
                request.reason()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Report submitted successfully."));
    }

    /**
     * Performs a moderation action on a team within a hackathon.
     * <p>
     * Depending on the value of {@code disabled}, the team is either
     * disabled (true) or re-enabled (false).
     *
     * @param idHackathon the identifier of the hackathon
     * @param idTeam      the identifier of the team
     * @param disabled    {@code true} to disable the team,
     *                    {@code false} to enable it
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     *         and a confirmation message
     */
    @PatchMapping("/management/hackathons/{idHackathon}/teams/{idTeam}")
    public ResponseEntity<Map<String, String>> reportManagement(
            @PathVariable Long idHackathon,
            @PathVariable Long idTeam,
            @RequestParam boolean disabled) {
        reportService.reportManagement(disabled, idHackathon, idTeam);
        String action = disabled ? "disabled" : "enabled";
        return ResponseEntity.ok(Map.of("message",
                "Team " + idTeam + " has been " + action + " " +
                "successfully."));
    }

    @GetMapping("hackathons/{idHackathon}/teams/{idTeam}")
    public ResponseEntity<List<ReportResponse>> getReportsByTeam( @PathVariable Long idHackathon,
                                                                  @PathVariable Long idTeam)
    {
        return ResponseEntity.ok(reportService.getReportsByTeam(idHackathon, idTeam).stream()
                .map(ReportResponse::fromEntity)
                .toList());
    }
}