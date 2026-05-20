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

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a team participating in one or more hackathons.
 *
 * <p>
 * A {@code Team} is composed of:
 * </p>
 * <ul>
 *     <li>A unique identifier</li>
 *     <li>A name and description</li>
 *     <li>A leader (mandatory)</li>
 *     <li>A set of members</li>
 * </ul>
 *
 * <p>
 * The leader is not automatically included in the members set.
 * </p>
 */
@Entity
@Table(name = "teams")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team {

    /**
     * Primary key of the team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team")
    @EqualsAndHashCode.Include
    private Long idTeam;

    /**
     * Name of the team.
     */
    @Column(nullable = false, unique = true,  length = 100)
    private String name;

    /**
     * Description of the team.
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Leader of the team.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private Account leader;

    /**
     * Members belonging to the team.
     */
    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> members = new HashSet<>();

    @OneToMany(mappedBy = "invitingTeam",
           cascade = CascadeType.REMOVE,
           orphanRemoval = true)
    private Set<Invitation> invitations = new HashSet<>();

    
    @OneToMany(mappedBy = "team",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true)
    private Set<Submission> submissions = new HashSet<>();

    /**
     * Protected constructor required by JPA.
     */
    protected Team() {
    }

    /**
     * Creates a new team with the specified name, description and leader.
     *
     * @param name        non-null, non-blank team name
     * @param description non-null, non-blank description
     * @param leader      non-null team leader
     * @throws IllegalArgumentException if name or description are invalid
     * @throws NullPointerException     if leader is null
     */
    public Team(String name, String description, Account leader) {
        setName(name);
        setDescription(description);
        setLeader(leader);
        this.members = new HashSet<>();
    }

    /**
     * Sets the team name.
     *
     * @param name non-null, non-blank name
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or empty");
        this.name = name;
    }

    /**
     * Sets the team description.
     *
     * @param description non-null, non-blank description
     * @throws IllegalArgumentException if the description is null or blank
     */
    public void setDescription(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Description cannot be null or empty");
        this.description = description;
    }

    /**
     * Sets the team leader.
     *
     * @param leader non-null account designated as leader
     * @throws NullPointerException if leader is null
     */
    public void setLeader(Account leader) {
        if (leader == null)
            throw new NullPointerException("Leader cannot be null");
        this.leader = leader;
    }

    /**
     * Adds a member to the team.
     *
     * @param member non-null account to add
     * @return {@code true} if the member was not already present
     * @throws NullPointerException if member is null
     */
    public boolean addMember(Account member) {
        if (member == null)
            throw new NullPointerException("Member cannot be null");
        return members.add(member);
    }

    /**
     * Removes a member from the team.
     *
     * @param member non-null account to remove
     * @return {@code true} if the member was present and removed
     * @throws NullPointerException if member is null
     */
    public boolean removeMember(Account member) {
        if (member == null)
            throw new NullPointerException("Member cannot be null");
        return members.remove(member);
    }

    /**
     * Checks whether an account belongs to this team.
     *
     * <p>
     * An account is considered part of the team if it is
     * either the leader or one of the registered members.
     * </p>
     *
     * @param idAccount identifier of the account to verify
     * @return {@code true} if the account belongs to the team,
     *         {@code false} otherwise
     */
    public boolean checkTeamMember(Long idAccount) {
        return leader.getIdAccount() == idAccount||members.stream().anyMatch(m->m.getIdAccount()==idAccount);
    }

    /**
     * Checks whether the specified account is the leader
     * and the team contains at least one additional member.
     *
     * @param account account to verify
     * @return {@code true} if the account is the leader
     *         and the team is not empty (excluding the leader),
     *         {@code false} otherwise
     */
    public boolean isLeaderAndNotAlone(Account account) {
        return leader==account &&  !(members.isEmpty()||members==null);
    }
}
