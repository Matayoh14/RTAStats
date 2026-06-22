package matthewholmes.e7rtastatsapp.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.model.Hero;
import matthewholmes.e7rtastatsapp.backend.model.MatchHero;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

	private String id;
	private List<MatchHero> picks;
	private boolean fpWin;
	private List<Hero> prebans;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
