import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "./AuthContext";

export default function AuthProvider({children}){

    const [jwtToken, setJwtToken] = useState(null);
    const [loggedIn, setLoggedIn] = useState(false);
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


    useEffect(()=>{
        console.log("app started")
        const jwtToken = localStorage.getItem("jwtToken");
        if(Boolean(jwtToken)){
            console.log("jwt is present");
            login(jwtToken);
        }
        else{
            console.log("go to home page")
            navigate("/");
        }
    },[]);

    window.addEventListener("auth : unauthorized", logout);

    return (
        <AuthContext.Provider value={{jwtToken, setJwtToken, loggedIn, setLoggedIn, logout, login}}>
            {children}
        </AuthContext.Provider>
    )
}