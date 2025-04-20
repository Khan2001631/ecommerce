import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

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
            const response = await axios.post('http://localhost:8080/users/login', payload);

            const { user, token } = response.data;
             // Store in Zustand
            const setUser = useAuthStore.getState().setUser;
            const setToken = useAuthStore.getState().setToken;
            setUser(user);
            setToken(token);
            navigate('/products');
        } catch (error) {
            console.error('Login failed:', error);
        }
    }
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
            <div>
            <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                Sign in to your account
            </h2>
            <p className="mt-2 text-center text-sm text-gray-600">
                Or{' '}
                <Link to="/signup" className="font-medium text-indigo-600 hover:text-indigo-500">
                create a new account
                </Link>
            </p>
            </div>
            
            <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm -space-y-px">
                <div>
                <label htmlFor="email" className="sr-only">Email address</label>
                <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className={`appearance-none rounded-none relative block w-full px-3 py-2 border ${
                    errors.email ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Email address"
                    value={formData.email}
                    onChange={handleChange}
                />
                {errors.email && (
                    <p className="text-red-500 text-xs mt-1">{errors.email}</p>
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
                    className={`appearance-none rounded-none relative block w-full px-3 py-2 border ${
                    errors.password ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                />
                {errors.password && (
                    <p className="text-red-500 text-xs mt-1">{errors.password}</p>
                )}
                </div>
            </div>
            <div>
                <button
                type="submit"
                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                <span className="absolute left-0 inset-y-0 flex items-center pl-3">
                    <svg className="h-5 w-5 text-indigo-500 group-hover:text-indigo-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                    </svg>
                </span>
                Sign in
                </button>
            </div>
            </form>
        </div>
        </div>
    );
};

export default Login; 