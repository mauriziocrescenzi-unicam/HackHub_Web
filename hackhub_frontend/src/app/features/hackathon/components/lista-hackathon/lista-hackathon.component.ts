import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lista-hackathon',
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-hackathon.component.html',
  styleUrls: ['./lista-hackathon.component.scss']
})
export class ListaHackathonComponent {
  currentFilter: string = 'all';
  searchQuery: string = '';

  // Dati di esempio (in futuro li prenderemo dal database con un Service)
  hackathons = [
    { title: 'HackAI 2025', status: 'upcoming', description: 'Build AI apps in 48h.', tags: ['AI', 'Web'], imageUrl: '...' },
    { title: 'Web3 Builders', status: 'ongoing', description: 'Blockchain smart contracts.', tags: ['Web3'], imageUrl: '...' }
  ];

  // Funzione che calcola dinamicamente cosa mostrare in base a filtro e ricerca
  filteredHackathons() {
    return this.hackathons.filter(h => {
      const matchesFilter = this.currentFilter === 'all' || h.status === this.currentFilter;
      const matchesSearch = h.title.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesFilter && matchesSearch;
    });
  }
}