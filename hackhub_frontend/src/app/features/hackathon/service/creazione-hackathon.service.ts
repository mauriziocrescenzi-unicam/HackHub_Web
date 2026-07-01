import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

import { Hackathon } from '../models/singolo-hackathon.model';
import { Rule } from '../models/rule.model';
import { Staff } from '../models/staff.model';



@Injectable({ providedIn: 'root' })
export class creaHackathonService {
  private apiUrl = environment.apiUrl + '/hackathons';
  private staffUrl = environment.apiUrl + '/accounts/staff';

  constructor(private http: HttpClient) {}

    getRules(): Observable<Rule[]> {
    return this.http.get<any[]>(`${this.apiUrl}/rules`).pipe(
        map(responses => responses.map(res => this.mapToRuleModel(res)))
    );
  }
    getStaff(): Observable<Staff[]> {
    return this.http.get<any[]>(`${this.staffUrl}`).pipe(
      map(responses => responses.map(res => this.mapToStaffModel(res)))
    );

  }
  createHackathon(hackathonData: any): Observable<Hackathon> {
    return this.http.post<any>(this.apiUrl, hackathonData).pipe(
      map(response => this.mapToHackathonModel(response))
    );
  }

  private mapToHackathonModel(backendObj: any): Hackathon {
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
