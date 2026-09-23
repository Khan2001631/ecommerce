import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { useCartStore } from '../store/useCartStore';

const Navbar = () => {
  // derive count directly from Zustand state
  const cartItemsCount = useCartStore(state => state.cart.length);
  const {user, logout} = useAuthStore();
  return (
    <nav className="navbar fixed inset-x-0 top-0 z-20">
      <div className="container mx-auto flex flex-wrap items-center justify-between gap-4 px-4 py-4">
        <Link to="/" className="navbar-brand inline-flex items-center gap-3 text-2xl font-black tracking-tight">
          <span className="inline-flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-br from-orange-400 via-fuchsia-500 to-violet-500 text-sm font-semibold shadow-lg shadow-orange-500/20">
            S
          </span>
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-400 via-fuchsia-500 to-violet-400">
            SHOPIFY
          </span>
        </Link>

        <div className="hidden md:flex items-center gap-8">
          <Link to="/" className="navbar-link">Home</Link>
          {user !== null && (
            <Link to="/aiChat" className="navbar-link">AI Chat</Link>
          )}
          {user !== null ? (
            <>
              <Link to="/cart" className="relative inline-flex items-center gap-2 rounded-full border border-slate-700 bg-slate-900/80 px-4 py-2 text-sm font-medium text-slate-100 transition hover:border-indigo-400 hover:text-white">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                Cart
                {cartItemsCount > 0 && (
                  <span className="rounded-full bg-gradient-to-r from-orange-500 to-violet-500 px-2 py-0.5 text-[11px] font-semibold text-white shadow-sm">
                    {cartItemsCount}
                  </span>
                )}
              </Link>
              <button onClick={logout} className="btn-cta inline-flex items-center justify-center rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-orange-500/25 transition duration-200 hover:brightness-110">
                Logout
              </button>
            </>
          ) : (
            <Link to="/login" className="btn-cta inline-flex items-center justify-center rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-5 py-2 text-sm font-semibold text-white shadow-lg shadow-orange-500/25 transition duration-200 hover:brightness-110">
              Login
            </Link>
          )}
        </div>

        <button className="md:hidden rounded-full border border-slate-700 bg-slate-900/80 p-3 text-slate-200 shadow-lg shadow-slate-950/30 transition hover:border-indigo-400 hover:text-white focus:outline-none">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      </div>
    </nav>
  );
};

export default Navbar; 