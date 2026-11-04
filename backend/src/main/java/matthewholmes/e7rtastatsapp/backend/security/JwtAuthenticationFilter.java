package matthewholmes.e7rtastatsapp.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.repository.UserRepository;

// Deliberately NOT a @Component: SecurityConfig creates it, so Spring Boot doesn't also register it as a plain servlet filter
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header != null && header.startsWith("Bearer ")
				&& SecurityContextHolder.getContext().getAuthentication() == null) {

			jwtService.extractEmail(header.substring(7))
					.flatMap(userRepository::findByEmail) // a deleted user's old token stops working here
					.ifPresent(user -> {
						AuthUser principal = new AuthUser(user.getId(), user.getEmail(), user.getRole());
						var authentication = new UsernamePasswordAuthenticationToken(
								principal, null,
								List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					});
		}

		// A missing or invalid token is not an error here: the request just continues unauthenticated,
		// and the authorisation rules in SecurityConfig decide whether that's allowed.
		chain.doFilter(request, response);
	}
}
