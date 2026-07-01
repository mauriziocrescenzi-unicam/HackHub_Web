import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Account } from '../../account/models/account.model';

@Injectable({
  providedIn: 'root',
})
export class StaffService {
  constructor(private http: HttpClient) { }
  private staffUrl = environment.apiUrl + '/accounts/staff';

  getStaff(): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.staffUrl}`);
  }
}
