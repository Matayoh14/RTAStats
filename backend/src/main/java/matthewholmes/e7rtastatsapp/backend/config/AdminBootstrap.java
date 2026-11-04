package matthewholmes.e7rtastatsapp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import matthewholmes.e7rtastatsapp.backend.model.Role;
import matthewholmes.e7rtastatsapp.backend.model.User;
import matthewholmes.e7rtastatsapp.backend.repository.UserRepository;

/**
 * Creates the first admin account on startup from configuration, so no password (or hash) is ever
 * committed to the repository. Registration only ever creates ordinary users.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.email:}")
	private String adminEmail;

	@Value("${app.admin.password:}")
	private String adminPassword;

	@Override
	public void run(ApplicationArguments args) {
		if (adminEmail.isBlank() || adminPassword.isBlank()) {
			log.info("No admin credentials configured (ADMIN_EMAIL / ADMIN_PASSWORD); skipping admin bootstrap");
			return;
		}

		String email = adminEmail.trim().toLowerCase();
		if (userRepository.existsByEmail(email)) {
			return;
		}

		User admin = new User();
		admin.setEmail(email);
		admin.setPasswordHash(passwordEncoder.encode(adminPassword));
		admin.setRole(Role.ADMIN);
		userRepository.save(admin);
		log.info("Created admin user {}", email);
	}
}
