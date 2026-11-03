package matthewholmes.e7rtastatsapp.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import matthewholmes.e7rtastatsapp.backend.dto.MatchRequest;
import matthewholmes.e7rtastatsapp.backend.dto.MatchResponse;
import matthewholmes.e7rtastatsapp.backend.dto.HeroResponse;
import matthewholmes.e7rtastatsapp.backend.dto.MatchHeroRequest;
import matthewholmes.e7rtastatsapp.backend.dto.MatchHeroResponse;
import matthewholmes.e7rtastatsapp.backend.model.Match;
import matthewholmes.e7rtastatsapp.backend.model.MatchHero;
import matthewholmes.e7rtastatsapp.backend.model.Hero;
import matthewholmes.e7rtastatsapp.backend.repository.HeroRepository;
import matthewholmes.e7rtastatsapp.backend.repository.MatchRepository;

@Service
@RequiredArgsConstructor
public class MatchService {

	private final HeroRepository heroRepository;
	private final MatchRepository matchRepository;
	
	@Transactional
	public MatchResponse createMatch(MatchRequest request) {
		// TODO: user authentication
		Match match = new Match();
		applyRequest(match, request);

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
	public List<MatchResponse> getAllMatches() {
		// TODO: user authentication
	    return matchRepository.findAllByOrderByCreatedAtDesc()
	            .stream()
	            .map(this::mapToResponse)
	            .collect(Collectors.toList());
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
		
		applyRequest(match, request);
		
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
				.fpWin(match.isFpWin())
				.picks(match.getPicks().stream().map(this::toMatchHeroResponse).toList())
				.prebans(match.getPrebans().stream().map(this::toHeroResponse).toList())
				.userTeam(match.getUserTeam())
				.createdAt(match.getCreatedAt())
				.updatedAt(match.getUpdatedAt())
				.build();
	}

	public HeroResponse toHeroResponse(Hero hero) {
		return HeroResponse.builder()
				.id(hero.getId())
				.name(hero.getName())
				.element(hero.getElement())
				.heroClass(hero.getHeroClass())
				.createdAt(hero.getCreatedAt())
				.updatedAt(hero.getUpdatedAt())
				.build();
	}

	public MatchHeroResponse toMatchHeroResponse(MatchHero mh) {
		return MatchHeroResponse.builder()
				.heroId(mh.getHero().getId())
				.heroName(mh.getHero().getName())
				.heroElement(mh.getHero().getElement())
				.team(mh.getTeam())
				.pickOrder(mh.getPickOrder())
				.banned(mh.isBanned())
				.build();
	}

	private void applyRequest(Match match, MatchRequest request) {
		Set<String> ids = new HashSet<>();
		request.getPicks().forEach(p -> ids.add(p.getHeroId()));
		request.getPrebanHeroIds().forEach(ids::add);

		Map<String, Hero> heroes = heroRepository.findAllById(ids).stream()
				.collect(Collectors.toMap(Hero::getId, Function.identity()));

		Function<String, Hero> find = heroId -> {
			Hero hero = heroes.get(heroId);
			if (hero == null)
				throw new RuntimeException("Hero not found: " + heroId);
			return hero;
		};

		match.setFpWin(request.isFpWin());
		match.setUserTeam(request.getUserTeam());

		match.getPicks().clear();
		for (MatchHeroRequest p : request.getPicks()) {
			MatchHero mh = new MatchHero();
			mh.setMatch(match);
			mh.setHero(find.apply(p.getHeroId()));
			mh.setTeam(p.getTeam());
			mh.setPickOrder(p.getPickOrder());
			mh.setBanned(p.isBanned());
			match.getPicks().add(mh);
		}

		match.getPrebans().clear();
		request.getPrebanHeroIds().forEach(heroId -> match.getPrebans().add(find.apply(heroId)));
	}

}
