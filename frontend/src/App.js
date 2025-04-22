import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Cart from './pages/Cart';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Footer from './components/Footer';
import Products from './pages/Products';
import AuthGuard from './auth/AuthGuard';
import { jwtDecode } from 'jwt-decode';
import { useAuthStore } from './store/useAuthStore';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NonAuthGuard from './auth/NonAuthGuard';


const App = () => {
    const {logout} = useAuthStore();
    const loggedInUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');

    useEffect(() => {
        // Check if token and user data are present in localStorage
        if (loggedInUser && token) {
            // Parse user and retrieve zustand setters
            const user = JSON.parse(loggedInUser);
            const setUser = useAuthStore.getState().setUser;
            const setToken = useAuthStore.getState().setToken;
    
            // Sync user and token into global store
            setUser(user);
            setToken(token);
            try {
                // Decode the token to extract expiry info
                const decoded = jwtDecode(token);
                const expirationTime = decoded.exp * 1000; // Convert to milliseconds
                const currentTime = Date.now();
    
                // Calculate remaining time until token expires
                const remainingTime = expirationTime - currentTime;
    
                if (remainingTime > 0) {
                    // Schedule automatic logout when token expires
                    const timeoutId = setTimeout(() => {
                        logout(); // Clear store + localStorage
                    }, remainingTime);    
                    // Cleanup function to clear the timeout if component unmounts
                    return () => clearTimeout(timeoutId);
                } else {
                    // Token has already expired — logout immediately
                    logout();
                }
            } catch (err) {
                // Token is invalid or corrupted — logout for safety
                console.error("Invalid token:", err);
                logout();
            }

            
        }
    }, [loggedInUser, token]); // Dependency: runs only when user or token changes
    

    return (
    <Router>
        <div className="flex flex-col min-h-screen">
            <Navbar />
            <main className="flex-grow">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/products" element={
                        <AuthGuard>
                            <Products />
                        </AuthGuard>
                    } />
                    <Route path="/cart" element={
                        <AuthGuard>
                            <Cart />
                        </AuthGuard>
                    } />
                    <Route path="/login" element={
                        <NonAuthGuard>
                            <Login />
                        </NonAuthGuard>
                    } />
                    <Route path="/signup" element={
                        <NonAuthGuard>
                            <Signup />
                        </NonAuthGuard>
                    } />
                </Routes>
            </main>
            <Footer />
            <ToastContainer 
                position="top-right" 
                autoClose={3000} 
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                theme="dark"
                draggable
                pauseOnHover
            />
        </div>
    </Router>
    );
}

export default App; 