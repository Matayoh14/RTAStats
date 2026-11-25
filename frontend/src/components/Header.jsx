import { NavLink } from "react-router-dom";
import { useAuth } from "./AuthContext.jsx";
import "../style.css";

function Header() {
  const { user, logout } = useAuth();

  return (
    <nav>
      <NavLink className="tab" to="/">
        RTAStats
      </NavLink>
      <NavLink className="tab" to="/heroes">
        Heroes
      </NavLink>
      <NavLink className="tab" to="/form">
        Input
      </NavLink>
      <NavLink className="tab" to="/admin">
        Admin
      </NavLink>
      {user ? (
        <>
          <span className="nav-user tab">{user.email}</span>
          <button type="button" onClick={logout}>
            Log out
          </button>
        </>
      ) : (
        <>
          <NavLink className="tab" to="/login">
            Log in
          </NavLink>
          <NavLink className="tab" to="/register">
            Register
          </NavLink>
        </>
      )}
    </nav>
  );
}

export default Header;
