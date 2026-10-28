import { useContext } from "react";
import { Link, NavLink } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { UserContext } from "../context/UserContext";
export function Navbar() {
    const { loggedIn, logout } = useContext(AuthContext);

    function handleLogout() {
        logout();
    }
    const { user } = useContext(UserContext);
    return (
        <nav className="navbar navbar-expand-lg bg-body-tertiary">
            <div className="container-fluid">
                <Link className="navbar-brand" to="/">Expense Tracker</Link>
                <div className="navbar-nav me-auto  ">
                    <NavLink className="nav-link" to="/">Home</NavLink>
                    {!loggedIn && <NavLink className="nav-link" to="/register">Register</NavLink>}
                    {!loggedIn && <NavLink className="nav-link" to="/login">Login</NavLink>}
                    {loggedIn && <NavLink className="nav-link" to="/profile">Profile</NavLink>}
                </div>
                {loggedIn && (
                    <div className="d-flex align-items-center ms-auto gap-3">
                        <span className="navbar-brand" >{user.name}</span>
                        <button type="button" onClick={handleLogout} className="btn btn-outline-danger">Logout</button>
                    </div>
                )}
            </div>
        </nav>
    )
}