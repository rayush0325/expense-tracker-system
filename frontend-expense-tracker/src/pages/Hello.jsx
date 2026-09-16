import { useEffect, useState } from "react";
import apiClient from "../services/apiClient"

export default function Hello() {
    const [message, setMessage] = useState("");

    useEffect(callback, [])

    function callback() {
        console.log("hello from callback");
        getData();
    }

    async function getData() {

        try {
            const response = await apiClient.get('/hello');
            setMessage(response.data.message);
        } catch (error) {
            console.log(error.message);
            setMessage(error.message);
        }
    }   


    return <h1>{message}</h1>
}


