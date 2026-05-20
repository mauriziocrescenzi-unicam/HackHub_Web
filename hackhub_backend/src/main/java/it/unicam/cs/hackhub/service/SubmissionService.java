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
import it.unicam.cs.hackhub.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service layer component responsible for managing {@link Submission} entities and submission workflows.
 *
 * <p>
 * Provides operations for creating and updating submissions, retrieving submissions with authorization checks
 * (staff/team members), evaluating submissions during the evaluation phase, and determining the winning team
 * of a hackathon.
 * </p>
 *
 * <p>
 * Submissions are created through registered {@link SubmissionFactory} implementations, allowing support for
 * multiple submission types (e.g., GitHub-based submissions).
 * </p>
 */
@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final HackathonService hackathonService;
    private final TeamService teamService;
    private final AccountService accountService;

    /** Registered factories for different submission types (e.g., "github"). */
    private final Map<String, SubmissionFactory> factories;

    /**
     * Creates a new {@code SubmissionService} with the required dependencies and registers available submission factories.
     *
     * @param submissionRepository the repository used to access and persist submissions
     * @param hackathonService the service used to retrieve hackathons and validate hackathon-related constraints
     * @param teamService the service used to retrieve teams and validate team membership
     * @param accountService the service used to retrieve and validate accounts
     */
    public SubmissionService(SubmissionRepository submissionRepository,
                             HackathonService hackathonService,
                             TeamService teamService,
                             AccountService accountService) {
        this.submissionRepository = submissionRepository;
        this.hackathonService = hackathonService;
        this.teamService = teamService;

        this.factories = new HashMap<>();
        this.factories.put("github", new GitHubSubmissionFactory());
        this.accountService = accountService;
    }

    /**
     * Submits (or replaces) a project submission for the authenticated user's team in the specified hackathon.
     *
     * <p>
     * The hackathon must exist and be in {@link StatusValue#ONGOING} state. The authenticated user must belong to a team
     * that is registered to the hackathon and not disabled. If a submission for the same team and hackathon already exists,
     * it is deleted and replaced with the new one.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @param type the submission type (e.g., {@code "github"})
     * @param source the source reference (e.g., repository URL)
     * @throws NullPointerException if the hackathon cannot be found
     * @throws IllegalArgumentException if the authenticated user does not belong to any team or if the submission type is unsupported
     * @throws IllegalStateException if the hackathon is not currently running
     * @throws AccessDeniedException if the team is not registered to the hackathon or is disabled
     * @throws java.util.NoSuchElementException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public void submit(Long idHackathon, String type, String source) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Hackathon h = hackathonService.getHackathon(idHackathon);
        Account a=accountService.findByEmail(auth.getName()).get();
        Team t = teamService.findMemberById(a.getIdAccount())
                .orElseThrow(() -> new IllegalArgumentException("You doesn't belong to " +
                        "any team"));
        if (h == null) {
            throw new NullPointerException("Hackathon not found");
        }
        if (h.getStatusValue() != StatusValue.ONGOING) {
            throw new IllegalStateException("Hackathon not running");
        }

        if (!h.getTeams().containsKey(t) || h.getTeams().get(t))
            throw new AccessDeniedException("Team not registered at this hackathon or " +
                    "disabled");

        Optional<Submission> existingOpt = submissionRepository.findByTeamAndHackathon(t, h);

        if (existingOpt.isPresent()) {
            Submission existing = existingOpt.get();
            existing.setSubmittedAt(LocalDateTime.now());

            if (existing instanceof GitHubSubmission githubSub) {
                githubSub.setRepositoryUrl(source);
            }

            submissionRepository.save(existing);
        } else {
            SubmissionFactory factory = factories.get(type.toLowerCase());
            if (factory == null) throw new IllegalArgumentException("Type not supported");

            Submission newSub = factory.create(t, h, source);
            submissionRepository.save(newSub);
        }
    }
    /**
     * Retrieves all submissions for a specific hackathon.
     * Verifies that the authenticated account is assigned to the hackathon staff.
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return a list of {@link Submission} for the hackathon
     * @throws NullPointerException if the hackathon does not exist
     * @throws AccessDeniedException if the authenticated staff account is not assigned to the hackathon
     */
    @PreAuthorize("hasRole('STAFF')")
    public List<Submission> getSubmissionsByHackathonStaff(Long idHackathon) {
        Hackathon h = hackathonService.getHackathon(idHackathon);
        if (h == null) {
            throw new NullPointerException("Hackathon not found");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a = accountService.findByEmail(auth.getName()).get();

        if (!h.checkStaff(a.getIdAccount())) {
            throw new AccessDeniedException("Staff not assigned to this hackathon");
        }

        return submissionRepository.findByHackathonIdHackathon(idHackathon);
    }

    /**
     * Updates the specified submission if it is still editable and the associated team is not disabled.
     *
     * <p>
     * The update operation is delegated to the concrete submission type (e.g., {@link GitHubSubmission}).
     * The submission cannot be updated if it is no longer editable (e.g., hackathon ended) or if the submission type
     * does not support updates.
     * </p>
     *
     * @param submissionId the unique identifier of the submission
     * @throws IllegalArgumentException if no submission with the specified identifier exists
     * @throws AccessDeniedException if the associated team is disabled
     * @throws IllegalStateException if the submission is not editable or if update is not supported for its concrete type
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public void update(Long submissionId) {
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if(s.getHackathon().getTeams().get(s.getTeam()))
            throw new AccessDeniedException("Team disabled");

        if (!s.isEditable()) {
            throw new IllegalStateException("Submission cannot be edited (Hackathon ended or other constraint)");
        }

        if (s instanceof GitHubSubmission g) {
            g.update();
        } else {
            throw new IllegalStateException("Update not supported for type: " + s.getClass().getSimpleName());
        }

        submissionRepository.save(s);
    }

    /**
     * Retrieves the submission with the specified identifier.
     *
     * @param idSubmission the unique identifier of the submission
     * @return the matching {@link Submission} if found; {@code null} otherwise
     */
    private Submission getSubmission(Long idSubmission) {
        return  submissionRepository.findById(idSubmission).orElse(null);
    }

    /**
     * Retrieves a submission for staff access, verifying that the authenticated account is assigned to the hackathon staff.
     *
     * @param idSubmission the unique identifier of the submission
     * @return the matching {@link Submission}
     * @throws NullPointerException if the submission does not exist
     * @throws AccessDeniedException if the authenticated staff account is not assigned to the submission hackathon
     * @throws java.util.NoSuchElementException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public Submission getSubmissionStaff(Long idSubmission) {
        Submission submission = getSubmission(idSubmission);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a=accountService.findByEmail(auth.getName()).get();
        if(submission==null)
            throw new NullPointerException("Submission not found");
        if (!submission.getHackathon().checkStaff(a.getIdAccount())) {
            throw new  AccessDeniedException("Staff not assigned to this hackathon");
        }
        return submission;
    }

    /**
     * Retrieves a submission for team-member access, verifying that the authenticated account belongs to the submission team.
     *
     * @param idSubmission the unique identifier of the submission
     * @return the matching {@link Submission}
     * @throws NullPointerException if the submission does not exist
     * @throws AccessDeniedException if the authenticated account is not a member of the submission team
     * @throws java.util.NoSuchElementException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public Submission getSubmissionTeamMembers(Long idHackathon){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a=accountService.findByEmail(auth.getName()).get();
        Team t=teamService.findMemberById(a.getIdAccount()).orElseThrow(() -> new NullPointerException("You doesn't belong to any team"));
        Submission submission = submissionRepository.findByTeamAndHackathon(t, hackathonService.getHackathon(idHackathon)).orElse(null);
        if(submission==null)
            throw new NullPointerException("Submission not found");
        if(!submission.getTeam().checkTeamMember(a.getIdAccount()))
            throw new AccessDeniedException("Team member not assigned to this team");
        return submission;
    }

    /**
     * Evaluates the specified submission by assigning a written judgment and a numeric score.
     *
     * <p>
     * Only the judge assigned to the submission hackathon can perform the evaluation. The hackathon must be in
     * {@link StatusValue#EVALUATION} phase, the associated team must not be disabled, and the submission must not have
     * been already evaluated.
     * </p>
     *
     * @param idSubmission the unique identifier of the submission
     * @param writtenJudgment the textual judgment for the submission
     * @param score the numeric score assigned to the submission
     * @throws IllegalArgumentException if the submission does not exist or if the hackathon is not in evaluation phase
     * @throws AccessDeniedException if the authenticated staff account is not the judge or if the team is disabled
     * @throws IllegalStateException if the submission has already been evaluated
     * @throws java.util.NoSuchElementException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void evaluateSubmission(Long idSubmission,
                                   String writtenJudgment,
                                   double score) {
        Submission submission = submissionRepository.findById(idSubmission)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a=accountService.findByEmail(auth.getName()).get();

        if(!submission.getHackathon().getStaff().getJudge().equals(a))
            throw new AccessDeniedException("Staff has to be a judge");

        if(submission.getHackathon().getTeams().get(submission.getTeam()))
            throw new AccessDeniedException("Team disabled");

        if (submission.getHackathon().getStatusValue() != StatusValue.EVALUATION)
            throw new IllegalArgumentException("Hackathon not in evaluation phase");

        if (submission.isEvaluated())
            throw new IllegalStateException("Submission already evaluated");

        Evaluation evaluation = new Evaluation(writtenJudgment, score);
        submission.setEvaluation(evaluation);

        submissionRepository.save(submission);
    }

    /**
     * Determines and sets the winning team of the hackathon identified by {@code idHackathon}.
     *
     * <p>
     * Only the organizer of the hackathon can proclaim the winner. The hackathon must be in the {@link StatusValue#EVALUATION}
     * phase and all submissions of non-disabled teams must be present and evaluated. The winner is selected by the highest
     * score; in case of a tie, the earliest submitted entry wins.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @throws NullPointerException if the hackathon cannot be found
     * @throws AccessDeniedException if the authenticated staff account is not the hackathon organizer
     * @throws IllegalArgumentException if the hackathon is not in evaluation phase or if submissions are missing/not all evaluated
     * @throws java.util.NoSuchElementException if the authenticated account cannot be resolved
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void proclamationWinner(Long idHackathon) {
        Hackathon h = hackathonService.getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a = accountService.findByEmail(auth.getName()).get();

        if (!h.getStaff().getOrganizer().equals(a))
            throw new AccessDeniedException("Staff has to be the organizer");

        if (h.getStatusValue() != StatusValue.EVALUATION)
            throw new IllegalArgumentException("Hackathon not in evaluation phase");

        List<Submission> hackathonSubmissions = submissionRepository.findByHackathonIdHackathon(idHackathon);

        if (hackathonSubmissions.isEmpty() || !hackathonSubmissions.stream()
                .filter(s->!s.getHackathon().getTeams().get(s.getTeam()))
                .allMatch(Submission::isEvaluated))
            throw new IllegalArgumentException("Submission not present or not all " +
                    "submissions have been evaluated yet.");

        Submission winner =
                hackathonSubmissions.stream()
                        .filter(s->!s.getHackathon().getTeams().get(s.getTeam()))
                        .max(Comparator.comparing((Submission s) -> s.getEvaluation().score())
                        .thenComparing(Submission::getSubmittedAt, Comparator.reverseOrder()))
                        .orElse(null);

        h.setWinningTeam(winner!=null?winner.getTeam():null);

        /*
        if(winner == null)
            return null;

        h.complete();
        return winner.getTeam();*/
    }

    /**
     * Counts the number of hackathons in which the specified team has participated.
     *
     * <p>This method delegates the operation to the underlying
     * submissionRepository, which performs the actual query.</p>
     *
     * @param idTeam the unique identifier of the team; must not be {@code null}.
     *
     * @return the total number of hackathons played by the team.
     */
    public int countPlayed(Long idTeam)
    {
        return this.submissionRepository.countPlayed(idTeam);
    }

    /**
     * Counts the number of hackathons won by the specified team.
     *
     * <p>The method relies on the submissionRepository to determine
     * how many wins are associated with the given team.</p>
     *
     * @param idTeam the unique identifier of the team; must not be {@code null}.
     *
     * @return the number of hackathons won by the team.
     */
    public int countWins(Long idTeam)
    {
        return this.submissionRepository.countWins(idTeam);
    }

    /**
     * Counts the number of podium finishes achieved by the specified team.
     *
     * <p>A podium finish typically includes 1st, 2nd, or 3rd place positions.
     * The actual logic is implemented by the submissionRepository.</p>
     *
     * @param idTeam the unique identifier of the team; must not be {@code null}.
     *
     * @return the number of podium results obtained by the team.
     */
    public int countPodiums(Long idTeam)
    {
        return this.submissionRepository.countPodiums(idTeam);
    }
}