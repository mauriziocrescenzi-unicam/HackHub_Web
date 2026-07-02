export interface SubmissionResponse {
  id: number;
  idTeam: number;
  team: string;
  hackathon: string;
  submittedAt: string;
  repositoryUrl: string;
  immutableReference: string;
  writtenJudgment?: string; // Opzionale perché potrebbe non essere ancora valutato
  score?: number; // Opzionale
  teamDisabled: boolean;
}

export interface RepoResponse {
  owner: string;
  repo: string;
}

export interface SubmitProjectPayload {
  idHackathon: number;
  type: string;
  source: string;
}

export interface EvaluationPayload {
  writtenJudgment: string;
  score: number;
}