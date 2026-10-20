import { useQuery, useQueries } from '@tanstack/react-query'
import { heroApi, statsApi, matchApi } from '../services/APIService'
import HeroTable from './HeroTable.jsx'

const TOP_N = 20
const MIN_MATCHES = 2            // raise this to hide heroes with tiny sample sizes (1 win from 1 match = 100%)
const WIN_RATE_IS_FRACTION = false

function toPercent(value) {
    if (typeof value !== 'number' || !Number.isFinite(value)) return null
    return WIN_RATE_IS_FRACTION ? value * 100 : value
}

function TopHeroesTable() {
    const heroesQuery = useQuery({
        queryKey: ['heroes'],
        queryFn: heroApi.getAll,
    })
    const heroes = heroesQuery.data ?? []

    // One win rate request per hero
    const winRateQueries = useQueries({
        queries: heroes.map((hero) => ({
            queryKey: ['stats', 'winrate', hero.id],
            queryFn: () => statsApi.getWinRate(hero.id),
        })),
    })

    // One matches request per hero; we only need the count
    const matchQueries = useQueries({
        queries: heroes.map((hero) => ({
            queryKey: ['matches', 'hero', hero.id, 'excludingBanned'],
            queryFn: () => matchApi.getByHeroIdExcludingBanned(hero.id),
            select: (matches) => matches.length,
        })),
    })

    const isPending =
        heroesQuery.isPending ||
        winRateQueries.some((q) => q.isPending) ||
        matchQueries.some((q) => q.isPending)

    const error =
        heroesQuery.error ??
        winRateQueries.find((q) => q.error)?.error ??
        matchQueries.find((q) => q.error)?.error

    if (error) return <p role="alert">Couldn't load hero stats. Check the backend is running and try again.</p>
    if (isPending) return <p>Loading hero stats...</p>

    const rows = heroes
        .map((hero, i) => ({
            id: hero.id,
            name: hero.name,
            element: hero.element,
            winRate: toPercent(winRateQueries[i].data),
            totalMatches: matchQueries[i].data ?? 0,
        }))
        .filter((row) => row.totalMatches >= MIN_MATCHES && row.winRate !== null)
        .sort((a, b) => b.winRate - a.winRate || b.totalMatches - a.totalMatches)
        .slice(0, TOP_N)

    if (rows.length === 0) return <p>No match data yet. Add a match on the Input page to see rankings.</p>

    return <HeroTable rows={rows} caption={`Top ${TOP_N} heroes by win rate`} />
}

export default TopHeroesTable
