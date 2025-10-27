import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import ApiService from '../services/apiService';
import './Orders.css';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      fetchOrders();
    }
  }, [user]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError('');
      const userOrders = await ApiService.getMyOrders();
      console.log('Raw orders data:', userOrders); // Debug log
      setOrders(userOrders);
    } catch (error) {
      console.error('Error fetching orders:', error);
      setError('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    if (!status) return 'status-pending';

    switch (status.toLowerCase()) {
      case 'delivered': return 'status-delivered';
      case 'preparing': return 'status-preparing';
      case 'out_for_delivery': return 'status-out-for-delivery';
      case 'cancelled': return 'status-cancelled';
      case 'completed': return 'status-delivered';
      case 'confirmed': return 'status-preparing';
      default: return 'status-pending';
    }
  };

  // Comprehensive date formatting function
  const formatDate = (dateString) => {
    console.log('Formatting date:', dateString); // Debug log

    if (!dateString) return 'Date not available';

    try {
      // Handle different date formats
      let date;

      // If it's already a Date object
      if (dateString instanceof Date) {
        date = dateString;
      }
      // If it's a string
      else if (typeof dateString === 'string') {
        // Remove timezone info if present and causing issues
        const cleanDateString = dateString.replace('Z', '').replace('+00:00', '');
        date = new Date(cleanDateString);

        // If still invalid, try parsing as ISO without timezone
        if (isNaN(date.getTime())) {
          date = new Date(dateString + 'Z');
        }
      }
      // If it's a number (timestamp)
      else if (typeof dateString === 'number') {
        date = new Date(dateString);
      }
      // If it's an object with timestamp
      else if (dateString && typeof dateString === 'object') {
        date = new Date(dateString);
      }
      else {
        return 'Invalid date format';
      }

      // Final validation
      if (isNaN(date.getTime())) {
        console.warn('Invalid date after parsing:', dateString);
        return 'Invalid date';
      }

      // Format the date nicely
      const options = {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      };

      return date.toLocaleString('en-US', options);

    } catch (error) {
      console.error('Error formatting date:', dateString, error);
      return 'Date error';
    }
  };

  // Alternative simple date display
  const displayDate = (dateString) => {
    // First try the comprehensive formatter
    const formatted = formatDate(dateString);

    // If it still shows error, show raw value for debugging
    if (formatted.includes('error') || formatted.includes('invalid')) {
      return `Order Date (Raw: ${dateString})`;
    }

    return formatted;
  };

  const formatStatus = (status) => {
    if (!status) return 'PENDING';
    return status.replace('_', ' ').toUpperCase();
  };

  if (loading) {
    return (
      <div className="orders-container">
        <div className="loading">Loading your orders...</div>
      </div>
    );
  }

  return (
    <div className="orders-container">
      <div className="orders-header">
        <h2>My Orders</h2>
        <button onClick={fetchOrders} className="btn btn-outline" disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh'}
        </button>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {!user ? (
        <div className="empty-orders">
          <h3>Please Login</h3>
          <p>You need to be logged in to view your orders.</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="empty-orders">
          <h3>No orders yet</h3>
          <p>When you place orders, they will appear here.</p>
          <button onClick={fetchOrders} className="btn btn-primary">
            Check for Orders
          </button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <div className="order-info">
                  <h3>Order #{order.id}</h3>
                  <span className="order-date">{displayDate(order.createdAt)}</span>
                </div>
                <div className="order-status">
                  <span className={`status-badge ${getStatusColor(order.status)}`}>
                    {formatStatus(order.status)}
                  </span>
                </div>
              </div>

              <div className="order-details">
                <div className="order-items">
                  <h4>Items:</h4>
                  {order.items && order.items.length > 0 ? (
                    order.items.map(item => (
                      <div key={item.id} className="order-item">
                        <span>{item.quantity}x {item.menuItemName}</span>
                        <span>${((item.price || 0) * (item.quantity || 1)).toFixed(2)}</span>
                      </div>
                    ))
                  ) : (
                    <div className="order-item">
                      <span>No items found</span>
                    </div>
                  )}
                </div>

                <div className="order-summary">
                  <div className="summary-row">
                    <span>Subtotal:</span>
                    <span>${order.totalAmount?.toFixed(2) || '0.00'}</span>
                  </div>
                  <div className="summary-row">
                    <span>Delivery:</span>
                    <span>$2.99</span>
                  </div>
                  <div className="summary-row total">
                    <strong>Total:</strong>
                    <strong>${((order.totalAmount || 0) + 2.99).toFixed(2)}</strong>
                  </div>
                </div>
              </div>

              <div className="order-footer">
                <div className="delivery-info">
                  <div className="info-row">
                    <strong>Customer:</strong> {order.customerName || 'Not specified'}
                  </div>
                  <div className="info-row">
                    <strong>Phone:</strong> {order.customerPhone || 'Not specified'}
                  </div>
                  {order.deliveryAddress && (
                    <div className="info-row">
                      <strong>Delivery to:</strong> {order.deliveryAddress}
                    </div>
                  )}
                  {order.specialInstructions && (
                    <div className="info-row">
                      <strong>Instructions:</strong> {order.specialInstructions}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;
