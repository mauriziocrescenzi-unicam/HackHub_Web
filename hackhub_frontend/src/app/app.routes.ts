import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { ProfileComponent } from './features/account/components/profile/profile';
import { authGuard } from './core/guard/auth.guard';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  // Rotta di fallback che reindirizza alla home
  { path: '**', redirectTo: '' },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard]}
];