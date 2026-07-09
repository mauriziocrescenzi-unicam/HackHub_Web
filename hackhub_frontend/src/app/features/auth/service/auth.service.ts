import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, switchMap, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginRequest, RegisterRequest, LoginResponse } from '../model/auth.model';
import { Account } from '../../account/models/account.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = environment.apiUrl + '/auth';
  private tokenKey = 'authToken';
  private userLoaded = false;

  private userSignal = signal<Account | null>(null);

  user = this.userSignal.asReadonly();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem(this.tokenKey);

    if (token) {
      this.loadUser$().subscribe({
        next: (user) => console.log('Sessione ripristinata con successo!'),
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            this.logout();
          }
        },
      });
    }
  }

  updateProfile(updateData: Partial<Account>): Observable<Account> {
    return this.http.put<Account>(`${environment.apiUrl}/accounts/profile`, updateData).pipe(
      tap((updatedUser: Account) => {
        this.userSignal.set(updatedUser);
      }),
    );
  }

  // ---------------- AUTH ----------------

  login(req: LoginRequest): Observable<Account> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, req).pipe(
      tap((res) => res.token && this.setToken(res.token)),
      switchMap(() => this.loadUser$()),
    );
  }

  signup(req: RegisterRequest): Observable<Account> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/signup`, req).pipe(
      tap((res) => res.token && this.setToken(res.token)),
      switchMap(() => this.loadUser$()),
    );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.userSignal.set(null);
    this.userLoaded = false;
  }

  // ---------------- TOKEN ----------------

  private setToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // ---------------- USER ----------------

  setUser(user: Account) {
    this.userSignal.set(user);
    this.userLoaded = true;
  }

  loadUser$(): Observable<Account> {
    if (this.userLoaded && this.currentUser) {
      return of(this.currentUser);
    }

    return this.http.get<Account>(`${this.baseUrl}/me`).pipe(
      tap((user) => {
        this.userSignal.set(user);
        this.userLoaded = true;
      }),
    );
  }

  updateTeamId(teamId: number | null) {
    const current = this.currentUser;
    if (current) {
      const updated = { ...current, idTeam: teamId };
      this.userSignal.set(updated);
    }
  }

  updateTeamName(teamName: string | null) {
    const current = this.currentUser;
    if (current) {
      const updated = { ...current, teamName: teamName };
      this.userSignal.set(updated);
    }
  }

  get currentUser(): Account | null {
    return this.userSignal();
  }

  get userId(): number | null {
    return this.currentUser?.idAccount ?? null;
  }

  get teamId(): number | null {
    return this.currentUser?.idTeam ?? null;
  }

  get teamName(): string | null {
    return this.currentUser?.teamName ?? null;
  }

  get role(): string | null {
    return this.currentUser?.role ?? null;
  }

  isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  isStaff(): boolean {
    return this.role === 'STAFF';
  }

  isUser(): boolean {
    return this.role === 'USER';
  }
}