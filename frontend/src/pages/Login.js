import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { useAuthStore } from '../store/useAuthStore';
import { toast } from 'react-toastify';

const Login = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        password: '',
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setErrors({});
        setFormData({
        ...formData,
        [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        
        // Basic validation
        const newErrors = {};
        if (!formData.email) newErrors.email = 'Email is required';
        if (!formData.password) newErrors.password = 'Password is required';
        
        if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
        }
        const payload = {
            email: formData.email,
            password: formData.password
        }
        handleLogin(payload);
    };

    const handleLogin = async (payload) => {
        try {
            const response = await api.post('/api/users/login', payload);
            const { user } = response.data;
            toast.success("Logged in succesfully!")
             // Store in Zustand
            const setUser = useAuthStore.getState().setUser;
            setUser(user);
            localStorage.setItem('user', JSON.stringify(user));
            navigate('/products');
        } catch (error) {
            toast.error("Login Failed. Please check your credentials and try again.")
        }
    }
    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-950 bg-[radial-gradient(circle_at_top,_rgba(99,102,241,0.18),_transparent_35%),linear-gradient(180deg,#020617_0%,#0f172a_100%)] py-12 px-4 sm:px-6 lg:px-8 text-slate-100">
        <div className="relative max-w-md w-full">
            <div className="absolute -top-10 left-1/2 -translate-x-1/2 h-24 w-24 rounded-full bg-indigo-500/15 blur-3xl" />
            <div className="relative auth-panel px-8 py-10 sm:px-10">
            <div className="text-center">
                <span className="inline-flex items-center rounded-full bg-indigo-500/10 px-3 py-1 text-sm font-semibold uppercase tracking-[0.2em] text-indigo-300 mb-4">
                Welcome back
                </span>
                <h2 className="text-3xl font-semibold tracking-tight text-white">
                Sign in to your account
                </h2>
                <p className="mt-3 text-sm text-slate-400">
                Securely access your store dashboard and continue shopping.
                </p>
            </div>
            
            <form className="mt-10 space-y-6" onSubmit={handleSubmit}>
                <div className="space-y-4">
                <div>
                    <label htmlFor="email" className="sr-only">Email address</label>
                    <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className={`auth-input block w-full px-4 py-3 rounded-2xl border ${
                        errors.email ? 'border-red-400' : 'border-slate-700'
                    } placeholder-slate-500 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm`}
                    placeholder="Email address"
                    value={formData.email}
                    onChange={handleChange}
                    />
                    {errors.email && (
                    <p className="text-red-400 text-xs mt-2">{errors.email}</p>
                    )}
                </div>
                <div>
                    <label htmlFor="password" className="sr-only">Password</label>
                    <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="current-password"
                    required
                    className={`auth-input block w-full px-4 py-3 rounded-2xl border ${
                        errors.password ? 'border-red-400' : 'border-slate-700'
                    } placeholder-slate-500 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm`}
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    />
                    {errors.password && (
                    <p className="text-red-400 text-xs mt-2">{errors.password}</p>
                    )}
                </div>
                </div>

                <div>
                <button
                    type="submit"
                    className="group relative w-full flex justify-center items-center py-3 px-4 rounded-2xl text-sm font-semibold text-white bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 shadow-lg shadow-orange-500/20 hover:from-orange-400 hover:via-fuchsia-400 hover:to-violet-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-300"
                >
                    <span className="absolute left-4 flex items-center">
                    <svg className="h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                        <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                    </svg>
                    </span>
                    Sign in
                </button>
                </div>
            </form>

            <p className="mt-6 text-center text-sm text-slate-400">
                Or{' '}
                <Link to="/signup" className="font-medium text-indigo-300 hover:text-white">
                create a new account
                </Link>
            </p>
            </div>
        </div>
        </div>
    );
};

export default Login; 