import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";

export default function AuthProvider({children}){

    const [jwtToken, setJwtToken] = useState(null);
    const navigate = useNavigate();
    
    function logout(){
        console.log("logging out")

        setJwtToken(null);
        setLoggedIn(false);
        localStorage.removeItem("jwtToken");
        navigate("/");
    }

    function login(jwtToken){
        console.log(`logging in with jwtToken = ${jwtToken}`)

        setJwtToken(jwtToken);
        localStorage.setItem("jwtToken", jwtToken);
        setLoggedIn(true);
        navigate("/profile")
    }


   
    return (
        <AuthContext.Provider value={{jwtToken, setJwtToken, loggedIn, setLoggedIn, logout, login}}>
            {children}
        </AuthContext.Provider>
    )
}