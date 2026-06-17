import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  // Rotta di fallback che reindirizza alla home
  { path: '**', redirectTo: '' }
];