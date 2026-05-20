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
import lombok.Getter;

import java.util.Set;

/**
 * JPA entity representing the staff assigned to a hackathon.
 *
 * <p>
 * A {@code Staff} aggregate groups the accounts responsible for
 * organizing and managing a hackathon.
 * </p>
 *
 * <p>
 * It is composed of:
 * </p>
 * <ul>
 *     <li>One organizer</li>
 *     <li>One judge</li>
 *     <li>One or more mentors</li>
 * </ul>
 */
@Entity
@Table(name = "staff")
@Getter
public class Staff {

    /**
     * Primary key of the staff entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_staff")
    private Long idStaff;

    /**
     * Organizer responsible for the hackathon.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Account organizer;

    /**
     * Judge assigned to evaluate submissions.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "judge_id", nullable = false)
    private Account judge;

    /**
     * Mentors assisting teams during the hackathon.
     */
    @ManyToMany
    @JoinTable(
            name = "staff_mentors",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id")
    )
    private Set<Account> mentors;

    /**
     * Protected constructor required by JPA.
     */
    protected Staff() {
    }

    /**
     * Creates a Staff instance with the specified organizer,
     * judge, and mentors.
     *
     * @param organizer non-null organizer account
     * @param judge     non-null judge account
     * @param mentors   non-null and non-empty set of mentors
     * @throws NullPointerException if any parameter is null
     *                              or if mentors is empty
     */
    public Staff(Account organizer, Account judge, Set<Account> mentors) {
        setOrganizer(organizer);
        setJudge(judge);
        setMentors(mentors);
    }

    /**
     * Sets the mentors assigned to the staff.
     *
     * <p>
     * The mentors set must not be {@code null} and must contain at least one mentor.
     * </p>
     *
     * @param mentors non-null and non-empty set of mentors
     * @throws NullPointerException if {@code mentors} is {@code null} or empty
     */
    private void setMentors(Set<Account> mentors) {
        if(mentors == null || mentors.isEmpty())
            throw new NullPointerException("Mentors cannot be null");
        this.mentors = mentors;
    }

    /**
     * Sets the organizer account.
     *
     * @param organizer non-null organizer
     * @throws NullPointerException if {@code organizer} is null
     */
    public void setOrganizer(Account organizer) {
        if (organizer == null)
            throw new NullPointerException("Organizer cannot be null");
        this.organizer = organizer;
    }

    /**
     * Sets the judge account.
     *
     * @param judge non-null judge
     * @throws NullPointerException if {@code judge} is null
     */
    public void setJudge(Account judge) {
        if (judge == null)
            throw new NullPointerException("Judge cannot be null");
        this.judge = judge;
    }

    /**
     * Adds a mentor to the staff.
     *
     * @param mentor non-null mentor account
     * @return {@code true} if the mentor was successfully added
     * @throws NullPointerException if {@code mentor} is null
     */
    public boolean addMentor(Account mentor) {
        if (mentor == null)
            throw new NullPointerException("Mentor cannot be null");
        return mentors.add(mentor);
    }

    /**
     * Removes a mentor from the staff.
     *
     * <p>
     * At least one mentor must always remain assigned.
     * </p>
     *
     * @param mentor non-null mentor account
     * @return {@code true} if the mentor was removed
     * @throws NullPointerException if {@code mentor} is null
     * @throws IllegalStateException if removing the mentor would leave the staff without mentors
     */
    public boolean removeMentor(Account mentor) {
        if (mentor == null)
            throw new NullPointerException("Mentor cannot be null");
        if (mentors.size() < 2)
            throw new IllegalStateException("At least one mentor required");
        return mentors.remove(mentor);
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

        return judge.getIdAccount() == idAccount
                || organizer.getIdAccount() == idAccount
                || mentors.stream()
                .map(Account::getIdAccount)
                .anyMatch(id -> id == idAccount);
    }
}

