import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Invito } from '../models/invito.model'; // Nome modello aggiornato
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InvitiService { // Nome classe aggiornato
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/invitations';

  // Recupera tutti gli inviti per l'utente loggato
  getAll(): Observable<Invito[]> {
    return this.http.get<Invito[]>(`${this.apiUrl}/user`);
  }

  // Risponde a un invito (accetta o rifiuta)
  // Il backend probabilmente si aspetta una risposta, se torna un oggetto team, 
  // potresti tipizzare il ritorno invece di 'any'
  respond(id: number, accept: boolean): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/response?accept=${accept}`, {});
  }

  // Invia un invito verso un email
  invite(email: string): Observable<any> {
    return this.http.post(this.apiUrl, { email });
  }
}