import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../features/auth/service/auth.service'; 

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Recupera il ruolo atteso definito in app.routes.ts
  const expectedRole = route.data['expectedRole'];
  
  // Accede al ruolo dell'utente che l'authGuard ha già caricato in memoria.
  const userRole = authService.currentUser?.role;

  if (userRole === expectedRole) {
    return true; // Ruolo corretto, può accedere
  }

  // Ruolo errato, reindirizza alla lista
  router.navigate(['/hackathons']);
  return false;
};