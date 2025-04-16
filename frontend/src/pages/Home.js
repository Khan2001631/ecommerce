import React, { useState, useEffect } from 'react';
import Carousel from '../components/Carousel';
import ProductCard from '../components/ProductCard';
import { fetchProducts } from '../services/api';

const Home = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const getProducts = async () => {
      try {
        setLoading(true);
        const data = await fetchProducts();
        setProducts(data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch products');
        setLoading(false);
      }
    };

    getProducts();
  }, []);

  // Filter products by category
  const trendingProducts = products.filter(product => product.category !== 'Clothing' && product.category !== 'Electronics');
  const clothingProducts = products.filter(product => product.category === 'Clothing');
  const electronicsProducts = products.filter(product => product.category === 'Electronics');

  if (loading) {
    return (
      <div className="h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-500 text-xl mb-4">{error}</p>
          <button onClick={() => window.location.reload()} className="btn-primary">
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <Carousel />
      
      <div className="container mx-auto px-4 py-8">
        {/* Trending Products */}
        <h2 className="text-2xl font-bold text-primary text-center mb-8">
          🔥 Trending Products
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {trendingProducts.map(product => (
            <div key={product.id} className="h-full">
              <ProductCard product={product} />
            </div>
          ))}
        </div>

        {/* Clothing */}
        <h2 className="text-2xl font-bold text-primary text-center mb-8">
          👗 Clothing Collection
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
          {clothingProducts.map(product => (
            <div key={product.id} className="h-full">
              <ProductCard product={product} />
            </div>
          ))}
        </div>

        {/* Electronics */}
        <h2 className="text-2xl font-bold text-primary text-center mb-8">
          💻 Electronics & Gadgets
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {electronicsProducts.map(product => (
            <div key={product.id} className="h-full">
              <ProductCard product={product} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Home; 