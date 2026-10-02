import { useState } from "react";
import apiClient from "../services/apiClient";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function RegisterPage() {


    const [formData, setFormData] = useState({
        email: "",
        name: "",
        password: ""
    });
    const [formErrors, setFormErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();
    async function handleSubmit(e) {
        e.preventDefault();
        const validationErrors = validateFormData();
        setFormErrors(validationErrors);
        

        if(Object.keys(validationErrors).length > 0){
            //form has error
            return;
        }
        setLoading(true);
        try {
            await apiClient.post("/user/register", formData);
            toast.success("Registered successfully! \nPlease login.");
            navigate("/login");
        } catch (error) {
            if (error.response) {
                const message = error?.response?.data?.title || "something went wrong";
                toast.error(message, {
                    toastId: message
                });
            }
            else if (error.request) {
                const message = "unable to connect to seerver"
                toast.error(message, {
                    toastId: message
                });
            }
            else {
                const message = "something went wrong"
                toast.error(message, {
                    toastId: message
                });
            }



        } finally {
            setLoading(false);
        }

    }
    function handleFieldChange(e) {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value
        });
    }
    function validateFormData() {
        let validationErrors = {};

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (formData.email.trim().length == 0) {
            validationErrors.email = "email can not be empty";
        }

        else if (!emailRegex.test(formData.email)) {
            validationErrors.email = "invalid email format";
        }

        const nameRegex = /^[A-Za-z ]+$/;
        if (formData.name.trim().length == 0) {
            validationErrors.name = "name can not be empty"
        }
        else if (!nameRegex.test(formData.name)) {
            validationErrors.name = "name should have only letters";
        }
        else if (formData.name.trim().length < 2 || formData.name.trim().length > 20) {
            validationErrors.name = "name length must be in range 2-20"
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
    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-dark text-white">
            <form onSubmit={handleSubmit} className="w-50 p-3 border border-secondary rounded bg-secondary text-white">
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">Email address</label>
                    <input id="email"
                        onChange={handleFieldChange}
                        value={formData.email}
                        type="text" className="form-control"
                        placeholder="name@example.com" />
                    {formErrors.email && (<div className="text-danger">{formErrors.email}</div>)}
                </div>

                <div className="mb-3">
                    <label htmlFor="name" className="form-label">Full Name</label>
                    <input id="name"
                        value={formData.name}
                        onChange={handleFieldChange}
                        className="form-control" type="text"
                        placeholder="full name"
                        aria-label="default input example" />
                    {formErrors.name && (<div className="text-danger">{formErrors.name}</div>)}
                </div>
                <div className="mb-3">
                    <label htmlFor="password" className="form-label">Password</label>
                    <input id="password"
                        value={formData.password}
                        onChange={handleFieldChange}
                        type="password"
                        className="form-control" />
                    {formErrors.password && (<div className="text-danger">{formErrors.password}</div>)}
                </div>
                <button type="submit" disabled={loading} className="btn btn-primary">
                    {loading ? "Registering..." : "Register"}
                </button>
            </form>
        </div>
    )
}