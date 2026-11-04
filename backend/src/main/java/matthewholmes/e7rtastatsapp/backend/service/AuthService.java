package matthewholmes.e7rtastatsapp.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.AuthResponse;
import matthewholmes.e7rtastatsapp.backend.dto.LoginRequest;
import matthewholmes.e7rtastatsapp.backend.dto.RegisterRequest;
import matthewholmes.e7rtastatsapp.backend.model.Role;
import matthewholmes.e7rtastatsapp.backend.model.User;
import matthewholmes.e7rtastatsapp.backend.repository.UserRepository;
import matthewholmes.e7rtastatsapp.backend.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = normalise(request.getEmail());

		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with that email already exists");
		}

		User user = new User();
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER); // registration never creates admins
		userRepository.save(user);

		return toResponse(user);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		// Same message for "no such email" and "wrong password", so the API doesn't reveal which accounts exist
		User user = userRepository.findByEmail(normalise(request.getEmail()))
				.filter(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		return toResponse(user);
	}

	private String normalise(String email) {
		return email.trim().toLowerCase();
	}

	private AuthResponse toResponse(User user) {
		return new AuthResponse(jwtService.generateToken(user), user.getEmail(), user.getRole());
	}
}
