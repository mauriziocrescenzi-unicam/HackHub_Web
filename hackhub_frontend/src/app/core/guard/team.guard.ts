import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from '../../features/auth/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TeamGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  /**
   * Route guard that handles redirection based on:
   * - User authentication
   * - Whether the user belongs to a team
   * - Whether the user is a normal user or something else (admin, manager, etc.)
   *
   * Logic flow:
   * 1. If the user is not authenticated → redirect to /login
   * 2. If the user has a teamId → redirect directly to /teams/:teamId
   * 3. If the user does NOT have a team but is also NOT a "user" → redirect to /dashboard
   * 4. Otherwise → allow access to the route
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