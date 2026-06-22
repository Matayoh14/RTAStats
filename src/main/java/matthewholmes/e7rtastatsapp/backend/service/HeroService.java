package matthewholmes.e7rtastatsapp.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.HeroRequest;
import matthewholmes.e7rtastatsapp.backend.dto.HeroResponse;
import matthewholmes.e7rtastatsapp.backend.model.Hero;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroClass;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroElement;
import matthewholmes.e7rtastatsapp.backend.repository.HeroRepository;

@Service
@RequiredArgsConstructor
public class HeroService {

	private final HeroRepository heroRepository;
	
	@Transactional
	public HeroResponse createHero(HeroRequest request) {
		// TODO: user authentication
		
		Hero hero = new Hero();
		hero.setName(request.getName());
		hero.setElement(request.getElement());
		hero.setHClass(request.getHClass());
		
		// TODO: save to repo
		
		Hero saved = heroRepository.save(hero);
		// TODO: create & send response object
		
		return mapToResponse(saved);
	}
	
	@Transactional(readOnly = true)
	public HeroResponse getHeroById(String id) {
		// TODO: user authentication
		Hero hero = heroRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Hero not found."));
		
		return mapToResponse(hero);
	}
	
	@Transactional(readOnly = true)
	public HeroResponse getHeroByName(String name) {
		// TODO: user authentication
		Hero hero = heroRepository.findByName(name)
				.orElseThrow(() -> new RuntimeException("Hero not found."));
		
		return mapToResponse(hero);
	}
	
	@Transactional(readOnly = true)
	public List<HeroResponse> getHeroesByNameContains(String name) {
		// TODO: user authentication
		List<Hero> heroes = heroRepository.findAllByNameContains(name);
		return heroes
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<HeroResponse> getHeroesByElement(HeroElement element) {
		// TODO: user authentication
		List<Hero> heroes = heroRepository.findAllByHeroElement(element);
		return heroes
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<HeroResponse> getHeroesByHeroClass(HeroClass hClass) {
		// TODO: user authentication
		List<Hero> heroes = heroRepository.findAllByHeroClass(hClass);
		return heroes
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<HeroResponse> getHeroesByElementAndHeroClass(HeroElement element, HeroClass hClass) {
		// TODO: user authentication
		List<Hero> heroes = heroRepository.findAllByHeroElementAndHeroClass(element, hClass);
		return heroes
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional
	public HeroResponse updateHero(HeroRequest request, String id) {
		// TODO: user authentication
		Hero hero = heroRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Hero not found."));
		
		hero.setName(request.getName());
		hero.setElement(request.getElement());
		hero.setHClass(request.getHClass());
		
		Hero updated = heroRepository.save(hero);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteHero(String id) {
		// TODO: user authentication
		Hero hero = heroRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Hero not found."));
		
		heroRepository.delete(hero);
	}
	
	private HeroResponse mapToResponse(Hero hero) {
		return HeroResponse.builder()
				.id(hero.getId())
				.name(hero.getName())
				.element(hero.getElement())
				.hClass(hero.getHClass())
				.appearances(hero.getAppearances())
				.createdAt(hero.getCreatedAt())
				.updatedAt(hero.getUpdatedAt())
				.build();
	}
}
