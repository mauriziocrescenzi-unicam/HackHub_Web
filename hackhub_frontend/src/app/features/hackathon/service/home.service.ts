import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

import { SingoloHackathon } from '../models/singolo-hackathon.model';

@Injectable({ providedIn: 'root' })
export class HomeService {
  private apiUrl = environment.apiUrl + '/hackathons';

  constructor(private http: HttpClient) {}

  getAll(): Observable<SingoloHackathon[]> {
    return this.http.get<SingoloHackathon[]>(this.apiUrl);
  }

  cerca(query: string): Observable<SingoloHackathon[]> {
    return this.http.get<SingoloHackathon[]>(`${this.apiUrl}?q=${query}`);
  }
}
