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

import it.unicam.cs.hackhub.DTO.TeamStatsResponse;
import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Team;
import it.unicam.cs.hackhub.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer component responsible for managing {@link Team} entities and team membership workflows.
 *
 * <p>
 * Provides operations for team creation, retrieval, update, and membership management (add/remove members,
 * leader removal and leader reassignment). Authorization constraints are enforced through
 * {@link PreAuthorize} annotations and runtime checks.
 * </p>
 */
@Service
@Transactional
public class TeamService {

    private final AccountService accountService;

    private final SubmissionService submissionService;

    private final TeamRepository teamRepository;

    /**
     * Creates a new {@code TeamService} with the required dependencies.
     *
     * <p>
     * {@link Lazy} injection is used for {@link AccountService} to prevent circular dependencies
     * between services.
     * </p>
     *
     * @param teamRepository the repository used to access and persist teams
     * @param accountService the service used to retrieve and validate accounts
     */
    public TeamService(TeamRepository teamRepository,
                       @Lazy AccountService accountService,
                       @Lazy SubmissionService submissionService) {

        this.teamRepository = teamRepository;
        this.accountService = accountService;
        this.submissionService = submissionService;
    }

    /**
     * Retrieves the team with the specified identifier.
     *
     * @param idTeam the unique identifier of the team
     * @return the matching {@link Team}
     * @throws IllegalArgumentException if no team with the specified identifier exists
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public Team getTeam(Long idTeam)
    {
        return teamRepository.findById(idTeam)
                .orElseThrow(() ->
                        new IllegalArgumentException("Team not found"));
    }

    /**
     * Retrieves all teams.
     *
     * <p>
     * If no teams exist, an empty list is returned.
     * </p>
     *
     * @return the list of all {@link Team} entities; an empty list if none exist
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    //@PreAuthorize("hasRole('USER')")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Creates a new team led by the authenticated user.
     *
     * <p>
     * The authenticated account becomes the team leader. Team creation is denied if the account already
     * belongs to a team (as leader or member). Team name and description must be non-null and non-blank.
     * </p>
     *
     * @param name the team name
     * @param description the team description
     * @return the team created
     * @throws IllegalArgumentException if the authenticated account cannot be resolved, if it already belongs to a team,
     *                                  or if name/description are invalid
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public Team createTeam(String name,
                           String description) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Account leader = accountService.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found")
                );

        if(teamRepository.existsByMembersIdAccount(leader.getIdAccount()) || teamRepository.existsByLeaderIdAccount(leader.getIdAccount()))
            throw new IllegalArgumentException("This account already belongs to a team");

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Invalid name");

        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Invalid description");

        Team team = new Team(name, description, leader);

        teamRepository.save(team);

        return team;
    }

    /**
     * Removes the specified account from the authenticated leader's team.
     *
     * <p>
     * The authenticated user must be the leader of a team. The member identified by {@code idAccount}
     * is removed from that team if present.
     * </p>
     *
     * @param idAccount the unique identifier of the account to remove
     * @return {@code true} if the member was removed; {@code false} if the account was not a member of the team
     * @throws IllegalArgumentException if the authenticated leader cannot be resolved, if the caller is not a team leader,
     *                                  or if the specified member cannot be found
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public boolean removeMember(Long idAccount) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account leader = accountService.findByEmail(auth.getName()).orElseThrow(() ->
                new IllegalArgumentException("Leader not found")
        );

        Team team = teamRepository.findByLeaderIdAccount(leader.getIdAccount())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));

        Account member = accountService.getAccount(idAccount);
        if (member == null)
            throw new IllegalArgumentException("Member not found");

        boolean response = team.removeMember(member);
        teamRepository.save(team);
        return response;
    }

    /**
     * Removes the authenticated user from their team.
     *
     * @return {@code true} if the member was removed; {@code false} if the member was not present in the team
     * @throws IllegalArgumentException if the authenticated account cannot be resolved or if it does not belong to any team
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public boolean removeMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account member = accountService.findByEmail(auth.getName()).orElseThrow(() ->
                new IllegalArgumentException("Member not found"));

        Team team = teamRepository.findByMembersIdAccount(member.getIdAccount())
                .orElseThrow(() -> new IllegalArgumentException("You are not member of " +
                        "any team"));

        boolean response = team.removeMember(member);
        teamRepository.save(team);
        return response;
    }

    /**
     * Checks whether the specified account belongs to any team, either as leader or as member.
     *
     * @param account the account to check
     * @return {@code true} if the account belongs to at least one team; {@code false} otherwise
     * @throws NullPointerException if {@code account} is {@code null}
     */
    public boolean isAccountInTeam(Account account) {
        return
                teamRepository.existsByMembersIdAccount(account.getIdAccount())
                ||
                teamRepository.existsByLeaderIdAccount(account.getIdAccount());
    }

