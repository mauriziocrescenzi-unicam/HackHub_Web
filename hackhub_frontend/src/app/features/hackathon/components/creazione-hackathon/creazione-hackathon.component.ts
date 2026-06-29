import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HackathonService } from '../../service/hackathon.service';
@Component({
  selector: 'app-creazione-hackathon',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './creazione-hackathon.component.html',
  styleUrl: './creazione-hackathon.component.scss'
})
export class CreazioneHackathonComponent {
  hackathonForm: FormGroup;
  tags: string[] = [];
  
  // Variabili per gestire lo stato della UI (alert e spinner)
  isSubmitting: boolean = false;
  showSuccess: boolean = false;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private hackathonService: HackathonService,
    private router: Router
  ) {
    this.hackathonForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      imageUrl: [''],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      location: ['', Validators.required],
      maxParticipants: [null, [Validators.required, Validators.min(1)]],
      prize: ['', Validators.required],
      status: ['', Validators.required]
    });
  }

  addTag(tagInput: HTMLInputElement) {
    const value = tagInput.value.trim();
    if (value && !this.tags.includes(value)) {
      this.tags.push(value);
      tagInput.value = '';
    }
  }

  removeTag(tag: string) {
    this.tags = this.tags.filter(t => t !== tag);
  }

  onSubmit() {
    this.errorMessage = '';
    
    if (this.hackathonForm.valid) {
      // Controllo date
      const start = this.hackathonForm.get('startDate')?.value;
      const end = this.hackathonForm.get('endDate')?.value;
      if (start && end && new Date(end) < new Date(start)) {
        this.errorMessage = 'La data di fine deve essere successiva alla data di inizio.';
        return;
      }

      this.isSubmitting = true;
      const payload = {
        ...this.hackathonForm.value,
        tags: this.tags
      };

      // Chiamata reale al backend Spring Boot
      this.hackathonService.createHackathon(payload).subscribe({
        next: (response : any) => {
          this.isSubmitting = false;
          this.showSuccess = true;
          
          // Aspetta 1.5 secondi per mostrare l'alert di successo, poi torna alla lista
          setTimeout(() => {
            this.router.navigate(['/hackathons']);
          }, 1500);
        },
        error: (error : any) => {
          this.isSubmitting = false;
          this.errorMessage = 'Errore durante la creazione dell\'hackathon. Riprova.';
          console.error('Errore backend:', error);
        }
      });
      
    } else {
      this.hackathonForm.markAllAsTouched();
      this.errorMessage = 'Compila tutti i campi obbligatori correttamente.';
    }
  }
}