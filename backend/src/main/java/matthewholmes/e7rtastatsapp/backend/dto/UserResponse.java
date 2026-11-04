package matthewholmes.e7rtastatsapp.backend.dto;

import matthewholmes.e7rtastatsapp.backend.model.Role;

public record UserResponse(String email, Role role) {
}
