import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EvaluationPayload, SubmissionResponse, SubmitProjectPayload } from '../model/submission.model';
import { Team } from '../../team/model/team.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl+'/submissions';

  submitProject(payload: SubmitProjectPayload): Observable<string> {
    return this.http.post(this.apiUrl, payload, { responseType: 'text' });
  }

  updateSubmission(id: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/${id}`, {}, { responseType: 'text' });
  }

  getSubmissionForStaff(id: number): Observable<SubmissionResponse> {
    return this.http.get<SubmissionResponse>(`${this.apiUrl}/${id}/staff`);
  }

  getSubmissionForTeam(id: number): Observable<SubmissionResponse> {
    return this.http.get<SubmissionResponse>(`${this.apiUrl}/${id}/team`);
  }

  evaluateSubmission(id: number, payload: EvaluationPayload): Observable<string> {
    return this.http.post(`${this.apiUrl}/${id}/evaluation`, payload, { responseType: 'text' });
  }

  proclaimWinner(idHackathon: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/winner/hackathons/${idHackathon}`, {}, { responseType: 'text' });
  }
  
  getSubmissionsByHackathon(idHackathon: number): Observable<SubmissionResponse[]> {
    return this.http.get<SubmissionResponse[]>(`${this.apiUrl}/hackathons/${idHackathon}/staff`);
  }
}