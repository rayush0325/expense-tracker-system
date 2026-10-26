import { useContext, useEffect, useState } from "react";
import apiClient from "../services/apiClient";

import { handleApiError } from "../utils/handleApiError";
import { AuthContext } from "./AuthContext";
import { UserContext } from "./UserContext";

export function UserProvider({children}){

    const {jwtToken} = useContext(AuthContext);
    const [user, setUser] = useState({
        id : "",
        name : "",
        email : "",
        role : ""
    })
    async function getProfileData(){
        try{
            if(!Boolean(jwtToken)){
                return;
            }
            
            const response = await apiClient.get("/user/profile");
            const profileResponse = response?.data;
            console.log(`fetching profileResponse = ${profileResponse}`)
            
            setUser(profileResponse);

        }catch(error){
            console.error("error in fetching profile response")

            handleApiError(error);

        }
        
    }


    useEffect(() =>{
            getProfileData();
    },[jwtToken]);
    return (
        <UserContext.Provider value={{user, setUser, getProfileData}}>
            {children}
        </UserContext.Provider>
    )
}