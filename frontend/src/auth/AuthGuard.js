import { useAuthStore } from '../store/useAuthStore';
import { Navigate } from 'react-router-dom';

const AuthGuard = ({ children }) => {
    const { user } = useAuthStore();
    console.log(user,'user');
    
    if (!user) {
        return <Navigate to="/login" />;
    }
    return children;
};

export default AuthGuard;