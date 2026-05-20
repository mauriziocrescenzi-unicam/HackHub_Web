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

import it.unicam.cs.hackhub.DTO.*;
import it.unicam.cs.hackhub.model.Account;
import it.unicam.cs.hackhub.model.Hackathon;
import it.unicam.cs.hackhub.model.Rule;
import it.unicam.cs.hackhub.model.Team;
import it.unicam.cs.hackhub.service.HackathonService;
import it.unicam.cs.hackhub.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller responsible for hackathon management operations.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>CRUD operations on hackathons</li>
 *     <li>Status and date management</li>
 *     <li>Team registration management</li>
 *     <li>Staff assignment (judges and mentors)</li>
 *     <li>Rule management</li>
 * </ul>
 * All business logic is delegated to the {@link HackathonService}.
 */
@RestController
@RequestMapping("/api/hackathons")
public class HackathonController {

    private final HackathonService hackathonService;
    private final TeamService teamService;

    /**
     * Creates a new {@code HackathonController}.
     *
     * @param hackathonService the service layer component responsible for
     *                         hackathon business logic
     */
    public HackathonController(HackathonService hackathonService, TeamService teamService) {
        this.hackathonService = hackathonService;
        this.teamService = teamService;
    }

    /**
     * Retrieves all hackathons available in the system.
     *
     * @return a {@link ResponseEntity} containing the list of
     *         {@link HackathonResponse} objects with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<HackathonResponse>> consultHackathons() {
        return ResponseEntity.ok(hackathonService.getAllHackathons().stream()
                .map(HackathonResponse::fromEntity)
                .toList());
    }

    /**
     * Retrieves a specific hackathon by its identifier.
     *
     * @param id the unique identifier of the hackathon
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *             <li>the {@link HackathonResponse} if found (200 OK)</li>
     *             <li>HTTP 404 (Not Found) if no hackathon exists with the given idAccount</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<HackathonResponse> consultHackathon(@PathVariable Long id) {
        Hackathon hackathon = hackathonService.getHackathon(id);
        if (hackathon == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(HackathonResponse.fromEntity(hackathon));
    }

    /**
     * Retrieves the list of hackathons associated with the authenticated user.
     *
     * <p>This endpoint returns all hackathons in which the current user
     * is participating or has visibility, mapped into {@link HackathonResponse}
     * objects.</p>
     *
     * @return a {@link ResponseEntity} containing a list of {@link HackathonResponse}
     *         representing the user's hackathons. The list may be empty but never {@code null}.
     */
    @GetMapping("/my")
    public ResponseEntity<List<HackathonResponse>> getMyHackathons()
    {
        return ResponseEntity.ok(hackathonService.getMyHackathons().stream()
                .map(HackathonResponse::fromEntity)
                .toList());
    }

    /**
     * Creates a new hackathon.
     * <p>
     * The request body must contain all required information
     * defined in {@link CreateHackathonRequest}.
     *
     * @param request the DTO containing hackathon creation parameters
     * @return a {@link ResponseEntity} with HTTP status 201 (Created)
     *         and a confirmation message
     */
    @PostMapping
    public ResponseEntity<HackathonResponse> createHackathon(@RequestBody CreateHackathonRequest request) {
        Hackathon hackathon = hackathonService.createHackathon(
                request.name(),
                request.location(),
                request.prize(),
                request.maxTeamMembers(),
                request.maxNumberTeams(),
                request.startDate(),
                request.endDate(),
                request.judgeEmail(),
                request.mentorEmails() != null ? request.mentorEmails() : List.of(),
                request.idRules() != null ? request.idRules() : List.of()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HackathonResponse.fromEntity(hackathon));
    }

    /**
     * Updates basic information of an existing hackathon.
     *
     * @param payload a map containing hackathon fields to update
     *                (idHackathon, name, location, prize,
     *                maxTeamMembers, maxNumberTeams)
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     *         and a confirmation message
     */
    @PutMapping
    public ResponseEntity<HackathonResponse> modifyHackathon(@RequestBody Map<String, Object> payload) {
        Long idHackathon = ((Number) payload.get("idHackathon")).longValue();
        double prize = ((Number) payload.get("prize")).doubleValue();
        int maxTeamMembers = ((Number) payload.get("maxTeamMembers")).intValue();
        int maxNumberTeams = ((Number) payload.get("maxNumberTeams")).intValue();

        hackathonService.modifyHackathon(
                idHackathon,
                (String) payload.get("name"),
                (String) payload.get("location"),
                prize,
                maxTeamMembers,
                maxNumberTeams
        );

        if (payload.containsKey("startDate") && payload.containsKey("endDate")) {
            LocalDateTime start = LocalDateTime.parse((String) payload.get("startDate"));
            LocalDateTime end = LocalDateTime.parse((String) payload.get("endDate"));
            hackathonService.setStatus(idHackathon, start, end);
        }

        if (payload.containsKey("idJudge")) {
            Long idJudge = ((Number) payload.get("idJudge")).longValue();
            hackathonService.assignJudge(idJudge, idHackathon);
        }

        Hackathon hackathon = hackathonService.getHackathon(idHackathon);

        return ResponseEntity.ok(HackathonResponse.fromEntity(hackathon));
    }

    // --- STATUS & DATE MANAGEMENT ---

