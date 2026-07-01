import { ChangeDetectorRef, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Team } from '../../model/team.model'; // Verifica che il percorso sia corretto
import { TeamService } from '../../service/team.service';
//
import { AuthService } from '../../../auth/service/auth.service';

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

  constructor(
    private router: Router,
    private teamService: TeamService,
    //private invitationService: InvitationService,
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
          this.router.navigate(['/dashboard']);
        }
      }
    });
  }

  inviteMember(){
    this.errorMessage.set(null);

    if (!this.newMemberEmail()) return;

    // this.invitationService.invite(this.newMemberEmail()).subscribe({
    //   next: () => {
    //     this.successMessage.set('Invito inviato con successo!');
    //     this.newMemberEmail.set('');
    //     setTimeout(() => this.successMessage.set(null), 3000);
    //   },
    //  error: err => {
    //    this.errorMessage.set(err.message || 'Errore durante l\'invio dell\'invito.');
    //  }
    //});
  }
  
  leave(){
    this.errorMessage.set(null);
    if (!confirm('Sei sicuro di voler lasciare il team?')) return;

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
        setTimeout(() => this.successMessage.set(null), 3000);
        this.router.navigate(['/teams']);
      },
      error: err => {
        this.errorMessage.set(err.message);
      }
    });
  }

  private leaveTeamForMember(){
    this.teamService.leaveTeamForMember().subscribe({
      next: () => {
        this.successMessage.set('Hai lasciato il team!');
        this.team.set(null);
        this.authService.updateTeamId(null);
        setTimeout(() => this.successMessage.set(null), 3000);
        this.router.navigate(['/teams']);
      },
      error: err => {
        this.errorMessage.set(err.message);
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
        setTimeout(() => this.successMessage.set(null), 3000);
      },
      error: err => {
        this.errorMessage.set(err.message);
      }
    });
  }

  removeMember(id: number) {
    this.errorMessage.set(null);
    if (!confirm('Vuoi davvero rimuovere questo membro dal team?')) return;

    this.teamService.removeMember(id).subscribe({
      next: () => {
        this.successMessage.set('Membro rimosso con successo.');
        const team = this.team();
        if (team) {
          team.members = (team.members ?? []).filter(m => m.id !== id);
        }
        this.cdr.detectChanges();
        setTimeout(() => this.successMessage.set(null), 3000);
      },
      error: err => {
        this.errorMessage.set(err.message);
      }
    });
  }
}