import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Team } from '../model/team.model';
import { MembroTeam } from '../model/membro-team.model';
import { SpecificheTeam } from '../model/specifiche-team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/teams';

  getAllTeams(): Observable<Team[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(responses => responses.map(res => this.mapToTeamModel(res)))
    );
  }

  getTeamById(id: number): Observable<Team> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(res => this.mapToTeamModel(res))
    );
  }

  createTeam(name: string, description: string): Observable<Team> {
    return this.http.post<any>(this.apiUrl, { name, description }).pipe(
      map(res => this.mapToTeamModel(res))
    );
  }

  updateTeam(name: string, description: string): Observable<Team> {
    return this.http.put<any>(this.apiUrl, { name, description }).pipe(
      map(res => this.mapToTeamModel(res))
    );
  }

  leaveTeamForMember(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/members`);
  }

  leaveTeamForLeader(): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/leader`);
  }

  removeMember(id: number) {
    return this.http.delete(`${this.apiUrl}/members/${id}`);
  }

  getTeamMembers(id: number): Observable<MembroTeam[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/members`);
  }

  private mapToTeamModel(backendObj: any): Team {
    return {
      id: backendObj.id || backendObj.idTeam,
      name: backendObj.name || '',
      description: backendObj.description || '',
      leader: {
        id: backendObj.leader?.idTeamMember || 0,
        nickname: backendObj.leader?.nickname || 'Leader',
        email: backendObj.leader?.email || ''
      } as MembroTeam,
      // Mappatura sicura dei membri verso l'interfaccia MembroTeam
      members: backendObj.members && Array.isArray(backendObj.members) 
        ? backendObj.members.map((m: any) => ({
            id: m.idTeamMember || 0,
            nickname: m.nickname || 'Membro sconosciuto',
            email: m.email || ''
          } as MembroTeam))
        : [],
        
      // Mappatura sicura delle statistiche del team verso l'interfaccia SpecificheTeam
      teamStats: {
        hackathonsPlayed: backendObj.teamStats?.hackathonsPlayed || 0,
        hackathonsWon: backendObj.teamStats?.hackathonsWon || 0, 
        podiums: backendObj.teamStats?.podiums || 0,
        winRate: Math.trunc((backendObj.stats?.winRate || 0) * 100) / 100
      } as SpecificheTeam
    };
  }
}