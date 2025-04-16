import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-secondary text-gray-300 pt-12 pb-8">
      <div className="container mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Online Store */}
          <div>
            <h5 className="footer-heading">ONLINE STORE</h5>
            <ul className="space-y-2">
              <li><Link to="/" className="footer-link">Men Clothing</Link></li>
              <li><Link to="/" className="footer-link">Women Clothing</Link></li>
              <li><Link to="/" className="footer-link">Men Accessories</Link></li>
              <li><Link to="/" className="footer-link">Women Accessories</Link></li>
            </ul>
          </div>

          {/* Helpful Links */}
          <div>
            <h5 className="footer-heading">HELPFUL LINKS</h5>
            <ul className="space-y-2">
              <li><Link to="/" className="footer-link">Home</Link></li>
              <li><Link to="/" className="footer-link">About</Link></li>
              <li><Link to="/" className="footer-link">Contact</Link></li>
            </ul>
          </div>

          {/* Brand Partners */}
          <div>
            <h5 className="footer-heading">BRAND PARTNERS</h5>
            <ul className="space-y-2">
              <li className="text-gray-300">Zara</li>
              <li className="text-gray-300">Pantaloons</li>
              <li className="text-gray-300">Levi's</li>
              <li className="text-gray-300">UCB</li>
              <li className="text-gray-300">+ Many More</li>
            </ul>
          </div>

          {/* Address */}
          <div>
            <h5 className="footer-heading">ADDRESS</h5>
            <ul className="space-y-2">
              <li className="text-gray-300">Building 101</li>
              <li className="text-gray-300">Central Avenue</li>
              <li className="text-gray-300">LA - 902722</li>
              <li className="text-gray-300">United States</li>
            </ul>
          </div>
        </div>

        {/* Copyright */}
        <div className="mt-8 pt-8 border-t border-gray-700 text-center">
          <p className="text-sm text-gray-400">
            © 2024 Shopify | Designed by <span className="font-bold">Ashwani Upadhyay</span>
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer; 