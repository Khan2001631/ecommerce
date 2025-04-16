import React, { createContext, useState, useEffect } from 'react';

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState([]);
  
  // Load cart from localStorage on initial render
  useEffect(() => {
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      setCart(JSON.parse(storedCart));
    }
  }, []);
  
  // Save cart to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);
  
  // Add item to cart
  const addToCart = (id, name, price, imageUrl) => {
    const itemIndex = cart.findIndex(item => item.id === id);
    
    if (itemIndex !== -1) {
      // If item already exists, increment quantity
      const updatedCart = [...cart];
      updatedCart[itemIndex].quantity += 1;
      setCart(updatedCart);
    } else {
      // If item doesn't exist, add it to cart
      setCart([...cart, {
        id,
        name,
        price: parseFloat(price),
        imageUrl,
        quantity: 1
      }]);
    }
  };
  
  // Change item quantity
  const changeQuantity = (index, change) => {
    const updatedCart = [...cart];
    updatedCart[index].quantity += change;
    
    if (updatedCart[index].quantity <= 0) {
      // Remove item if quantity becomes 0 or negative
      updatedCart.splice(index, 1);
    }
    
    setCart(updatedCart);
  };
  
  // Remove item from cart
  const removeFromCart = (index) => {
    const updatedCart = [...cart];
    updatedCart.splice(index, 1);
    setCart(updatedCart);
  };
  
  // Calculate total cart amount
  const getTotalAmount = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };
  
  return (
    <CartContext.Provider value={{
      cart,
      addToCart,
      changeQuantity,
      removeFromCart,
      getTotalAmount,
      cartItemsCount: cart.length
    }}>
      {children}
    </CartContext.Provider>
  );
}; 