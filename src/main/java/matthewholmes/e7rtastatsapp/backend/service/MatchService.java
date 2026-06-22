package matthewholmes.e7rtastatsapp.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.MatchRequest;
import matthewholmes.e7rtastatsapp.backend.dto.MatchResponse;
import matthewholmes.e7rtastatsapp.backend.model.Match;
import matthewholmes.e7rtastatsapp.backend.repository.MatchRepository;

@Service
@RequiredArgsConstructor
public class MatchService {

	private final MatchRepository matchRepository;
	
	@Transactional
	public MatchResponse createMatch(MatchRequest request) {
		// TODO: user authentication
		Match match = new Match();
		match.setPicks(request.getPicks());
		match.setFpWin(request.isFpWin());
		match.setPrebans(request.getPrebans());
		
		Match saved = matchRepository.save(match);
		return mapToResponse(saved);
	}
	
	@Transactional(readOnly = true)
	public MatchResponse getMatchById(String id) {
		// TODO: user authentication
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Match not found."));
		
		return mapToResponse(match);
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponse> getMatchesByHeroId(String id) {
		// TODO: user authentication
		return matchRepository.findMatchesByHeroId(id)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponse> getMatchesByHeroIdExcludingBanned(String id) {
		// TODO: user authentication
		return matchRepository.findMatchesByHeroIdExcludingBanned(id)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponse> getMatchesByHeroIdWhereHeroWon(String id) {
		// TODO: user authentication
		return matchRepository.findMatchesByHeroIdWhereHeroWon(id)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponse> getMatchesByHeroIdWhereHeroLost(String id) {
		// TODO: user authentication
		return matchRepository.findMatchesByHeroIdWhereHeroLost(id)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional
	public MatchResponse updateMatch(MatchRequest request, String id) {
		
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Match not found."));
		
		match.setPicks(request.getPicks());
		match.setFpWin(request.isFpWin());
		match.setPrebans(request.getPrebans());
		
		Match updated = matchRepository.save(match);
		return mapToResponse(updated);
	}
	
	@Transactional
	public void deleteMatch(String id) {
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Match not found."));
		
		matchRepository.delete(match);
	}

	private MatchResponse mapToResponse(Match match) {
		return MatchResponse.builder()
				.id(match.getId())
				.picks(match.getPicks())
				.fpWin(match.isFpWin())
				.prebans(match.getPrebans())
				.createdAt(match.getCreatedAt())
				.updatedAt(match.getUpdatedAt())
				.build();
	}
}
