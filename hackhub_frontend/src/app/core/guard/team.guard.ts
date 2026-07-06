import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../features/auth/service/auth.service';

export const teamGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const teamId = authService.teamId;

  // Se ha già un team, mandalo alla sua area team privata
  if (teamId) {
    router.navigate(['/teams/my']);
    return false;
  } 
  // Se non è un utente base (es. è STAFF o ADMIN), rimandalo alla home
  else if (!authService.isUser()) {
    router.navigate(['/home']);
    return false;
  }

  // Se arriva qui: è autenticato, è un USER normale, e NON ha ancora un team. Può passare!
  return true;
};