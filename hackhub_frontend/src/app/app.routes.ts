import { Routes } from '@angular/router';
import { RegisterComponent } from './features/auth/components/register/register.component';
import { LoginComponent } from './features/auth/components/login/login.component';
import { ProfileComponent } from './features/account/components/profile/profile.component';
import { ListaHackathonComponent } from './features/hackathon/components/lista-hackathon/lista-hackathon.component';
import { CreazioneHackathonComponent } from './features/hackathon/components/creazione-hackathon/creazione-hackathon.component';
import { SingoloHackathonComponent } from './features/hackathon/components/singolo-hackathon/singolo-hackathon.component';
import { ModificaHackathonComponent } from './features/hackathon/components/modifica-hackathon/modifica-hackathon.component';
import { TeamComponent } from './features/team/components/team/team.component';
import { HackerTeamComponent } from './features/team/components/hacker-team/hacker-team.component';
import { authGuard } from './core/guard/auth.guard';
import { TeamGuard } from './core/guard/team.guard';
import { roleGuard } from './core/guard/role.guard';

export const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  
  // 1. Lista generale degli hackathon
  { path: 'hackathons', component: ListaHackathonComponent },
  
  // 2. Creazione Hackathon 
  { 
    path: 'hackathons/create', 
    component: CreazioneHackathonComponent, 
    canActivate: [authGuard, roleGuard], 
    data: { expectedRole: 'ORGANIZER' } 
  },
  
  // 3. Dettaglio Singolo Hackathon
  { path: 'hackathons/:id', component: SingoloHackathonComponent },

  // 4. Modifica Hackathon 
  { 
    path: 'hackathons/:id/edit', 
    component: ModificaHackathonComponent, 
    canActivate: [authGuard, roleGuard], 
    data: { expectedRole: 'ORGANIZER' } 
  },

  // Gestione Team
  { path: 'teams', component: TeamComponent, canActivate: [TeamGuard] },
  { path: 'teams/my', component: HackerTeamComponent, canActivate: [authGuard] },
  
  // Rotta di fallback (Wildcard)
  { path: '**', redirectTo: '' }
];