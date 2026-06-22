package matthewholmes.e7rtastatsapp.backend.dto;

import lombok.Data;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroClass;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroElement;
import jakarta.validation.constraints.NotBlank;

@Data
public class HeroRequest {

	@NotBlank(message = "Hero name is required")
	private String name;
	
	private HeroElement element;
	
	private HeroClass hClass;
}
