package matthewholmes.e7rtastatsapp.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.AuthResponse;
import matthewholmes.e7rtastatsapp.backend.dto.LoginRequest;
import matthewholmes.e7rtastatsapp.backend.dto.RegisterRequest;
import matthewholmes.e7rtastatsapp.backend.dto.UserResponse;
import matthewholmes.e7rtastatsapp.backend.security.AuthUser;
import matthewholmes.e7rtastatsapp.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	// Who am I? Lets the front end check a stored token is still valid
	@GetMapping("/me")
	public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthUser user) {
		return ResponseEntity.ok(new UserResponse(user.email(), user.role()));
	}
}
