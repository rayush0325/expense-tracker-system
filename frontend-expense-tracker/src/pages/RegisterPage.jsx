import { useState } from "react";
import apiClient from "../services/apiClient";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function RegisterPage() {

    const [email, setEmail] = useState("");
    const [fullName, setFullName] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();
    async function handleSubmit(e) {
        e.preventDefault();

        try {
            const response = await apiClient.post("/user/register", {
                email : email,
                name : fullName,
                password : password
            });
            toast.success("Registered successfully! Please login.");
            navigate("/login");
        } catch (error) {
            const message = error?.response?.data?.title || "something went wrong";
            toast.error(message);
        }

    }
    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-dark text-white">
            <form onSubmit={handleSubmit} className="w-50 p-3 border border-secondary rounded bg-secondary text-white">
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Email address</label>
                    <input id="email" onChange={(e) => { setEmail(e.target.value) }} type="email" className="form-control"  placeholder="name@example.com" />
                </div>
                <div className="mb-3">
                    <label htmlFor="fullName" className="form-label">Full Name</label>
                    <input id="fullName" onChange={(e) => { setFullName(e.target.value) }} className="form-control" type="text"  placeholder="full name" aria-label="default input example" />

                </div>
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">Password</label>
                    <input id="password" onChange={(e) => { setPassword( e.target.value) }} type="password"  className="form-control" />
                </div>
                <button type="submit" className="btn btn-primary">Submit</button>
            </form>
        </div>
    )
}