package it.unicam.cs.hackhub.DTO;

/**
 * Represents aggregated statistical data for a team participating
 * in hackathons.
 *
 * <p>This record is used as a DTO (Data Transfer Object) to return
 * performance metrics related to a team's participation in hackathon events,
 * including results and derived statistics.</p>
 *
 * @param hackathonsPlayed the total number of hackathons in which the team has participated.
 * @param hackathonsWon    the number of hackathons the team has won.
 * @param podiums          the total number of podium finishes (1st, 2nd, or 3rd place).
 * @param winRate          the win percentage of the team, typically calculated as
 *                         {@code (hackathonsWon / (double) hackathonsPlayed) * 100}.
 */
public record TeamStatsResponse(
        int hackathonsPlayed,
        int hackathonsWon,
        int podiums,
        double winRate
) {}
