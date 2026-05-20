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


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Domain entity representing a Hackathon event.
 *
 * <p>
 * A {@code Hackathon} aggregates configuration data (name, location, prize,
 * participation limits), assigned {@link Staff}, associated {@link Rule}s,
 * registered {@link Team}s and its lifecycle {@link Status}.
 * </p>
 *
 * <p>
 * The entity also implements {@link Activable} in order to allow
 * enabling/disabling registered teams within the event.
 * </p>
 */
@Entity
@Table(name = "hackathons")
@Getter
@EqualsAndHashCode
public class Hackathon implements Activable<Long> {

    /** Unique identifier of the hackathon (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hackathon")
    private Long idHackathon;

    /** Official name of the hackathon (unique). */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Physical or virtual location where the hackathon takes place. */
    @Column(nullable = false, length = 100)
    private String location;

    /** Monetary prize assigned to the winner. */
    @Column(nullable = false)
    private double prize;

    /** Maximum number of members allowed per participating team. */
    @Column(nullable = false)
    private int maxTeamMembers;

    /** Maximum number of teams allowed to register. */
    @Column(nullable = false)
    private int maxNumberTeams;

    /** Staff assigned to manage and evaluate the hackathon. */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff = new Staff();

    /**
     * Teams registered for the hackathon and whether they are disabled.
     * Implemented as a Map<Team, Boolean>.
     */
    @ElementCollection
    @CollectionTable(
            name = "hackathon_teams",
            joinColumns = @JoinColumn(name = "hackathon_id")
    )
    @MapKeyJoinColumn(name = "team_id")
    @Column(name = "disabled")
    private Map<Team, Boolean> teams = new HashMap<>();

    /** Current status of the hackathon. */
    @Embedded
    private Status status;

