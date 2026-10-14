
package com.lostlink.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record ResetRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String newPassword
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r) {
        String email = r.email().trim().toLowerCase();

        if (users.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Email already registered"));
        }

        User u = users.save(
            new User(r.name().trim(), email, encoder.encode(r.password()))
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail(),
                "message", "Registration successful"
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r) {
        return users.findByEmailIgnoreCase(r.email().trim())
            .filter(u -> encoder.matches(r.password(), u.getPasswordHash()))
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                Map.of(
                    "id", u.getId(),
                    "name", u.getName(),
                    "email", u.getEmail(),
                    "message", "Login successful"
                )
            ))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password")));
    }

    // TEMPORARY LOCAL-DEVELOPMENT PASSWORD RESET
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetRequest r) {
        String email = r.email().trim().toLowerCase();

        return users.findByEmailIgnoreCase(email)
            .<ResponseEntity<?>>map(user -> {
                user.setPasswordHash(encoder.encode(r.newPassword()));
                users.save(user);

                return ResponseEntity.ok(
                    Map.of("message", "Password reset successful. You can now log in.")
                );
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "No account found for this email")));
    }
}
