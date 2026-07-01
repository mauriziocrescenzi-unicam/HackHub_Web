import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Hackathon } from '../../models/hackathon.model';
import { HackathonService } from '../../service/hackathon.service'; // Assicurati che il path sia corretto

@Component({
  selector: 'app-lista-hackathon',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './lista-hackathon.component.html',
  styleUrls: ['./lista-hackathon.component.scss']
})
export class ListaHackathonComponent implements OnInit {
  currentFilter: string = 'all';
  searchQuery: string = '';
  
  // Usiamo un segnale per tenere traccia della lista
  hackathons = signal<Hackathon[]>([]);

  constructor(private hackathonService: HackathonService) {}

  // Sostituisci il tuo OnInit così:
  ngOnInit() {
    console.log("DEBUG: OnInit avviato");
    
    // Ignoriamo il service per un secondo, iniettiamo i dati direttamente
    const datiMock = [
      { id: 1, title: 'Test 1', status: 'upcoming', description: 'Test', tags: ['T'], imageUrl: 'https://via.placeholder.com/150', startDate: '2026-07-01', registrationDeadline: '2026-07-01' },
      { id: 2, title: 'Test 2', status: 'ongoing', description: 'Test', tags: ['T'], imageUrl: 'https://via.placeholder.com/150', startDate: '2026-07-01', registrationDeadline: '2026-07-01' }
    ];
    
    this.hackathons.set(datiMock as Hackathon[]);
    console.log("DEBUG: Dati impostati nel segnale, valore attuale:", this.hackathons());
  }

  filteredHackathons(): Hackathon[] {
    return this.hackathons().filter(h => {
      const matchesFilter = this.currentFilter === 'all' || h.status === this.currentFilter;
      const matchesSearch = h.title.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesFilter && matchesSearch;
    });
  }
}