package matthewholmes.e7rtastatsapp.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "matches")
@NoArgsConstructor
@AllArgsConstructor
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("pickOrder ASC")
	private List<MatchHero> picks =  new ArrayList<>();
	
	@Column(nullable = false)
	private boolean fpWin; // true = 1st pick/Team1 won, false = 2nd pick/Team2 won
	
	@ManyToMany
	@JoinTable(
	    name = "match_prebans",
	    joinColumns = @JoinColumn(name = "match_id"),
	    inverseJoinColumns = @JoinColumn(name = "hero_id")
	)
	private List<Hero> prebans = new ArrayList<>();
	
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
    
    // Helper methods
    public List<Hero> getTeam1Picks() {
    	return picks.stream()
    			.filter(pick -> pick.getTeam() == 1)
    			.sorted(Comparator.comparingInt(MatchHero::getPickOrder))
    			.map(MatchHero::getHero)
    			.collect(Collectors.toList());
    }
    
    public List<Hero> getTeam2Picks() {
    	return picks.stream()
    			.filter(pick -> pick.getTeam() == 2)
    			.sorted(Comparator.comparingInt(MatchHero::getPickOrder))
    			.map(MatchHero::getHero)
    			.collect(Collectors.toList());
    }
    
    public boolean didTeamWin(int team) {
    	return (team == 1 && fpWin) || (team == 2 && !fpWin);
    }
    
    public List<MatchHero> getTeam1MatchHeroes() {
    	return picks.stream()
    			.filter(pick -> pick.getTeam() == 1)
    			.collect(Collectors.toList());
    }
}
