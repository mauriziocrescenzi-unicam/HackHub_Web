import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { ReportRequest } from '../models/reportRequest.model';
import { Observable } from 'rxjs';
import { Report } from '../models/report.model';

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/reports';

  reportTeam(payload: ReportRequest) {
    // Aggiunto responseType: 'text' nel caso il backend ritorni una stringa
    return this.http.post(this.apiUrl, payload, { responseType: 'text' });
  }

  reportManagement(idHackathon: number, idTeam: number, disabled: boolean) {
    // Aggiunto responseType: 'text'
    return this.http.patch(
      `${this.apiUrl}/management/hackathons/${idHackathon}/teams/${idTeam}?disabled=${disabled}`,
      {},
      { responseType: 'text' }
    );
  }

  getReportsByTeam(idHackathon: number, idTeam: number): Observable<Report[]> {
    return this.http.get<Report[]>(
      `${this.apiUrl}/hackathons/${idHackathon}/teams/${idTeam}`
    );
  }
}