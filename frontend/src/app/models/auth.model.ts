export interface User {
  userId: string;
  username: string;
  role: 'ADMIN' | 'PLAYER';
}

export interface AuthResponse {
  userId: string;
  username: string;
  role: 'ADMIN' | 'PLAYER';
  message: string;
}
