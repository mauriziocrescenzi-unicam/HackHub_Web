import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { RegisterRequest } from '../../model/auth.model';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule, 
    CommonModule,
    RouterModule
  ]
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = signal<string | null>(null);

  isPasswordVisible: boolean = false;
  isConfirmPasswordVisible: boolean = false;

  constructor(
    private authService: AuthService, 
    private router: Router,
    private fb: FormBuilder
  ) {
    // Creazione del form con tutte le validazioni
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      nickname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/)
      ]],
      confirmPassword: ['', Validators.required],
      role: ['', Validators.required] // Il valore vuoto iniziale forzerà la scelta
    }, { 
      validators: this.passwordMatchValidator // Validatore di gruppo per comparare i due campi
    });
  }

  // Validatore custom per controllare che le password siano identiche
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (password && confirmPassword && password !== confirmPassword) {
      // Imposta un errore specifico sul campo confirmPassword
      control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  register() {
    this.errorMessage.set(null);

    // Se il form non è valido, mostriamo tutti gli errori rossi e fermiamo l'invio
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    // Estraiamo i dati dal form escludendo il confirmPassword che non serve al backend
    const formValue = this.registerForm.value;
    const req: RegisterRequest = {
      name: formValue.name,
      surname: formValue.surname,
      nickname: formValue.nickname,
      email: formValue.email,
      password: formValue.password,
      role: formValue.role
    };

    // Dopo la registrazione l'utente viene reindirizzato alla home
    this.authService.signup(req).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: err => {
        this.errorMessage.set(err.message || 'Errore durante la registrazione.');
      }
    });
  }
}