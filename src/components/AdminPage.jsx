import { NavLink, Navigate, Route, Routes } from 'react-router-dom'
import HeroAdmin from './HeroAdmin.jsx'
import MatchAdmin from './MatchAdmin.jsx'
import '../AdminPage.css'

// Everything under /admin/* lives here, so step 3 (authentication) only has to guard this one component.
function AdminPage() {
    return (
        <div className="admin-page">
            {/* A div rather than <nav>, so it doesn't pick up the header's "nav a" styles */}
            <div className="admin-subnav" role="navigation" aria-label="Admin sections">
                <NavLink to="/admin/heroes">Heroes</NavLink>
                <NavLink to="/admin/matches">Matches</NavLink>
            </div>

            <Routes>
                <Route index element={<Navigate to="/admin/heroes" replace />} />
                <Route path="heroes" element={<HeroAdmin />} />
                <Route path="matches" element={<MatchAdmin />} />
            </Routes>
        </div>
    )
}

export default AdminPage
