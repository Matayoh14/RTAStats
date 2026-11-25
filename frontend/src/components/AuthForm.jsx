import { useState } from 'react'
import { Link, Navigate, useLocation } from 'react-router-dom'
import { useAuth } from './AuthContext.jsx'
import { getErrorMessage } from '../services/APIService'
import '../AuthForm.css'

const MIN_PASSWORD_LENGTH = 8
const MAX_PASSWORD_LENGTH = 72 // BCrypt ignores anything past 72 bytes, and the backend enforces the same limit

// Friendly messages by status, so the page reads sensibly whether or not the backend exposes its error text
function messageFor(error) {
    switch (error.response?.status) {
        case 401:
            return 'Incorrect email or password.'
        case 409:
            return 'An account with that email already exists.'
        case 400:
            return 'Please check your details and try again.'
        default:
            return getErrorMessage(error)
    }
}

// One component for both pages: <AuthForm mode="login" /> and <AuthForm mode="register" />
function AuthForm({ mode }) {
    const isRegister = mode === 'register'
    const { user, login, register } = useAuth()
    const location = useLocation()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [submitting, setSubmitting] = useState(false)

    // Already logged in (including just now, after a successful submit): go on to where they were headed.
    // Route guards can pass the original page in location.state.from.
    if (user) return <Navigate to={location.state?.from ?? '/'} replace />

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')

        if (isRegister && password.length < MIN_PASSWORD_LENGTH) {
            setError(`Password must be at least ${MIN_PASSWORD_LENGTH} characters.`)
            return
        }

        setSubmitting(true)
        try {
            await (isRegister ? register : login)(email.trim(), password)
            // On success the session changes and the redirect above takes over
        } catch (err) {
            setError(messageFor(err))
            setSubmitting(false)
        }
    }

    return (
        <form className="auth-form" onSubmit={handleSubmit}>
            <h2>{isRegister ? 'Create an account' : 'Log in'}</h2>

            <label>
                Email
                <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    autoComplete="email"
                    required
                />
            </label>

            <label>
                Password
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    autoComplete={isRegister ? 'new-password' : 'current-password'}
                    minLength={isRegister ? MIN_PASSWORD_LENGTH : undefined}
                    maxLength={MAX_PASSWORD_LENGTH}
                    required
                />
                {isRegister && <span className="auth-form__hint">At least {MIN_PASSWORD_LENGTH} characters.</span>}
            </label>

            {error && <p role="alert" className="auth-form__error">{error}</p>}

            <button type="submit" disabled={submitting}>
                {submitting ? 'Please wait...' : isRegister ? 'Register' : 'Log in'}
            </button>

            <p className="auth-form__switch">
                {isRegister ? (
                    <>Already have an account? <Link to="/login" state={location.state}>Log in</Link></>
                ) : (
                    <>No account yet? <Link to="/register" state={location.state}>Register</Link></>
                )}
            </p>
        </form>
    )
}

export default AuthForm
