export interface Account {
  idAccount: number;
  name: string;
  surname: string;
  nickname: string;
  email: string;
  role: 'USER' | 'STAFF' | 'ADMIN';
  disabled: boolean;
  idTeam?: number | null;
  teamName?: string | null;
}