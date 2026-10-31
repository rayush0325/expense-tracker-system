import { useContext } from "react";
import { UserContext } from "../context/UserContext";

export default function ProfilePage(){
    
    const {user} = useContext(UserContext); 

    return (
        <div className="vh-100 vw-100 d-flex justify-content-center align-items-center bg-dark text-white">
            <div className="w-75 p-3 border border-secondary rounded bg-secondary text-white">
                <h1 className="text-center">Welcome {user.name}</h1>
                <h2>your profile</h2>   
                <h3>id : {user.id}</h3>
                <h3>name : {user.name}</h3>
                <h3>email : {user.email}</h3>
                <h3>role : {user.role}</h3>
            </div>
        </div>
    )

}