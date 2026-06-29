import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { ProfileComponent } from './features/account/components/profile/profile.component';
import { ListaHackathonComponent } from './features/hackathon/components/lista-hackathon/lista-hackathon.component';
import { CreazioneHackathonComponent } from './features/hackathon/components/creazione-hackathon/creazione-hackathon.component';
import { authGuard } from './core/guard/auth.guard';
import { roleGuard } from './core/guard/role.guard';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'hackathons', component: ListaHackathonComponent },
  { path: 'hackathons/create', component: CreazioneHackathonComponent },
  // Rotta di fallback che reindirizza alla home
  { path: '**', redirectTo: '' },
  { path: 'hackathons/create', 
    component: CreazioneHackathonComponent, 
    canActivate: [authGuard, roleGuard], 
    data: { expectedRole: 'ORGANIZER' } 
  }
];