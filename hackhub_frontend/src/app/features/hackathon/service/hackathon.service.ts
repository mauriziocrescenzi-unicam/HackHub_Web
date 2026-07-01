import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hackathon } from '../models/hackathon.model';

@Injectable({
  providedIn: 'root'
})
export class HackathonService {
  private apiUrl = 'http://localhost:8080/api/hackathons';

  constructor(private http: HttpClient) { }

  // Crea un nuovo hackathon
  createHackathon(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, payload);
  }

  // Legge un hackathon per ID
  getHackathonById(id: number): Observable<Hackathon> {
    return this.http.get<Hackathon>(`${this.apiUrl}/${id}`);
  }

  // Aggiorna un hackathon esistente
  updateHackathon(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, payload);
  }

  // Elimina un hackathon
  deleteHackathon(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}