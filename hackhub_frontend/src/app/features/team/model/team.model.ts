import { MembroTeam } from "./membro-team.model";
import { SpecificheTeam } from "./specifiche-team.model";

export interface Team {
    id: number;
    name: string;
    leader: MembroTeam;
    members: MembroTeam[];
    description: string;
    teamStats: SpecificheTeam;
}