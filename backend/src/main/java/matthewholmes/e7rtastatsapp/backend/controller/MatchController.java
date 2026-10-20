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
import matthewholmes.e7rtastatsapp.backend.dto.MatchRequest;
import matthewholmes.e7rtastatsapp.backend.dto.MatchResponse;
import matthewholmes.e7rtastatsapp.backend.service.MatchService;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

	private final MatchService matchService;
	
	@PostMapping
	public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody MatchRequest request) {
		return new ResponseEntity<>(matchService.createMatch(request), HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<MatchResponse>> getAllMatches() {
		return ResponseEntity.ok(matchService.getAllMatches());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MatchResponse> getMatchById(@PathVariable String id) {
		return ResponseEntity.ok(matchService.getMatchById(id));
	}
	
	@GetMapping("/herosearch/{id}")
	public ResponseEntity<List<MatchResponse>> getMatchesByHeroId(@PathVariable String id) {
		return ResponseEntity.ok(matchService.getMatchesByHeroId(id));
	}
	
	@GetMapping("/herosearch/excludebanned/{id}")
	public ResponseEntity<List<MatchResponse>> getMatchesByHeroIdExcludingBanned(@PathVariable String id) {
		return ResponseEntity.ok(matchService.getMatchesByHeroIdExcludingBanned(id));
	}
	
	@GetMapping("/herosearch/wins/{id}")
	public ResponseEntity<List<MatchResponse>> getMatchesByHeroIdWhereHeroWon(@PathVariable String id) {
		return ResponseEntity.ok(matchService.getMatchesByHeroIdWhereHeroWon(id));
	}
	
	@GetMapping("/herosearch/losses/{id}")
	public ResponseEntity<List<MatchResponse>> getMatchesByHeroIdWhereHeroLost(@PathVariable String id) {
		return ResponseEntity.ok(matchService.getMatchesByHeroIdWhereHeroLost(id));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<MatchResponse> updateMatch(@Valid @RequestBody MatchRequest request, @PathVariable String id) {
		return ResponseEntity.ok(matchService.updateMatch(request, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMatch(@PathVariable String id) {
		matchService.deleteMatch(id);
		return ResponseEntity.noContent().build();
	}
}
