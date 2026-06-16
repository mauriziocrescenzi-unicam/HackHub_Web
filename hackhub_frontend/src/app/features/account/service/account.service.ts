import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from '../models/account.model';

@Injectable({
  providedIn: 'root',
})
export class AccountService {

  private api = environment.apiUrl + '/accounts';

  constructor(private http: HttpClient) {}

  getAllAccounts() : Observable<Account[]>{
    return this.http.get<Account[]>(this.api);
  }

  getAccountById(id : number) : Observable<Account>{
    return this.http.get<Account>(`${this.api}/${id}`);
  }

  changeStatus(id: number, disabled: boolean): Observable<any> {
    const params = new HttpParams().set('disabled', disabled);
    return this.http.patch(`${this.api}/${id}/status`, null, { params });
  }
}