import { toast } from "react-toastify";
export function handleApiError(error) {
    

    if (error.response) {
        if(error.response.status === 401){
            return;
        }

        const message = error?.response?.data?.title || "something went wrong";
        toast.error(message, {
            toastId: message
        });

    }
    else if (error.request) {
        console.log(error.request)
        const message = "unable to connect to server"
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
}