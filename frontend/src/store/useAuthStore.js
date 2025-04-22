// store/useAuthStore.js
import { toast } from 'react-toastify';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create((set) => ({
    user: null,
    token: null,
    setUser: (user) => set({ user }),
    setToken: (token) => set({ token }),
    logout: () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        set({ user: null, token: null });
        toast.success("Succesfully logged out.")
    }
}));
