import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Carousel from '../components/Carousel';
import { fetchProducts } from '../services/api';

const Home = () => {
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const getProducts = async () => {
      try {
        setLoading(true);
        const products = await fetchProducts();
        setFeaturedProducts(products.slice(0, 6)); // Get first 6 products for featured section
        setLoading(false);
      } catch (err) {
        console.error('Failed to fetch products', err);
        setLoading(false);
      }
    };

    getProducts();
  }, []);

  return (
    <div className="min-h-screen">
      {/* Hero Banner */}
      <div className="relative h-screen bg-secondary overflow-hidden">
        <img 
          src="/img/img2.png" 
          alt="Hero Background" 
          className="absolute w-full h-full object-contain opacity-60"
        />
        <div className="absolute inset-0 flex flex-col items-center justify-center px-4 text-center">
          <h1 className="text-4xl md:text-6xl font-bold text-white mb-4">
            <span className="text-primary">Elevate</span> Your Style
          </h1>
          <p className="text-xl md:text-2xl text-gray-200 mb-8 max-w-2xl">
            Discover the latest trends and premium products for your lifestyle
          </p>
          <div className="space-x-4">
            <Link 
              to="/products" 
              className="btn-primary text-lg px-8 py-3 rounded-full transition-transform hover:scale-105"
            >
              Shop Now
            </Link>
            <Link 
              to="/signup" 
              className="border-2 border-white text-white px-8 py-3 rounded-full hover:bg-white hover:text-secondary transition-all"
            >
              Sign Up
            </Link>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="bg-gray-100 py-16">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-lg shadow-md text-center transform transition-transform hover:scale-105">
              <div className="text-primary text-4xl mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Free Shipping</h3>
              <p className="text-gray-600">On orders over ₹499</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center transform transition-transform hover:scale-105">
              <div className="text-primary text-4xl mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Secure Payments</h3>
              <p className="text-gray-600">100% secure checkout</p>
            </div>
            
            <div className="bg-white p-6 rounded-lg shadow-md text-center transform transition-transform hover:scale-105">
              <div className="text-primary text-4xl mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </div>
              <h3 className="text-xl font-semibold mb-2">Easy Returns</h3>
              <p className="text-gray-600">30 days return policy</p>
            </div>
          </div>
        </div>
      </div>

      {/* Carousel Section */}
      <div className="py-12 bg-secondary">
        <div className="container mx-auto px-4 mb-8">
          <h2 className="text-3xl font-bold text-primary text-center mb-12">
            Featured Collections
          </h2>
        </div>
        <Carousel />
      </div>

      {/* Categories Section */}
      <div className="py-16 bg-gray-100">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">Shop by Category</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <Link to="/products" className="group">
              <div className="relative h-80 overflow-hidden rounded-lg">
                <img 
                  src="/img/img4.png" 
                  alt="Men's Fashion" 
                  className="w-full h-full object-contain transition-transform duration-700 group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
                  <div className="text-center px-4 py-8 bg-black bg-opacity-60 rounded-lg transform transition-transform group-hover:scale-105">
                    <h3 className="text-2xl text-primary font-bold mb-2">Men's Fashion</h3>
                    <p className="text-white mb-4">Explore stylish collection</p>
                    <span className="inline-block px-4 py-2 border border-primary text-white rounded-full group-hover:bg-primary transition-colors">
                      Shop Now
                    </span>
                  </div>
                </div>
              </div>
            </Link>
            
            <Link to="/products" className="group">
              <div className="relative h-80 overflow-hidden rounded-lg">
                <img 
                  src="/img/img5.jpg" 
                  alt="Women's Fashion" 
                  className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
                  <div className="text-center px-4 py-8 bg-black bg-opacity-60 rounded-lg transform transition-transform group-hover:scale-105">
                    <h3 className="text-2xl text-primary font-bold mb-2">Women's Fashion</h3>
                    <p className="text-white mb-4">Trendy women's collection</p>
                    <span className="inline-block px-4 py-2 border border-primary text-white rounded-full group-hover:bg-primary transition-colors">
                      Shop Now
                    </span>
                  </div>
                </div>
              </div>
            </Link>
            
            <Link to="/products" className="group">
              <div className="relative h-80 overflow-hidden rounded-lg">
                <img 
                  src="/img/img6.jpg" 
                  alt="Accessories" 
                  className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                />
                <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
                  <div className="text-center px-4 py-8 bg-black bg-opacity-60 rounded-lg transform transition-transform group-hover:scale-105">
                    <h3 className="text-2xl text-primary font-bold mb-2">Accessories</h3>
                    <p className="text-white mb-4">Complete your look</p>
                    <span className="inline-block px-4 py-2 border border-primary text-white rounded-full group-hover:bg-primary transition-colors">
                      Shop Now
                    </span>
                  </div>
                </div>
              </div>
            </Link>
          </div>
        </div>
      </div>

      {/* Featured Products Section */}
      {!loading && featuredProducts.length > 0 && (
        <div className="py-16 bg-white">
          <div className="container mx-auto px-4">
            <h2 className="text-3xl font-bold text-center mb-12">Popular Products</h2>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
              {featuredProducts.map((product) => (
                <div key={product.id} className="card h-full flex flex-col">
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
                      <Link 
                        to="/products" 
                        className="btn-primary w-full block text-center py-2"
                      >
                        View Details
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            
            <div className="text-center mt-12">
              <Link 
                to="/products" 
                className="inline-block bg-secondary text-white px-8 py-3 rounded-full hover:bg-primary transition-colors"
              >
                View All Products
              </Link>
            </div>
          </div>
        </div>
      )}

      {/* Newsletter Section */}
      <div className="py-16 bg-primary bg-opacity-10">
        <div className="container mx-auto px-4">
          <div className="max-w-3xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-4">Subscribe to Our Newsletter</h2>
            <p className="text-lg text-gray-600 mb-8">
              Get updates on new products, special offers and discounts!
            </p>
            
            <form className="flex flex-col sm:flex-row gap-4 max-w-xl mx-auto">
              <input 
                type="email" 
                placeholder="Enter your email" 
                className="flex-1 px-4 py-3 rounded-l-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary"
                required
              />
              <button 
                type="submit" 
                className="bg-primary text-white px-6 py-3 rounded-r-lg hover:bg-hover transition-colors"
              >
                Subscribe
              </button>
            </form>
          </div>
        </div>
      </div>
      
    </div>
  );
};

export default Home; 