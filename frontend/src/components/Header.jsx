import { NavLink } from "react-router-dom"
import '../style.css'

function Header() {
    return (
        <nav>
            <NavLink class="tab" to="/">RTAStats</NavLink>
            <NavLink class="tab" to="/heroes">Heroes</NavLink>
            <NavLink class="tab" to="/form">Input</NavLink>
            <NavLink class="tab" to="/admin">Admin</NavLink>
        </nav>
    )
}

export default Header