import { Injectable, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Invito } from '../models/invito.model'; 
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InvitiService { 
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/invitations';

  getAll(): Observable<Invito[]> {
    return this.http.get<Invito[]>(`${this.apiUrl}/user`);
  }

  respond(id: number, accept: boolean) {
    return this.http.patch(`${this.apiUrl}/${id}/response?accept=${accept}`, {});
  }

  invite(email: string) {
    return this.http.post(this.apiUrl, { email });
  }
}