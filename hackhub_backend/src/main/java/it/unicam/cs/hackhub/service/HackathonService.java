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

import it.unicam.cs.hackhub.DTO.RuleResponse;
import it.unicam.cs.hackhub.model.*;
import it.unicam.cs.hackhub.repository.HackathonRepository;
import it.unicam.cs.hackhub.repository.RuleRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service layer component responsible for managing {@link Hackathon} entities and hackathon workflows.
 *
 * <p>
 * Provides operations for creating hackathons, retrieving hackathon data, registering and unsubscribing teams,
 * assigning staff members (organizer/judge/mentors), managing rules, updating hackathon status, and handling
 * administrative actions related to team participation.
 * </p>
 *
 * <p>
 * Access to sensitive operations is enforced through {@link PreAuthorize} annotations and additional runtime
 * checks (e.g., organizer ownership validation).
 * </p>
 */
@Service
@Transactional
public class
HackathonService {

    private final HackathonRepository hackathonRepository;
    private final RuleRepository ruleRepository;
    private final TeamService teamService;
    private final AccountService accountService;

    /**
     * Creates a new {@code HackathonService} with the required dependencies.
     *
     * <p>
     * {@link Lazy} injection is used for {@link AccountService} to prevent circular dependencies
     * between services.
     * </p>
     *
     * @param hackathonRepository the repository used to access and persist hackathons
     * @param teamService the service used to manage teams and retrieve team information
     * @param accountService the service used to retrieve and validate accounts involved in hackathon operations
     * @param ruleRepository the repository used to retrieve and persist rules
     */
    public HackathonService(HackathonRepository hackathonRepository,
                            TeamService teamService,
                            @Lazy AccountService accountService,
                            RuleRepository ruleRepository) {
        this.hackathonRepository = hackathonRepository;
        this.teamService = teamService;
        this.accountService = accountService;
        this.ruleRepository = ruleRepository;
    }

    /**
     * Retrieves a hackathon by its identifier.
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return the matching {@link Hackathon} if found; {@code null} otherwise
     */
    public Hackathon getHackathon(Long idHackathon) {
        return hackathonRepository.findById(idHackathon)
                .orElse(null);
    }

    /**
     * Retrieves the list of hackathons in which the team identified by the given idTeam is registered.
     *
     * @param idTeam the unique identifier of the team
     * @return a list of hackathons containing the specified team; an empty list if none are found
     */
    public List<Hackathon> getHackathonsByTeam(Long idTeam) {
        return hackathonRepository.findByTeamId(idTeam);
    }

    /**
     * Registers the authenticated user's team to the hackathon identified by the given idHackathon.
     *
     * <p>
     * The authenticated user must be the leader of a team. Registration is performed by adding the team to the
     * hackathon participants; the underlying model decides whether the team can be added (e.g., duplicates,
     * capacity constraints).
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return {@code true} if the team was added; {@code false} if it was already registered or could not be added
     * @throws IllegalArgumentException if the authenticated user is not leader of any team
     * @throws NullPointerException if the hackathon does not exist
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public boolean registerTeam(Long idHackathon) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Team t = teamService.findTeamByLeader(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));

        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");

        boolean added = h.addTeam(t);
        hackathonRepository.save(h);
        return added;
    }

    /**
     * Creates a new {@link Hackathon} with the provided configuration parameters.
     *
     * <p>This method is restricted to users with the {@code STAFF} role, as enforced by
     * the {@link PreAuthorize} annotation. The authenticated user will be treated as
     * the organizer of the hackathon.</p>
     *
     * <p>The method performs the following operations:</p>
     * <ul>
     *     <li>Retrieves the authenticated organizer account.</li>
     *     <li>Validates and loads the judge and mentor accounts.</li>
     *     <li>Ensures that judge, organizer, and mentors all have the {@code STAFF} role.</li>
     *     <li>Loads the rules associated with the hackathon.</li>
     *     <li>Uses a {@link StandardHackathonBuilder} to assemble the hackathon instance.</li>
     *     <li>Persists the created hackathon to the repository.</li>
     * </ul>
     *
     * @param name             the name of the hackathon; must not be {@code null} or blank.
     * @param location         the location where the hackathon will take place.
     * @param prize            the prize amount awarded to the winning team; must be non-negative.
     * @param maxTeamMembers   the maximum allowed number of members per team.
     * @param maxNumberTeams   the maximum number of teams allowed to participate.
     * @param startDate        the start date and time of the hackathon.
     * @param endDate          the end date and time of the hackathon.
     * @param judgeEmail       email of the staff member assigned as judge.
     * @param mentorEmails     list of emails of staff members serving as mentors.
     * @param idRules          list of IDs corresponding to rules applied to the hackathon.
     *
     * @return the fully constructed and persisted {@link Hackathon} instance.
     *
     * @throws RuntimeException          if the organizer or judge account cannot be found.
     * @throws IllegalArgumentException  if any account has an invalid role or
     *                                   if any provided rule ID does not match an existing rule.
     */
    @PreAuthorize("hasRole('STAFF')")
    public Hackathon createHackathon(String name,
                                String location,
                                double prize,
                                int maxTeamMembers,
                                int maxNumberTeams,
                                LocalDateTime startDate,
                                LocalDateTime endDate,
                                String judgeEmail,
                                List<String> mentorEmails,
                                List<Long> idRules) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account organizer = accountService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        Account judge = accountService.findByEmail(judgeEmail)
                .orElseThrow(() -> new RuntimeException("Judge not found"));
        Set<Account> mentors = new HashSet<>();

        for (String mentorEmail : mentorEmails) {
            Account mentor = accountService.findByEmail(mentorEmail)
                    .orElseThrow(() -> new RuntimeException("Mentor not found: " + mentorEmail));
            mentors.add(mentor);
        }

        if (organizer.getRole() != Role.STAFF)
            throw new IllegalArgumentException("The organizer cannot have a User role");

        if (judge.getRole() != Role.STAFF)
            throw new IllegalArgumentException("The judge cannot have a User role");

        for(Account mentor : mentors)
            if(mentor.getRole() != Role.STAFF)
                throw new IllegalArgumentException("The mentor cannot have a User role");

        Set<Rule> rules = new HashSet<>();
        for(Long idRule : idRules)
            rules.add(ruleRepository.findByIdRule(idRule)
                    .orElseThrow(() -> new IllegalArgumentException("Rule (" + idRule + ") not found")));

        StandardHackathonBuilder shb = new StandardHackathonBuilder();
        shb.buildBasicInfo(name, location, prize, maxTeamMembers, maxNumberTeams);
        shb.buildStatus(new Status(startDate, endDate));
        shb.buildStaff(new Staff(organizer, judge, mentors));
        shb.buildRules(rules);
        Hackathon hackathon = shb.getResult();

        hackathonRepository.save(hackathon);

        return hackathon;
    }

    /**
     * Updates the status (start and end dates) of the specified hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to modify its status.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @param startDate the new start date
     * @param endDate the new end date
     * @throws IllegalArgumentException if the hackathon does not exist
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void setStatus(Long idHackathon, LocalDateTime startDate, LocalDateTime endDate) {
        Hackathon hackathon = getHackathon(idHackathon);

        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon does not exist");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!hackathon.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer can modify the state of" +
                    " the hackathon");

        hackathon.setStatus(new Status(startDate, endDate));
        hackathonRepository.save(hackathon);
    }

    /**
     * Adds the rule identified by the given name to the specified hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to add rules.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @param idRule the idAccount of the rule to add
     * @return {@code true} if the rule was added; {@code false} if it was already present
     * @throws IllegalArgumentException if the hackathon does not exist or the rule cannot be found
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public Rule addRule(Long idHackathon, Long idRule) {
        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new IllegalArgumentException("Hackathon not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer can add a rule to this " +
                    "hackathon");

        Rule rule = ruleRepository.findByIdRule(idRule)
                .orElseThrow(() ->new IllegalArgumentException("Rule not found"));

        boolean added = h.addRule(rule);
        hackathonRepository.save(h);

        return added? rule : null;
    }

    /**
     * Assigns the specified staff account as judge of the given hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to change the judge.
     * </p>
     *
     * @param idAccount the unique identifier of the judge account
     * @param idHackathon the unique identifier of the hackathon
     * @throws NullPointerException if the account or the hackathon cannot be resolved
     * @throws IllegalArgumentException if the account does not have the STAFF role
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void assignJudge(Long idAccount, Long idHackathon) {
        Account a = accountService.getAccount(idAccount);
        Hackathon h = getHackathon(idHackathon);

        if (a == null || h == null)
            throw new NullPointerException("Parameters not found");

        if(a.getRole() != Role.STAFF)
            throw new IllegalArgumentException("The account has a User role");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer can change judge");

        h.getStaff().setJudge(a);
        hackathonRepository.save(h);
    }

    /**
     * Assigns the specified staff account as mentor of the given hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to assign mentors.
     * </p>
     *
     * @param email the email of the mentor account
     * @param idHackathon the unique identifier of the hackathon
     * @return {@code true} if the mentor was added; {@code false} if the mentor was already assigned
     * @throws IllegalArgumentException if parameters are invalid or the account does not have the STAFF role
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public Account assignMentor(String email, Long idHackathon) {
        Account a = accountService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        Hackathon h = getHackathon(idHackathon);

        if (h == null)
            throw new IllegalArgumentException("Hackathon not found");

        if(a.getRole() != Role.STAFF)
            throw new IllegalArgumentException("The account has a User role");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer of the hackathon can " +
                    "assign mentors");

        boolean added = h.getStaff().addMentor(a);
        hackathonRepository.save(h);
        return added? a : null;
    }

    /**
     * Removes the specified mentor account from the staff of the given hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to remove mentors.
     * </p>
     *
     * @param idAccount the unique identifier of the mentor account
     * @param idHackathon the unique identifier of the hackathon
     * @return {@code true} if the mentor was removed; {@code false} if the mentor was not assigned
     * @throws NullPointerException if parameters cannot be resolved
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public boolean removeMentor(Long idAccount, Long idHackathon) {
        Account a = accountService.getAccount(idAccount);
        Hackathon h = getHackathon(idHackathon);

        if (a == null || h == null)
            throw new NullPointerException("Parameters not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer of the hackathon can " +
                    "assign mentors");

        boolean removed = h.getStaff().removeMentor(a);
        hackathonRepository.save(h);
        return removed;
    }

    /**
     * Removes the rule identified by the given name from the specified hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to remove rules.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @param idRule the idAccount of the rule to remove
     * @return {@code true} if the rule was removed; {@code false} if it was not present
     * @throws NullPointerException if the hackathon does not exist
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws IllegalArgumentException if the specified rule does not exist in the hackathon
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public boolean removeRule(Long idHackathon, Long idRule) {
        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer of the hackathon can " +
                    "assign mentors");

        Rule r = h.getRule(idRule);
        boolean removed = h.removeRule(r);
        hackathonRepository.save(h);
        return removed;
    }

    /**
     * Modifies basic information of the specified hackathon.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to modify these fields.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @param name the new hackathon name
     * @param location the new hackathon location
     * @param prize the new prize value
     * @param maxTeamMembers the new maximum number of members per team
     * @param maxNumberTeams the new maximum number of teams allowed
     * @throws NullPointerException if the hackathon does not exist
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void modifyHackathon(Long idHackathon, String name, String location,
                                double prize, int maxTeamMembers, int maxNumberTeams) {

        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer of the hackathon can " +
                    "modify the hackathon");

        h.setName(name);
        h.setLocation(location);
        h.setPrize(prize);
        h.setMaxTeamMembers(maxTeamMembers);
        h.setMaxNumberTeams(maxNumberTeams);
        hackathonRepository.save(h);
    }

    /**
     * Retrieves all hackathons.
     *
     * @return the list of all {@link Hackathon} entities; an empty list if none exist
     */
    public List<Hackathon> getAllHackathons() {
        return hackathonRepository.findAll();
    }

    /**
     * Unsubscribes the authenticated user's team from the hackathon identified by the given idHackathon.
     *
     * <p>
     * The authenticated user must be the leader of a team. If the team is removed, the hackathon is persisted.
     * </p>
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return {@code true} if the team was removed; {@code false} if the team was not registered
     * @throws IllegalArgumentException if the authenticated user is not leader of any team or the hackathon does not exist
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public boolean unsubscribeTeam(Long idHackathon) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Team t = teamService.findTeamByLeader(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));

        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new IllegalArgumentException("Hackathon doesn't exist");

        boolean removed = h.getTeams().entrySet().removeIf(e ->
                e.getKey().getIdTeam().equals(t.getIdTeam())
        );

        if(removed)
            hackathonRepository.save(h);

        return removed;
    }

    /**
     * Enables or disables a team within a hackathon for report/management purposes.
     *
     * <p>
     * Only the organizer of the hackathon is allowed to perform this operation.
     * </p>
     *
     * @param disabledTeam the disabled state to apply to the specified team
     * @param idHackathon the unique identifier of the hackathon
     * @param idTeam the unique identifier of the team
     * @throws NullPointerException if the hackathon does not exist
     * @throws AccessDeniedException if the authenticated user is not the hackathon organizer
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('STAFF')")
    public void reportManagement(boolean disabledTeam, Long idHackathon, Long idTeam) {
        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!h.getStaff().getOrganizer().getEmail().equals(auth.getName()))
            throw new AccessDeniedException("Only the organizer of the hackathon can " +
                    "assign mentors");

        h.setDisabled(idTeam, disabledTeam);
        hackathonRepository.save(h);
    }

    /**
     * Checks whether the account identified by the given idAccount is assigned to the staff of at least one hackathon.
     *
     * @param idAccount the unique identifier of the account
     * @return {@code true} if the account belongs to the staff of at least one hackathon; {@code false} otherwise
     */
    public boolean findStaffById(Long idAccount) {
       return hackathonRepository.isAccountInStaff(idAccount);
    }

    /**
     * Retrieves all available {@link Rule} entities from the system.
     *
     * <p>This method simply delegates to the underlying {@link RuleRepository}
     * and returns the full list of rules configured in the application.
     * The resulting list may be empty but is never {@code null}.</p>
     *
     * @return a list of all {@link Rule} objects stored in the repository.
     */
    public List<Rule> getRules() {
        return ruleRepository.findAll();
    }

    /**
     * Retrieves the list of hackathons associated with the currently authenticated user.
     *
     * <p>The method identifies the user using Spring Security’s
     * {@link Authentication} object. It then resolves the corresponding
     * {@link Account} entity and queries the repository to fetch the
     * hackathons linked to that account.</p>
     *
     * @return a list of {@link Hackathon} instances in which the authenticated
     *         user participates. The list may be empty but is never {@code null}.
     *
     * @throws java.util.NoSuchElementException if the authenticated user's account
     *         cannot be found (i.e., {@code accountService.findByEmail(...)} returns empty).
     */
    public List<Hackathon> getMyHackathons() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return hackathonRepository.findMyHackathons(accountService.findByEmail(auth.getName()).get().getIdAccount());
    }


    /**
     * Retrieves the winning team of the specified hackathon.
     *
     * @param idHackathon the unique identifier of the hackathon
     * @return the winning {@link Team} of the hackathon, or {@code null} if no winner has been proclaimed yet
     * @throws NullPointerException if the hackathon cannot be found
     */
    public Team getWinner(Long idHackathon) {
        Hackathon h = getHackathon(idHackathon);
        if (h == null)
            throw new NullPointerException("Hackathon not found");
        return h.getWinningTeam();
    }
}