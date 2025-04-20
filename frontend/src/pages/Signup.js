import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const Signup = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
        ...formData,
        [name]: value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        
        // Basic validation
        const newErrors = {};
        if (!formData.firstName) newErrors.firstName = 'First name is required';
        if (!formData.lastName) newErrors.lastName = 'Last name is required';
        if (!formData.email) newErrors.email = 'Email is required';
        if (!formData.password) newErrors.password = 'Password is required';
        if (formData.password && formData.password.length < 6) newErrors.password = 'Password must be at least 6 characters';
        if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
        
        if (Object.keys(newErrors).length > 0) {
        setErrors(newErrors);
        return;
        }
        
        // Here you would normally handle the signup logic
        console.log('Signup attempt:', formData);

        const payload = {
            name: `${formData.firstName} ${formData.lastName}`,
            email: formData.email,
            password: formData.password
        }
        handleSignup(payload);
    };

    const handleSignup = async (payload) => {
        try {
            const response = await axios.post('http://localhost:8080/users/register', payload);
            console.log(response);
        } catch (error) {
            console.error('Signup failed:', error);
        }
    }
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
            <div>
            <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                Create your account
            </h2>
            <p className="mt-2 text-center text-sm text-gray-600">
                Already have an account?{' '}
                <Link to="/login" className="font-medium text-indigo-600 hover:text-indigo-500">
                Sign in
                </Link>
            </p>
            </div>
            
            <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm -space-y-px">
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 mb-4">
                <div>
                    <label htmlFor="firstName" className="sr-only">First Name</label>
                    <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    autoComplete="given-name"
                    required
                    className={`appearance-none rounded-md relative block w-full px-3 py-2 border ${
                        errors.firstName ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="First Name"
                    value={formData.firstName}
                    onChange={handleChange}
                    />
                    {errors.firstName && (
                    <p className="text-red-500 text-xs mt-1">{errors.firstName}</p>
                    )}
                </div>
                <div>
                    <label htmlFor="lastName" className="sr-only">Last Name</label>
                    <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    autoComplete="family-name"
                    required
                    className={`appearance-none rounded-md relative block w-full px-3 py-2 border ${
                        errors.lastName ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Last Name"
                    value={formData.lastName}
                    onChange={handleChange}
                    />
                    {errors.lastName && (
                    <p className="text-red-500 text-xs mt-1">{errors.lastName}</p>
                    )}
                </div>
                </div>
                
                <div className="mb-4">
                <label htmlFor="email" className="sr-only">Email address</label>
                <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className={`appearance-none rounded-md relative block w-full px-3 py-2 border ${
                    errors.email ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Email address"
                    value={formData.email}
                    onChange={handleChange}
                />
                {errors.email && (
                    <p className="text-red-500 text-xs mt-1">{errors.email}</p>
                )}
                </div>
                
                <div className="mb-4">
                <label htmlFor="password" className="sr-only">Password</label>
                <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="new-password"
                    required
                    className={`appearance-none rounded-md relative block w-full px-3 py-2 border ${
                    errors.password ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                />
                {errors.password && (
                    <p className="text-red-500 text-xs mt-1">{errors.password}</p>
                )}
                </div>
                
                <div>
                <label htmlFor="confirmPassword" className="sr-only">Confirm Password</label>
                <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    required
                    className={`appearance-none rounded-md relative block w-full px-3 py-2 border ${
                    errors.confirmPassword ? 'border-red-300' : 'border-gray-300'
                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm`}
                    placeholder="Confirm Password"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                />
                {errors.confirmPassword && (
                    <p className="text-red-500 text-xs mt-1">{errors.confirmPassword}</p>
                )}
                </div>
            </div>

            <div>
                <div className="flex items-center">
                <input
                    id="terms"
                    name="terms"
                    type="checkbox"
                    className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                    required
                />
                <label htmlFor="terms" className="ml-2 block text-sm text-gray-900">
                    I agree to the <a href="#" className="text-indigo-600 hover:text-indigo-500">Terms of Service</a> and <a href="#" className="text-indigo-600 hover:text-indigo-500">Privacy Policy</a>
                </label>
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
                Create Account
                </button>
            </div>
            </form>
        </div>
        </div>
    );
};

export default Signup; 