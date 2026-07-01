import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hackathon } from '../models/hackathon.model'; // <-- Importiamo il modello!

@Injectable({
  providedIn: 'root'
})
export class HackathonService {
  // URL dell'endpoint Spring Boot
  private apiUrl = 'http://localhost:8080/api/hackathons';

  constructor(private http: HttpClient) { }

  // Metodo per creare un hackathon
  createHackathon(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, payload);
  }

  // NUOVO METODO: Recupera un singolo hackathon tramite il suo ID
  getHackathonById(id: number): Observable<Hackathon> {
    // Fa una chiamata GET a http://localhost:8080/api/hackathons/1 (ad esempio)
    return this.http.get<Hackathon>(`${this.apiUrl}/${id}`);
  }
}