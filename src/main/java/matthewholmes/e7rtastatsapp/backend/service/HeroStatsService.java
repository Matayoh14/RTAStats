package matthewholmes.e7rtastatsapp.backend.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.repository.MatchRepository;

@Service
@RequiredArgsConstructor
public class HeroStatsService {

	private final MatchRepository matchRepository;
	
	@Transactional
	public double getHeroWinRate(String id) {
		// TODO: User authentication
		int totalAppearances = matchRepository.findMatchesByHeroIdExcludingBanned(id).size();
		int totalWins = matchRepository.findMatchesByHeroIdWhereHeroWon(id).size();
		
		
		return totalAppearances > 0 ? (double)(100f * totalWins/totalAppearances) : 0f;
	}
}
