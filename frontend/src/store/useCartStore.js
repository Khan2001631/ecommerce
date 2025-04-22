import { create } from 'zustand';

export const useCartStore = create((set, get) => ({
    // initialize from localStorage or empty
    cart: JSON.parse(localStorage.getItem('cart')) || [],

    addToCart: (id, name, price, imageUrl) => {
    set(state => {
        const existingIndex = state.cart.findIndex(item => item.id === id);
        let updatedCart;
        if (existingIndex !== -1) {
        // increment quantity
        updatedCart = state.cart.map((item, idx) =>
            idx === existingIndex
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
        } else {
        // new item
            updatedCart = [
                ...state.cart,
                { id, name, price: parseFloat(price), imageUrl, quantity: 1 }
            ];
        }
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        return { cart: updatedCart };
    });
    },

    changeQuantity: (index, change) => {
    set(state => {
        const updatedCart = [...state.cart];
        updatedCart[index].quantity += change;
        if (updatedCart[index].quantity <= 0) {
        updatedCart.splice(index, 1);
        }
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        return { cart: updatedCart };
    });
    },

    removeFromCart: (index) => {
    set(state => {
        const updatedCart = [...state.cart];
        updatedCart.splice(index, 1);
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        return { cart: updatedCart };
    });
    },

    getTotalAmount: () => {
    return get().cart.reduce((total, item) => total + item.price * item.quantity, 0);
    },
    deleteCart: () => {
        set({ cart: [] });
        localStorage.setItem('cart', JSON.stringify([]));
    },
}));