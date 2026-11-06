import { useContext } from "react";
import { UserContext } from "../context/UserContext";

export default function ProfilePage() {

    const { user } = useContext(UserContext);

    return (
        // <div className="min-vh-100 bg-dark text-white d-flex justify-content-center align-items-center p-3">

        //     <div className="card bg-dark text-white border-secondary rounded-4 shadow w-100" style={{maxWidth : "550px"}}>
        //         <h1 className="text-center">Welcome</h1>
        //         <h2>your profile</h2>
        //         <h3>id : {user.id}</h3>
        //         <h3>name : {user.name}</h3>
        //         <h3>email : {user.email}</h3>
        //         <h3>role : {user.role}</h3>
        //     </div>
        // </div>

        <div className="min-vh-100 bg-dark text-white d-flex justify-content-center align-items-center p-3">

            <div className="card bg-dark text-white border-secondary rounded-4 shadow w-100"
                style={{ maxWidth: "550px" }}>

                <div className="card-body p-4 p-md-5">

                    <h1 className="text-center mb-2">Welcome!</h1>
                    <p className="text-center text-secondary mb-4">
                        Your Profile
                    </p>

                    <hr className="border-secondary mb-4" />

                    <div className="mb-3">
                        <label className="text-secondary small">User ID</label>
                        <h5>{user.id}</h5>
                    </div>

                    <div className="mb-3">
                        <label className="text-secondary small">Name</label>
                        <h5>{user.name}</h5>
                    </div>

                    <div className="mb-3">
                        <label className="text-secondary small">Email</label>
                        <h5 className="text-break">{user.email}</h5>
                    </div>

                    <div>
                        <label className="text-secondary small">Role</label>
                        <div>
                            <span className="badge text-bg-secondary fs-6">
                                {user.role}
                            </span>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    )

}