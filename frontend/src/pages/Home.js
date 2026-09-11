import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Carousel from '../components/Carousel';
import { fetchProducts } from '../services/api';
import { useAuthStore } from '../store/useAuthStore';

const Home = () => {
	const [featuredProducts, setFeaturedProducts] = useState([]);
	const [loading, setLoading] = useState(true);
	const user = useAuthStore((state) => state.user);

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
		<div className="min-h-screen bg-slate-950 text-slate-100">
			{/* Hero Banner */}
			<div className="relative overflow-hidden">
				<div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(99,102,241,0.18),_transparent_30%),linear-gradient(180deg,#020617_0%,#0f172a_90%)]" />
				<img 
					src="/img/img2.png" 
					alt="Hero Background" 
					className="absolute inset-0 w-full h-full object-cover opacity-30 mix-blend-screen"
				/>
				<div className="relative mx-auto flex min-h-[92vh] max-w-7xl flex-col items-center justify-center px-4 text-center">
					<div className="relative z-10 w-full rounded-[2rem] border border-white/10 bg-slate-900/70 p-10 shadow-[0_40px_120px_rgba(15,23,42,0.55)] backdrop-blur-xl">
						<p className="mb-4 inline-flex rounded-full bg-orange-500/10 px-4 py-2 text-sm font-semibold uppercase tracking-[0.25em] text-orange-300 ring-1 ring-orange-400/20">
							Curated picks for modern shoppers
						</p>
						<h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold tracking-tight text-white">
							<span className="block">Elevate your</span>
							<span className="block bg-gradient-to-r from-orange-400 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent">
								Style with confidence
							</span>
						</h1>
						<p className="mt-6 text-lg sm:text-xl text-slate-300">
							Discover premium, trend-forward products crafted for every look and lifestyle.
						</p>
						<div className="mt-10 flex flex-col gap-4 sm:flex-row sm:justify-center">
							<Link 
								to="/products" 
								className="btn-cta inline-flex items-center justify-center rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-8 py-4 text-base font-semibold text-white shadow-lg shadow-orange-500/20 transition duration-200 hover:brightness-110"
							>
								Shop Now
							</Link>
							{user === null && (
								<Link 
									to="/signup" 
									className="inline-flex items-center justify-center rounded-full border border-white/20 bg-white/10 px-8 py-4 text-base font-semibold text-white transition hover:bg-white/20"
								>
									Create Account
								</Link>
							)}
						</div>
						<div className="mt-10 grid gap-4 sm:grid-cols-3">
							<div className="rounded-3xl border border-white/10 bg-slate-950/50 px-5 py-6">
								<p className="text-sm uppercase tracking-[0.22em] text-slate-400">Fast delivery</p>
								<p className="mt-3 text-xl font-semibold text-white">Across India</p>
							</div>
							<div className="rounded-3xl border border-white/10 bg-slate-950/50 px-5 py-6">
								<p className="text-sm uppercase tracking-[0.22em] text-slate-400">Trusted checkout</p>
								<p className="mt-3 text-xl font-semibold text-white">Secure payments</p>
							</div>
							<div className="rounded-3xl border border-white/10 bg-slate-950/50 px-5 py-6">
								<p className="text-sm uppercase tracking-[0.22em] text-slate-400">Easy returns</p>
								<p className="mt-3 text-xl font-semibold text-white">30 day policy</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			{/* Features Section */}
			<div className="py-20 bg-slate-950">
				<div className="container mx-auto px-4">
					<div className="grid gap-6 md:grid-cols-3">
						<div className="feature-card p-8">
							<div className="icon-shell mb-6">
								<svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-orange-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
								</svg>
							</div>
							<h3 className="text-2xl font-semibold text-white mb-3">Free Shipping</h3>
							<p className="text-slate-400">On orders over ₹499, delivered fast to your door.</p>
						</div>
						<div className="feature-card p-8">
							<div className="icon-shell mb-6">
								<svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-fuchsia-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
								</svg>
							</div>
							<h3 className="text-2xl font-semibold text-white mb-3">Secure Payments</h3>
							<p className="text-slate-400">Safe checkout powered by trusted payment partners.</p>
						</div>
						<div className="feature-card p-8">
							<div className="icon-shell mb-6">
								<svg xmlns="http://www.w3.org/2000/svg" className="h-12 w-12 text-violet-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
								</svg>
							</div>
							<h3 className="text-2xl font-semibold text-white mb-3">Easy Returns</h3>
							<p className="text-slate-400">Hassle-free returns within 30 days for simple shopping.</p>
						</div>
					</div>
				</div>
			</div>

			{/* Carousel Section */}
			<div className="py-16 bg-slate-900">
				<div className="container mx-auto px-4 mb-10">
					<h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-orange-400 via-fuchsia-500 to-violet-500 mb-6">
						Featured Collections
					</h2>
				</div>
				<Carousel />
			</div>

			{/* Categories Section */}
			<div className="py-16 bg-slate-950">
				<div className="container mx-auto px-4">
					<h2 className="text-3xl font-bold text-center text-white mb-12">Shop by Category</h2>
					<div className="grid grid-cols-1 gap-8 md:grid-cols-3">
						{[
							{src: '/img/img4.png', title: "Men's Fashion", subtitle: 'Explore stylish collection'},
							{src: '/img/img5.jpg', title: "Women's Fashion", subtitle: 'Trendy women’s collection'},
							{src: '/img/img6.jpg', title: 'Accessories', subtitle: 'Complete your look'},
						].map((item) => (
							<Link key={item.title} to="/products" className="group block overflow-hidden rounded-[1.75rem] border border-white/10 bg-slate-900/70 shadow-[0_30px_80px_rgba(15,23,42,0.35)] transition duration-500 hover:-translate-y-1">
								<div className="relative h-80 overflow-hidden">
									<img src={item.src} alt={item.title} className="h-full w-full object-cover transition duration-700 group-hover:scale-110" />
									<div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent" />
									<div className="absolute inset-0 flex items-end p-6">
										<div className="rounded-3xl bg-black/40 px-5 py-6 backdrop-blur-sm">
											<h3 className="text-2xl font-semibold text-white mb-2">{item.title}</h3>
											<p className="text-slate-300 mb-4">{item.subtitle}</p>
											<span className="inline-flex rounded-full border border-orange-400/30 bg-white/10 px-4 py-2 text-sm text-orange-300 transition group-hover:bg-orange-500/20">
												Shop Now
											</span>
										</div>
									</div>
								</div>
							</Link>
						))}
					</div>
				</div>
			</div>

			{/* Featured Products Section */}
			{!loading && featuredProducts.length > 0 && (
				<div className="py-16 bg-slate-950">
					<div className="container mx-auto px-4">
						<h2 className="text-3xl font-bold text-center text-white mb-12">Popular Products</h2>
						<div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
							{featuredProducts.map((product) => (
								<div key={product.id} className="card h-full flex flex-col overflow-hidden rounded-[1.5rem] border border-white/10 bg-slate-900/90 shadow-[0_30px_80px_rgba(15,23,42,0.45)]">
									<img 
										src={product.imageUrl} 
										alt={product.name} 
										className="h-52 w-full object-cover transition duration-500 hover:scale-105"
										/>
									<div className="p-6 flex flex-col flex-grow">
										<h3 className="text-xl font-semibold text-white mb-3">{product.name}</h3>
										<p className="text-slate-400 mb-5 flex-grow">{product.description}</p>
										<div className="mt-auto">
											<p className="text-2xl font-bold text-white mb-4">₹{product.price}</p>
											<Link 
												to="/products" 
												className="btn-cta w-full block rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-5 py-3 text-center text-sm font-semibold text-white shadow-lg shadow-orange-500/20 transition duration-200 hover:brightness-110"
											>
												View Details
											</Link>
											</div>
									</div>
								</div>
							))}
						</div>
						<div className="mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
							<Link 
								to="/products" 
								className="inline-flex items-center justify-center rounded-full border border-white/10 bg-white/5 px-8 py-4 text-sm font-semibold text-white transition hover:-translate-y-1 hover:bg-white/10"
							>
								View All Products
							</Link>
							<Link 
								to="/aiChat" 
								className="inline-flex items-center justify-center rounded-full border border-white/10 bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-8 py-4 text-sm font-semibold text-white shadow-lg shadow-orange-500/20 transition duration-200 hover:brightness-110"
							>
								AI Chat
							</Link>
						</div>
					</div>
				</div>
			)}

			{/* Newsletter Section */}
			<div className="py-20 bg-slate-900">
				<div className="container mx-auto px-4">
					<div className="relative overflow-hidden rounded-[2rem] border border-white/10 bg-gradient-to-br from-slate-900/80 via-slate-950/70 to-slate-900/90 p-10 shadow-[0_40px_90px_rgba(15,23,42,0.45)]">
						<div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,_rgba(248,113,113,0.12),_transparent_20%)]" />
						<div className="relative text-center">
							<h2 className="text-3xl font-bold text-white mb-4">Subscribe to our newsletter</h2>
							<p className="text-slate-400 text-lg mb-8">Get updates on new products, special offers and discounts.</p>
							<form className="mx-auto flex max-w-3xl flex-col gap-4 sm:flex-row">
								<input 
									type="email" 
									placeholder="Enter your email" 
									className="flex-1 rounded-3xl border border-white/10 bg-slate-950/90 px-5 py-4 text-slate-100 outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-400/20"
									required
								/>
								<button 
									type="submit" 
									className="btn-cta rounded-3xl bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-8 py-4 text-base font-semibold text-white shadow-lg shadow-orange-500/20 transition duration-200 hover:brightness-110"
								>
									Subscribe
								</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};

export default Home; 