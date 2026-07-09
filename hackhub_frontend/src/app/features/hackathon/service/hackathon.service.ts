import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hackathon } from '../models/hackathon.model';
import { environment } from '../../../../environments/environment';
import { Rule } from '../models/rule.model';
import { CreazioneHackathon } from '../models/creazione-hackathon.model';
import { Team } from '../../team/model/team.model';

@Injectable({ 
  providedIn: 'root' 
})

export class HackathonService {
  
  private readonly BASE_URL = environment.apiUrl + '/hackathons';

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<Hackathon> {
    return this.http.get<Hackathon>(`${this.BASE_URL}/${id}`);
  }

  getAll(): Observable<Hackathon[]> {
    return this.http.get<Hackathon[]>(this.BASE_URL);
  }
  getMyHackathons(): Observable<Hackathon[]> {
    return this.http.get<Hackathon[]>(`${this.BASE_URL}/my`);
  }

  register(id: number): Observable<any> {
    return this.http.post<any>(`${this.BASE_URL}/${id}/teams`, {});
  }

  getRules(): Observable<Rule[]> {
    return this.http.get<any[]>(`${this.BASE_URL}/rules`);
  }
    
  createHackathon(hackathonData: CreazioneHackathon): Observable<Hackathon> {
    return this.http.post<any>(this.BASE_URL, hackathonData);
  }

  updateHackathon(data: any): Observable<Hackathon> {
    return this.http.put<any>(this.BASE_URL, data);
  }

  assignMentor(hackathonId: number, email: string): Observable<any> {
    return this.http.post<any>(`${this.BASE_URL}/${hackathonId}/staff/mentors`, { email });
  }

  removeMentor(hackathonId: number, mentorId: number): Observable<any> {
    return this.http.delete<any>(`${this.BASE_URL}/${hackathonId}/staff/mentors/${mentorId}`);
  }

  unsubscribeTeam(hackathonId: number): Observable<any> {
    return this.http.delete<any>(`${this.BASE_URL}/${hackathonId}/teams`);
  }

  addRule(hackathonId: number, ruleId: number): Observable<Rule> {
    return this.http.post<Rule>(`${this.BASE_URL}/${hackathonId}/rules/${ruleId}`, {});
  }

  removeRule(hackathonId: number, ruleId: number): Observable<any> {
    return this.http.delete<any>(`${this.BASE_URL}/${hackathonId}/rules/${ruleId}`);
  }
  getWinner(idHackathon: number): Observable<Team> {
    return this.http.get<Team>(`${this.BASE_URL}/winner/hackathons/${idHackathon}`);
  }

  deleteHackathon(id: number): Observable<any> {
    return this.http.delete<any>(`${this.BASE_URL}/${id}`);
  }

}