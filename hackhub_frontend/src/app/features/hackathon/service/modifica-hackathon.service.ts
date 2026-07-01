import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { SingoloHackathon } from '../models/singolo-hackathon.model';
import { Rule } from '../models/rule.model';
import { Staff } from '../models/staff.model';

//da rimuovere insieme al mock
import { of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ModificaHackathonService {
  private apiUrl = environment.apiUrl + '/hackathons';

  constructor(private http: HttpClient) {}

  /** GET /api/hackathons/:id — per precaricare i dati nel form */
  getById(id: number): Observable<SingoloHackathon> {
    // TODO: rimuovere quando il backend avrà dati reali
  const mock: any = {
    id: 1,
    name: 'AI Innovation Hackathon 2025',
    location: 'Milano, Talent Garden Calabiana',
    prize: 5000,
    maxTeamMembers: 5,
    maxNumberTeams: 48,
    startDate: '2025-03-14T09:00:00',
    endDate: '2026-12-16T18:00:00',
    status: 'OPEN',
    teams: [],
    staff: {
      organizerId: 1,
      organizerEmail: 'organizer@test.com',
      judgeId: 2,
      judgeEmail: 'judge@test.com',
      mentors: [{ id: 3, email: 'mentor@test.com' }]
    },
    rules: [{ id: 1, name: 'Regola 1', description: 'Descrizione regola 1' }, 
      { id: 2, name: 'Regola 2', description: 'Descrizione regola 2' },
      { id: 3, name: 'Regola 3', description: 'Descrizione regola 3' }]
  };
  return of(mock);

  // return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(...)
  }

  /** PUT /api/hackathons/:id — per salvare le modifiche */
  updateHackathon(id: number, data: any): Observable<SingoloHackathon> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data).pipe(
      map(res => this.mapToHackathonModel(res))
    );
  }

  private mapToHackathonModel(backendObj: any): SingoloHackathon {
    return {
      id: backendObj.id,
      name: backendObj.name,
      location: backendObj.location,
      startDate: backendObj.startDate,
      endDate: backendObj.endDate,
      prize: backendObj.prize,
      maxTeamMembers: backendObj.maxTeamMembers,
      maxNumberTeams: backendObj.maxNumberTeams,
      status: backendObj.status,
      teams: backendObj.teams || [],
      staff: (backendObj.staff || []).map((s: any) => this.mapToStaffModel(s)),
      rules: (backendObj.rules || []).map((r: any) => this.mapToRuleModel(r))
    };
  }

  private mapToRuleModel(backendObj: any): Rule {
    return {
      id: backendObj.id,
      name: backendObj.name,
      description: backendObj.description
    };
  }

  private mapToStaffModel(backendObj: any): Staff {
    return {
      id: backendObj.id,
      name: backendObj.name,
      email: backendObj.email
    };
  }
}