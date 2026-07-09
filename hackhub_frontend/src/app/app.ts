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

  // Gestione dropdown del profilo
  @ViewChild('userDropdown') userDropdown!: ElementRef;

  isAuthenticated = computed(() => !!this.authService.user());
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

  // Gestione click-outside per chiudere il dropdown del profilo
  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    // chiusura del dropdown del profilo se l'utente clicca fuori da esso
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