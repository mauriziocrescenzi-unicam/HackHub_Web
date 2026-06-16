import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = signal('hackhub_frontend');

  // MOCK: Segnali temporanei per l'interfaccia (sostituiranno l'AuthService)
  isAuthenticated = signal<boolean>(false); // Cambialo a 'true' per vedere il menu utente
  userProfile = signal<{name: string, role: string, email: string, avatarUrl: string} | null>({
    name: 'Andrea',
    role: 'USER',
    email: 'andrea@example.com',
    avatarUrl: 'https://ui-avatars.com/api/?name=Andrea&background=0f172a&color=fff'
  });

  // Gestione stato dei menu (sostituisce il JS di Bootstrap)
  isMobileMenuOpen = signal<boolean>(false);
  isProfileDropdownOpen = signal<boolean>(false);

  toggleMobileMenu() {
    this.isMobileMenuOpen.update(v => !v);
  }

  toggleProfileDropdown() {
    this.isProfileDropdownOpen.update(v => !v);
  }

  logout() {
    console.log('Logout action triggered');
    this.isAuthenticated.set(false);
    this.isProfileDropdownOpen.set(false);
    // this.router.navigate(['/login']);
  }
}