import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { LoginRequest } from '../../model/auth.model';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule, 
    CommonModule,
    RouterModule
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  isPasswordVisible: boolean = false;
  
  errorMessage = signal<string | null>(null);

  constructor(
    private authService: AuthService, 
    private router: Router,
    private fb: FormBuilder 
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]], 
      password: ['', [Validators.required]] 
    });
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  login() {
    this.errorMessage.set(null);

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched(); 
      return;
    }

    const req: LoginRequest = { 
      email: this.loginForm.value.email, 
      password: this.loginForm.value.password 
    };

    this.authService.login(req).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: err => {
        this.errorMessage.set(err.message || 'Errore durante il login. Controlla le credenziali.');
      }
    });
  }
}