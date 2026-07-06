import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../auth/service/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {
  isEditing = signal(false);
  isSaving = signal(false);
  isPasswordVisible = false;
  isConfirmPasswordVisible = false;
  isOldPasswordVisible = false;
  
  editName = signal('');
  editSurname = signal('');
  editNickname = signal('');
  editOldPassword = signal('');
  editNewPassword = signal('');
  editConfirmPassword = signal('');
  
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  // Reintrodotta la regex di sicurezza
  private passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
  private messageTimeout: any;

  constructor(public authService: AuthService) {}

  toggleEdit() {
    const user = this.authService.user();
    if (user) {
      this.editName.set(user.name || '');
      this.editSurname.set(user.surname || '');
      this.editNickname.set(user.nickname || '');
    }
    this.isEditing.set(true); 
  }

  cancelEdit() { 
    this.isEditing.set(false); 
    this.editOldPassword.set('');
    this.editNewPassword.set('');
    this.editConfirmPassword.set('');
    this.errorMessage.set(null);
  }

  // Reintrodotto il metodo per prevenire bug sui messaggi che spariscono troppo in fretta
  private clearMessagesAfterDelay() {
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
      this.successMessage.set(null);
    }, 5000);
  }

  saveChanges() {
    this.isSaving.set(true);
    this.errorMessage.set(null);
    
    const currentUser = this.authService.user();

    const data: any = { 
      name: this.editName(), 
      surname: this.editSurname(), 
      nickname: this.editNickname(),
      email: currentUser?.email 
    };
    
    // Controllo Password
    if (this.editOldPassword() || this.editNewPassword()) {
      
      if (this.editNewPassword() !== this.editConfirmPassword()) {
        this.errorMessage.set('Le nuove password non coincidono.');
        this.isSaving.set(false);
        this.clearMessagesAfterDelay();
        return;
      }

      if (!this.passwordRegex.test(this.editNewPassword())) {
        this.errorMessage.set('La password deve essere di almeno 8 caratteri e contenere una maiuscola, una minuscola e un numero.');
        this.isSaving.set(false);
        this.clearMessagesAfterDelay();
        return;
      }

      data.oldPassword = this.editOldPassword();
      data.newPassword = this.editNewPassword();
    }

    this.authService.updateProfile(data).subscribe({
      next: () => {
        this.successMessage.set('Profilo aggiornato con successo!');
        this.isSaving.set(false);
        this.isEditing.set(false);
        
        // Pulizia campi password
        this.editOldPassword.set('');
        this.editNewPassword.set('');
        this.editConfirmPassword.set('');
        
        this.clearMessagesAfterDelay();
      },
      error: (err: any) => {
        this.isSaving.set(false);
        const backendMessage = typeof err.error === 'string' ? err.error : err.error?.message;
        this.errorMessage.set(backendMessage || 'Errore durante l\'aggiornamento del profilo.');
        this.clearMessagesAfterDelay();
      }
    });
  }
  
  toggleOldPasswordVisibility() { this.isOldPasswordVisible = !this.isOldPasswordVisible; }
  togglePasswordVisibility() { this.isPasswordVisible = !this.isPasswordVisible; }
  toggleConfirmPasswordVisibility() { this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible; }
}