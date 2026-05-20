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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the request payload used to create a new hackathon.
 *
 * <p>This record contains all the necessary information required to
 * configure and initialize a hackathon, including basic details,
 * scheduling, participant limits, and associated rules.</p>
 *
 * @param name            the name of the hackathon; must not be {@code null} or blank.
 * @param location        the physical or virtual location where the hackathon will take place.
 * @param prize           the prize amount awarded to the winning team; must be non-negative.
 * @param maxTeamMembers  the maximum number of members allowed per team.
 * @param maxNumberTeams  the maximum number of teams that can participate in the hackathon.
 * @param startDate       the date and time when the hackathon begins; must not be {@code null}.
 * @param endDate         the date and time when the hackathon ends; must not be {@code null}.
 * @param judgeEmail      the email address of the judge responsible for overseeing the event.
 * @param mentorEmails    the list of email addresses of mentors assisting participants.
 * @param idRules         the list of rule identifiers applied to the hackathon.
 */
public record CreateHackathonRequest(
        String name,
        String location,
        double prize,
        int maxTeamMembers,
        int maxNumberTeams,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String judgeEmail,
        List<String> mentorEmails,
        List<Long> idRules
) { }
