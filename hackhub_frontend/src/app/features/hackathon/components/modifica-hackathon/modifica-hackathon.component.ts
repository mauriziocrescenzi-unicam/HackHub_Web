import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { HackathonService } from '../../service/hackathon.service';
import { Hackathon } from '../../models/singolo-hackathon.model'; 

@Component({
  selector: 'app-modifica-hackathon',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './modifica-hackathon.component.html',
  styleUrl: './modifica-hackathon.component.scss'
})
export class ModificaHackathonComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private hackathonService = inject(HackathonService);

  hackathonId!: number;
  loading = signal<boolean>(true);
  saving = signal<boolean>(false);
  
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  formData: any = {
    title: '',
    description: '',
    imageUrl: '',
    startDate: '',
    endDate: '',
    location: '',
    maxParticipants: 500,
    prize: '',
    status: 'upcoming'
  };

  tags = signal<string[]>([]);
  newTag = signal<string>('');

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.hackathonId = Number(idParam);
      this.loadHackathon();
    } else {
      this.router.navigate(['/hackathons']);
    }
  }

  private loadHackathon() {
    this.hackathonService.getHackathonById(this.hackathonId).subscribe({
      next: (data: Hackathon) => {
        this.formData = { ...data };
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Errore nel caricamento dei dati.');
        this.loading.set(false);
      }
    });
  }

  addTag() {
    const val = this.newTag().trim();
    if (!val || this.tags().includes(val)) {
      this.newTag.set('');
      return;
    }
    this.tags.update(t => [...t, val]);
    this.newTag.set('');
  }

  removeTag(tagToRemove: string) {
    this.tags.update(t => t.filter(tag => tag !== tagToRemove));
  }

  handleTagKey(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.addTag();
    }
  }

  onSubmit() {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (this.formData.endDate && this.formData.startDate && this.formData.endDate < this.formData.startDate) {
      this.errorMessage.set('La data di fine deve essere successiva alla data di inizio.');
      window.scrollTo({ top: 0, behavior: 'smooth' });
      return;
    }

    this.saving.set(true);

    this.hackathonService.updateHackathon(this.hackathonId, this.formData).subscribe({
      next: () => {
        this.saving.set(false);
        this.successMessage.set('Hackathon aggiornato con successo!');
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('Errore durante il salvataggio.');
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  confirmDelete() {
    if (confirm("Sei sicuro di voler eliminare questo hackathon? L'azione è irreversibile.")) {
      this.hackathonService.deleteHackathon(this.hackathonId).subscribe({
        next: () => this.router.navigate(['/hackathons']),
        error: () => this.errorMessage.set('Errore durante l\'eliminazione.')
      });
    }
  }
}