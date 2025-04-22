import React from 'react';
import { useCartStore } from '../store/useCartStore';

const ProductCard = ({ product }) => {
  const addToCart = useCartStore(state => state.addToCart);
  
  const handleAddToCart = () => {
    addToCart(product.id, product.name, product.price, product.imageUrl);
  };
  
  return (
    <div className="card h-full flex flex-col">
      <img 
        src={product.imageUrl} 
        alt={product.name} 
        className="w-full h-48 object-cover transition-transform duration-300 hover:scale-110"
      />
      <div className="p-4 flex flex-col flex-grow">
        <h3 className="text-lg font-semibold mb-2">{product.name}</h3>
        <p className="text-gray-300 mb-3 flex-grow">{product.description}</p>
        <div className="mt-auto">
          <p className="text-xl font-bold mb-3">₹{product.price}</p>
          <button 
            onClick={handleAddToCart}
            className="btn-primary w-full"
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard; 