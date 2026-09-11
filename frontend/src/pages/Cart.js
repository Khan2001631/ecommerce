import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { api } from '../services/api';
import { toast } from 'react-toastify';
import { useCartStore } from '../store/useCartStore';

const Cart = () => {
    const navigate = useNavigate();
    const {user} = useAuthStore();
    const cart = useCartStore(state => state.cart);
    const changeQuantity = useCartStore(state => state.changeQuantity);
    const removeFromCart = useCartStore(state => state.removeFromCart);
    const getTotalAmount = useCartStore(state => state.getTotalAmount);
    const deleteCart = useCartStore(state => state.deleteCart);
    const fetchCart = useCartStore(state => state.fetchCart);
    const loading = useCartStore(state => state.loading);
    const [isLoading, setIsLoading] = useState(false);

    // Fetch cart on mount
    useEffect(() => {
        fetchCart();
    }, [fetchCart]);


    const handleCheckout = async () => {
        setIsLoading(true);
        const productQuantities = cart.reduce((map, item) => {
            map[item.productId] = item.quantity;
            return map;
        }, {});
        try {
            await placeOrder(user?.id, productQuantities);
            const payloadForPaymentAPI = {
                name: user?.name || "Guest",
                email: user?.email || "guest@example.com",
                phone: user?.phone || "9999999999",
                amount: getTotalAmount(),
            };
            await savePaymentDetails(payloadForPaymentAPI);
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    const savePaymentDetails = async (payload) => {
        try {
            const res = await api.post("/payment/create-order", payload);
            toast.success(res.data);
            navigate('/products');
        }
        catch(error) {
            toast.error("Payment Failed.");
        }
    }

    // TODO: Razorpay is implemented. Just need a key to make it work.
    // const handleCheckout = async () => {
    //     const amount = getTotalAmount(); // in rupees
    
    //     const paymentOrder = {
    //         name: user?.name || "Guest",
    //         email: user?.email || "guest@example.com",
    //         phone: user?.phone || "9999999999", // assuming you store phone
    //         amount,
    //     };
    
    //     try {
            // const res = await axios.post("http://localhost:8080/payment/create-order", paymentOrder, {
            //     headers: {
            //         Authorization: `Bearer ${token}`,
            //     },
            // });
    
    //         const { id: razorpayOrderId } = res.data;
    
    //         const options = {
    //             key: "dXWsxbV1d0XntN66yokALhrs",
    //             amount: amount * 100,
    //             currency: "INR",
    //             name: "Shopify",
    //             description: "Test Transaction",
    //             order_id: razorpayOrderId,
    //             handler: async function (response) {
    //                 toast.success("Payment Successful! Payment ID: " + response.razorpay_payment_id);
                    
    //                 // Optionally update backend with paymentId
    //                 await axios.post(`http://localhost:8080/orders/place/${user?.id}`, 
    //                     cart.reduce((map, item) => {
    //                         map[item.id] = item.quantity;
    //                         return map;
    //                     }, {}),
    //                     {
    //                         headers: {
    //                             Authorization: `Bearer ${token}`,
    //                         }
    //                     }
    //                 );
    //             },
    //             prefill: {
    //                 name: paymentOrder.name,
    //                 email: paymentOrder.email,
    //                 contact: paymentOrder.phone,
    //             },
    //             theme: {
    //                 color: "#3399cc",
    //             },
    //         };
    
    //         const rzp = new window.Razorpay(options);
    //         rzp.open();
    //     } catch (err) {
    //         console.error("Payment failed", err);
    //         alert("Something went wrong during payment.");
    //     }
    // };
    
    const placeOrder = async (userId, productQuantities) => {
        try {
        const response = await api.post(
            `/orders/place/${userId}`,
            productQuantities
        );
        if(response.status === 200) {
            toast.success("Order created succesfully")
        }
        deleteCart();
        return response.data;
        } catch (error) {
        toast.error("Order failed:");
        throw error;
        }
    };

    // If cart is empty
    if (cart.length === 0 && !isLoading) {
        return (
        <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center px-4 py-20 text-white">
            <div className="rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-8 text-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-20 w-20 text-slate-400 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <h2 className="text-3xl font-bold mt-4 mb-2 bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent">Your cart is empty</h2>
                <p className="text-slate-300 mb-6">Add some items to your cart and come back here.</p>
                <Link to="/" className="btn-cta inline-flex items-center justify-center rounded-full bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-orange-500/25 transition duration-200 hover:brightness-110">
                    Continue Shopping
                </Link>
            </div>
        </div>
        );
    }

    if (isLoading) {
        return (
            <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center transition-opacity duration-300">
                <div className="w-16 h-16 border-4 border-white border-t-orange-500 rounded-full animate-spin"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-950 text-white">
        <div className="container mx-auto px-4 py-20">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 bg-clip-text text-transparent mb-8 text-center">Your Shopping Cart</h1>
        
        <div className="rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-6 mb-8 overflow-x-auto">
            <table className="w-full text-white">
            <thead className="border-b border-slate-700">
                <tr>
                <th className="py-4 px-6 text-left text-slate-300">Product</th>
                <th className="py-4 px-6 text-left text-slate-300">Name</th>
                <th className="py-4 px-6 text-left text-slate-300">Price</th>
                <th className="py-4 px-6 text-left text-slate-300">Quantity</th>
                <th className="py-4 px-6 text-left text-slate-300">Total</th>
                <th className="py-4 px-6 text-left text-slate-300">Remove</th>
                </tr>
            </thead>
            <tbody>
                {cart.map((item) => (
                <tr key={item.productId} className="border-b border-slate-700">
                    <td className="py-4 px-6">
                    <img src={item.imageUrl} alt={item.name} className="w-16 h-16 object-cover rounded-lg" />
                    </td>
                    <td className="py-4 px-6 font-medium text-slate-200">{item.name}</td>
                    <td className="py-4 px-6 text-slate-300">₹{item.price}</td>
                    <td className="py-4 px-6">
                    <div className="flex items-center space-x-2">
                        <button 
                        onClick={() => changeQuantity(item.productId, item.quantity - 1)}
                        className="w-8 h-8 flex items-center justify-center bg-slate-700 rounded-full hover:bg-slate-600 text-white transition"
                        >
                        -
                        </button>
                        <span className="text-slate-200">{item.quantity}</span>
                        <button 
                        onClick={() => changeQuantity(item.productId, item.quantity + 1)}
                        className="w-8 h-8 flex items-center justify-center bg-slate-700 rounded-full hover:bg-slate-600 text-white transition"
                        >
                        +
                        </button>
                    </div>
                    </td>
                    <td className="py-4 px-6 font-bold text-slate-200">₹{(item.price * item.quantity).toFixed(2)}</td>
                    <td className="py-4 px-6">
                    <button 
                        onClick={() => removeFromCart(item.productId)}
                        className="text-red-400 hover:text-red-300 transition"
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
        
        <div className="flex flex-col md:flex-row md:justify-end">
            <div className="md:w-1/3 rounded-3xl bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 p-6 text-white">
            <h2 className="text-xl font-bold mb-4 text-slate-200">Order Summary</h2>
            <div className="flex justify-between mb-2 text-slate-300">
                <span>Items ({cart.length}):</span>
                <span>₹{getTotalAmount().toFixed(2)}</span>
            </div>
            <div className="flex justify-between mb-2 text-slate-300">
                <span>Shipping:</span>
                <span>Free</span>
            </div>
            <div className="border-t border-slate-700 my-4"></div>
            <div className="flex justify-between text-xl font-bold text-slate-200">
                <span>Total:</span>
                <span>₹{getTotalAmount().toFixed(2)}</span>
            </div>
            <button 
                onClick={handleCheckout}
                className="w-full mt-6 btn-cta py-3 rounded-full"
            >
                Proceed to Payment
            </button>
            </div>
        </div>
        </div>
        </div>
    );
};

export default Cart; 