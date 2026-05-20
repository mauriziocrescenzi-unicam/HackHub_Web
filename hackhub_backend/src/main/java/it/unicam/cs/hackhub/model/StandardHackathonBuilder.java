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
 * Standard concrete implementation of {@link HackathonBuilder}.
 *
 * <p>
 * This builder follows the Builder design pattern and enables
 * step-by-step construction of a {@link Hackathon} aggregate.
 * </p>
 *
 * <p>
 * The internal {@link Hackathon} instance must be initialized
 * via {@link #buildBasicInfo(String, String, double, int, int)}
 * before invoking other build steps.
 * </p>
 */
public class StandardHackathonBuilder implements HackathonBuilder {

    private Hackathon result;

    /**
     * Resets the builder state.
     *
     * <p>
     * After calling this method, the internal {@link Hackathon}
     * instance is discarded and must be reinitialized before use.
     * </p>
     */
    @Override
    public void reset() {
        result = null;
    }

    /**
     * Builds and initializes the basic information of a {@link Hackathon} entity.
     *
     * <p>This method creates a new {@link Hackathon} instance and assigns its
     * core properties such as name, location, prize, and team-related constraints.
     * It represents the first step of the builder pattern used to progressively
     * construct a hackathon object.</p>
     *
     * @param name            the name of the hackathon; must not be {@code null} or blank.
     * @param location        the physical or virtual location where the hackathon will take place.
     * @param prize           the prize amount awarded to the winning team; must be non-negative.
     * @param maxTeamMembers  the maximum number of members allowed per team.
     * @param maxNumberTeams  the maximum number of teams allowed to participate.
     */
    @Override
    public void buildBasicInfo(String name,
                               String location,
                               double prize,
                               int maxTeamMembers,
                               int maxNumberTeams) {

        result = new Hackathon();
        result.setName(name);
        result.setLocation(location);
        result.setPrize(prize);
        result.setMaxTeamMembers(maxTeamMembers);
        result.setMaxNumberTeams(maxNumberTeams);
    }

    /**
     * Sets the lifecycle {@link Status} of the hackathon under construction.
     *
     * @param status the status to assign
     * @throws IllegalStateException if the hackathon has not been initialized
     */
    @Override
    public void buildStatus(Status status) {
        checkInitialized();
        result.setStatus(status);
    }

    /**
     * Assigns the {@link Staff} to the hackathon under construction.
     *
     * @param staff the staff aggregate
     * @throws IllegalStateException if the hackathon has not been initialized
     */
    @Override
    public void buildStaff(Staff staff) {
        checkInitialized();
        result.setStaff(staff);
    }

    /**
     * Initializes the teams map of the hackathon under construction.
     *
     * @param teams map of teams and their disabled status
     * @throws IllegalStateException if the hackathon has not been initialized
     */
    @Override
    public void buildTeams(Map<Team, Boolean> teams) {
        checkInitialized();
        result.setTeams(teams);
    }

    /**
     * Initializes the rules set of the hackathon under construction.
     *
     * @param rules the set of rules
     * @throws IllegalStateException if the hackathon has not been initialized
     */
    @Override
    public void buildRules(Set<Rule> rules) {
        checkInitialized();
        result.setRules(rules);
    }

    /**
     * Returns the constructed {@link Hackathon} instance.
     *
     * @return the fully constructed hackathon
     * @throws IllegalStateException if the hackathon has not been initialized
     */
    public Hackathon getResult() {
        checkInitialized();
        return result;
    }

    /**
     * Ensures that the hackathon instance has been initialized.
     *
     * @throws IllegalStateException if no hackathon is currently under construction
     */
    private void checkInitialized() {
        if (result == null)
            throw new IllegalStateException("Hackathon not initialized");
    }
}
