import { Component, computed, inject, signal, OnInit } from '@angular/core';
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

  // Segnale calcolato per autenticazione (true se user è presente)
  isAuthenticated = computed(() => !!this.authService.user());
  
  // Riferimento diretto al segnale utente del servizio
  userProfile = this.authService.user; 

  // Gestione stato menu
  isMobileMenuOpen = signal<boolean>(false);
  isProfileDropdownOpen = signal<boolean>(false);

  ngOnInit() {
    // Carica i dati dell'utente se il token esiste ma non abbiamo ancora i dati
    if (this.authService.getToken() && !this.authService.user()) {
      this.authService.loadUser$().subscribe({
        error: () => this.logout() // Se il token non è valido, pulisce tutto
      });
    }
  }

  toggleMobileMenu() { this.isMobileMenuOpen.update(v => !v); }
  toggleProfileDropdown() { this.isProfileDropdownOpen.update(v => !v); }

  logout() {
    this.authService.logout();
    this.isProfileDropdownOpen.set(false);
    this.router.navigate(['/login']);
  }
}