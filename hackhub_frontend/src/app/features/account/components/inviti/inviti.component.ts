import { CommonModule } from '@angular/common';
import { Component, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InvitiService } from '../../service/inviti.service'; // Aggiornato
import { Invito } from '../../models/invito.model';          // Aggiornato
import { AuthService } from '../../../auth/service/auth.service';

@Component({
  selector: 'app-inviti',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inviti.component.html',
  styleUrl: './inviti.component.scss',
})
export class InvitiComponent implements OnInit {
  inviti = signal<Invito[]>([]);
  isLoading = signal<boolean>(true);
  
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);
  private messageTimeout: any;

  constructor(private invitiService: InvitiService, private authService: AuthService) {}

  ngOnInit() {
    this.loadInviti();
  }

  loadInviti() {
    this.isLoading.set(true);
    this.invitiService.getAll().subscribe({
      next: (data) => {
        this.inviti.set(data.filter(inv => inv.state === 'PENDING').map(inv => ({
          idInvitation: inv.idInvitation,
          state: inv.state,
          invitationDate: inv.invitationDate,
          idInvitedAccount: inv.idInvitedAccount,
          invitedAccountEmail: inv.invitedAccountEmail,
          idInvitingTeam: inv.idInvitingTeam,
          invitingTeamName: inv.invitingTeamName,
          senderName: inv.invitingTeamName
        })));
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.errorMessage.set('Errore nel caricamento degli inviti.');
        this.isLoading.set(false);
        this.clearMessagesAfterDelay();
      }
    });
  }

  respond(idInvito: number, accept: boolean) {
    this.invitiService.respond(idInvito, accept).subscribe({
      next: (res: any) => {
        if (accept && res.idInvitingTeam) {
          this.authService.updateTeamId(res.idInvitingTeam);
          this.authService.updateTeamName(res.invitingTeamName);
        }
        this.handleResponse(idInvito, accept ? 'accettato' : 'rifiutato');
      },
      error: () => {
        this.errorMessage.set("Errore nella gestione dell'invito.");
        this.clearMessagesAfterDelay();
      }
    });
  }

  private handleResponse(id: number, action: 'accettato' | 'rifiutato') {
    this.successMessage.set(`Invito ${action} con successo!`);
    this.errorMessage.set(null);
    this.clearMessagesAfterDelay();
    this.inviti.update(current => current.filter(inv => inv.idInvitation !== id));
  }

  private clearMessagesAfterDelay() {
    if (this.messageTimeout) clearTimeout(this.messageTimeout);
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
      this.successMessage.set(null);
    }, 5000);
  }
}