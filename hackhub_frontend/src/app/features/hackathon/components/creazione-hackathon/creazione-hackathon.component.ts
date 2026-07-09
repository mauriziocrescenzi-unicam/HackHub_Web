import { Component, OnInit, signal } from '@angular/core';
import { AuthService } from '../../../auth/service/auth.service';
import { FormsModule } from '@angular/forms';
import { HackathonService } from '../../service/hackathon.service';
import { StaffService } from '../../service/staff.service';
import { CreazioneHackathon } from '../../models/creazione-hackathon.model';
import { Rule } from '../../models/rule.model';
import { Router, RouterLink } from '@angular/router';
import { Account } from '../../../account/models/account.model';

@Component({
  selector: 'app-create-hackathon',
  imports: [FormsModule, RouterLink],
  templateUrl: './creazione-hackathon.component.html',
  styleUrl: './creazione-hackathon.component.scss',
})
export class CreazioneHackathonComponent implements OnInit {
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  minDate!: string;
  accounts: Account[] = [];
  regole: Rule[] = [];
  giudiceSelezionato: Account | null = null;
  mentoriSelezionati: Account[] = [];
  regoleSelezionate: Rule[] = [];

  // campi di ricerca e filtri
  searchGiudice: string = '';
  searchMentore: string = '';
  searchRegola: string = '';

  // dati filtrati per i dropdown
  giudiciFiltrati: Account[] = [];
  mentoriFiltrati: Account[] = [];
  regoleFiltrate: Rule[] = [];
  showGiudici = signal(false);
  showMentori = signal(false);
  showRegole = signal(false);

  // dati del form
  hackathonData: CreazioneHackathon = {
    name: '',
    location: '',
    prize: 0,
    maxTeamMembers: 1,
    maxNumberTeams: 1,
    startDate: '',
    endDate: '',
    judgeEmail: '',
    mentorEmails: [],
    idRules: [],
  };

  constructor(
    public authService: AuthService,
    private HackathonService: HackathonService,
    private router: Router,
    private StaffService: StaffService,
  ) {
    const user = this.authService.user();
  }

  ngOnInit(): void {
    const now = new Date();
    const pad = (n: number) => String(n).padStart(2, '0');

    // Helper function per formattare la data nel formato richiesto da datetime-local
    const formatForInput = (date: Date) => {
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    };

    // La data minima selezionabile nel calendario è "Oggi"
    this.minDate = formatForInput(now);

    // Imposta la data di INIZIO a +1 settimana da oggi
    const start = new Date();
    start.setDate(now.getDate() + 7);
    this.hackathonData.startDate = formatForInput(start);

    // Imposta la data di FINE a +1 settimana dalla data di inizio (+14 giorni totali da oggi)
    const end = new Date(start);
    end.setDate(start.getDate() + 7);
    this.hackathonData.endDate = formatForInput(end);

    // Caricamento dati esistenti (Rules e Staff)
    this.HackathonService.getRules().subscribe((data) => {
      this.regole = data;
    });
    this.StaffService.getStaff().subscribe((data) => {
      this.accounts = data;
    });
  }

  onStartDateChange() {
    const start = new Date(this.hackathonData.startDate);
    const end = new Date(this.hackathonData.endDate);

    // Se la data di fine è precedente o uguale alla data di inizio, aggiorna la data di fine a +7 giorni dalla nuova data di inizio
    if (end <= start) {
      const newEnd = new Date(start);
      newEnd.setDate(start.getDate() + 7); // Sposta la fine a +7 giorni dalla nuova data inizio

      const pad = (n: number) => String(n).padStart(2, '0');
      this.hackathonData.endDate = `${newEnd.getFullYear()}-${pad(newEnd.getMonth() + 1)}-${pad(newEnd.getDate())}T${pad(newEnd.getHours())}:${pad(newEnd.getMinutes())}`;
    }
  }

