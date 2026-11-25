import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? "/api",
    headers: { "Content-Type": "application/json" },
    timeout: 10000,
});

// Authentication
let authToken = null
export const setAuthToken = (token) => { authToken = token }

let onUnauthorized = () => {}
export const setUnauthorizedHandler = (handler) => { onUnauthorized = handler }

api.interceptors.request.use((config) => {
    if (authToken) config.headers.Authorization = `Bearer ${authToken}`
    return config
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const isAuthCall = error.config?.url?.startsWith('/auth/')
        if (error.response?.status === 401 && !isAuthCall) onUnauthorized()
        return Promise.reject(error)
    }
)

export const authApi = {
    register: (email, password) => api.post('/auth/register', { email, password }).then(data),
    login: (email, password) => api.post('/auth/login', { email, password }).then(data),
    me: () => api.get('/auth/me').then(data),
}

// Path segments
const seg = (value) => encodeURIComponent(value);
const data = (res) => res.data;

// ---------- Heroes (/api/heroes) ----------
export const heroApi = {
    create: (hero) => api.post("/heroes", hero).then(data),
    getAll: () => api.get(`/heroes`).then(data),
    getById: (id) => api.get(`/heroes/${seg(id)}`).then(data),
    getByName: (name) => api.get(`/heroes/hero/${seg(name)}`).then(data),
    getByElement: (element) => api.get(`/heroes/element/${seg(element)}`).then(data),
    getByClass: (heroClass) => api.get(`/heroes/class/${seg(heroClass)}`).then(data),
    getByElementAndClass: (element, heroClass) =>
        api.get(`/heroes/element/${seg(element)}/class/${seg(heroClass)}`).then(data),
    searchByName: (name) => api.get(`/heroes/search/${seg(name)}`).then(data),
    update: (id, hero) => api.put(`/heroes/${seg(id)}`, hero).then(data),
    remove: (id) => api.delete(`/heroes/${seg(id)}`).then(data), // 204, resolves with empty data
};

// ---------- Stats (/api/stats) ----------
export const statsApi = {
    getWinRate: (heroId) => api.get(`/stats/winrate/${seg(heroId)}`).then(data), // returns a number
};

// ---------- Matches (/api/matches) ----------
export const matchApi = {
    create: (match) => api.post("/matches", match).then(data),
    getById: (id) => api.get(`/matches/${seg(id)}`).then(data),
    getAll: () => api.get("/matches").then(data),
    getByHeroId: (heroId) => api.get(`/matches/herosearch/${seg(heroId)}`).then(data),
    getByHeroIdExcludingBanned: (heroId) =>
        api.get(`/matches/herosearch/excludebanned/${seg(heroId)}`).then(data),
    getWinsByHeroId: (heroId) => api.get(`/matches/herosearch/wins/${seg(heroId)}`).then(data),
    getLossesByHeroId: (heroId) => api.get(`/matches/herosearch/losses/${seg(heroId)}`).then(data),
    update: (id, match) => api.put(`/matches/${seg(id)}`, match).then(data),
    remove: (id) => api.delete(`/matches/${seg(id)}`).then(data),
};

export function getErrorMessage(error) {
    return (
        error.response?.data?.message ??
        error.message ??
        "Something went wrong"
    );
}