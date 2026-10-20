package matthewholmes.e7rtastatsapp.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MatchHeroRequest {

    @NotBlank(message = "heroId is required")
    private String heroId;

    @Min(1)
    @Max(2)
    private int team;

    @Min(1)
    @Max(5)
    private int pickOrder;

    private boolean banned;
}
