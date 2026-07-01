import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../auth/service/auth.service';
import { creaHackathonService } from '../../service/creazione-hackathon.service';
import { Rule } from '../../models/rule.model';
import { Staff } from '../../models/staff.model';

@Component({
  selector: 'app-creazione-hackathon',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './creazione-hackathon.component.html',
  styleUrls: ['./creazione-hackathon.component.scss'],
})
export class CreazioneHackathonComponent implements OnInit {
  // Messaggi di errore/successo
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  // Data minima per i campi data (oggi)
  minDate!: string;
  
  // Liste per giudici, mentori e regole
  giudiceSelezionato: Staff | null = null;
  mentoriSelezionati: Staff[] = [];
  items: Staff[] = [];
  regole: Rule[] = [];
  regoleSelezionate: Rule[] = [];

  // Variabili per la ricerca e i risultati filtrati
  searchGiudice: string = '';
  searchMentore: string = '';
  searchRegola: string = '';
  
  // Risultati filtrati
  giudiciFiltrati: Staff[] = [];
  mentoriFiltrati: Staff[] = [];
  regoleFiltrate: Rule[] = [];
  
  showGiudici = signal(false);
  showMentori = signal(false);
  showRegole = signal(false);

  // Dati del form
  hackathonData = {
    nome: '',
    localita: '',
    startDate: '',
    endDate: '',
    premio: 0,
    maxParticipants: 0,
    maxParticipantsPerTeam: 0,
  };

  constructor(
    public authService: AuthService, 
    private creaHackathonService: creaHackathonService, 
    private router: Router
  ) {
    // const user = this.authService.user();
  }

  ngOnInit(): void {
    // Imposta la data minima (oggi) per i campi data
    const now = new Date();
    const pad = (n: number) => String(n).padStart(2, '0');
    const localDateTime = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;

    this.hackathonData.startDate = localDateTime;
    this.minDate = localDateTime;
    this.hackathonData.endDate = localDateTime;
    
    // Carica regole e staff per i dropdown
    this.creaHackathonService.getRules().subscribe(data => {
      this.regole = data;
    });
    
    this.creaHackathonService.getStaff().subscribe(data => {
      this.items = data;
    });
  }

  saveChanges() {
    // Validazione dei campi
    if (!this.hackathonData.nome || !this.hackathonData.localita || !this.hackathonData.startDate || !this.hackathonData.endDate) {
      this.errorMessage.set("Compila tutti i campi obbligatori");
      return;
    }
    
    if (this.hackathonData.premio < 0 || this.hackathonData.maxParticipants < 1 || this.hackathonData.maxParticipantsPerTeam < 1) {
      this.errorMessage.set("Valori numerici non validi");
      return;
    }
    
    if (this.giudiceSelezionato === null) {
      this.errorMessage.set("Seleziona un giudice");
      return;
    }
    
    if (this.mentoriSelezionati.length === 0) {
      this.errorMessage.set("Seleziona almeno un mentore");
      return;
    }

    // Trasforma i dati in formato adatto al backend
    const data = {
      name: this.hackathonData.nome,
      location: this.hackathonData.localita,
      prize: this.hackathonData.premio,
      maxTeamMembers: this.hackathonData.maxParticipantsPerTeam,
      maxNumberTeams: this.hackathonData.maxParticipants,
      startDate: this.hackathonData.startDate + ':00',
      endDate: this.hackathonData.endDate + ':00',
      judgeEmail: this.giudiceSelezionato?.email,
      mentorEmails: this.mentoriSelezionati.map(m => m.email),
      idRules: this.regoleSelezionate.map(r => r.id)
    };

    // Invia i dati al backend per creare l'hackathon
    this.creaHackathonService.createHackathon(data).subscribe({
      next: (res) => {
        this.successMessage.set("Hackathon creato con successo!");
        this.errorMessage.set(null);
        console.log('Hackathon creato:', res);
        
        setTimeout(() => {
          this.router.navigate(['/hackathons/' + res.id]);
        }, 1500);
      },
      error: (err) => {
        this.errorMessage.set("Errore nella creazione dell'hackathon");
        this.successMessage.set(null);
        console.error(err);
      }
    });
  }

  cancelEdit() {
    // Resetta i dati del form
    this.hackathonData = {
      nome: '',
      localita: '',
      startDate: this.minDate,
      endDate: this.minDate,
      premio: 0,
      maxParticipants: 0,
      maxParticipantsPerTeam: 0,
    };
    this.giudiceSelezionato = null;
    this.searchGiudice = '';
    this.mentoriSelezionati = [];
    this.searchMentore = '';
    this.regoleSelezionate = [];
    this.searchRegola = '';
    this.errorMessage.set(null);
    this.successMessage.set(null);
  }

  // --- Filtri e Selezioni ---

  filtraGiudici() {
    const val = this.searchGiudice.toLowerCase();
    this.giudiciFiltrati = this.items.filter(i =>
      val === '' || i.email.toLowerCase().includes(val)
    );
    this.showGiudici.set(true);
  }

  selezionaGiudice(item: Staff) {
    this.searchGiudice = item.email;
    this.giudiceSelezionato = item;
    this.showGiudici.set(false);
  }

  filtraMentori() {
    const val = this.searchMentore.toLowerCase();
    this.mentoriFiltrati = this.items.filter(i =>
      (val === '' || i.email.toLowerCase().includes(val)) &&
      !this.mentoriSelezionati.find(m => m.id === i.id)
    );
    this.showMentori.set(true);
  }

  selezionaMentore(item: Staff) {
    this.mentoriSelezionati.push(item);
    this.searchMentore = '';
    this.showMentori.set(false);
  }

  rimuoviMentore(item: Staff) {
    this.mentoriSelezionati = this.mentoriSelezionati.filter(m => m.id !== item.id);
  }

  filtraRegole() {
    const val = this.searchRegola.toLowerCase();
    this.regoleFiltrate = this.regole.filter(r =>
      (val === '' || r.description.toLowerCase().includes(val)) &&
      !this.regoleSelezionate.find(s => s.id === r.id)
    );
    this.showRegole.set(true);
  }

  selezionaRegola(item: Rule) {
    this.regoleSelezionate.push(item);
    this.searchRegola = '';
    this.showRegole.set(false);
  }

  rimuoviRegola(item: Rule) {
    this.regoleSelezionate = this.regoleSelezionate.filter(r => r.id !== item.id);
  }

  chiudiDropdown(tipo: string) {
    // setTimeout permette al (mousedown) di completarsi prima che la lista sparisca
    setTimeout(() => {
      if (tipo === 'giudice') this.showGiudici.set(false);
      if (tipo === 'mentore') this.showMentori.set(false);
      if (tipo === 'regola') this.showRegole.set(false);
    }, 200);
  }
}