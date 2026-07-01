import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
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
    return this.http.get<Hackathon>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.warn("Backend non trovato, utilizzo dati mock per test:", error);
        // Restituisci un oggetto che rispetta l'interfaccia Hackathon
        return of({
          id: id,
          title: "Hackathon di Prova (Mock)",
          description: "Questa è una descrizione caricata in locale perché il backend non risponde correttamente.",
          startDate: "2026-07-20",
          registrationDeadline: "2026-07-15",
          status: "upcoming",
          tags: ["AI", "Cloud"],
          imageUrl: "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=1400&q=80"
        } as Hackathon);
      })
    );
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