import { Injectable } from '@angular/core';
import { SingoloHackathon } from '../models/singolo-hackathon.model';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ListaHackathonService {
  
  private apiUrl = environment.apiUrl + '/hackathons';

  constructor(private http: HttpClient) {}

  getAll(): Observable<SingoloHackathon[]> {
    return this.http.get<SingoloHackathon[]>(this.apiUrl);

  }
}