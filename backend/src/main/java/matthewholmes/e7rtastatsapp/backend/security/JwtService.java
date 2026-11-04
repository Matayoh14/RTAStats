package matthewholmes.e7rtastatsapp.backend.security;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import matthewholmes.e7rtastatsapp.backend.model.User;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMs;

	public JwtService(@Value("${app.jwt.secret}") String base64Secret,
			@Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
		this.expirationMs = expirationMs;
	}

	public String generateToken(User user) {
		Date now = new Date();
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs))
				.signWith(key)
				.compact();
	}

	/** Returns the email inside a valid, unexpired token, or empty if the token is bad in any way. */
	public Optional<String> extractEmail(String token) {
		try {
			String subject = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload()
					.getSubject();
			return Optional.ofNullable(subject);
		} catch (JwtException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}
