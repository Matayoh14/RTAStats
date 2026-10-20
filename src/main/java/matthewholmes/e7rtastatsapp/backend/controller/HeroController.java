package matthewholmes.e7rtastatsapp.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.HeroRequest;
import matthewholmes.e7rtastatsapp.backend.dto.HeroResponse;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroClass;
import matthewholmes.e7rtastatsapp.backend.model.Hero.HeroElement;
import matthewholmes.e7rtastatsapp.backend.service.HeroService;

@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
public class HeroController {

	private final HeroService heroService;
	
	@PostMapping
	public ResponseEntity<HeroResponse> createHero(@Valid @RequestBody HeroRequest request) {
		return new ResponseEntity<>(heroService.createHero(request), HttpStatus.CREATED);
	}

	@GetMapping()
	public ResponseEntity<List<HeroResponse>> getAllHeroes() {
		return ResponseEntity.ok(heroService.getAllHeroes());
	}

	@GetMapping("/{id}")
	public ResponseEntity<HeroResponse> getHeroById(@PathVariable String id) {
		return ResponseEntity.ok(heroService.getHeroById(id));
	}
	
	@GetMapping("/hero/{name}")
	public ResponseEntity<HeroResponse> getHeroByName(@PathVariable String name) {
		return ResponseEntity.ok(heroService.getHeroByName(name));
	}
	
	@GetMapping("/element/{element}")
	public ResponseEntity<List<HeroResponse>> getHeroesByElement(@PathVariable HeroElement element) {
		return ResponseEntity.ok(heroService.getHeroesByElement(element));
	}
	
	@GetMapping("/class/{hClass}")
	public ResponseEntity<List<HeroResponse>> getHeroesByHeroClass(@PathVariable HeroClass hClass) {
		return ResponseEntity.ok(heroService.getHeroesByHeroClass(hClass));
	}
	
	@GetMapping("/element/{element}/class/{hClass}")
	public ResponseEntity<List<HeroResponse>> getHeroesByElementAndClass(@PathVariable HeroElement element, @PathVariable HeroClass hClass) {
		return ResponseEntity.ok(heroService.getHeroesByElementAndHeroClass(element, hClass));
	}
	
	@GetMapping("/search/{name}")
	public ResponseEntity<List<HeroResponse>> getHeroesByNameContains(@PathVariable String name) {
		return ResponseEntity.ok(heroService.getHeroesByNameContains(name));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<HeroResponse> updateHero(@Valid @RequestBody HeroRequest request, @PathVariable String id) {
		return ResponseEntity.ok(heroService.updateHero(request, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteHero(@PathVariable String id) {
		heroService.deleteHero(id);
		return ResponseEntity.noContent().build();
	}
	
}
