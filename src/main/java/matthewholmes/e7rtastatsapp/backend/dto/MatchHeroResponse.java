package matthewholmes.e7rtastatsapp.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroElement;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchHeroResponse {

	private String heroId;
	private String heroName;
	private HeroElement heroElement;
	private int team;
	private int pickOrder;
	private boolean banned;
}