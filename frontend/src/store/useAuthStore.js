// store/useAuthStore.js
import { toast } from 'react-toastify';
import { create } from 'zustand';
import { api } from '../services/api';

export const useAuthStore = create((set) => ({
    user: null,
    setUser: (user) => set({ user }),
    logout: async () => {
        try {
            await api.post('/users/logout');
        } catch (error) {
            console.error("Logout API failed", error);
        }
        localStorage.removeItem('user');
        set({ user: null });
        toast.success("Succesfully logged out.")
    }
}));
