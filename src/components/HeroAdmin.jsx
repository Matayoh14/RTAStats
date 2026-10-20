import { useMemo, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { heroApi, getErrorMessage } from '../services/APIService'
import { ELEMENTS, HERO_CLASSES } from '../constants'
import '../HeroTable.css' // reused for the element-coloured rows
import '../HeroAdmin.css'

const EMPTY_FORM = { name: '', element: ELEMENTS[0], heroClass: HERO_CLASSES[0] }
const byName = (a, b) => a.name.localeCompare(b.name)

function HeroAdmin() {
    const queryClient = useQueryClient()
    const [form, setForm] = useState(EMPTY_FORM)
    const [editingId, setEditingId] = useState(null) // null = adding a new hero
    const [validationError, setValidationError] = useState('')

    const heroesQuery = useQuery({ queryKey: ['heroes'], queryFn: heroApi.getAll })
    const heroes = useMemo(
        () => (Array.isArray(heroesQuery.data) ? [...heroesQuery.data].sort(byName) : []),
        [heroesQuery.data]
    )

    const resetForm = () => {
        setForm(EMPTY_FORM)
        setEditingId(null)
        setValidationError('')
    }
    const refresh = () => queryClient.invalidateQueries({ queryKey: ['heroes'] })

    const saveMutation = useMutation({
        mutationFn: ({ id, hero }) => (id ? heroApi.update(id, hero) : heroApi.create(hero)),
        onSuccess: () => {
            refresh()
            resetForm()
        },
    })

    const deleteMutation = useMutation({
        mutationFn: heroApi.remove,
        onSuccess: (_data, deletedId) => {
            refresh()
            if (deletedId === editingId) resetForm()
        },
    })

    const startEdit = (hero) => {
        setForm({ name: hero.name, element: hero.element, heroClass: hero.heroClass })
        setEditingId(hero.id)
        setValidationError('')
        saveMutation.reset()
    }

    const handleDelete = (hero) => {
        if (window.confirm(`Delete ${hero.name}? This cannot be undone.`)) {
            deleteMutation.mutate(hero.id)
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        const name = form.name.trim()
        if (!name) {
            setValidationError('Name is required')
            return
        }
        // The match form looks heroes up by name, so names need to be unique
        const duplicate = heroes.some(
            (h) => h.id !== editingId && h.name.trim().toLowerCase() === name.toLowerCase()
        )
        if (duplicate) {
            setValidationError(`A hero named "${name}" already exists`)
            return
        }
        setValidationError('')
        // Field names must match HeroRequest on the backend
        saveMutation.mutate({ id: editingId, hero: { name, element: form.element, heroClass: form.heroClass } })
    }

    const update = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))

    return (
        <section className="hero-admin">
            <h2>Manage heroes</h2>

            <form className="hero-admin__form" onSubmit={handleSubmit} noValidate>
                <h3>{editingId ? 'Edit hero' : 'Add hero'}</h3>

                <label>
                    Name
                    <input value={form.name} onChange={update('name')} placeholder="Hero name" autoComplete="off" />
                </label>
                <label>
                    Element
                    <select value={form.element} onChange={update('element')}>
                        {ELEMENTS.map((el) => (
                            <option key={el} value={el}>{el}</option>
                        ))}
                    </select>
                </label>
                <label>
                    Class
                    <select value={form.heroClass} onChange={update('heroClass')}>
                        {HERO_CLASSES.map((c) => (
                            <option key={c} value={c}>{c}</option>
                        ))}
                    </select>
                </label>

                <div className="hero-admin__buttons">
                    <button type="submit" disabled={saveMutation.isPending}>
                        {saveMutation.isPending ? 'Saving...' : editingId ? 'Save changes' : 'Add hero'}
                    </button>
                    {editingId && (
                        <button type="button" className="secondary" onClick={resetForm}>Cancel</button>
                    )}
                </div>

                {validationError && <p role="alert" className="hero-admin__error">{validationError}</p>}
                {saveMutation.isError && (
                    <p role="alert" className="hero-admin__error">Couldn't save: {getErrorMessage(saveMutation.error)}</p>
                )}
            </form>

            {deleteMutation.isError && (
                <p role="alert" className="hero-admin__error">Couldn't delete: {getErrorMessage(deleteMutation.error)}</p>
            )}

            {heroesQuery.isPending && <p>Loading heroes...</p>}
            {heroesQuery.error && <p role="alert">Couldn't load heroes. Check the backend is running.</p>}
            {heroesQuery.isSuccess && heroes.length === 0 && <p>No heroes yet. Add one above.</p>}

            {heroes.length > 0 && (
                <table className="hero-table">
                    <thead>
                        <tr>
                            <th scope="col">Name</th>
                            <th scope="col">Element</th>
                            <th scope="col">Class</th>
                            <th scope="col"><span className="sr-only">Actions</span></th>
                        </tr>
                    </thead>
                    <tbody>
                        {heroes.map((hero) => (
                            <tr key={hero.id} data-element={hero.element}>
                                <td>{hero.name}</td>
                                <td>{hero.element}</td>
                                <td>{hero.heroClass}</td>
                                <td className="actions">
                                    <button type="button" onClick={() => startEdit(hero)}>Edit</button>
                                    <button
                                        type="button"
                                        className="danger"
                                        onClick={() => handleDelete(hero)}
                                        disabled={deleteMutation.isPending}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </section>
    )
}

export default HeroAdmin
