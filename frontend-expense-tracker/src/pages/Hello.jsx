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
            console.log("try")
            console.log(response.data)
            setMessage(response.data.message);
        } catch (error) {
            console.log("catch");
            setMessage(`${error.response.data.title} status = ${error.response.status}`);
        }
    }   


    return <h1>{message}</h1>
}


