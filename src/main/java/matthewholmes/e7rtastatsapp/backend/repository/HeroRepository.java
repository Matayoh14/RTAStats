package matthewholmes.e7rtastatsapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import matthewholmes.e7rtastatsapp.backend.model.Hero;

@Repository
public interface HeroRepository extends JpaRepository<Hero, String>{
	Optional<Hero> findById(String id);
	Optional<Hero> findByName(String name);
	List<Hero> findAllByNameContains(String name);
	List<Hero> findAllByHeroElement(Hero.HeroElement element);
	List<Hero> findAllByHeroClass(Hero.HeroClass hclass);
	List<Hero> findAllByHeroElementAndHeroClass(Hero.HeroElement element, Hero.HeroClass hclass);
}
