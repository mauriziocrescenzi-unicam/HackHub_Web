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

  // Recupera tutti gli hackathon (con mock in caso di errore)
  getAllHackathons(): Observable<Hackathon[]> {
    return this.http.get<Hackathon[]>(this.apiUrl).pipe(
      catchError((error: any) => {
        console.warn("Backend non trovato, utilizzo array mock per la lista:", error);
        return of([
          { 
            id: 1, title: 'HackAI 2025', status: 'upcoming', description: 'Build AI apps in 48h.', 
            tags: ['AI', 'Web'], imageUrl: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=500', 
            startDate: '2026-08-01', registrationDeadline: '2026-07-25' 
          },
          { 
            id: 2, title: 'Web3 Builders', status: 'ongoing', description: 'Blockchain smart contracts.', 
            tags: ['Web3'], imageUrl: 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=500', 
            startDate: '2026-07-15', registrationDeadline: '2026-07-10' 
          }
        ] as Hackathon[]);
      })
    );
  }

  createHackathon(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, payload);
  }

  getHackathonById(id: number): Observable<Hackathon> {
    return this.http.get<Hackathon>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.warn("Backend non trovato, utilizzo dati mock per test:", error);
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

  updateHackathon(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, payload);
  }

  deleteHackathon(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}