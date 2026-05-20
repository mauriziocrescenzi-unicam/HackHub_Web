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

import java.util.Map;
import java.util.Set;

/**
 * Builder interface for constructing {@link Hackathon} instances.
 *
 * <p>
 * Defines the step-by-step operations required to assemble
 * a valid {@link Hackathon} aggregate.
 * </p>
 */
public interface HackathonBuilder {

    /**
     * Resets the builder to its initial state.
     * This method should be called before starting
     * the construction of a new hackathon.
     */
    void reset();

    /**
     * Sets the core configuration attributes of the hackathon.
     *
     * @param name           the hackathon name
     * @param location       the hackathon location (physical or virtual)
     * @param prize          the prize amount
     * @param maxTeamMembers the maximum number of members allowed per team
     * @param maxNumberTeams the maximum number of teams allowed to register
     */
    void buildBasicInfo(
            String name,
            String location,
            double prize,
            int maxTeamMembers,
            int maxNumberTeams
    );

    /**
     * Assigns the lifecycle {@link Status} of the hackathon.
     *
     * @param status the status to associate with the hackathon
     */
    void buildStatus(Status status);

    /**
     * Assigns the {@link Staff} responsible for managing the hackathon.
     *
     * @param staff the staff aggregate
     */
    void buildStaff(Staff staff);

    /**
     * Initializes the map of registered teams and their disabled status.
     *
     * @param teams a map where the key represents the {@link Team}
     *              and the value indicates whether the team is disabled
     */
    void buildTeams(Map<Team, Boolean> teams);

    /**
     * Initializes the set of rules governing the hackathon.
     *
     * @param rules the set of {@link Rule} objects
     */
    void buildRules(Set<Rule> rules);
}

