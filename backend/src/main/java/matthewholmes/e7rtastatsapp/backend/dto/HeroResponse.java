package matthewholmes.e7rtastatsapp.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroClass;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroElement;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroResponse {

	private String id;
	private String name;
	private HeroElement element;
	private HeroClass heroClass;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
}
