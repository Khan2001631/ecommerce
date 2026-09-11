import { Navigate } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore"

const NonAuthGuard = ({children}) => {
    const user = useAuthStore((state) => state.user);
    
    if(user !== null) {
        return (
            <Navigate to='/' />
        )
    }
    return children;
}

export default NonAuthGuard;