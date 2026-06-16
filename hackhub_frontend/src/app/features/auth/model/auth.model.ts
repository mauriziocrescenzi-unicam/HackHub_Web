export interface RegisterRequest {
  name: string;
  surname: string;
  nickname: string;
  email: string;
  password: string;
  role: 'USER' | 'STAFF';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}
