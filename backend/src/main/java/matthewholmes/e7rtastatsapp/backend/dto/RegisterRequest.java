package matthewholmes.e7rtastatsapp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be a valid address")
	private String email;

	// BCrypt only uses the first 72 bytes, so cap it there
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
	private String password;
}
