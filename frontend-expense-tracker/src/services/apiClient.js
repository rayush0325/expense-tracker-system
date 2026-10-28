import axios from "axios";

const apiClient = axios.create({
    baseURL : 'http://localhost:8080/api/v1',
    headers : {
        'Content-Type' : 'application/json'
    }

    
});
apiClient.interceptors.request.use((config)=>{
    console.log("intercepting request");
    const jwtToken = localStorage.getItem("jwtToken");

    if(Boolean(jwtToken)){
        console.log(`setting jwtToken in request header jwtToken = ${jwtToken}`)
        config.headers.Authorization = `Bearer ${jwtToken}`
    }

    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.log(`response interceptor - url type = ${error.config.url !== "/user/login"}`)
        if(error.config.url !== "/user/login" && error.response.status === 401){
            console.log("callin a protected endpoint with invalid token");
            window.dispatchEvent(new Event("auth : unauthorized"));
        }
        return Promise.reject(error);
    }

);


export default apiClient;