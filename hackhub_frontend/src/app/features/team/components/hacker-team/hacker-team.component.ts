import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Team } from '../../model/team.model'; 
import { TeamService } from '../../service/team.service';
import { AuthService } from '../../../auth/service/auth.service';
import { InvitiService } from '../../../account/service/inviti.service'; 
import { ModaleConferma } from '../../model/modale-conferma.model';

@Component({
  selector: 'app-hacker-team',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hacker-team.component.html',
  styleUrls: ['./hacker-team.component.scss']
})
export class HackerTeamComponent implements OnInit {
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  team = signal<Team | null>(null); 
  loading = signal(true);

  newMemberEmail = signal('');
  showModifyForm = false;
  name = '';
  description = '';
  isLeader = false;  

  confirmDialog = signal<ModaleConferma | null>(null);
  private messageTimeout: any;

  constructor(
    private router: Router,
    private teamService: TeamService,
    private invitiService: InvitiService, // Utilizzo di InvitiService
    protected authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const teamId = this.authService.teamId;
    if (!teamId) {
      this.router.navigate(['/teams']);
      return;
    }

    this.teamService.getTeamById(teamId).subscribe({
      next: (data) => {
        this.team.set(data);
        this.loading.set(false);

        const currentUserId = this.authService.currentUser?.idAccount;
        this.isLeader = currentUserId !== null && data.leader?.id === currentUserId;

        this.name = this.team()!.name;
        this.description = this.team()!.description;
      },
      error: () => {
        if(this.authService.isUser()){
          this.router.navigate(['/teams']);
        } else {
          this.router.navigate(['/home']);
        }
      }
    });
  }

  inviteMember(){
    this.errorMessage.set(null);
    if (!this.newMemberEmail()) return;

    this.invitiService.invite(this.newMemberEmail()).subscribe({
      next: () => {
        this.successMessage.set('Invito inviato con successo!');
        this.newMemberEmail.set('');
        this.clearMessagesAfterDelay();
      },
      error: (err: any) => {
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante l\'invio dell\'invito.');
        this.clearMessagesAfterDelay();
      }
    });
  }
  
  leave(){
    this.errorMessage.set(null);
    if(this.isLeader) {
      this.leaveTeamForLeader();
    } else {
      this.leaveTeamForMember();
    }
  }

  private leaveTeamForLeader(){
    this.teamService.leaveTeamForLeader().subscribe({
      next: () => {
        this.successMessage.set('Hai lasciato il team!');
        this.team.set(null);
        this.authService.updateTeamId(null);
        setTimeout(() => {
          this.successMessage.set(null);
          this.router.navigate(['/teams']);
        }, 1500);
      },
      error: (err: any) => {
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante l\'abbandono del team.');
        this.clearMessagesAfterDelay();
      }
    });
  }

  private leaveTeamForMember(){
    this.teamService.leaveTeamForMember().subscribe({
      next: () => {
        this.successMessage.set('Hai lasciato il team!');
        this.team.set(null);
        this.authService.updateTeamId(null);
        setTimeout(() => {
          this.successMessage.set(null);
          this.router.navigate(['/teams']);
        }, 1500);
      },
      error: (err: any) => {
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante l\'abbandono del team.');
        this.clearMessagesAfterDelay();
      }
    });
  }

  modifyTeam(){
    this.errorMessage.set(null);
    if (!this.name.trim()) {
      this.errorMessage.set('Il nome del team è obbligatorio.');
      return;
    }

    this.teamService.updateTeam(this.name, this.description).subscribe({
      next: (team) => {
        this.successMessage.set('Team aggiornato con successo!');
        this.team.set(team);
        this.name = team.name;
        this.description = team.description;
        this.showModifyForm = false;
        this.cdr.detectChanges();
        this.clearMessagesAfterDelay();
      },
      error: (err: any) => {
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante l\'aggiornamento del team.');
        this.clearMessagesAfterDelay();
      }
    });
  }

  removeMember(id: number) {
    this.errorMessage.set(null);

    this.teamService.removeMember(id).subscribe({
      next: () => {
        this.successMessage.set('Membro rimosso con successo.');
        const team = this.team();
        if (team) {
          team.members = (team.members ?? []).filter(m => m.id !== id);
        }
        this.cdr.detectChanges();
        this.clearMessagesAfterDelay();
      },
      error: (err: any) => {
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante la rimozione del membro.');
        this.clearMessagesAfterDelay();
      }
    });
  }

  // --- LOGICA MODALE DI CONFERMA ---

  closeDialog() {
    this.confirmDialog.set(null);
  }

  askLeave() {
    const hasMembers = (this.team()?.members?.length ?? 0) > 0;

    if (this.isLeader) {
      this.confirmDialog.set({
        type: 'danger',
        icon: 'fa-solid fa-arrow-right-from-bracket',
        title: 'Abbandona il team',
        message: 'Stai per lasciare il team di cui sei Leader.',
        warning: hasMembers
          ? 'Il ruolo di Leader verrà assegnato casualmente a un altro membro del team.'
          : 'Non ci sono altri membri: se abbandoni il team, verrà eliminato definitivamente.',
        confirmLabel: 'Sì, abbandona',
        onConfirm: () => {
          this.closeDialog();
          this.leave();
        }
      });
    } else {
      this.confirmDialog.set({
        type: 'danger',
        icon: 'fa-solid fa-arrow-right-from-bracket',
        title: 'Abbandona il team',
        message: 'Sei sicuro di voler lasciare il team? Potrai unirti a uno nuovo in qualsiasi momento.',
        confirmLabel: 'Sì, abbandona',
        onConfirm: () => {
          this.closeDialog();
          this.leave();
        }
      });
    }
  }

  askRemoveMember(member: any) {
    this.confirmDialog.set({
      type: 'danger',
      icon: 'fa-solid fa-user-xmark',
      title: 'Rimuovi membro',
      message: `Stai per rimuovere ${member.nickname} dal team. Potrai inviargli un nuovo invito in futuro.`,
      confirmLabel: 'Rimuovi',
      onConfirm: () => {
        this.closeDialog();
        this.removeMember(member.id);
      }
    });
  }

  private clearMessagesAfterDelay() {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
      this.successMessage.set(null);
    }, 5000);
  }
}