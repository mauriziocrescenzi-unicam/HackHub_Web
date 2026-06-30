import { Component, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Team } from '../../model/team.model';
import { TeamService } from '../../service/team.service';
import { AuthService } from '../../../auth/service/auth.service';

@Component({
  selector: 'app-team',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.scss']
})
export class TeamComponent {
  // Stato generale
  teams = signal<Team[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string|null>('');
  searchQuery = signal<string>('');
  isLoggedIn = signal<boolean>(false);
  messageTimeout: any;

  showCreateForm = signal<boolean>(false);

  // Ricerca e filtro
  filteredTeams = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.teams().filter(team => 
      team.name.toLowerCase().includes(query) || 
      (team.description && team.description.toLowerCase().includes(query))
    );
  });

  myTeam = signal<Team | null>(null); 
  selectedTeam = signal<Team | null>(null);

  // Stato form creazione
  newTeamName = '';
  newTeamDescription = '';
  isCreating = signal<boolean>(false);
  createErrorMessage = signal<string | null>(null);
  createSuccessMessage = signal<string | null>(null);

  // Limite massimo membri per un team (usato per calcolare i badge Full/Looking)
  maxMembers = 5;

  constructor(private teamService: TeamService, private authService: AuthService, private router: Router) {
    effect(() => {
      const user = this.authService.user();
      this.isLoggedIn.set(!!user);
      this.myTeam.set(null); 
      this.loadTeams();
    }, { allowSignalWrites: true }); 
  }

  loadMyTeam() {
    this.router.navigate(['/teams/my']);
  }
  
  loadTeams() {
    this.isLoading.set(true);
    this.teamService.getAllTeams().subscribe({
      next: (data) => {
        this.teams.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage.set('Errore durante il caricamento dei team.');
        this.clearMessagesAfterDelay(); 
        this.isLoading.set(false);
      }
    });
  }

  private clearMessagesAfterDelay() {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
    }, 5000);
  }

  openCreateTeam() {
    this.showCreateForm.set(true);
    document.body.style.overflow = 'hidden';
  }

  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  openTeamDetails(team: Team) {
    this.selectedTeam.set(team);
    document.body.style.overflow = 'hidden';
  }

  closePopup() {
    this.selectedTeam.set(null);
    document.body.style.overflow = 'auto';
  }

  closeCreateForm() {
    this.showCreateForm.set(false);
    this.newTeamName = '';
    this.newTeamDescription = '';
    this.createErrorMessage.set(null);
    this.createSuccessMessage.set(null);
    this.isCreating.set(false);
    document.body.style.overflow = 'auto';
  }

  submitCreateTeam() {
    const name = this.newTeamName.trim();
    const description = this.newTeamDescription.trim();

    if (!name) {
      this.createErrorMessage.set('Il nome del team è obbligatorio.');
      return;
    }

    this.isCreating.set(true);
    this.createErrorMessage.set(null);
    this.createSuccessMessage.set(null);

    this.teamService.createTeam(name, description).subscribe({
      next: (team) => {
        this.isCreating.set(false);
        this.createSuccessMessage.set(`Team "${team.name}" creato con successo!`);
        this.authService.updateTeamId(team.id);
        
        setTimeout(() => {
          this.closeCreateForm();
          this.loadTeams(); 
        }, 1500);
      },
      error: (err) => {
        this.isCreating.set(false);
        this.createErrorMessage.set(
          err?.error?.message || 'Errore durante la creazione del team. Riprova.'
        );
      }
    });
  }

  getTotalMembers(team: Team): number {
    const membersCount = team.members ? team.members.length : 0;
    const leaderCount = team.leader ? 1 : 0;
    return membersCount + leaderCount;
  }
}