import { Component, computed, signal } from '@angular/core';
import { Account } from '../../../account/models/account.model';
import { AccountService } from '../../../account/service/account.service';
import { LowerCasePipe } from '@angular/common';
import { ModaleConferma } from '../../../team/model/modale-conferma.model';
import { AuthService } from '../../../auth/service/auth.service';

type FilterValue = 'ALL' | 'USER' | 'STAFF' | 'ADMIN' | 'DISABLED';

@Component({
  selector: 'app-user.component',
  imports: [LowerCasePipe],
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss',
})
export class UserComponent {
  accounts = signal<Account[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  searchQuery = signal('');
  activeFilter = signal<FilterValue>('ALL');
  confirmDialog = signal<ModaleConferma | null>(null);
  private messageTimeout: any;

  filters: { label: string; value: FilterValue }[] = [
    { label: 'Tutti',         value: 'ALL'      },
    { label: 'Utenti',        value: 'USER'     },
    { label: 'Staff',         value: 'STAFF'    },
    { label: 'Disabilitati',  value: 'DISABLED' },
  ];

  filteredAccounts = computed(() => {
    const q = this.searchQuery().toLowerCase();
    const f = this.activeFilter();

    return this.accounts().filter(a => {
      if (a.role === 'ADMIN') return false;
      
      const matchSearch =
        a.name.toLowerCase().includes(q) ||
        a.surname.toLowerCase().includes(q) ||
        a.nickname.toLowerCase().includes(q) ||
        a.email.toLowerCase().includes(q);

      const matchFilter =
        f === 'ALL'      ? true :
        f === 'DISABLED' ? a.disabled :
        a.role === f && !a.disabled;

      return matchSearch && matchFilter;
    });
  });

  constructor(
    private accountService: AccountService, 
    public authService: AuthService) {
    
    this.loadAccounts();
  }

  loadAccounts() {
    this.isLoading.set(true);
    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.accounts.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Errore durante il caricamento degli utenti.');
        this.clearMessagesAfterDelay();
        this.isLoading.set(false);
      }
    });
  }

  updateSearch(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  setFilter(value: FilterValue) {
    this.activeFilter.set(value);
  }

  askToggleStatus(account: Account) {
    const enabling = account.disabled;
    this.confirmDialog.set({
      type: enabling ? 'default' : 'danger',
      icon: enabling ? 'fa-solid fa-lock-open' : 'fa-solid fa-lock',
      title: enabling ? "Abilita utente?" : "Disabilita utente?",
      message: enabling
        ? `Stai per riabilitare l'account di ${account.name} ${account.surname} (@${account.nickname}). L'utente potrà accedere nuovamente alla piattaforma.`
        : `Stai per disabilitare l'account di ${account.name} ${account.surname} (@${account.nickname}). L'utente non potrà più accedere alla piattaforma.`,
      confirmLabel: enabling ? 'Sì, abilita' : 'Sì, disabilita',
      onConfirm: () => {
        this.closeDialog();
        this.toggleStatus(account);
      }
    });
  }

  private toggleStatus(account: Account) {
    this.accountService.changeStatus(account.idAccount, !account.disabled).subscribe({
      next: () => {
        this.accounts.update(list =>
          list.map(a =>
            a.idAccount === account.idAccount
              ? { ...a, disabled: !a.disabled }
              : a
          )
        );
        this.successMessage.set(
          account.disabled
            ? `Account di ${account.nickname} abilitato.`
            : `Account di ${account.nickname} disabilitato.`
        );
        this.clearMessagesAfterDelay();
      },
      error: res => {
        this.errorMessage.set(res);
        this.clearMessagesAfterDelay();
      }
    });
  }

  closeDialog() {
    this.confirmDialog.set(null);
  }

  private clearMessagesAfterDelay() {
    if (this.messageTimeout) clearTimeout(this.messageTimeout);
    this.messageTimeout = setTimeout(() => {
      this.errorMessage.set(null);
      this.successMessage.set(null);
    }, 4000);
  }
}