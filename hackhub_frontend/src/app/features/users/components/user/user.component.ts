import { Component, computed, signal } from '@angular/core';
import { Account } from '../../../account/models/account.model';
import { AccountService } from '../../../account/service/account.service';
import { UpperCasePipe, LowerCasePipe } from '@angular/common';

type FilterValue = 'ALL' | 'USER' | 'STAFF' | 'ADMIN' | 'DISABLED';

// Note: Add ConfirmDialogConfig interface if it is not exported elsewhere.
export interface ConfirmDialogConfig {
  type: 'default' | 'danger';
  icon: string;
  title: string;
  message: string;
  warning?: string;
  confirmLabel: string;
  onConfirm: () => void;
}

@Component({
  selector: 'app-user.component',
  imports: [UpperCasePipe, LowerCasePipe],
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
  confirmDialog = signal<ConfirmDialogConfig | null>(null);
  private messageTimeout: any;

  filters: { label: string; value: FilterValue }[] = [
    { label: 'All',       value: 'ALL'      },
    { label: 'User',      value: 'USER'     },
    { label: 'Staff',     value: 'STAFF'    },
    { label: 'Disabled',  value: 'DISABLED' },
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

  constructor(private accountService: AccountService) {
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
        this.errorMessage.set('Error loading users.');
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
      title: enabling ? "Enable user?" : "Disable user?",
      message: enabling
        ? `You are about to re-enable the account of ${account.name} ${account.surname} (@${account.nickname}). The user will be able to access the platform again.`
        : `You are about to disable the account of ${account.name} ${account.surname} (@${account.nickname}). The user will no longer be able to access the platform.`,
      confirmLabel: enabling ? 'Yes, enable' : 'Yes, disable',
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
            ? `${account.nickname}'s account enabled.`
            : `${account.nickname}'s account disabled.`
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