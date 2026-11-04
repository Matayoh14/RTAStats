package matthewholmes.e7rtastatsapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.repository.UserRepository;
import matthewholmes.e7rtastatsapp.backend.security.JwtAuthenticationFilter;
import matthewholmes.e7rtastatsapp.backend.security.JwtService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// Stateless API using an Authorization header (not cookies), so CSRF protection isn't needed
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/me").authenticated()
						// Step A of the auth work: authentication exists but isn't enforced anywhere else yet,
						// so the app keeps working. The real access rules arrive in a later branch.
						.anyRequest().permitAll())
				// Unauthenticated requests get a 401 (Spring's default here would be a 403)
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
						UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
