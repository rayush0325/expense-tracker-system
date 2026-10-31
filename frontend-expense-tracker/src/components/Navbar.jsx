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
                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarContent"
                    aria-controls="navbarContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse " id="navbarContent">
                    <div className="navbar-nav ms-auto  ">
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
            </div>
        </nav>
    )
}