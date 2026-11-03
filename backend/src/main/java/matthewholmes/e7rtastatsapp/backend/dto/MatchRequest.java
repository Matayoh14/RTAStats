package matthewholmes.e7rtastatsapp.backend.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MatchRequest {

	@NotNull(message = "Picks are required")
	@Size(min = 10, max = 10, message = "A match needs exactly 10 picks (5 per team)")
	@Valid
	private List<MatchHeroRequest> picks;

	private boolean fpWin;
	
	private List<String> prebanHeroIds = new ArrayList<>();
	
	@Min(value = 1, message = "userTeam must be 1 or 2")
	@Max(value = 2, message = "userTeam must be 1 or 2")
	private int userTeam;
}
