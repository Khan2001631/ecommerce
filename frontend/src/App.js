import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Cart from './pages/Cart';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Footer from './components/Footer';
import Products from './pages/Products';
import { useAuthStore } from './store/useAuthStore';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import AuthGuard from './auth/AuthGuard';
import NonAuthGuard from './auth/NonAuthGuard';
import AiChat from './pages/aiChat';


const App = () => {
    const {setUser} = useAuthStore();
    const loggedInUser = localStorage.getItem('user');

    useEffect(() => {
        if (loggedInUser) {
            try {
                const user = JSON.parse(loggedInUser);
                setUser(user);
            } catch (e) {
                console.error("Failed to parse user from local storage", e);
            }
        }
    }, [loggedInUser, setUser]);

    return (
    <Router>
        <div className="flex flex-col min-h-screen">
            <Navbar />
            <main className="flex-grow pt-24">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/products" element={
                        <AuthGuard>
                            <Products />
                        </AuthGuard>
                    } />
                    <Route path="/aiChat" element={
                        <AuthGuard>
                            <AiChat />
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