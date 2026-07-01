import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../../features/auth/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TeamGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  /**
   * Guardia di routing che gestisce i reindirizzamenti in base a:
   * - Autenticazione dell'utente
   * - Appartenenza dell'utente a un team
   * - Ruolo dell'utente (utente normale vs admin, staff, ecc.)
   *
   * Flusso logico:
   * 1. Se l'utente non è autenticato → reindirizza a /login
   * 2. Se l'utente ha già un teamId → reindirizza direttamente a /teams/my
   * 3. Se l'utente NON ha un team ma NON è un "user" normale → reindirizza a /dashboard
   * 4. Altrimenti → consenti l'accesso alla rotta
   */
  canActivate(): boolean | UrlTree {
    
    if (!this.auth.isAuthenticated()) {
      return this.router.createUrlTree(['/login']);
    }

    const teamId = this.auth.teamId;

    if (teamId) {
      return this.router.createUrlTree([`/teams/my`]);
    }
    else if(!this.auth.isUser()){
      return this.router.createUrlTree([`/dashboard`]);
    }

    return true;
  }
}