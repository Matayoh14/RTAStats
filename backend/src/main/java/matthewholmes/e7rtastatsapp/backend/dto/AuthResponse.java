package matthewholmes.e7rtastatsapp.backend.dto;

import matthewholmes.e7rtastatsapp.backend.model.Role;

public record AuthResponse(String token, String email, Role role) {
}