    /**
     * Updates the start and end dates of a hackathon.
     *
     * @param id  the hackathon identifier
     * @param dto the {@link StatusResponse} containing start and end dates
     * @return a {@link ResponseEntity} with HTTP status 200 (OK)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> setStatus(
            @PathVariable Long id,
            @RequestBody StatusResponse dto) {
        hackathonService.setStatus(id, dto.startDate(), dto.endDate());
        return ResponseEntity.ok("Status updated successfully.");
    }


    /**
     * Registers the current user's team to the specified hackathon.
     *
     * @param id the hackathon identifier
     * @return HTTP 200 (OK) if registration succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @PostMapping("/{id}/teams")
    public ResponseEntity<Map<String, String>> registerTeam(@PathVariable Long id) {
        boolean registered = hackathonService.registerTeam(id);

        Map<String, String> response = new HashMap<>();
        if (registered) {
            response.put("message", "Team registered successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Registration failed. Team might already exist or hackathon is full.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Unsubscribes the current user's team from the specified hackathon.
     *
     * @param id the hackathon identifier
     * @return HTTP 200 (OK) if removal succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @DeleteMapping("/{id}/teams")
    public ResponseEntity<Map<String, String>> unsubscribeTeam(@PathVariable Long id) {
        boolean removed = hackathonService.unsubscribeTeam(id);
        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Team unsubscribed.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Team not found in hackathon.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // --- STAFF MANAGEMENT ---

    /**
     * Assigns a judge to the specified hackathon.
     *
     * @param id        the hackathon identifier
     * @param idAccount the account identifier of the judge
     * @return HTTP 200 (OK) with confirmation message
     */
    @PostMapping("/{id}/staff/judge/{idAccount}")
    public ResponseEntity<String> assignJudge(@PathVariable Long id,
                                              @PathVariable Long idAccount) {
        hackathonService.assignJudge(idAccount, id);
        return ResponseEntity.ok("Judge assigned.");
    }

    /**
     * Assigns a mentor to the specified hackathon.
     *
     * @param id        the hackathon identifier
     * @param payload a map containing the required assignment parameters
     * @return HTTP 200 (OK) if assignment succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @PostMapping("/{id}/staff/mentors")
    public ResponseEntity<MentorResponse> assignMentor(@PathVariable Long id,
                                                        @RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Account mentor = hackathonService.assignMentor(email, id);
        if (mentor != null)
            return ResponseEntity.ok(MentorResponse.fromEntity(mentor));
        else
            return ResponseEntity.badRequest().body(null);
    }

    /**
     * Adds a rule to the specified hackathon.
     *
     * @param id   the hackathon identifier
     * @param idRule the idAccount of the rule to add
     * @return HTTP 200 (OK) if addition succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @PostMapping("/{id}/rules/{idRule}")
    public ResponseEntity<RuleResponse> addRule(@PathVariable Long id,
                                                @PathVariable Long idRule) {
        Rule rule = hackathonService.addRule(id, idRule);
        if(rule != null)
            return ResponseEntity.ok(RuleResponse.fromEntity(rule));
        else
            return ResponseEntity.badRequest().body(null);
    }
    /**
     * Removes a mentor from the specified hackathon.
     *
     * @param id        the hackathon identifier
     * @param idAccount the account identifier of the mentor
     * @return HTTP 200 (OK) if removal succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @DeleteMapping("/{id}/staff/mentors/{idAccount}")
    public ResponseEntity<Map<String, String>> removeMentor(@PathVariable Long id,
                                               @PathVariable Long idAccount) {
        boolean removed = hackathonService.removeMentor(idAccount, id);

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Mentor removed.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Mentor not found in staff.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Removes a rule from the specified hackathon.
     *
     * @param id   the hackathon identifier
     * @param idRule the idAccount of the rule to remove
     * @return HTTP 200 (OK) if removal succeeds,
     *         HTTP 400 (Bad Request) otherwise
     */
    @DeleteMapping("/{id}/rules/{idRule}")
    public ResponseEntity<Map<String, String>> removeRule(@PathVariable Long id,
                                                          @PathVariable Long idRule) {
        boolean removed = hackathonService.removeRule(id, idRule);

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Rule removed.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Rule not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Retrieves all rules associated with the hackathon system.
     *
     * <p>This endpoint returns the full list of rules, each mapped to a
     * {@link RuleResponse} object. The list may be empty but is never {@code null}.</p>
     *
     * @return a {@link ResponseEntity} containing a list of {@link RuleResponse}
     *         representing all available rules.
     */
    @GetMapping("/rules")
    public ResponseEntity<List<RuleResponse>> getRules() {
        return ResponseEntity.ok(hackathonService.getRules()
                .stream().map(RuleResponse::fromEntity).toList());
    }


    /**
     * Retrieves the winning team of a hackathon.
     *
     * @param idHackathon the identifier of the hackathon
     * @return 200 OK containing the TeamResponse if a winner exists,
     * or 404 (Not Found) if the winner has not been proclaimed yet.
     */
    @GetMapping("/winner/hackathons/{idHackathon}")
    public ResponseEntity<TeamResponse> getWinnerTeam(@PathVariable Long idHackathon) {
        Team w = hackathonService.getWinner(idHackathon);
        return ResponseEntity.ok(w != null ? TeamResponse.fromEntity(w, null) : null);
    }
}