  saveChanges() {
    // Validazione dei campi
    if (
      !this.hackathonData.name ||
      !this.hackathonData.location ||
      !this.hackathonData.startDate ||
      !this.hackathonData.endDate
    ) {
      this.errorMessage.set('Please fill in all required fields');
      return;
    }
    if (
      this.hackathonData.prize < 0 ||
      this.hackathonData.maxNumberTeams < 1 ||
      this.hackathonData.maxTeamMembers < 1
    ) {
      this.errorMessage.set('Invalid numeric values');
      return;
    }
    if (this.giudiceSelezionato === null) {
      this.errorMessage.set('Please select a judge');
      return;
    }
    if (this.mentoriSelezionati.length === 0) {
      this.errorMessage.set('Please select at least one mentor');
      return;
    }

    // Prepara i dati da inviare al backend
    this.hackathonData.judgeEmail = this.giudiceSelezionato.email;
    this.hackathonData.mentorEmails = this.mentoriSelezionati.map((m) => m.email);
    this.hackathonData.idRules = this.regoleSelezionate.map((r) => r.id);
    // aggiunge i secondi alle date
    const data = {
      ...this.hackathonData,
      startDate: this.hackathonData.startDate + ':00',
      endDate: this.hackathonData.endDate + ':00',
    };

    // Invia i dati al backend per creare l'hackathon e reindirizza alla pagina del nuovo hackathon
    this.HackathonService.createHackathon(data).subscribe({
      next: (res) => {
        this.successMessage.set('Hackathon created successfully!');
        this.errorMessage.set(null);
        setTimeout(() => {
          this.router.navigate(['/hackathons', res.id], { replaceUrl: true });
        }, 1500);
      },
      error: (err) => {
        this.errorMessage.set('Error creating the hackathon');
        this.successMessage.set(null);
      },
    });
  }

  cancelEdit() {
    // Resetta i dati del form
    this.hackathonData = {
      name: '',
      location: '',
      prize: 0,
      maxTeamMembers: 1,
      maxNumberTeams: 1,
      startDate: this.minDate,
      endDate: this.minDate,
      judgeEmail: '',
      mentorEmails: [],
      idRules: [],
    };
    this.giudiceSelezionato = null;
    this.mentoriSelezionati = [];
    this.regoleSelezionate = [];
    this.showGiudici.set(false);
    this.showMentori.set(false);
    this.showRegole.set(false);
    this.searchGiudice = '';
    this.searchMentore = '';
    this.searchRegola = '';
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  // ---- GIUDICI ----
  filtraGiudici() {
    const val = this.searchGiudice.toLowerCase();
    this.giudiciFiltrati = this.accounts.filter(
      (i) => val === '' || i.email.toLowerCase().includes(val),
    );
    this.showGiudici.set(true);
  }

  selezionaGiudice(item: Account) {
    this.searchGiudice = item.email;
    this.giudiceSelezionato = item;
    this.showGiudici.set(false);
  }

  // ---- MENTORI ----
  filtraMentori() {
    const val = this.searchMentore.toLowerCase();
    this.mentoriFiltrati = this.accounts.filter(
      (i) =>
        (val === '' || i.email.toLowerCase().includes(val) || i.name.toLowerCase().includes(val)) &&
        !this.mentoriSelezionati.find((m) => m.email === i.email),
    );
    this.showMentori.set(true);
  }

  selezionaMentore(item: Account) {
    this.mentoriSelezionati.push(item);
    this.searchMentore = '';
    this.showMentori.set(false);
  }

  rimuoviMentore(item: Account) {
    this.mentoriSelezionati = this.mentoriSelezionati.filter((m) => m.email !== item.email);
    const val = this.searchMentore.toLowerCase();
    this.mentoriFiltrati = this.accounts.filter(
      (i) =>
        (val === '' || i.email.toLowerCase().includes(val) || i.name.toLowerCase().includes(val)) &&
        !this.mentoriSelezionati.find((m) => m.email === i.email),
    );
  }

  // ---- REGOLE ----
  filtraRegole() {
    const val = this.searchRegola.toLowerCase();
    this.regoleFiltrate = this.regole.filter(
      (r) =>
        (val === '' || r.description.toLowerCase().includes(val)) &&
        !this.regoleSelezionate.find((s) => s.id === r.id),
    );
    this.showRegole.set(true);
  }

  selezionaRegola(item: Rule) {
    this.regoleSelezionate.push(item);
    this.searchRegola = '';
    this.regoleFiltrate = this.regoleFiltrate.filter((r) => r.id !== item.id);
    this.showRegole.set(false);
  }

  rimuoviRegola(item: Rule) {
    this.regoleSelezionate = this.regoleSelezionate.filter((r) => r.id !== item.id);

    const val = this.searchRegola.toLowerCase();
    this.regoleFiltrate = this.regole.filter(
      (r) =>
        (val === '' || r.description.toLowerCase().includes(val)) &&
        !this.regoleSelezionate.find((s) => s.id === r.id),
    );
  }

  chiudiDropdown(tipo: string) {
    setTimeout(() => {
      if (tipo === 'giudice') this.showGiudici.set(false);
      if (tipo === 'mentore') this.showMentori.set(false);
      if (tipo === 'regola') this.showRegole.set(false);
    }, 150);
  }
}
