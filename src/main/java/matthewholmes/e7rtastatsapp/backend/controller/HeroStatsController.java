package matthewholmes.e7rtastatsapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.service.HeroStatsService;

@RestController
@RequestMapping("api/stats")
@RequiredArgsConstructor
public class HeroStatsController {

	private final HeroStatsService heroStatsService;
	
	@GetMapping("/winrate/{id}")
	public ResponseEntity<Double> getWinrateById(@PathVariable String id) {
		return ResponseEntity.ok(heroStatsService.getHeroWinRate(id));
	}
	
}
