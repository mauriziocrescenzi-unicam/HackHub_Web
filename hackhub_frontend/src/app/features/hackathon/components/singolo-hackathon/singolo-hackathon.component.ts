import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { AuthService } from '../../../auth/service/auth.service';
import { SingoloHackathonService } from '../../service/singolo-hackathon.service';
import { SingoloHackathon } from '../../models/singolo-hackathon.model'; 

@Component({
  selector: 'app-singolo-hackathon',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './singolo-hackathon.component.html',
  styleUrl: './singolo-hackathon.component.scss',
})
export class SingoloHackathonComponent implements OnInit {
  
  private route = inject(ActivatedRoute);
  private hackathonService = inject(SingoloHackathonService);
  private authService = inject(AuthService);

  hackathon = signal<SingoloHackathon | null>(null);
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
  
  this.hackathonService.getHackathonById(id).subscribe({
    next: (data) => {
      this.hackathon.set(data);
      this.loading.set(false);
    },
    error: (err) => {
      console.error("Errore, carico dati mock:", err);
      // FORZATURA: ignoriamo l'errore e carichiamo dati finti
      this.hackathon.set({
        id: id,
        title: "Hackathon Dimostrativo (Dati Mock)",
        description: "Il backend non risponde, ma il frontend è operativo!",
        startDate: "2026-07-20",
        registrationDeadline: "2026-07-15",
        status: "upcoming",
        tags: ["Test", "Mock"],
        imageUrl: "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=1400&q=80"
      });
      this.loading.set(false); // Smette di caricare
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