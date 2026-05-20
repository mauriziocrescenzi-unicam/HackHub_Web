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

import it.unicam.cs.hackhub.model.Report;
import it.unicam.cs.hackhub.model.Rule;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a report submitted
 * against a team within a hackathon.
 * <p>
 * Contains identifying information about the reported team,
 * the mentor who submitted the report, and the report details.
 *
 * @param id          the unique identifier of the report
 * @param teamId      the identifier of the reported team
 * @param teamName    the name of the reported team
 * @param mentorId    the identifier of the mentor who submitted the report
 * @param mentorName  the name of the mentor who submitted the report
 * @param description the detailed description of the reported issue
 * @param reason      the short reason or category of the report
 * @param date        the date and time when the report was created
 */
public record ReportResponse(Long id,
     Long teamId,
     String teamName,
     Long mentorId,
     String mentorName,
     String description,
     String reason,
     LocalDateTime date) {

    public static ReportResponse fromEntity(Report report) {
        return new ReportResponse(
                report.getIdReport(),
                report.getTeam().getIdTeam(),
                report.getTeam().getName(),
                report.getMentor().getIdAccount(),
                report.getMentor().getName(),
                report.getDescription(),
                report.getReason(),
                report.getDate()
        );
    }
}
