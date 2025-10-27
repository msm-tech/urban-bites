import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import ApiService from '../services/apiService';
import './OrderForm.css';

const OrderForm = ({ selectedItems = [], onOrderSuccess, onUpdateQuantity, onClearCart }) => {
  const [orderInfo, setOrderInfo] = useState({
    customerName: '',
    customerPhone: '',
    deliveryAddress: '', // Now optional
    specialInstructions: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { user, isAuthenticated } = useAuth();

  // Auto-fill user info when user is available
  React.useEffect(() => {
    if (user && user.email) {
      setOrderInfo(prev => ({
        ...prev,
        customerName: user.fullName || '',
        customerPhone: user.phone || ''
      }));
    }
  }, [user]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setOrderInfo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const calculateTotal = () => {
    return selectedItems.reduce((total, item) => total + (item.price * item.quantity), 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isAuthenticated) {
      setError('Please login to place an order');
      return;
    }

    if (selectedItems.length === 0) {
      setError('Please add items to your order');
      return;
    }

    // Remove delivery address validation - it's now optional
    if (!orderInfo.customerName.trim()) {
      setError('Please enter your name');
      return;
    }

    if (!orderInfo.customerPhone.trim()) {
      setError('Please enter your phone number');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const orderData = {
        customerName: orderInfo.customerName,
        customerPhone: orderInfo.customerPhone,
        deliveryAddress: orderInfo.deliveryAddress, // Can be empty
        specialInstructions: orderInfo.specialInstructions,
        totalAmount: calculateTotal(),
        items: selectedItems.map(item => ({
          menuItemId: item.id,
          menuItemName: item.name,
          quantity: item.quantity,
          price: item.price
        })),
        status: "PENDING",
        user: user ? { id: user.id } : null
      };

      const response = await ApiService.createOrder(orderData);

      // Clear form and cart
      setOrderInfo({
        customerName: user?.fullName || '',
        customerPhone: user?.phone || '',
        deliveryAddress: '', // Clear delivery address
        specialInstructions: ''
      });

      if (onOrderSuccess) {
        onOrderSuccess(response);
      }

      if (onClearCart) {
        onClearCart();
      }

      alert('Order placed successfully!');

    } catch (error) {
      console.error('Order submission error:', error);
      setError(error.message || 'Failed to place order. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuantityChange = (itemId, change) => {
    const item = selectedItems.find(item => item.id === itemId);
    if (item && onUpdateQuantity) {
      const newQuantity = item.quantity + change;
      onUpdateQuantity(itemId, newQuantity);
    }
  };

  const removeItem = (itemId) => {
    if (onUpdateQuantity) {
      onUpdateQuantity(itemId, 0);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="order-form-container">
        <div className="auth-required-message">
          <h3>Please Login to Place Orders</h3>
          <p>You need to be logged in to place an order.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="order-form-container">
      <h3>Place Your Order</h3>

      {selectedItems.length > 0 ? (
        <>
          <div className="order-summary">
            <h4>Order Summary</h4>
            {selectedItems.map(item => (
              <div key={item.id} className="order-item">
                <div className="item-info">
                  <span className="item-name">{item.name} - ${item.price.toFixed(2)}</span>
                  <div className="quantity-controls">
                    <button
                      type="button"
                      className="quantity-btn"
                      onClick={() => handleQuantityChange(item.id, -1)}
                    >
                      -
                    </button>
                    <span className="quantity-display">x {item.quantity}</span>
                    <button
                      type="button"
                      className="quantity-btn"
                      onClick={() => handleQuantityChange(item.id, 1)}
                    >
                      +
                    </button>
                    <button
                      type="button"
                      className="remove-btn"
                      onClick={() => removeItem(item.id)}
                    >
                      Remove
                    </button>
                  </div>
                </div>
                <span className="item-total">${(item.price * item.quantity).toFixed(2)}</span>
              </div>
            ))}
            <div className="order-total">
              <strong>Total: ${calculateTotal().toFixed(2)}</strong>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="order-form">
            {error && <div className="alert alert-error">{error}</div>}

            <div className="form-group">
              <label htmlFor="customerName">Full Name *</label>
              <input
                type="text"
                id="customerName"
                name="customerName"
                value={orderInfo.customerName}
                onChange={handleInputChange}
                required
                placeholder="Enter your full name"
              />
            </div>

            <div className="form-group">
              <label htmlFor="customerPhone">Phone Number *</label>
              <input
                type="tel"
                id="customerPhone"
                name="customerPhone"
                value={orderInfo.customerPhone}
                onChange={handleInputChange}
                required
                placeholder="Enter your phone number"
                pattern="[0-9]{10}"
              />
            </div>

            <div className="form-group">
              <label htmlFor="deliveryAddress">Delivery Address</label>
              <textarea
                id="deliveryAddress"
                name="deliveryAddress"
                value={orderInfo.deliveryAddress}
                onChange={handleInputChange}
                placeholder="Enter delivery address (optional)"
                rows="3"
              />
              <small className="field-hint">Leave blank for pickup orders</small>
            </div>

            <div className="form-group">
              <label htmlFor="specialInstructions">Special Instructions</label>
              <textarea
                id="specialInstructions"
                name="specialInstructions"
                value={orderInfo.specialInstructions}
                onChange={handleInputChange}
                placeholder="Any special instructions for your order..."
                rows="2"
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={loading || selectedItems.length === 0}
            >
              {loading ? 'Placing Order...' : `Place Order - $${calculateTotal().toFixed(2)}`}
            </button>
          </form>
        </>
      ) : (
        <div className="empty-order">
          <p>Your cart is empty. Add some delicious items from the menu!</p>
        </div>
      )}
    </div>
  );
};

export default OrderForm;