package matthewholmes.e7rtastatsapp.backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import matthewholmes.e7rtastatsapp.backend.model.Hero;
import matthewholmes.e7rtastatsapp.backend.model.MatchHero;

@Data
public class MatchRequest {

	@NotNull(message = "MatchHeroes are required")
	private List<MatchHero> picks;
	
	@NotNull
	private boolean fpWin;
	
	private List<Hero> prebans;
}
