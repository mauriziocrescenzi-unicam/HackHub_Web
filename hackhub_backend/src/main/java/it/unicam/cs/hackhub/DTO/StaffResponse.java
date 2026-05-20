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

import it.unicam.cs.hackhub.model.Staff;

import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing the staff configuration
 * of a hackathon.
 * <p>
 * Encapsulates organizer, judge, and mentors assigned to the event.
 *
 * @param organizerId    the identifier of the organizer
 * @param organizerEmail the email of the organizer
 * @param judgeId        the identifier of the assigned judge
 * @param judgeEmail     the email of the assigned judge
 * @param mentors        the list of assigned mentors
 */
public record StaffResponse(
        Long organizerId,
        String organizerEmail,

        Long judgeId,
        String judgeEmail,

        List<MentorResponse> mentors
) {
    /**
     * Converts a {@link Staff} entity into a {@link StaffResponse}.
     * <p>
     * Safely handles potential {@code null} values for organizer,
     * judge, and mentor collections.
     *
     * @param staff the {@link Staff} entity to convert
     * @return a corresponding {@link StaffResponse},
     *         or {@code null} if the provided entity is {@code null}
     */
    public static StaffResponse fromEntity(Staff staff) {
        if (staff == null) {
            return null;
        }

        return new StaffResponse(
                staff.getOrganizer() != null ? staff.getOrganizer().getIdAccount() : null,
                staff.getOrganizer() != null ? staff.getOrganizer().getEmail() : null,

                staff.getJudge() != null ? staff.getJudge().getIdAccount() : null,
                staff.getJudge() != null ? staff.getJudge().getEmail() : null,

                staff.getMentors() != null ? staff.getMentors().stream()
                        .map(MentorResponse::fromEntity)
                        .toList() : Collections.emptyList()
        );
    }
}