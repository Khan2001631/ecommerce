import axios from "axios";
import { API_BASE_URL } from "../config";

export const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true,
});
const BASE_URL = API_BASE_URL;

export const fetchProducts = async () => {
    try {
        const response = await fetch(`${BASE_URL}/products`);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } 
    catch (error) {
        console.error("Error fetching products:", error);
        return [];
    }
};

// ============ CART API METHODS ============

/**
 * Add product to cart
 * POST /cart/add
 */
export const addProductToCart = async (productId, quantity = 1) => {
    try {
        const response = await api.post('/cart/add', {
            productId,
            quantity
        });
        return response.data;
    } catch (error) {
        console.error("Error adding product to cart:", error);
        throw error;
    }
};

/**
 * Get cart items
 * GET /cart
 */
export const getCart = async () => {
    try {
        const response = await api.get('/cart');
        return response.data;
    } catch (error) {
        console.error("Error fetching cart:", error);
        throw error;
    }
};

/**
 * Update cart item quantity
 * PUT /cart/update
 * @param {number} productId - Product ID
 * @param {number} quantity - New quantity (0 to remove)
 */
export const updateCartQuantity = async (productId, quantity) => {
    try {
        const response = await api.put('/cart/update', {
            productId,
            quantity
        });
        return response.data;
    } catch (error) {
        console.error("Error updating cart quantity:", error);
        throw error;
    }
};

/**
 * Remove item from cart
 * DELETE /cart/remove
 */
export const removeCartItem = async (productId) => {
    try {
        const response = await api.delete('/cart/remove', {
            data: { productId }
        });
        return response.data;
    } catch (error) {
        console.error("Error removing cart item:", error);
        throw error;
    }
};