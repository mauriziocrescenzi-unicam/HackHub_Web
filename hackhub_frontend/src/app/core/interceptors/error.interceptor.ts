import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject, Injector } from '@angular/core'; // <--- Aggiunto Injector
import { Router } from '@angular/router';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../features/auth/service/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  // Iniezione dei servizi tramite Injector per evitare problemi di dipendenze circolari
  const injector = inject(Injector);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Si è verificato un errore sconosciuto';

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Errore di rete: ${error.error.message}`;
      } else {
        if (typeof error.error === 'string') {
          errorMessage = error.error;
        } else if (error.error && error.error.message) {
          errorMessage = error.error.message;
        } else {
          errorMessage = `Errore del server: codice ${error.status}`;
        }

        // --- GESTIONE GLOBALE DEL 401 (Token Scaduto o Non Valido) ---
        if (error.status === 401) {
          console.warn('Token scaduto o non valido. Disconnessione in corso...');
          
          // recupero dei servizi dall'injector SOLO al momento del bisogno
          const authService = injector.get(AuthService);
          const router = injector.get(Router);
          
          authService.logout();
          router.navigate(['/login']);
        }
      }

      console.error('[ErrorInterceptor] Intercettato:', errorMessage);
      
      return throwError(() => new Error(errorMessage));
    })
  );
};