import { Component, computed, HostListener, OnInit, signal } from '@angular/core';
import { Hackathon } from '../../models/hackathon.model';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { HackathonService } from '../../service/hackathon.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-hackathons',
  standalone: true,
  imports: [RouterLink, FormsModule], 
  templateUrl: './lista-hackathon.component.html',
  styleUrl: './lista-hackathon.component.scss',
})
export class ListaHackathonComponent implements OnInit {
  hackathon = signal<Hackathon[]>([]);
  errorMessage = signal<string | null>(null);
  
  // Segnali dei filtri
  searchTerm = signal<string>('');
  filtroTempo = signal('tutti');
  filtroPartecipazione = signal('tutti'); 
  ordinamento = signal('data-asc');
  
  showFiltri = false;
  currentPage = signal<number>(1);
  itemsPerPage = 9;

  showScrollTop = signal<boolean>(false);

  constructor(private hackathonService: HackathonService, protected authService: AuthService) { }
  
  ngOnInit(): void {
    console.log("DEBUG: Il component ListaHackathon è entrato in ngOnInit");
    this.caricaHackathon();
  }

  caricaHackathon(): void {
    this.hackathonService.getAll().subscribe({
      next: (data) => {
        const ordinati = data
          .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime());
        this.hackathon.set(ordinati);
      },
      error: (err) => {
        this.errorMessage.set(err.message);
      }
    });
  }

  filtro = computed(() => {
    if (!this.hackathon()) {
      return [];
    }
    const dataAttuale = new Date();
    
    // Otteniamo l'utente corrente dal segnale dell'AuthService
    const currentUser = this.authService.user(); 

    return this.hackathon()!.filter(hackathon => {
      
      // 1. Filtro Nome
      const termine = this.searchTerm();
      const nomeMatch = !termine || hackathon.name.toLowerCase().includes(termine.toLowerCase());
      
      // 2. Filtro Tempo
      let tempoMatch = true;
      if (this.filtroTempo() === 'attivi') {
        tempoMatch = new Date(hackathon.startDate) > dataAttuale;
      } else if (this.filtroTempo() === 'passati') {
        tempoMatch = new Date(hackathon.endDate) < dataAttuale;
      }

      // 3. Filtro Partecipazione
      let partecipazioneMatch = true;
      if (this.filtroPartecipazione() === 'iscritti') {
          if (!currentUser) {
              partecipazioneMatch = false; 
          } else if (this.authService.isStaff()) {
              partecipazioneMatch = hackathon.staff.organizerId === currentUser.idAccount 
                                        || hackathon.staff.judgeId === currentUser.idAccount 
                                          || hackathon.staff.mentors.some(m => m.idAccount === currentUser.idAccount) 
          } else {
              partecipazioneMatch = !!hackathon.teams?.some((team: any) => {
            if (currentUser.idTeam && team.idTeam === currentUser.idTeam) return true;
            if (team.leader && team.leader.idTeamMember === currentUser.idAccount) return true;
            if (team.members && team.members.some((m: any) => m.idTeamMember === currentUser.idAccount)) return true;
            return false;
          });
          }
      }

      return nomeMatch && tempoMatch && partecipazioneMatch;

    }).sort((a, b) => {
        switch (this.ordinamento()) {
          case 'data-asc':
            return new Date(a.startDate).getTime() - new Date(b.startDate).getTime();
          case 'data-desc':
            return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
          case 'az':
            return a.name.localeCompare(b.name);
          case 'za':
            return b.name.localeCompare(a.name);
          default:
            return 0;
        }
      });
  });

  setFiltroPartecipazione(filtro: string) {
    this.filtroPartecipazione.set(filtro);
    this.currentPage.set(1);
  }

  setFiltroTempo(filtro: string) {
    this.filtroTempo.set(filtro);
    this.currentPage.set(1);
  }

  setOrdinamento(ord: string) {
    this.ordinamento.set(ord);
    this.currentPage.set(1);
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('it-IT', {
        day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }

  paginato = computed(() => {
      const start = (this.currentPage() - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filtro().slice(start, end);
  });

  totalPages = computed(() => {
      return Math.ceil(this.filtro().length / this.itemsPerPage);
  });

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
}