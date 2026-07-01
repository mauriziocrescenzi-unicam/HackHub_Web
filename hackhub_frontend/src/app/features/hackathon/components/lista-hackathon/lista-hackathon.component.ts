import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Hackathon } from '../../models/hackathon.model'; // Importa l'interfaccia

@Component({
  selector: 'app-lista-hackathon',
  standalone: true, // Assicurati di avere questo
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './lista-hackathon.component.html',
  styleUrls: ['./lista-hackathon.component.scss']
})
export class ListaHackathonComponent {
  currentFilter: string = 'all';
  searchQuery: string = '';

  // Tipizziamo l'array usando l'interfaccia Hackathon[]
  hackathons: Hackathon[] = [
    { 
      id: 1, // Aggiunto l'id
      title: 'HackAI 2025', 
      status: 'upcoming', 
      description: 'Build AI apps in 48h.', 
      tags: ['AI', 'Web'], 
      imageUrl: '...',
      startDate: '2026-08-01',
      registrationDeadline: '2026-07-25'
    },
    { 
      id: 2, // Aggiunto l'id
      title: 'Web3 Builders', 
      status: 'ongoing', 
      description: 'Blockchain smart contracts.', 
      tags: ['Web3'], 
      imageUrl: '...',
      startDate: '2026-07-15',
      registrationDeadline: '2026-07-10'
    }
  ];

  // La funzione ora restituisce esplicitamente Hackathon[]
  filteredHackathons(): Hackathon[] {
    return this.hackathons.filter(h => {
      const matchesFilter = this.currentFilter === 'all' || h.status === this.currentFilter;
      const matchesSearch = h.title.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesFilter && matchesSearch;
    });
  }
}