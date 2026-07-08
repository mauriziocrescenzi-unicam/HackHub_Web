import { Component, computed, inject, signal, OnInit, HostListener, ViewChild, ElementRef } from '@angular/core';
import { RouterModule, RouterOutlet, Router } from '@angular/router';
import { AuthService } from './features/auth/service/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  // Agganciamo il div specifico del menu a tendina tramite il suo ID di template #userDropdown
  @ViewChild('userDropdown') userDropdown!: ElementRef;

  // Segnale calcolato per autenticazione (true se user è presente)
  isAuthenticated = computed(() => !!this.authService.user());
  
  // Riferimento diretto al segnale utente del servizio
  userProfile = this.authService.user; 

  // Gestione stato menu
  isMobileMenuOpen = signal<boolean>(false);
  isProfileDropdownOpen = signal<boolean>(false);

  ngOnInit() {
    if (this.authService.getToken() && !this.authService.user()) {
      this.authService.loadUser$().subscribe({
        error: () => this.logout()
      });
    }
  }

  // Ascoltatore globale che verifica dove avviene il click
  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    // Se il menu è aperto E il click avviene fuori dal div #userDropdown, lo chiudiamo
    if (this.isProfileDropdownOpen() && this.userDropdown && !this.userDropdown.nativeElement.contains(event.target)) {
      this.isProfileDropdownOpen.set(false);
    }
  }

  toggleMobileMenu() { 
    this.isMobileMenuOpen.update(v => !v); 
  }

  toggleProfileDropdown() { 
    this.isProfileDropdownOpen.update(v => !v); 
  }

  logout() {
    this.authService.logout();
    this.isProfileDropdownOpen.set(false);
    this.router.navigate(['/login']);
  }
}