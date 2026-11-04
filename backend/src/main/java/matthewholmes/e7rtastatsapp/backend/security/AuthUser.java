package matthewholmes.e7rtastatsapp.backend.security;

import matthewholmes.e7rtastatsapp.backend.model.Role;

// What controllers receive via @AuthenticationPrincipal: a small immutable snapshot, not the JPA entity
public record AuthUser(String id, String email, Role role) {
}
