import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../features/auth/service/auth.service';
import { catchError, map, of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Controlla se esiste il token nel localStorage. Se non c'è, reindirizza al login.
  if (!authService.getToken()) {
    router.navigate(['/login']);
    return of(false);
  }

  // Se l'utente è già caricato in memoria sblocca subito la rotta.
  if (authService.currentUser) {
    return of(true);
  }

  // Ripristino Sessione: c'è il token ma la memoria si è svuotata (es. F5).
  // Richiamiama il metodo loadUser$() per fare la fetch da /me.
  return authService.loadUser$().pipe(
    map(() => true), // Se il server risponde 200 OK, la rotta si apre
    catchError(() => {
      // Se il server risponde con un errore, reindirizza al login
      router.navigate(['/login']);
      return of(false);
    })
  );
};