    /** Rules defined for the hackathon. */
    @ManyToMany
    @JoinTable(
            name = "hackathon_rules",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<Rule> rules;

    @ManyToOne
    @JoinColumn(name = "winning_team_id_team")
    private Team winningTeam;




    /**
     * Protected constructor required by JPA.
     */
    protected Hackathon() {
    }

    /**
     * Updates the lifecycle {@link Status} of the hackathon.
     *
     * <p>
     * Lifecycle transition rules are enforced:
     * <ul>
     *     <li>The status must not be {@code null}</li>
     *     <li>The start date cannot be modified after the hackathon has started</li>
     *     <li>Status cannot be changed once in {@code EVALUATION} or {@code COMPLETED}</li>
     *     <li>If setting the initial status, the start date cannot be in the past</li>
     * </ul>
     * </p>
     *
     * @param status the new status
     * @throws NullPointerException if {@code status} is null
     * @throws IllegalArgumentException if lifecycle constraints are violated
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new NullPointerException("Status is null");
        }

        if(this.status != null)
        {
            if(getStatusValue() != StatusValue.REGISTRATION && !this.status.getStartDate().isEqual(status.getStartDate()))
                throw new IllegalArgumentException("Start date must be equal, because " +
                        "hackathon is started");

            if(getStatusValue() == StatusValue.EVALUATION || getStatusValue() == StatusValue.COMPLETED)
                throw new IllegalArgumentException("Status cannot be changed anymore");
        }
        else
            if(status.getStartDate().isBefore(LocalDateTime.now()))
                throw new IllegalArgumentException("Start date cannot be before now");

        this.status = status;
    }

    /**
     *
     * @param winningTeam
     */
    public void setWinningTeam(Team winningTeam) {
        this.complete();
        this.winningTeam = winningTeam;
    }

    /**
     * Sets the rules of the hackathon.
     *
     * @param rules the set of rules
     * @throws NullPointerException if the rules set is {@code null}
     */
    public void setRules(Set<Rule> rules) {
        if (rules == null) {
            throw new NullPointerException("Rule is null");
        }
        this.rules = rules;
    }

    /**
     * Sets the staff assigned to the hackathon.
     *
     * @param staff the staff object
     * @throws NullPointerException if the staff is {@code null}
     */
    public void setStaff(Staff staff) {
        if (staff == null) {
            throw new NullPointerException("Staff is null");
        }
        this.staff = staff;
    }

    /**
     * Sets the teams registered for the hackathon.
     *
     * @param teams the set of teams
     * @throws NullPointerException if the teams set is {@code null}
     */
    public void setTeams(Map<Team, Boolean> teams) {
        Objects.requireNonNull(teams, "Teams map cannot be null");
        if (this.teams == null) {
            this.teams = new HashMap<>();
        }
        this.teams.clear();
        this.teams.putAll(teams);
    }

    /**
     * Sets the maximum number of teams allowed.
     *
     * @param maxNumberTeams the maximum number of teams
     * @throws IllegalArgumentException if the value is not greater than zero
     */
    public void setMaxNumberTeams(int maxNumberTeams) {
        if (maxNumberTeams <= 0) {
            throw new IllegalArgumentException("Max number teams must be greater than 0");
        }
        this.maxNumberTeams = maxNumberTeams;
    }

    /**
     * Sets the maximum number of members per team.
     *
     * @param maxTeamMembers the maximum team size
     * @throws IllegalArgumentException if the value is not greater than zero
     */
    public void setMaxTeamMembers(int maxTeamMembers) {
        if (maxTeamMembers <= 0) {
            throw new IllegalArgumentException("Max team members must be greater than 0");
        }
        this.maxTeamMembers = maxTeamMembers;
    }

    /**
     * Sets the prize amount.
     *
     * @param prize the prize amount
     * @throws IllegalArgumentException if the prize is negative
     */
    public void setPrize(double prize) {
        if (prize < 0) {
            throw new IllegalArgumentException("Prize cannot be less than 0");
        }
        this.prize = prize;
    }

    /**
     * Sets the hackathon name.
     *
     * @param name the hackathon name
     * @throws NullPointerException if the name is {@code null} or empty
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException("Name is null");
        }
        this.name = name;
    }

    /**
     * Sets the hackathon location.
     *
     * @param location the location
     * @throws NullPointerException if the location is {@code null} or empty
     */
    public void setLocation(String location) {
        if (location == null || location.isEmpty()) {
            throw new NullPointerException("Location is null");
        }
        this.location = location;
    }

    /**
     * Adds a rule to the hackathon.
     *
     * @param rule the rule to add
     * @return {@code true} if the rule was successfully added
     * @throws NullPointerException if the rule is {@code null}
     */
    public boolean addRule(Rule rule) {
        if (rule == null) {
            throw new NullPointerException("Rule is null");
        }
        return rules.add(rule);
    }

    /**
     * Retrieves a registered team by its unique identifier.
     *
     * @param idTeam the team identifier
     * @return the {@link Team} with the given ID
     * @throws NullPointerException if no team with the given ID is registered
     */
    public Team getTeamById(Long idTeam) {
        return teams.keySet().stream()
                .filter(team -> team.getIdTeam().equals(idTeam))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Team not registered"));
    }

    /**
     * Registers a team to the hackathon.
     *
     * <p>
     * Registration is allowed only during the {@link StatusValue#REGISTRATION} phase
     * and if participation constraints are satisfied.
     * </p>
     *
     * @param team the team to register
     * @return {@code true} if the team was successfully registered,
     *         {@code false} if the team was already registered
     * @throws NullPointerException if {@code team} is null
     * @throws IllegalStateException if registrations are closed or maximum capacity is reached
     * @throws IllegalArgumentException if the team exceeds the maximum allowed size
     */
    public boolean addTeam(Team team) {
        if (team == null)
            throw new NullPointerException("Team is null");

        if (status.getStatus() != StatusValue.REGISTRATION)
            throw new IllegalStateException("Registrations are closed");

        if (teams.size() == maxNumberTeams)
            throw new IllegalStateException("Max number of teams reached");

        if (team.getMembers().size() > maxTeamMembers)
            throw new IllegalArgumentException("Team is too big for this hackathon");

        return teams.put(team,false) == null;
    }

    /**
     * Retrieves a rule by idAccount.
     *
     * @param id the rule idAccount
     * @return the matching {@link Rule}, or {@code null} if not found
     */
    public Rule getRule(Long id) {
        return rules.stream()
                .filter(rule -> rule.getIdRule().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Removes a rule from the hackathon.
     *
     * @param rule the rule to remove
     * @return {@code true} if the rule was successfully removed
     * @throws NullPointerException if the rule is {@code null}
     */
    public boolean removeRule(Rule rule) {
        if (rule == null)
            throw new NullPointerException("Rule is null");
        return rules.remove(rule);
    }

    /**
     * Returns the current {@link StatusValue} of the hackathon.
     *
     * @return the hackathon status value
     */
    public StatusValue getStatusValue() {
        return status.getStatus();
    }

    /**
     * Enables or disables a specific registered team.
     *
     * @param id the unique identifier of the team
     * @param disabled {@code true} to disable the team, {@code false} to enable it
     * @throws NullPointerException if no team with the specified ID is registered
     */
    @Override
    public void setDisabled(Long id, boolean disabled) {
        Team team = getTeamById(id);
        teams.put(team, disabled);
    }

    /**
     * Marks the hackathon as completed.
     *
     * @throws IllegalStateException if the hackathon is already completed
     */
    public void complete() {
        if (getStatusValue() == StatusValue.COMPLETED) {
            throw new IllegalStateException("Hackathon already completed");
        }

        this.status.completeHackathon();
    }

    /**
     * Checks whether the given account identifier belongs to the staff.
     *
     * <p>
     * An account is considered part of the staff if it is:
     * </p>
     * <ul>
     *     <li>The organizer</li>
     *     <li>The judge</li>
     *     <li>One of the mentors</li>
     * </ul>
     *
     * @param idAccount the identifier of the account to check
     * @return {@code true} if the account is part of the staff,
     *         {@code false} otherwise
     */
    public boolean checkStaff(Long idAccount) {
        return staff.checkStaff(idAccount);
    }
}