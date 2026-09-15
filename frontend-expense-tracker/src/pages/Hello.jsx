import { useEffect, useState } from "react";
import apiClient from "../services/apiClient"

export default function Hello() {
    const [message, setMessage] = useState("");

    useEffect(() => {
        getData();
    }, [])


    async function getData() {
        const response = await apiClient.get('/hello');
        setMessage(response.data.message);
    }

    return <h1>{message}</h1>
}