    /**
     * Adds the specified account as a member of the specified team.
     *
     * <p>
     * This method performs a direct membership update and persists the team.
     * Caller authorization and business constraints should be enforced by the calling workflow.
     * </p>
     *
     * @param account the account to add
     * @param team the team to which the account is added
     * @throws NullPointerException if {@code account} is {@code null}
     */
    public void addMember(Account account, Team team) {
        if (account == null)
            throw new NullPointerException("Account not found");

        team.addMember(account);
        teamRepository.save(team);
    }

    /**
     * Modifies name and description of the authenticated leader's team.
     *
     * @param name the new team name
     * @param description the new team description
     * @throws IllegalArgumentException if the authenticated user is not leader of any team
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public Team modifyTeam(String name,
                           String description) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Team team = findTeamByLeader(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));

        team.setName(name);
        team.setDescription(description);

        teamRepository.save(team);

        return team;
    }

    /**
     * Removes the leader from the authenticated leader's team.
     *
     * <p>
     * If the team has no remaining members, the team is deleted. Otherwise, one of the current members
     * becomes the new leader and is removed from the members set.
     * </p>
     *
     * @throws IllegalArgumentException if the authenticated user is not leader of any team
     * @throws org.springframework.security.access.AccessDeniedException if the caller is not authorized
     */
    @PreAuthorize("hasRole('USER')")
    public void removeLeader() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Team team = findTeamByLeader(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("You are not leader of any team"));

        Set<Account> members = team.getMembers();

        if (members == null || members.isEmpty())
            teamRepository.delete(team);
        else {
            Account newLeader = members.iterator().next();
            team.setLeader(newLeader);
            team.removeMember(team.getLeader());
            teamRepository.save(team);
        }
    }

    /**
     * Retrieves the team associated with the specified account identifier, searching both membership and leadership.
     *
     * @param idAccount the unique identifier of the account
     * @return an {@link Optional} containing the associated team, or empty if none exists
     */
    public Optional<Team> findMemberById(Long idAccount) {
        return teamRepository.findByMembersIdAccount(idAccount)
                .or(() -> teamRepository.findByLeaderIdAccount(idAccount));
    }

    /**
     * Retrieves the team led by the account identified by the specified email.
     *
     * @param email the email of the leader account
     * @return an {@link Optional} containing the team led by the specified account, or empty if none exists
     * @throws IllegalArgumentException if no account with the specified email exists
     */
    public Optional<Team> findTeamByLeader(String email) {
        Account account = accountService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return teamRepository.findByLeaderIdAccount(account.getIdAccount());
    }

    @PreAuthorize("hasRole('USER')")
    public Set<Account> getTeamMembers(Long idTeam) {
        return getTeam(idTeam).getMembers();
    }

    /**
     * Computes and returns statistical performance data for a specific team.
     *
     * <p>The method aggregates several metrics related to the team's participation
     * in hackathons, including:</p>
     * <ul>
     *     <li>the total number of hackathons played,</li>
     *     <li>the total number of wins,</li>
     *     <li>the total number of podium finishes,</li>
     *     <li>the win rate percentage computed from wins over played events.</li>
     * </ul>
     *
     * <p>If the team has not participated in any hackathons, the win rate
     * defaults to {@code 0} to avoid division by zero.</p>
     *
     * @param idTeam the unique identifier of the team whose statistics are being retrieved;
     *               must not be {@code null}.
     *
     * @return a {@link TeamStatsResponse} containing all computed statistics
     *         for the team.
     */
    public TeamStatsResponse getTeamStats(Long idTeam) {

        int played = submissionService.countPlayed(idTeam);
        int wins = submissionService.countWins(idTeam);
        int podiums = submissionService.countPodiums(idTeam);

        double winRate = played == 0 ? 0 : (wins * 100.0) / played;

        return new TeamStatsResponse(
                played,
                wins,
                podiums,
                winRate
        );
    }
}
