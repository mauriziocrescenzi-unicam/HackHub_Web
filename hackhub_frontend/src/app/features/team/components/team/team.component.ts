import { Component, computed, effect, HostListener, signal } from '@angular/core';
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
  myTeam = signal<Team | null>(null); 
  selectedTeam = signal<Team | null>(null);

  // Stato form creazione
  newTeamName = '';
  newTeamDescription = '';
  isCreating = signal<boolean>(false);
  createErrorMessage = signal<string | null>(null);
  createSuccessMessage = signal<string | null>(null);

  // Paginazione e UI
  currentPage = signal<number>(1);
  itemsPerPage = 8;
  showScrollTop = signal<boolean>(false);
  maxMembers = 5;

  constructor(private teamService: TeamService, private authService: AuthService, private router: Router) {
    // Gestione reattiva dell'utente: se ha già un team viene reindirizzato alla pagina del team, altrimenti carica la lista team
    effect(() => {
      const user = this.authService.user();
      if (user) {
        this.isLoggedIn.set(true);
        if (user.idTeam) {
          this.loadMyTeam();
          return;
        }
        this.myTeam.set(null);
        this.loadTeams();
      } else {
        this.isLoggedIn.set(false);
        this.myTeam.set(null);
        this.loadTeams();
      }
    }, { allowSignalWrites: true });
  }

  // Computed properties per filtraggio, paginazione e conteggio totale pagine

  // 1. Filtra i team in base alla ricerca testuale
  filteredTeams = computed(() => {
    const query = this.searchQuery().toLowerCase();
    return this.teams().filter(team => 
      team.name.toLowerCase().includes(query) || 
      (team.description && team.description.toLowerCase().includes(query))
    );
  });

  // 2. Taglia i team filtrati in base alla pagina corrente
  paginato = computed(() => {
    const start = (this.currentPage() - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredTeams().slice(start, end);
  });

  // 3. Calcola il numero totale di pagine
  totalPages = computed(() => {
    return Math.ceil(this.filteredTeams().length / this.itemsPerPage);
  });

  // --- METODI DI CARICAMENTO DATI ---

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

  // --- METODI DI INTERAZIONE UTENTE ---

  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
    this.currentPage.set(1); // Resetta la pagina corrente quando cambia la ricerca
  }

  changePage(page: number) {
    this.currentPage.set(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  @HostListener('window:scroll')
  onScroll() {
    this.showScrollTop.set(window.scrollY > 200);
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  openTeamDetails(team: Team) {
    this.selectedTeam.set(team);
    document.body.style.overflow = 'hidden';
  }

  closePopup() {
    this.selectedTeam.set(null);
    document.body.style.overflow = 'auto';
  }

  // --- CREAZIONE TEAM ---

  openCreateTeam() {
    this.showCreateForm.set(true);
    document.body.style.overflow = 'hidden';
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
          // Dopo la creazione del team, l'utente creatore è il leader, ricarica i dati dell'utente per aggiornare lo stato del team
          this.loadMyTeam(); 
        }, 1500);
      },
      error: (err: any) => {
        this.isCreating.set(false);
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.createErrorMessage.set(backendMessage || 'Errore durante la creazione del team.');
      }
    });
  }

  // --- UTILITY ---

  getTotalMembers(team: Team): number {
    const membersCount = team.members ? team.members.length : 0;
    const leaderCount = team.leader ? 1 : 0;
    return membersCount + leaderCount;
  }

  private clearMessagesAfterDelay() {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
    }, 5000);
  }
}