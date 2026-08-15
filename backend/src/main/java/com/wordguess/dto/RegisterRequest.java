package com.wordguess.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    // Username: at least 5 letters (both upper and lower case)
    @NotBlank(message = "Username is required")
    @Size(min = 5, message = "Username must be at least 5 letters long")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z]{5,}$",
        message = "Username must have at least 5 letters containing both upper and lower case letters"
    )
    private String username;

    // Password: at least 5 characters (alpha, numeric, and one of $, %, *, &)
    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[\\$%\\*&]).{5,}$",
        message = "Password must be at least 5 characters containing alpha, numeric, and at least one of $, %, *, &"
    )
    private String password;

    private String role = "PLAYER"; // "ADMIN" or "PLAYER"

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
