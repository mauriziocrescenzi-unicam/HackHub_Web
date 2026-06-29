import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HackathonService {
  // Sostituisci questo URL con l'endpoint esatto del tuo controller Spring Boot
  private apiUrl = 'http://localhost:8080/api/hackathons'; 

  constructor(private http: HttpClient) { }

  // Metodo che riceve il payload dal componente e lo spedisce al server
  createHackathon(payload: any): Observable<any> {
    // Se il tuo endpoint è /api/hackathons/create, aggiungi '/create' qui sotto
    return this.http.post(`${this.apiUrl}`, payload);
  }
}