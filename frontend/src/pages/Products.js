import React, { useState, useEffect } from 'react';
import Carousel from '../components/Carousel';
import ProductCard from '../components/ProductCard';
import { fetchProducts } from '../services/api';

const Products = () => {
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
        <div className="min-h-screen bg-slate-950 flex items-center justify-center">
            <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-white"></div>
        </div>
        );
    }

    if (error) {
        return (
        <div className="min-h-screen bg-slate-950 flex items-center justify-center">
            <div className="text-center">
            <p className="text-red-400 text-xl mb-4">{error}</p>
            <button onClick={() => window.location.reload()} className="btn-cta">
                Try Again
            </button>
            </div>
        </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-950 text-white">
        <Carousel />
        
        <div className="container mx-auto px-4 py-8">
            {/* Trending Products */}
            <div className="rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-8 mb-12">
                <h2 className="text-3xl font-bold bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent text-center mb-8">
                🔥 Trending Products
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {trendingProducts.map(product => (
                    <div key={product.id} className="h-full">
                    <ProductCard product={product} />
                    </div>
                ))}
                </div>
            </div>

            {/* Clothing */}
            <div className="rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-8 mb-12">
                <h2 className="text-3xl font-bold bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent text-center mb-8">
                👗 Clothing Collection
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {clothingProducts.map(product => (
                    <div key={product.id} className="h-full">
                    <ProductCard product={product} />
                    </div>
                ))}
                </div>
            </div>

            {/* Electronics */}
            <div className="rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-8">
                <h2 className="text-3xl font-bold bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent text-center mb-8">
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
        </div>
    );
};

export default Products; 