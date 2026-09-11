import { create } from 'zustand';
import { getCart, addProductToCart, updateCartQuantity, removeCartItem } from '../services/api';

export const useCartStore = create((set, get) => ({
    // UI cache - stores cart items from API
    cart: [],
    totalAmount: 0,
    loading: false,
    error: null,

    /**
     * Fetch cart from API
     * Call this on app initialization or when user logs in
     */
    fetchCart: async () => {
        set({ loading: true, error: null });
        try {
            const response = await getCart();
            set({ 
                cart: response.items || [], 
                totalAmount: response.totalAmount || 0,
                loading: false 
            });
        } catch (error) {
            console.error("Error fetching cart:", error);
            set({ error: error.message, loading: false });
        }
    },

    /**
     * Add to cart - Optimistic Update Flow:
     * 1. Update UI instantly
     * 2. Call API
     * 3. If API fails → rollback
     */
    addToCart: async (id, name, price, imageUrl) => {
        // Optimistic update - update UI first
        const previousCart = get().cart;
        const existingIndex = get().cart.findIndex(item => item.productId === id);
        
        let updatedCart;
        if (existingIndex !== -1) {
            // Increment quantity
            updatedCart = get().cart.map((item, idx) =>
                idx === existingIndex
                ? { ...item, quantity: item.quantity + 1, totalPrice: (item.quantity + 1) * item.price }
                : item
            );
        } else {
            // New item
            updatedCart = [
                ...get().cart,
                { productId: id, name, price: parseFloat(price), imageUrl, quantity: 1, totalPrice: parseFloat(price) }
            ];
        }
        
        // Update total amount
        const newTotal = updatedCart.reduce((total, item) => total + (item.price * item.quantity), 0);
        set({ cart: updatedCart, totalAmount: newTotal });
        
        // Call API
        try {
            await addProductToCart(id, 1);
        } catch (error) {
            // API failed - rollback to previous state
            console.error("API failed, rolling back:", error);
            set({ cart: previousCart });
            throw error;
        }
    },

    /**
     * Change quantity - Optimistic Update
     */
    changeQuantity: async (productId, newQuantity) => {
        const previousCart = get().cart;
        const previousTotal = get().totalAmount;
        
        let updatedCart;
        if (newQuantity <= 0) {
            // Remove item
            updatedCart = get().cart.filter(item => item.productId !== productId);
        } else {
            // Update quantity
            updatedCart = get().cart.map(item =>
                item.productId === productId
                ? { ...item, quantity: newQuantity, totalPrice: newQuantity * item.price }
                : item
            );
        }
        
        // Update total amount
        const newTotal = updatedCart.reduce((total, item) => total + (item.price * item.quantity), 0);
        set({ cart: updatedCart, totalAmount: newTotal });
        
        // Call API
        try {
            await updateCartQuantity(productId, newQuantity);
        } catch (error) {
            // Rollback on failure
            console.error("API failed, rolling back:", error);
            set({ cart: previousCart, totalAmount: previousTotal });
            throw error;
        }
    },

    /**
     * Remove from cart - Optimistic Update
     */
    removeFromCart: async (productId) => {
        const previousCart = get().cart;
        const previousTotal = get().totalAmount;
        
        // Optimistic update
        const updatedCart = get().cart.filter(item => item.productId !== productId);
        const newTotal = updatedCart.reduce((total, item) => total + (item.price * item.quantity), 0);
        set({ cart: updatedCart, totalAmount: newTotal });
        
        // Call API
        try {
            await removeCartItem(productId);
        } catch (error) {
            // Rollback on failure
            console.error("API failed, rolling back:", error);
            set({ cart: previousCart, totalAmount: previousTotal });
            throw error;
        }
    },

    getTotalAmount: () => {
        return get().totalAmount;
    },
    
    deleteCart: () => {
        set({ cart: [], totalAmount: 0 });
    },
}));