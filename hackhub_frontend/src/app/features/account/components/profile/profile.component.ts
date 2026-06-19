import { Component, signal, computed, effect } from '@angular/core';
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
  private messageTimeout: any;

  constructor(public authService: AuthService) {
    effect(() => {
      const user = this.authService.user();
      if (user && !this.isEditing()) {
        this.editName.set(user.name || '');
        this.editSurname.set(user.surname || '');
        this.editNickname.set(user.nickname || '');
      }
    });
  }

  toggleEdit() { this.isEditing.set(true); }
  cancelEdit() { this.isEditing.set(false); }

  saveChanges() {
    this.isSaving.set(true);
    const data: any = { name: this.editName(), surname: this.editSurname(), nickname: this.editNickname() };
    
    if (this.editNewPassword()) {
      if (this.editNewPassword() !== this.editConfirmPassword()) {
        this.errorMessage.set('Passwords do not match!');
        this.isSaving.set(false);
        return;
      }
      data.oldPassword = this.editOldPassword();
      data.newPassword = this.editNewPassword();
    }

    this.authService.updateProfile(data).subscribe({
      next: () => {
        this.successMessage.set('Profile updated!');
        this.isSaving.set(false);
        this.isEditing.set(false);
        setTimeout(() => this.successMessage.set(null), 5000);
      },
      error: (err) => {
        this.isSaving.set(false);
        this.errorMessage.set(err.message || 'Error updating profile');
        setTimeout(() => this.errorMessage.set(null), 5000);
      }
    });
  }
  
  toggleOldPasswordVisibility() { this.isOldPasswordVisible = !this.isOldPasswordVisible; }
  togglePasswordVisibility() { this.isPasswordVisible = !this.isPasswordVisible; }
  toggleConfirmPasswordVisibility() { this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible; }
}