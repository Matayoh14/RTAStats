import { useMemo, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { heroApi, matchApi, getErrorMessage } from '../services/APIService'
import '../MatchForm.css'

const PICKS_PER_SIDE = 5
const PREBANS_PER_SIDE = 2

// "left" is your team, "right" is the opponent. Victory always refers to the left side.
const SIDES = [
    { key: 'left', title: 'Your team' },
    { key: 'right', title: 'Opponent' },
]

const emptySide = () => ({
    picks: Array(PICKS_PER_SIDE).fill(''),
    prebans: Array(PREBANS_PER_SIDE).fill(''),
    postban: null, // index (0-4) of the postbanned pick, or null if none chosen yet
})
const emptySides = () => ({ left: emptySide(), right: emptySide() })

const lookup = (heroByName, text) => heroByName.get(text.trim().toLowerCase())

// Returns an object of { 'left.picks.2': 'message', ... }. Empty object means valid.
function validate(sides, heroByName) {
    const errors = {}
    const pickOwners = new Map() // heroId -> [field keys], picks only

    for (const { key: side } of SIDES) {
        sides[side].picks.forEach((name, i) => {
            const field = `${side}.picks.${i}`
            if (!name.trim()) {
                errors[field] = 'Required'
                return
            }
            const hero = lookup(heroByName, name)
            if (!hero) {
                errors[field] = 'Not a known hero'
                return
            }
            pickOwners.set(hero.id, [...(pickOwners.get(hero.id) ?? []), field])
        })

        const prebanOwners = new Map()
        sides[side].prebans.forEach((name, i) => {
            const field = `${side}.prebans.${i}`
            if (!name.trim()) return
            const hero = lookup(heroByName, name)
            if (!hero) {
                errors[field] = 'Not a known hero'
                return
            }
            prebanOwners.set(hero.id, [...(prebanOwners.get(hero.id) ?? []), field])
        })
        for (const fields of prebanOwners.values()) {
            if (fields.length > 1) fields.forEach((f) => (errors[f] = 'Already prebanned by this player'))
        }

        if (sides[side].postban === null) errors[`${side}.postban`] = 'Select a postban'
    }

    // A hero can only be picked once per match
    for (const fields of pickOwners.values()) {
        if (fields.length > 1) fields.forEach((f) => (errors[f] = 'Already picked in this match'))
    }
    return errors
}

// The ONE place that knows the backend's MatchRequest shape. Adjust field names here if yours differ.
function buildMatchRequest({ victory, firstPick, sides, heroByName }) {
    const secondPick = firstPick === 'left' ? 'right' : 'left'

    const picksFor = (sideKey, teamNumber) =>
        sides[sideKey].picks.map((name, i) => ({
            heroId: lookup(heroByName, name).id,
            team: teamNumber,
            pickOrder: i + 1,
            banned: sides[sideKey].postban === i,
        }))

    return {
        // The first-pick side is always team 1. Victory refers to the left side, so the
        // first-pick team won if (left won and left picked first) or (left lost and right picked first).
        fpWin: victory === (firstPick === 'left'),
        picks: [...picksFor(firstPick, 1), ...picksFor(secondPick, 2)],
        prebanHeroIds: [...sides.left.prebans, ...sides.right.prebans]
            .filter((name) => name.trim())
            .map((name) => lookup(heroByName, name).id),
    }
}

// Text input backed by the shared <datalist>: type to search, pick a suggestion.
function HeroInput({ value, onChange, label, error }) {
    return (
        <div className="field">
            <input
                list="hero-options"
                value={value}
                onChange={(e) => onChange(e.target.value)}
                placeholder="Hero name"
                aria-label={label}
                aria-invalid={error ? 'true' : 'false'}
                autoComplete="off"
            />
            {error && <span className="field__error">{error}</span>}
        </div>
    )
}

function MatchForm() {
    const queryClient = useQueryClient()
    const [victory, setVictory] = useState(false)
    const [firstPick, setFirstPick] = useState('left')
    const [sides, setSides] = useState(emptySides)
    const [submitted, setSubmitted] = useState(false)

    const heroesQuery = useQuery({ queryKey: ['heroes'], queryFn: heroApi.getAll })

    const heroByName = useMemo(() => {
        const list = Array.isArray(heroesQuery.data) ? heroesQuery.data : []
        return new Map(list.map((h) => [h.name.trim().toLowerCase(), h]))
    }, [heroesQuery.data])

    const heroNames = useMemo(
        () => [...heroByName.values()].map((h) => h.name).sort((a, b) => a.localeCompare(b)),
        [heroByName]
    )

    const errors = useMemo(() => validate(sides, heroByName), [sides, heroByName])

    const resetForm = () => {
        setVictory(false)
        setFirstPick('left')
        setSides(emptySides())
        setSubmitted(false)
    }

    const mutation = useMutation({
        mutationFn: matchApi.create,
        onSuccess: () => {
            // Refresh anything derived from match data (the homepage table uses these keys)
            queryClient.invalidateQueries({ queryKey: ['matches'] })
            queryClient.invalidateQueries({ queryKey: ['stats'] })
            resetForm()
        },
    })

    const setSlot = (side, field, index, value) =>
        setSides((prev) => ({
            ...prev,
            [side]: {
                ...prev[side],
                [field]: prev[side][field].map((v, i) => (i === index ? value : v)),
            },
        }))

    const setPostban = (side, index) =>
        setSides((prev) => ({ ...prev, [side]: { ...prev[side], postban: index } }))

    const handleSubmit = (e) => {
        e.preventDefault()
        setSubmitted(true)
        if (Object.keys(errors).length > 0) return
        mutation.mutate(buildMatchRequest({ victory, firstPick, sides, heroByName }))
    }

    if (heroesQuery.isPending) return <p>Loading heroes...</p>
    if (heroesQuery.error) return <p role="alert">Couldn't load heroes. Check the backend is running.</p>

    const errorFor = (field) => (submitted ? errors[field] : undefined)

    return (
        <form className="match-form" onSubmit={handleSubmit} noValidate>
            <h2>Add match</h2>

            <datalist id="hero-options">
                {heroNames.map((name) => (
                    <option key={name} value={name} />
                ))}
            </datalist>

            <div className="match-form__result">
                <label>
                    <input type="checkbox" checked={victory} onChange={(e) => setVictory(e.target.checked)} />
                    Victory (your team won)
                </label>
            </div>

            <div className="match-form__sides">
                {SIDES.map(({ key, title }) => (
                    <fieldset className="side" key={key}>
                        <legend>{title}</legend>

                        <label className="side__firstpick">
                            <input
                                type="radio"
                                name="firstPick"
                                value={key}
                                checked={firstPick === key}
                                onChange={() => setFirstPick(key)}
                            />
                            First pick
                        </label>

                        <h3>Prebans (optional)</h3>
                        {sides[key].prebans.map((name, i) => (
                            <div className="slot" key={i}>
                                <HeroInput
                                    value={name}
                                    onChange={(v) => setSlot(key, 'prebans', i, v)}
                                    label={`${title} preban ${i + 1}`}
                                    error={errorFor(`${key}.prebans.${i}`)}
                                />
                            </div>
                        ))}

                        <h3>Picks</h3>
                        {sides[key].picks.map((name, i) => (
                            <div className="slot" key={i}>
                                <HeroInput
                                    value={name}
                                    onChange={(v) => setSlot(key, 'picks', i, v)}
                                    label={`${title} pick ${i + 1}`}
                                    error={errorFor(`${key}.picks.${i}`)}
                                />
                                <label className="postban">
                                    <input
                                        type="radio"
                                        name={`postban-${key}`}
                                        checked={sides[key].postban === i}
                                        onChange={() => setPostban(key, i)}
                                        aria-label={`${title} pick ${i + 1} was postbanned`}
                                    />
                                    Postban
                                </label>
                            </div>
                        ))}
                        {errorFor(`${key}.postban`) && (
                            <span className="field__error">{errorFor(`${key}.postban`)}</span>
                        )}
                    </fieldset>
                ))}
            </div>

            {submitted && Object.keys(errors).length > 0 && (
                <p role="alert" className="match-form__error">Please fix the highlighted fields.</p>
            )}
            {mutation.isError && (
                <p role="alert" className="match-form__error">Couldn't save: {getErrorMessage(mutation.error)}</p>
            )}
            {mutation.isSuccess && (
                <p role="status" className="match-form__success">Match saved.</p>
            )}

            <button type="submit" disabled={mutation.isPending}>
                {mutation.isPending ? 'Saving...' : 'Submit match'}
            </button>
        </form>
    )
}

export default MatchForm
