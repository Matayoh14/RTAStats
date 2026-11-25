import Header from './Header.jsx'
import { BrowserRouter, Routes, Route } from "react-router-dom"
import TopHeroesTable from './TopHeroesTable.jsx';
import MatchForm from './MatchForm.jsx';
import AdminPage from './AdminPage.jsx';
import AuthForm from './AuthForm.jsx';

function RTAStatsApp() {
    return (
        <BrowserRouter >
            <Header />
            <main>
                <Routes>
                    <Route path="/" element={<TopHeroesTable />}/>
                    <Route path="/heroes" element={<h1>Heroes</h1>}/>
                    <Route path="/form" element={<MatchForm />}/>
                    <Route path="/admin/*" element={<AdminPage />}/>
                    <Route path="/login" element={<AuthForm mode="login" />} />
                    <Route path="/register" element={<AuthForm mode="register" />} />
                </Routes>
            </main>
        </BrowserRouter >
    )
}

export default RTAStatsApp;