import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { AuthService } from '../../../auth/service/auth.service';
import { HackathonService } from '../../service/hackathon.service';
import { Hackathon } from '../../models/hackathon.model'; 

@Component({
  selector: 'app-singolo-hackathon',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './singolo-hackathon.component.html',
  styleUrl: './singolo-hackathon.component.scss',
})
export class SingoloHackathonComponent implements OnInit {
  
  private route = inject(ActivatedRoute);
  private hackathonService = inject(HackathonService);
  private authService = inject(AuthService);

  hackathon = signal<Hackathon | null>(null);
  loading = signal<boolean>(true);

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      const hackathonId = Number(idParam);
      this.loadHackathon(hackathonId);
    } else {
      this.loading.set(false);
    }
  }

private loadHackathon(id: number) {
  this.loading.set(true);
  this.hackathon.set(null); // <--- Resetta i dati precedenti
  
  this.hackathonService.getHackathonById(id).subscribe({
    next: (data) => {
      this.hackathon.set(data);
      this.loading.set(false);
    },
    error: (err) => {
      console.error("Errore durante il caricamento dell'hackathon:", err);
      this.loading.set(false);
    }
  });
}

  isUser(): boolean {
    return this.authService.isUser();
  }

  hasTeam(): boolean {
    return !!this.authService.teamId; 
  }
}