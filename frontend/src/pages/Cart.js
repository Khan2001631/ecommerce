import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';

const Cart = () => {
  const { cart, changeQuantity, getTotalAmount } = useContext(CartContext);

  const handleCheckout = () => {
    alert('Proceeding to payment...');
    // Implementation would go here
  };

  // If cart is empty
  if (cart.length === 0) {
    return (
      <div className="container mx-auto px-4 py-20 min-h-screen">
        <div className="flex flex-col items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-20 w-20 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <h2 className="text-2xl font-bold mt-4 mb-2">Your cart is empty</h2>
          <p className="text-gray-600 mb-6">Add some items to your cart and come back here.</p>
          <Link to="/" className="btn-primary">
            Continue Shopping
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-20 min-h-screen">
      <h1 className="text-3xl font-bold text-primary mb-8 text-center">Your Shopping Cart</h1>
      
      <div className="overflow-x-auto">
        <table className="w-full bg-secondary text-white rounded-lg overflow-hidden">
          <thead className="border-b border-gray-700">
            <tr>
              <th className="py-4 px-6 text-left">Product</th>
              <th className="py-4 px-6 text-left">Name</th>
              <th className="py-4 px-6 text-left">Price</th>
              <th className="py-4 px-6 text-left">Quantity</th>
              <th className="py-4 px-6 text-left">Total</th>
              <th className="py-4 px-6 text-left">Remove</th>
            </tr>
          </thead>
          <tbody>
            {cart.map((item, index) => (
              <tr key={item.id} className="border-b border-gray-700">
                <td className="py-4 px-6">
                  <img src={item.imageUrl} alt={item.name} className="w-16 h-16 object-cover rounded" />
                </td>
                <td className="py-4 px-6 font-medium">{item.name}</td>
                <td className="py-4 px-6">₹{item.price}</td>
                <td className="py-4 px-6">
                  <div className="flex items-center space-x-2">
                    <button 
                      onClick={() => changeQuantity(index, -1)}
                      className="w-8 h-8 flex items-center justify-center bg-gray-700 rounded-full hover:bg-gray-600"
                    >
                      -
                    </button>
                    <span>{item.quantity}</span>
                    <button 
                      onClick={() => changeQuantity(index, 1)}
                      className="w-8 h-8 flex items-center justify-center bg-gray-700 rounded-full hover:bg-gray-600"
                    >
                      +
                    </button>
                  </div>
                </td>
                <td className="py-4 px-6 font-bold">₹{(item.price * item.quantity).toFixed(2)}</td>
                <td className="py-4 px-6">
                  <button 
                    onClick={() => changeQuantity(index, -item.quantity)}
                    className="text-red-500 hover:text-red-400"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      
      <div className="mt-8 flex flex-col md:flex-row md:justify-end">
        <div className="md:w-1/3 bg-secondary text-white p-6 rounded-lg">
          <h2 className="text-xl font-bold mb-4">Order Summary</h2>
          <div className="flex justify-between mb-2">
            <span>Items ({cart.length}):</span>
            <span>₹{getTotalAmount().toFixed(2)}</span>
          </div>
          <div className="flex justify-between mb-2">
            <span>Shipping:</span>
            <span>Free</span>
          </div>
          <div className="border-t border-gray-700 my-4"></div>
          <div className="flex justify-between text-xl font-bold">
            <span>Total:</span>
            <span>₹{getTotalAmount().toFixed(2)}</span>
          </div>
          <button 
            onClick={handleCheckout}
            className="w-full mt-6 btn-primary py-3"
          >
            Proceed to Payment
          </button>
        </div>
      </div>
    </div>
  );
};

export default Cart; 