import { HttpInterceptorFn } from '@angular/common/http';

// Aggiunta del token JWT a tutte le richieste HTTP in uscita che intercetta, se l'utente è autenticato.
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('authToken');

  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  return next(req);
};