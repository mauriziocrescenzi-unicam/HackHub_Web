import { Component, inject, OnInit, signal, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterLink } from '@angular/router';
import { SubmissionService } from '../../service/submission.service';
import { SubmissionResponse, SubmitProjectPayload } from '../../model/submission.model';
import { Team } from '../../../team/model/team.model';
import { AuthService } from '../../../auth/service/auth.service';
import { HackathonService } from '../../../hackathon/service/hackathon.service';
import { Hackathon } from '../../../hackathon/models/hackathon.model';
import { ReportService } from '../../../report/service/report.service';
import { Report } from '../../../report/models/report.model';

@Component({
  selector: 'app-submission',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './submission.component.html',
  styleUrl: './submission.component.scss',
})
export class SubmissionComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private submissionService = inject(SubmissionService);
  protected authService = inject(AuthService);
  private reportService = inject(ReportService);

  hackathonId = signal<number | null>(null);
  hackathon = signal<Hackathon | null>(null);

  // --- DATA STATES ---
  submissions = signal<SubmissionResponse[]>([]);
  winner = signal<Team | null>(null);             

  // --- FORM & UI STATES ---
  githubUrl = signal<string>('');
  submittedAt = signal<string>('');
  mySubmissionId = signal<number | null>(null);
  isSubmitting = signal<boolean>(false);
  isUpdating = signal<boolean>(false);
  editMode = signal<boolean>(false);
  editUrlTemp = signal<string>('');

  // --- MESSAGE HANDLING ---
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);
  private successTimeoutId: any;
  private errorTimeoutId: any;

  // --- REACTIVE ROLES ---
  isTeamMember = computed(() => !!this.authService.user()?.idTeam);
  isStaff = computed(() => this.authService.user()?.role === 'STAFF');

  // --- EVALUATION & REPORT ---
  // Popup state
  showReportModal = signal(false);
  showEvaluateModal = signal(false);
  showReportListModal = signal(false);
  showWinnerModal = signal(false);
  activeSubmission = signal<any>(null);

  // Report form
  reportReason = signal('');
  reportDescription = signal('');
  isReporting = signal(false);
  teamReports = signal<Report[]>([]);
  isLoadingReports = signal(false);
  myTeamDisabled = signal<boolean>(false);

  // Evaluate form
  evalScore = signal(7.5);
  evalJudgment = signal('');
  isEvaluating = signal(false);
  myScore = signal<number | null>(null);
  myJudgment = signal<string | null>(null);

  constructor(private hackathonService: HackathonService) {
    effect(() => {
      const user = this.authService.user();
      const id = this.hackathonId();

      if (user && id) {
        if (this.isStaff()) {
          this.loadStaffDashboard(id);
        }
        if (this.isTeamMember()) {
          this.checkMySubmission();
        }
      }
    }, { allowSignalWrites: true });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      const numId = +id;
      this.hackathonId.set(numId);
    }
    
    this.hackathonService.getById(this.hackathonId()!).subscribe({
      next: (hackathon) => {
        this.hackathon.set(hackathon);
        
        this.checkWinner(this.hackathonId()!); 
      },
      error: (err: any) => this.showError(err)
    });
  }

  // --- GENERAL METHODS ---
  checkWinner(idHackathon: number) {
    this.hackathonService.getWinner(idHackathon).subscribe({
      next: (winnerTeam) => {
        this.winner.set(winnerTeam);
      },
      error: (err: any) => {
        if (err.status === 404) {
           this.winner.set(null);
        } else {
           this.showError(err);
        }
      }
    });
  }

  // --- STAFF METHODS ---
  declareWinner() { 
    const id = this.hackathonId();
    if (!id) return;

    this.submissionService.proclaimWinner(id).subscribe({
      next: (message) => {
        // Show success message from backend
        this.showSuccess(message || 'Winner proclaimed successfully!');
        
        this.checkWinner(id);
      },
      error: (err: any) => this.showError(err)
    });
  }

  // --- HELPER METHODS FOR MESSAGES (5 seconds) ---
  private showSuccess(message: string) {
    this.successMessage.set(message);
    if (this.successTimeoutId) clearTimeout(this.successTimeoutId);
    this.successTimeoutId = setTimeout(() => this.successMessage.set(null), 5000);
  }

  private showError(err: any) {
    const backendMessage = err?.error?.message || err?.error || err?.message || 'An unexpected error occurred';
    this.errorMessage.set(backendMessage);

    if (this.errorTimeoutId) clearTimeout(this.errorTimeoutId);
    this.errorTimeoutId = setTimeout(() => this.errorMessage.set(null), 5000);
  }

  // --- STAFF METHODS ---
  loadStaffDashboard(idHackathon: number) {
    this.submissionService.getSubmissionsByHackathon(idHackathon).subscribe({
      next: (data) => {
        this.submissions.set(data);
      },
      error: (err: any) => this.showError(err)
    });
  }

  // --- TEAM METHODS ---
  checkMySubmission() {
    this.submissionService.getSubmissionForTeam(this.hackathonId()!).subscribe({
      next: (submission) => {
        this.githubUrl.set(submission.repositoryUrl ?? '');
        this.mySubmissionId.set(submission.id ?? null);
        this.submittedAt.set(submission.submittedAt ?? '');
        this.myTeamDisabled.set(submission.teamDisabled ?? false);
        this.myScore.set(submission.score ?? null);
        this.myJudgment.set(submission.writtenJudgment ?? null); 
        this.isUpdating.set(false);
      },
      error: (err: any) => {
        if (err.status === 400 || err.status === 404) {
          this.isUpdating.set(false);
        }
      }
    });
  }

  submitGithubRepo() {
    const id = this.hackathonId();
    if (!id || !this.githubUrl()) return;

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const payload: SubmitProjectPayload = { idHackathon: id, type: 'github', source: this.githubUrl() };

    this.submissionService.submitProject(payload).subscribe({
      next: () => {
        this.showSuccess('GitHub repository linked successfully!');
        this.checkMySubmission(); 
        this.isSubmitting.set(false);
      },
      error: (err: any) => {
        this.showError(err);
        this.isSubmitting.set(false);
      }
    });
  }

  updateCommit() {
    const subId = this.mySubmissionId();
    if (!subId) return;

    this.isUpdating.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.submissionService.updateSubmission(subId).subscribe({
      next: () => {
        this.showSuccess('Latest commit fetched from the server successfully!');
        this.isUpdating.set(false);
      },
      error: (err: any) => {
        this.showError(err);
        this.isUpdating.set(false);
      }
    });
  }

  openReport(sub: any) {
    this.activeSubmission.set(sub);
    this.reportReason.set('');
    this.reportDescription.set('');
    this.showReportModal.set(true);
  }

  openEvaluate(sub: any) {
    this.activeSubmission.set(sub);
    this.evalScore.set(7.5);
    this.evalJudgment.set('');
    this.showEvaluateModal.set(true);
  }

  openReportList(sub: SubmissionResponse) {
    this.activeSubmission.set(sub);
    this.teamReports.set([]);
    this.isLoadingReports.set(true);
    this.showReportListModal.set(true);

    this.reportService.getReportsByTeam(this.hackathonId()!, sub.idTeam).subscribe({
      next: (reports: any) => {
        this.teamReports.set(reports);
        this.isLoadingReports.set(false);
      },
      error: (err: any) => {
        this.showError(err);
        this.isLoadingReports.set(false);
      }
    });
  }

  closeModals() {
    this.showReportModal.set(false);
    this.showEvaluateModal.set(false);
    this.showReportListModal.set(false);
    this.activeSubmission.set(null);
  }

  submitReport() {
    this.isReporting.set(true);
    const payload = {
      idTeam: this.activeSubmission()?.idTeam,
      idHackathon: this.hackathonId()!,
      reason: this.reportReason(),
      description: this.reportDescription()
    };
    this.reportService.reportTeam(payload).subscribe({
      next: () => {
        this.showSuccess('Report submitted successfully.');
        this.closeModals();
        this.isReporting.set(false);
      },
      error: (err: any) => {
        this.showError(err);
        this.isReporting.set(false);
      }
    });
  }

  submitEvaluation() {
    this.isEvaluating.set(true);
    this.submissionService.evaluateSubmission(
      this.activeSubmission()?.id,
      { writtenJudgment: this.evalJudgment(), score: this.evalScore() }
    ).subscribe({
      next: () => {
        this.showSuccess('Evaluation submitted successfully.');
        this.closeModals();
        this.isEvaluating.set(false);
        this.loadStaffDashboard(this.hackathonId()!);
      },
      error: (err: any) => {
        this.showError(err);
        this.isEvaluating.set(false);
      }
    });
  }

  toggleTeamStatus(sub: any) {
    const newStatus = !sub.teamDisabled;
    this.reportService.reportManagement(
      this.hackathonId()!,
      sub.idTeam,
      newStatus
    ).subscribe({
      next: () => {
        this.showSuccess(`Team ${newStatus ? 'disabled' : 'enabled'} successfully.`);
        this.closeModals();
        this.loadStaffDashboard(this.hackathonId()!);
      },
      error: (err: any) => this.showError(err)
    });
  }

  startEdit() {
    this.editUrlTemp.set(this.githubUrl());
    this.editMode.set(true);
  }

  saveEdit() {
    const newUrl = this.editUrlTemp();
    const id = this.hackathonId();

    if (!id || !newUrl || newUrl.trim() === '') {
      this.errorMessage.set('Please enter a valid repository URL.');
      return;
    }

    const payload: SubmitProjectPayload = {
      idHackathon: id, 
      type: 'github', 
      source: newUrl
    };

    this.isSubmitting.set(true);

    this.submissionService.submitProject(payload).subscribe({
      next: (response) => {
        this.showSuccess('Project URL updated successfully.');
        this.githubUrl.set(newUrl); 
        this.editMode.set(false);
        this.isSubmitting.set(false);
      },
      error: (err: any) => {
        this.showError('Error saving the new URL.');
        console.error('Error saving edit:', err);
        this.isSubmitting.set(false);
      }
    });
  }

  cancelEdit() {
    this.editMode.set(false);
    this.editUrlTemp.set(this.githubUrl());
  }

  isOrganizer = computed(() => {
    const user = this.authService.user();
    const hackathon = this.hackathon();
    if (!user || !hackathon?.staff) return false;
    return hackathon.staff.organizerId === user.idAccount;
  });

  isJudge = computed(() => {
    const user = this.authService.user();
    const hackathon = this.hackathon();
    if (!user || !hackathon?.staff) return false;
    return hackathon.staff.judgeId === user.idAccount;
  });

  isMentor = computed(() => {
    const user = this.authService.user();
    const hackathon = this.hackathon();
    if (!user || !hackathon?.staff) return false;
    return hackathon.staff.mentors?.some((m: any) => m.idAccount === user.idAccount);
  });

  allEvaluated = computed(() => {
    const active = this.submissions().filter(s => !s.teamDisabled);
    return active.length > 0 && active.every(s => (s.score ?? 0) > 0);
  });

  canDoAction = computed(() => {
    const status = this.hackathon()?.status;
    if (this.isJudge() && status === 'EVALUATION') return true;
    if (this.isMentor() && status === 'ONGOING') return true;
    if (this.isOrganizer() && status === 'EVALUATION') return true;
    return false;
  });
}