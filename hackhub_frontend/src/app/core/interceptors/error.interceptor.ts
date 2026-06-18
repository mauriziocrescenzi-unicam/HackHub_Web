import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../features/auth/service/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  // Iniettiamo i servizi necessari nel caso serva fare un logout forzato
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Si è verificato un errore sconosciuto';

      if (error.error instanceof ErrorEvent) {
        // 1. Errore lato Client (es. assenza di connessione di rete)
        errorMessage = `Errore di rete: ${error.error.message}`;
      } else {
        // 2. Errore lato Server
        if (typeof error.error === 'string') {
          // Il server ha restituito una stringa pura
          errorMessage = error.error;
        } else if (error.error && error.error.message) {
          // Il server ha restituito un JSON con un campo "message"
          errorMessage = error.error.message;
        } else {
          // Fallback generico con il codice di stato HTTP
          errorMessage = `Errore del server: codice ${error.status}`;
        }

        // --- GESTIONE GLOBALE DEL 401 (Token Scaduto o Non Valido) ---
        if (error.status === 401) {
          console.warn('Token scaduto o non valido. Disconnessione in corso...');
          authService.logout(); // Pulisce il LocalStorage e i Signal
          router.navigate(['/login']); // Rimanda l'utente al login
        }
      }

      console.error('[ErrorInterceptor] Intercettato:', errorMessage);
      
      // Rilancia l'errore in un formato standard, così i componenti lo leggono facilmente
      return throwError(() => new Error(errorMessage));
    })
  );
};