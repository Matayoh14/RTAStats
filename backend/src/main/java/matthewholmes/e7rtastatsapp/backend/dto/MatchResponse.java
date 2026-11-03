package matthewholmes.e7rtastatsapp.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

	private String id;
	private List<MatchHeroResponse> picks;
	private boolean fpWin;
	private List<HeroResponse> prebans;
	private int userTeam;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
