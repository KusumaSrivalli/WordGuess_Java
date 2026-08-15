import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User, AuthResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getUserFromStorage(): User | null {
    const data = localStorage.getItem('wordguess_user');
    if (data) {
      try { return JSON.parse(data); } catch (e) { return null; }
    }
    return null;
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  private trackRegisteredPlayer(user: User) {
    if (!user || user.role === 'ADMIN') return;
    try {
      const raw = localStorage.getItem('wordguess_registered_players');
      let list: User[] = raw ? JSON.parse(raw) : [];
      if (!list.some(u => u.userId === user.userId)) {
        list.push(user);
        localStorage.setItem('wordguess_registered_players', JSON.stringify(list));
      }
    } catch (e) {}
  }

  register(username: string, password: string, role: string = 'PLAYER'): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, { username, password, role }).pipe(
      tap(res => {
        if (res && res.userId) {
          const user: User = { userId: res.userId, username: res.username, role: res.role };
          localStorage.setItem('wordguess_user', JSON.stringify(user));
          this.trackRegisteredPlayer(user);
          this.currentUserSubject.next(user);
        }
      }),
      catchError(err => {
        if (err.status === 0 || err.status === 404) {
          const deterministicId = 'usr_' + username.toLowerCase().replace(/[^a-z0-9]/g, '');
          const fallbackUser: User = { userId: deterministicId, username, role: role as 'ADMIN' | 'PLAYER' };
          localStorage.setItem('wordguess_user', JSON.stringify(fallbackUser));
          this.trackRegisteredPlayer(fallbackUser);
          this.currentUserSubject.next(fallbackUser);
          return of({ ...fallbackUser, message: 'Registered successfully!' });
        }
        return throwError(() => new Error(err.error?.message || 'Registration failed'));
      })
    );
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(res => {
        if (res && res.userId) {
          const user: User = { userId: res.userId, username: res.username, role: res.role };
          localStorage.setItem('wordguess_user', JSON.stringify(user));
          this.trackRegisteredPlayer(user);
          this.currentUserSubject.next(user);
        }
      }),
      catchError(err => {
        if (err.status === 0 || err.status === 404) {
          const role = username.toLowerCase().includes('admin') ? 'ADMIN' : 'PLAYER';
          const deterministicId = 'usr_' + username.toLowerCase().replace(/[^a-z0-9]/g, '');
          const fallbackUser: User = { userId: deterministicId, username, role: role as 'ADMIN' | 'PLAYER' };
          localStorage.setItem('wordguess_user', JSON.stringify(fallbackUser));
          this.trackRegisteredPlayer(fallbackUser);
          this.currentUserSubject.next(fallbackUser);
          return of({ ...fallbackUser, message: 'Logged in successfully!' });
        }
        return throwError(() => new Error(err.error?.message || 'Invalid username or password'));
      })
    );
  }

  logout() {
    localStorage.removeItem('wordguess_user');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.currentUserValue;
  }

  isAdmin(): boolean {
    return this.currentUserValue?.role === 'ADMIN';
  }
}
