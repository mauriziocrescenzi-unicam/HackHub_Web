import { Component, OnInit, signal, inject, computed } from '@angular/core';
import { HackathonService } from '../../../hackathon/service/hackathon.service';
import { Hackathon } from '../../../hackathon/models/hackathon.model';
import { RouterLink } from "@angular/router";
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../features/auth/service/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [RouterLink, FormsModule]
})
export class HomeComponent implements OnInit {
  private hackathonService = inject(HackathonService);
  private authService = inject(AuthService);
  
  isAuthenticated = computed(() => !!this.authService.user());

  hackathon = signal<Hackathon[]>([]);
  errorMessage = signal<string | null>(null);
  search = '';

  constructor() { }

  ngOnInit(): void {
    this.caricaHackathon();
  }

  caricaHackathon(): void {
    this.hackathonService.getAll().subscribe({
      next: (data) => {
        const ora = new Date();
        const ordinati = data
          .filter(h => new Date(h.startDate) > ora)
          .sort((a, b) => new Date(a.startDate).getTime() - new Date(b.startDate).getTime())
          .slice(0, 3); 
        this.hackathon.set(ordinati);
      },
      error: (err) => {
        this.errorMessage.set(err.message);
      }
    });
  }

  filtro(): Hackathon[] {
    if (!this.hackathon()) return [];
    return this.hackathon()!.filter(h => {
      return !this.search || h.name.toLowerCase().includes(this.search.toLowerCase());
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('it-IT', {
        day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }
}