import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { authApi, setAuthToken, setUnauthorizedHandler } from '../services/APIService'

const STORAGE_KEY = 'rtastats.auth'
const AuthContext = createContext(null)

function readStoredSession() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY)
        const parsed = raw ? JSON.parse(raw) : null
        return parsed?.token ? parsed : null
    } catch {
        return null // storage unavailable or corrupt: behave as logged out
    }
}

function writeStoredSession(session) {
    try {
        if (session) localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
        else localStorage.removeItem(STORAGE_KEY)
    } catch {
        // Ignore: the session just won't survive a refresh
    }
}

// Holds who is logged in. A session is { token, email, role }, exactly what the backend's AuthResponse returns.
export function AuthProvider({ children }) {
    const queryClient = useQueryClient()

    // Read synchronously and hand the token to the API layer straight away. Child components fetch in their own
    // effects, and child effects run BEFORE this component's effects, so setting the token in an effect is too late.
    const [session, setSession] = useState(() => {
        const stored = readStoredSession()
        setAuthToken(stored?.token ?? null)
        return stored
    })
    // True while a stored token is being checked against the backend
    const [checking, setChecking] = useState(() => session !== null)

    // Used for login, register and logout: swaps the session and drops cached data from the previous user
    const replaceSession = useCallback(
        (next) => {
            setAuthToken(next?.token ?? null)
            writeStoredSession(next)
            setSession(next)
            queryClient.clear()
        },
        [queryClient]
    )

    const login = useCallback(
        async (email, password) => replaceSession(await authApi.login(email, password)),
        [replaceSession]
    )
    const register = useCallback(
        async (email, password) => replaceSession(await authApi.register(email, password)),
        [replaceSession]
    )
    const logout = useCallback(() => replaceSession(null), [replaceSession])

    // If any API call comes back 401 (token expired, user deleted), log out
    useEffect(() => {
        setUnauthorizedHandler(logout)
        return () => setUnauthorizedHandler(() => {})
    }, [logout])

    // On first load, confirm a stored token is still valid and refresh the role from the server
    useEffect(() => {
        if (!session) return
        let cancelled = false
        authApi
            .me()
            .then((me) => {
                if (cancelled) return
                const refreshed = { ...session, email: me.email, role: me.role }
                writeStoredSession(refreshed)
                setSession(refreshed)
            })
            .catch((error) => {
                // Only a 401 means "this token is no good". A network error keeps the session.
                if (!cancelled && error.response?.status === 401) replaceSession(null)
            })
            .finally(() => {
                if (!cancelled) setChecking(false)
            })
        return () => {
            cancelled = true
        }
        // Deliberately runs once, on mount
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const value = useMemo(
        () => ({
            user: session ? { email: session.email, role: session.role } : null,
            isAdmin: session?.role === 'ADMIN',
            checking,
            login,
            register,
            logout,
        }),
        [session, checking, login, register, logout]
    )

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) throw new Error('useAuth must be used inside <AuthProvider>')
    return context
}
