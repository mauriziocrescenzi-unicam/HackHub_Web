import { Team } from '../../team/model/team.model';
import { Staff } from './staff.model';
import { Rule } from './rule.model';

export interface SingoloHackathon {
    id: number;
    name: string;
    location: string;
    prize: number;
    maxTeamMembers: number;
    maxNumberTeams: number;
    winningTeam?: Team;
    startDate: string;        
    endDate: string;
    status: 'REGISTRATION' | 'ONGOING' | 'EVALUATION' | 'COMPLETED';
    teams: Team[];
    staff: StaffResponse;
    rules: Rule[];
}