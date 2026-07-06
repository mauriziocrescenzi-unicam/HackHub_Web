export interface ModaleConferma {
  type: 'danger' | 'default';
  icon: string;
  title: string;
  message: string;
  warning?: string;
  confirmLabel: string;
  onConfirm: () => void;
}