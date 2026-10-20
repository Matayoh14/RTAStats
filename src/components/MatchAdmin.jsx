import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { matchApi, getErrorMessage } from '../services/APIService'
import MatchForm from './MatchForm.jsx'
import '../MatchAdmin.css'

const EXPECTED_PICKS = 5

const picksFor = (match, team) =>
    match.picks.filter((p) => p.team === team).sort((a, b) => a.pickOrder - b.pickOrder)

// Flags matches that break the rules the input form enforces, so corrupted data stands out
function findProblems(match) {
    const problems = []
    for (const team of [1, 2]) {
        const picks = picksFor(match, team)
        const postbans = picks.filter((p) => p.banned).length
        if (picks.length !== EXPECTED_PICKS) problems.push(`Team ${team} has ${picks.length} picks`)
        if (postbans !== 1) problems.push(`Team ${team} has ${postbans} postbans`)
    }
    return problems
}

const formatDate = (value) => (value ? new Date(value).toLocaleString() : '–')

function PickNames({ picks }) {
    if (picks.length === 0) return <span className="muted">None</span>
    return (
        <ol className="pick-list">
            {picks.map((p, i) => (
                <li key={i} className={p.banned ? 'banned' : undefined}>
                    {p.heroName}
                    {p.banned ? ' (postbanned)' : ''}
                </li>
            ))}
        </ol>
    )
}

function MatchAdmin() {
    const queryClient = useQueryClient()
    const [editingMatch, setEditingMatch] = useState(null)

    const matchesQuery = useQuery({ queryKey: ['matches', 'all'], queryFn: matchApi.getAll })
    const matches = Array.isArray(matchesQuery.data) ? matchesQuery.data : []

    const deleteMutation = useMutation({
        mutationFn: matchApi.remove,
        onSuccess: (_data, deletedId) => {
            queryClient.invalidateQueries({ queryKey: ['matches'] })
            queryClient.invalidateQueries({ queryKey: ['stats'] })
            if (editingMatch?.id === deletedId) setEditingMatch(null)
        },
    })

    const handleEdit = (match) => {
        setEditingMatch(match)
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    const handleDelete = (match) => {
        if (window.confirm(`Delete the match from ${formatDate(match.createdAt)}? This cannot be undone.`)) {
            deleteMutation.mutate(match.id)
        }
    }

    return (
        <section className="match-admin">
            {editingMatch && (
                <MatchForm
                    key={editingMatch.id}
                    match={editingMatch}
                    onDone={() => setEditingMatch(null)}
                />
            )}

            <h2>Manage matches</h2>

            {deleteMutation.isError && (
                <p role="alert" className="match-admin__error">
                    Couldn't delete: {getErrorMessage(deleteMutation.error)}
                </p>
            )}

            {matchesQuery.isPending && <p>Loading matches...</p>}
            {matchesQuery.error && <p role="alert">Couldn't load matches. Check the backend is running.</p>}
            {matchesQuery.isSuccess && matches.length === 0 && <p>No matches yet.</p>}

            {matches.length > 0 && (
                <div className="match-admin__scroll">
                    <table className="match-table">
                        <thead>
                            <tr>
                                <th scope="col">Date</th>
                                <th scope="col">Result</th>
                                <th scope="col">Team 1 (first pick)</th>
                                <th scope="col">Team 2</th>
                                <th scope="col">Prebans</th>
                                <th scope="col">Check</th>
                                <th scope="col"><span className="sr-only">Actions</span></th>
                            </tr>
                        </thead>
                        <tbody>
                            {matches.map((match) => {
                                const problems = findProblems(match)
                                return (
                                    <tr key={match.id} className={editingMatch?.id === match.id ? 'editing' : undefined}>
                                        <td>{formatDate(match.createdAt)}</td>
                                        <td>{match.fpWin ? 'Team 1 won' : 'Team 2 won'}</td>
                                        <td><PickNames picks={picksFor(match, 1)} /></td>
                                        <td><PickNames picks={picksFor(match, 2)} /></td>
                                        <td>
                                            {match.prebans.length > 0
                                                ? match.prebans.map((h) => h.name).join(', ')
                                                : '–'}
                                        </td>
                                        <td>
                                            {problems.length === 0 ? (
                                                'OK'
                                            ) : (
                                                <ul className="problems">
                                                    {problems.map((text) => (
                                                        <li key={text}>{text}</li>
                                                    ))}
                                                </ul>
                                            )}
                                        </td>
                                        <td className="actions">
                                            <button type="button" onClick={() => handleEdit(match)}>Edit</button>
                                            <button
                                                type="button"
                                                className="danger"
                                                onClick={() => handleDelete(match)}
                                                disabled={deleteMutation.isPending}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    )
}

export default MatchAdmin
