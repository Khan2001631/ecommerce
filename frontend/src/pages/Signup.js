import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

const Signup = () => {
    const navigate = useNavigate();
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
        const payload = {
            name: `${formData.firstName} ${formData.lastName}`,
            email: formData.email,
            password: formData.password
        }
        handleSignup(payload);
    };

    const handleSignup = async (payload) => {
        try {
            const response = await axios.post('http://localhost:8080/api/users/register', payload);
            if (response.status === 200) {
                toast.success("Succesfully registered. Please login to coninue exploring the website.")
                navigate('/login');
            }
        } catch (error) {
            toast.error("Signup failed. Please try again.")
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-950 bg-[radial-gradient(circle_at_top,_rgba(139,92,246,0.15),_transparent_30%),linear-gradient(180deg,#020617_0%,#0f172a_100%)] py-12 px-4 sm:px-6 lg:px-8 text-slate-100">
        <div className="relative max-w-xl w-full">
            <div className="absolute -top-10 left-1/2 -translate-x-1/2 h-24 w-24 rounded-full bg-orange-500/15 blur-3xl" />
            <div className="relative auth-panel px-8 py-10 sm:px-10">
            <div className="text-center">
                <span className="inline-flex items-center rounded-full bg-orange-500/10 px-3 py-1 text-sm font-semibold uppercase tracking-[0.2em] text-orange-300 mb-4">
                Ready to get started?
                </span>
                <h2 className="text-3xl font-semibold tracking-tight text-white">
                Create your account
                </h2>
                <p className="mt-3 text-sm text-slate-400">
                Join now and unlock a cleaner shopping experience.
                </p>
            </div>
            
            <form className="mt-10 space-y-6" onSubmit={handleSubmit}>
                <div className="grid gap-4 sm:grid-cols-2">
                <div>
                    <label htmlFor="firstName" className="sr-only">First Name</label>
                    <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    autoComplete="given-name"
                    required
                    className={`auth-input block w-full px-4 py-3 rounded-2xl border ${
                        errors.firstName ? 'border-red-400' : 'border-slate-700'
                    } placeholder-slate-500 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm`}
                    placeholder="First Name"
                    value={formData.firstName}
                    onChange={handleChange}
                    />
                    {errors.firstName && (
                    <p className="text-red-400 text-xs mt-2">{errors.firstName}</p>
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
                    className={`auth-input block w-full px-4 py-3 rounded-2xl border ${
                        errors.lastName ? 'border-red-400' : 'border-slate-700'
                    } placeholder-slate-500 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm`}
                    placeholder="Last Name"
                    value={formData.lastName}
                    onChange={handleChange}
                    />
                    {errors.lastName && (
                    <p className="text-red-400 text-xs mt-2">{errors.lastName}</p>
                    )}
                </div>
                </div>
                
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
                
                <div className="grid gap-4 sm:grid-cols-2">
                <div>
                    <label htmlFor="password" className="sr-only">Password</label>
                    <input
                    id="password"
                    name="password"
                    type="password"
                    autoComplete="new-password"
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
                <div>
                    <label htmlFor="confirmPassword" className="sr-only">Confirm Password</label>
                    <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    autoComplete="new-password"
                    required
                    className={`auth-input block w-full px-4 py-3 rounded-2xl border ${
                    errors.confirmPassword ? 'border-red-400' : 'border-slate-700'
                    } placeholder-slate-500 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm`}
                    placeholder="Confirm Password"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    />
                    {errors.confirmPassword && (
                    <p className="text-red-400 text-xs mt-2">{errors.confirmPassword}</p>
                    )}
                </div>
                </div>

                <div className="flex items-start gap-3 text-sm text-slate-300">
                <input
                    id="terms"
                    name="terms"
                    type="checkbox"
                    className="auth-checkbox mt-1 h-4 w-4 rounded border-slate-600 bg-slate-900 text-indigo-500 focus:ring-indigo-500"
                    required
                />
                <label htmlFor="terms" className="leading-5">
                    I agree to the <a href="#" className="text-indigo-300 hover:text-white underline">Terms of Service</a> and <a href="#" className="text-indigo-300 hover:text-white underline">Privacy Policy</a>
                </label>
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
                    Create Account
                </button>
                </div>
            </form>

            <p className="mt-6 text-center text-sm text-slate-400">
                Already have an account?{' '}
                <Link to="/login" className="font-medium text-indigo-300 hover:text-white">
                Sign in
                </Link>
            </p>
            </div>
        </div>
        </div>
    );
};

export default Signup; 