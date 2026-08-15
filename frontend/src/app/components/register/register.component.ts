import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  username = '';
  password = '';
  role = 'PLAYER';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  // Real-time Username validation checks
  get usernameMin5(): boolean { return this.username.length >= 5; }
  get usernameHasUpper(): boolean { return /[A-Z]/.test(this.username); }
  get usernameHasLower(): boolean { return /[a-z]/.test(this.username); }
  get isUsernameValid(): boolean {
    return this.usernameMin5 && this.usernameHasUpper && this.usernameHasLower && /^[a-zA-Z]{5,}$/.test(this.username);
  }

  // Real-time Password validation checks
  get passwordMin5(): boolean { return this.password.length >= 5; }
  get passwordHasAlpha(): boolean { return /[a-zA-Z]/.test(this.password); }
  get passwordHasNumeric(): boolean { return /[0-9]/.test(this.password); }
  get passwordHasSpecial(): boolean { return /[\$%\*&]/.test(this.password); }
  get isPasswordValid(): boolean {
    return this.passwordMin5 && this.passwordHasAlpha && this.passwordHasNumeric && this.passwordHasSpecial;
  }

  get isFormValid(): boolean {
    return this.isUsernameValid && this.isPasswordValid;
  }

  onSubmit() {
    if (!this.isFormValid) {
      this.errorMessage = 'Please fix the validation errors before submitting.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(this.username, this.password, this.role).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.successMessage = res.message || 'Registration successful! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.message || 'Registration failed. Username may be taken.';
      }
    });
  }
}
