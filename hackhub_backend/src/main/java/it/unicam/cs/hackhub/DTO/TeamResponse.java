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

import it.unicam.cs.hackhub.model.Team;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a team.
 * <p>
 * Used to expose team information through REST responses,
 * including leader details.
 *
 * @param idTeam         the unique identifier of the team
 * @param name           the name of the team
 * @param description    the description of the team
 * @param leader       the team leader
 * @param members the members of the team
 * @param stats the statistics related to the team
 */
public record TeamResponse(
        Long idTeam,
        String name,
        String description,

        TeamMemberResponse leader,
        List<TeamMemberResponse> members,
        TeamStatsResponse stats
) {
    /**
     * Converts a {@link Team} entity into a {@link TeamResponse}.
     * <p>
     * Safely handles potential {@code null} values for the team
     * and its leader.
     *
     * @param team the {@link Team} entity to convert
     * @param stats the {@link TeamStatsResponse} team statistics to convert
     * @return a corresponding {@link TeamResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static TeamResponse fromEntity(Team team, TeamStatsResponse stats) {
        if (team == null) {
            return null;
        }

        return new TeamResponse(
                team.getIdTeam(),
                team.getName(),
                team.getDescription(),
                TeamMemberResponse.fromEntity(team.getLeader()),
                team.getMembers() != null ?
                        team.getMembers().stream().map(TeamMemberResponse :: fromEntity).toList() : null,
                stats
        );
    }
}
