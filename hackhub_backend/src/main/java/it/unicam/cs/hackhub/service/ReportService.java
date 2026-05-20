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
package it.unicam.cs.hackhub.service;

import it.unicam.cs.hackhub.model.*;
import it.unicam.cs.hackhub.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Service layer component responsible for managing {@link Report} entities and report-related workflows.
 *
 * <p>
 * Provides operations for creating reports against teams within hackathons and delegates
 * hackathon-related management actions (e.g., enable/disable team participation) to {@link HackathonService}.
 * </p>
 *
 * <p>
 * Access to report operations is restricted to staff members and may require additional checks,
 * such as verifying mentor assignment within the target hackathon.
 * </p>
 */
@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final HackathonService hackathonService;
    private final AccountService accountService;

    /**
     * Creates a new {@code ReportService} with the required dependencies.
     *
     * @param reportRepository the repository used to access and persist reports
     * @param hackathonService the service used to retrieve hackathons and perform hackathon-related operations
     * @param accountService the service used to retrieve and validate accounts
     */
    public ReportService(ReportRepository reportRepository, HackathonService hackathonService,
                         AccountService accountService) {
        this.reportRepository = reportRepository;
        this.hackathonService = hackathonService;
        this.accountService = accountService;
    }

    /**
     * Delegates team enable/disable management within a hackathon to {@link HackathonService}.
     *
     * @param disabledTeam {@code true} to disable the team; {@code false} to enable it
     * @param idHackathon the unique identifier of the hackathon
     * @param idTeam the unique identifier of the team to update
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     * @throws NullPointerException if the hackathon does not exist (propagated from {@link HackathonService})
     */
    @PreAuthorize("hasRole('STAFF')")
    public void reportManagement(boolean disabledTeam, Long idHackathon, Long idTeam) {
        hackathonService.reportManagement(disabledTeam, idHackathon, idTeam);
    }

    /**
     * Creates and persists a new report for the specified team within the specified hackathon.
     *
     * <p>
     * The authenticated staff member must be a mentor assigned to the target hackathon.
     * The report is associated with the team, the hackathon, and the reporting mentor account.
     * </p>
     *
     * @param idTeam the unique identifier of the team being reported
     * @param idHackathon the unique identifier of the hackathon in which the report is filed
     * @param description the report description
     * @param reason the report reason
     * @throws IllegalArgumentException if the hackathon cannot be found or the authenticated account cannot be resolved
     * @throws AccessDeniedException if the authenticated staff member is not a mentor assigned to the specified hackathon
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void reportTeam(Long idTeam, Long idHackathon, String description, String reason) {
        Hackathon hackathon = hackathonService.getHackathon(idHackathon);
        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon not found with ID: " + idHackathon);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account currentUser = accountService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + auth.getName()));
        boolean isMentor = hackathon.getStaff()
                .getMentors()
                .stream()
                .anyMatch(a -> a.getIdAccount().equals(currentUser.getIdAccount()));

        if (!isMentor)
            throw new AccessDeniedException("Mentor not assigned to this hackathon");

        Team team = hackathon.getTeamById(idTeam);

        Report report = new Report(team, hackathon, currentUser, description, reason);
        reportRepository.save(report);
    }

    @PreAuthorize("hasRole('STAFF')")
    public List<Report> getReportsByTeam(Long idHackathon, Long idTeam) {
        Hackathon hackathon = hackathonService.getHackathon(idHackathon);
        Team team = hackathon.getTeamById(idTeam);
        return reportRepository.findByTeamAndHackathon(team, hackathon);
    }
}