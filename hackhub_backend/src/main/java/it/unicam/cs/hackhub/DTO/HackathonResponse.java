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
package it.unicam.cs.hackhub.DTO;

import it.unicam.cs.hackhub.model.Hackathon;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a hackathon response.
 * <p>
 * This record is used to expose hackathon data through REST endpoints,
 * including general information, scheduling details, status, staff
 * configuration, and associated rules.
 *
 * @param id              the unique identifier of the hackathon
 * @param name            the name of the hackathon
 * @param location        the physical or virtual location
 * @param prize           the prize amount awarded to the winner
 * @param maxTeamMembers  the maximum number of members allowed per team
 * @param maxNumberTeams  the maximum number of teams allowed
 * @param startDate       the start date and time of the hackathon
 * @param endDate         the end date and time of the hackathon
 * @param status          the current status value of the hackathon
 * @param teams           the list of subscribed teams
 * @param staff           the staff configuration (judge and mentors)
 * @param rules           the list of associated rules
 */
public record HackathonResponse(
        Long id,
        String name,
        String location,
        double prize,
        int maxTeamMembers,
        int maxNumberTeams,
        TeamResponse winningTeam,

        LocalDateTime startDate,
        LocalDateTime endDate,

        String status,

        List<TeamResponse> teams,
        StaffResponse staff,
        List<RuleResponse> rules
) {

    /**
     * Converts a {@link Hackathon} entity into a {@link HackathonResponse}.
     * <p>
     * Safely handles {@code null} values for nested objects such as
     * status, staff, and rules.
     *
     * @param h the {@link Hackathon} entity to convert
     * @return a corresponding {@link HackathonResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static HackathonResponse fromEntity(Hackathon h) {
        if (h == null) {
            return null;
        }

        return new HackathonResponse(
                h.getIdHackathon(),
                h.getName(),
                h.getLocation(),
                h.getPrize(),
                h.getMaxTeamMembers(),
                h.getMaxNumberTeams(),
                h.getWinningTeam() != null ? TeamResponse.fromEntity(h.getWinningTeam(), null) : null,                h.getStatus() != null ? h.getStatus().getStartDate() : null,
                h.getStatus() != null ? h.getStatus().getEndDate() : null,
                h.getStatusValue() != null ? h.getStatusValue().name() : null,

                h.getTeams() != null ? h.getTeams().keySet().stream()
                        .map(team -> TeamResponse.fromEntity(
                                team, null)
                        )
                        .toList() : Collections.emptyList(),

                StaffResponse.fromEntity(h.getStaff()),

                h.getRules() != null ? h.getRules().stream()
                        .map(RuleResponse::fromEntity)
                        .toList() : Collections.emptyList()
        );
    }
}


