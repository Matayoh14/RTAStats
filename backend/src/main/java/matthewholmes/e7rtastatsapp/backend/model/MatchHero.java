package matthewholmes.e7rtastatsapp.backend.model;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "match_heroes")
@NoArgsConstructor
@AllArgsConstructor
public class MatchHero {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_id", nullable = false)
	private Match match;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hero_id", nullable = false)
	private Hero hero;
	
	@Column(name = "team", nullable = false)
	private int team; // 1 or 2
	
	@Column(name = "pick_order", nullable = false)
	private int pickOrder; // 1-5
	
	@Column(name = "banned", nullable = false)
	private boolean banned;
	
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper functions
    public boolean isWin() {
    	return (match.isFpWin() && team == 1) || (!match.isFpWin() && team == 2);
    }
	
}
