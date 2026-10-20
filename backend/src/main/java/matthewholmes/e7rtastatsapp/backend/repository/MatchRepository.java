package matthewholmes.e7rtastatsapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import matthewholmes.e7rtastatsapp.backend.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, String> {
	Optional<Match> findById(String id);
	
	List<Match> findAllByOrderByCreatedAtDesc();

	@Query("SELECT DISTINCT m FROM Match m JOIN m.picks p WHERE p.hero.id = :heroId")
    List<Match> findMatchesByHeroId(@Param("heroId") String heroId);
	
	@Query("SELECT DISTINCT m FROM Match m JOIN m.picks p WHERE p.hero.id = :heroId AND p.banned = false")
	List<Match> findMatchesByHeroIdExcludingBanned(@Param("heroId") String heroId);
	
	@Query("SELECT DISTINCT m FROM Match m JOIN m.picks p WHERE p.hero.id = :heroId AND p.banned = false AND ((m.fpWin = true AND p.team = 1) OR (m.fpWin = false AND p.team = 2))")
	List<Match> findMatchesByHeroIdWhereHeroWon(@Param("heroId") String heroId);
	
	@Query("SELECT DISTINCT m FROM Match m JOIN m.picks p WHERE p.hero.id = :heroId AND p.banned = false AND ((m.fpWin = false AND p.team = 1) OR (m.fpWin = true AND p.team = 2))")
	List<Match> findMatchesByHeroIdWhereHeroLost(@Param("heroId") String heroId);
}
