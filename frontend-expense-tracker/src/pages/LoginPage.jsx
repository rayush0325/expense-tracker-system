import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { AuthContext } from "../context/AuthContext";
import apiClient from "../services/apiClient";

export default function LoginPage() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const {login} = useContext(AuthContext);

    const [errors, setErrors] = useState({});
    const navigate = useNavigate();
    async function handleSubmit(e) {
        e.preventDefault();

        const validationErrors = validate();
        setErrors(validationErrors);

        if (Object.keys(validationErrors) > 0) {
            return;
        }
        try {
            const loginResponse = await apiClient.post("/user/login", formData);
            login(loginResponse.data.jwtToken);
            const message = "logged in"
                    toast.success(message, {
                        toastId : message
                    });
            
        } catch (error) {
            console.error("error in logging in into backend");
            
            if(error.response.status === 401){
                const message = "invalid email or password";
                toast.error(message, {
                    toastId : message
                });
            }
        }


    }

    function validate() {
        const validationErrors = {};

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (formData.email.trim().length == 0) {
            validationErrors.email = "email can not be empty";
        }

        else if (!emailRegex.test(formData.email)) {
            validationErrors.email = "invalid email format";
        }

        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).+$/;
        if (formData.password.trim().length == 0) {
            validationErrors.password = "password can not be empty";
        }
        else if (formData.password.trim().length < 2 || formData.password.trim().length > 20) {
            validationErrors.password = "password length must be in range 2-20"
        }

        else if (!passwordRegex.test(formData.password)) {
            validationErrors.password = "password must have both numbers and letters"
        }

        return validationErrors;
    }

    function handleChange(event) {
        setFormData({
            ...formData,
            [event.target.id]: event.target.value
        });
    }
    return (
        <div className="vw-100 vh-100 d-flex justify-content-center align-items-center bg-dark text-white">
            <form action="" onSubmit={handleSubmit} className="w-50 p-3 border border-secondary rounded bg-secondary text-white">
                <h2 className="text-center">login form</h2>
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Email Address</label>
                    <input id="email" onChange={handleChange} className="form-control" placeholder="name@example.com" type="text" />
                    {errors.email && <div className="text-danger">{errors.email}</div>}
                </div>
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">Password</label>
                    <input id="password" onChange={handleChange} className="form-control" type="password" />
                    {errors.password && <div className="text-danger">{errors.password}</div>}
                </div>
                <button type="submit" className="btn btn-primary">login</button>
            </form>
        </div>  
    )
}