package matthewholmes.e7rtastatsapp.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "heroes")
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	private HeroElement element;
	
	@Enumerated(EnumType.STRING)
	private HeroClass hClass;
	
    @OneToMany(mappedBy = "hero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchHero> appearances = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
	
	public enum HeroElement {
		FIRE, ICE, EARTH, LIGHT, DARK
	}
	
	public enum HeroClass {
		WARRIOR, KNIGHT, RANGER, MAGE, SOULWEAVER, THIEF
	}
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	// Helper methods (for readability)
	public int getTotalAppearances() {
		return appearances.size();
	}
	
	public List<Match> getMatches() {
		return appearances.stream()
				.map(MatchHero::getMatch)
				.collect(Collectors.toList());
	}
	
}
