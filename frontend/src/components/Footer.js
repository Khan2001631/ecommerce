import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-slate-950 text-slate-300">
      <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="grid gap-8 md:grid-cols-3">
          <div className="space-y-4">
            <div className="inline-flex rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-4 py-2 text-sm font-semibold text-white shadow-lg shadow-orange-500/20">
              Shopify
            </div>
            <p className="max-w-sm text-sm text-slate-400">
              A modern shopping experience built for style, ease, and secure checkout.
            </p>
          </div>

          <div>
            <h3 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-400 mb-4">
              Quick links
            </h3>
            <ul className="space-y-3 text-sm">
              <li><a href="#" className="text-slate-300 hover:text-white transition">Home</a></li>
              <li><a href="#" className="text-slate-300 hover:text-white transition">Products</a></li>
              <li><a href="#" className="text-slate-300 hover:text-white transition">AI Chat</a></li>
            </ul>
          </div>

          <div>
            <h3 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-400 mb-4">
              Contact
            </h3>
            <p className="text-sm text-slate-400">Need help? Reach out anytime.</p>
            <p className="mt-3 text-sm text-slate-300">support@shopify.com</p>
          </div>
        </div>

        <div className="mt-10 border-t border-white/10 pt-6 text-center text-sm text-slate-500">
          © 2024 Shopify | Designed by <span className="font-semibold text-slate-100">Mohamamd Khan</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer; 