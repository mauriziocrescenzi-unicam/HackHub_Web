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
package it.unicam.cs.hackhub.controller;

import it.unicam.cs.hackhub.DTO.TeamMemberResponse;
import it.unicam.cs.hackhub.DTO.TeamResponse;
import it.unicam.cs.hackhub.DTO.TeamStatsResponse;
import it.unicam.cs.hackhub.model.Team;
import it.unicam.cs.hackhub.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing team-related operations.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>Retrieving teams</li>
 *     <li>Creating and updating teams</li>
 *     <li>Managing team membership</li>
 *     <li>Handling leader rotation or team disbanding</li>
 * </ul>
 * All business logic is delegated to the {@link TeamService}.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    /**
     * Creates a new {@code TeamController}.
     *
     * @param teamService the service responsible for team management logic
     */
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Retrieves all teams available in the system.
     *
     * @return a {@link ResponseEntity} containing a list of
     *         {@link TeamResponse} objects with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<TeamResponse>> consultTeams() {
        return ResponseEntity.ok(teamService.getAllTeams().stream()
                .map(team -> TeamResponse.fromEntity(team, teamService.getTeamStats(team.getIdTeam())))
                .toList());
    }

    /**
     * Retrieves a specific team by its identifier.
     *
     * @param id the unique identifier of the team
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *             <li>the {@link TeamResponse} if found (200 OK)</li>
     *             <li>HTTP 404 (Not Found) if no team exists with the given idAccount</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> consultTeam(@PathVariable Long id) {
        Team team = teamService.getTeam(id);
        TeamStatsResponse stats = teamService.getTeamStats(id);

        return team != null
                ? ResponseEntity.ok(TeamResponse.fromEntity(team, stats))
                : ResponseEntity.notFound().build();
    }

    /**
     * Creates a new team.
     * <p>
     * The request body must contain:
     * <ul>
     *     <li>{@code name} – team name</li>
     *     <li>{@code description} – team description</li>
     * </ul>
     *
     * @param payload a map containing team creation parameters
     * @return a {@link ResponseEntity} with HTTP status 201 (Created)
     *         and a confirmation message
     */
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String description = (String) payload.get("description");

        Team team = teamService.createTeam(name, description);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TeamResponse.fromEntity(team, null));
    }

    /**
     * Updates the current team's basic information.
     *
     * @param payload a map containing:
     *                <ul>
     *                    <li>{@code name} – updated team name</li>
     *                    <li>{@code description} – updated team description</li>
     *                </ul>
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @PutMapping
    public ResponseEntity<TeamResponse> modifyTeam(@RequestBody Map<String,
            String> payload) {
        Team team = teamService.modifyTeam(payload.get("name"), payload.get(
                "description"));
        TeamStatsResponse stats = teamService.getTeamStats(team.getIdTeam());
        return ResponseEntity.ok(TeamResponse.fromEntity(team, stats));
    }

    /**
     * Removes a specific member from the team.
     *
     * @param idAccount the identifier of the member to remove
     * @return HTTP 200 (OK) if removal succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @DeleteMapping("/members/{idAccount}")
    public ResponseEntity<Map<String, String>> removeMember(@PathVariable Long idAccount) {
        boolean removed = teamService.removeMember(idAccount);
        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Member removed.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Removal failed.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Allows the current authenticated member to leave the team.
     *
     * @return HTTP 200 (OK) if the operation succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @DeleteMapping("/members")
    public ResponseEntity<Map<String, String>> leaveTeamForMember() {
        boolean removed = teamService.removeMember();

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "You left the team.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Removal failed.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Removes the current team leader.
     * <p>
     * If other members exist, leadership is transferred.
     * If the leader was the only member, the team is disbanded.
     *
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @DeleteMapping("/leader")
    public ResponseEntity<Map<String,String>> leaveTeamForLeader() {
        teamService.removeLeader();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leader removed/rotated successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the list of members belonging to the specified team.
     *
     * <p>The team is identified by its unique ID provided in the URL path.
     * The method fetches all members of the team and maps them into
     * {@link TeamMemberResponse} objects. The returned list may be empty
     * but is never {@code null}.</p>
     *
     * @param id the unique identifier of the team whose members are to be retrieved;
     *           must not be {@code null}.
     *
     * @return a {@link ResponseEntity} containing a list of {@link TeamMemberResponse}
     *         representing the members of the specified team.
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamMembers(id).stream()
                .map(TeamMemberResponse::fromEntity).toList());
    }
}