/*
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
      setOrders(userOrders || []);
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

  const formatDate = (dateString) => {
    if (!dateString) return 'Date not available';

    try {
      let cleanDateString = dateString;

      // Fix corrupted formats from old orders
      if (typeof cleanDateString === 'string') {
        const missingTPattern = /^(\d{4}-\d{2}-\d{2})(\d{1,2}:\d{2}:\d{2}\.\d+)$/;
        const match = cleanDateString.match(missingTPattern);
        if (match) {
          cleanDateString = match[1] + 'T' + match[2];
        }
        if (!cleanDateString.includes('+') && !cleanDateString.endsWith('Z')) {
          cleanDateString += 'Z';
        }
      }

      const date = new Date(cleanDateString);
      if (isNaN(date.getTime())) return 'Invalid date';

      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      });

    } catch (error) {
      return `Date: ${dateString}`;
    }
  };

  const formatStatus = (status) => {
    if (!status) return 'PENDING';
    return status.replace('_', ' ').toUpperCase();
  };

  if (loading) {
    return <div className="orders-container"><div className="loading">Loading your orders...</div></div>;
  }

  return (
    <div className="orders-container">
      <div className="orders-header">
        <h2>My Orders</h2>
        <button onClick={fetchOrders} className="btn btn-outline" disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh'}
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {!user ? (
        <div className="empty-orders">
          <h3>Please Login</h3>
          <p>You need to be logged in to view your orders.</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="empty-orders">
          <h3>No orders yet</h3>
          <p>When you place orders, they will appear here.</p>
          <button onClick={fetchOrders} className="btn btn-primary">Check for Orders</button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-header">
                <div className="order-info">
                  <h3>Order #{order.id}</h3>
                  <span className="order-date">{formatDate(order.createdAt)}</span>
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
                    <div className="order-item"><span>No items found</span></div>
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

export default Orders;*/


// src/components/Orders.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import ApiService from '../services/apiService';
import './Orders.css';

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      fetchOrders();
    } else {
      setLoading(false);
    }
  }, [user]);

  const fetchOrders = async (isRefresh = false) => {
    try {
      if (isRefresh) {
        setRefreshing(true);
      } else {
        setLoading(true);
      }
      setError('');

      const userOrders = await ApiService.getMyOrders();
      setOrders(userOrders || []);
    } catch (error) {
      console.error('Error fetching orders:', error);
      setError('Failed to load orders. Please check your connection and try again.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const handleRefresh = () => {
    fetchOrders(true);
  };

  const getStatusColor = (status) => {
    if (!status) return 'status-pending';
    switch (status.toLowerCase()) {
      case 'delivered':
      case 'completed':
        return 'status-delivered';
      case 'preparing':
      case 'confirmed':
        return 'status-preparing';
      case 'out_for_delivery':
      case 'out for delivery':
        return 'status-out-for-delivery';
      case 'cancelled':
        return 'status-cancelled';
      default:
        return 'status-pending';
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Date not available';

    try {
      // Handle various date formats
      let date;

      if (dateString.includes('T')) {
        date = new Date(dateString);
      } else if (dateString.includes(' ')) {
        // Handle space-separated datetime
        date = new Date(dateString.replace(' ', 'T'));
      } else {
        // Try parsing as is
        date = new Date(dateString);
      }

      if (isNaN(date.getTime())) {
        return 'Invalid date';
      }

      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      });

    } catch (error) {
      console.warn('Date formatting error:', error);
      return 'Date unavailable';
    }
  };

  const formatStatus = (status) => {
    if (!status) return 'PENDING';
    return status.replace(/_/g, ' ').toUpperCase();
  };

  // Show loading state
  if (loading) {
    return (
      <div className="orders-container">
        <div className="loading">
          <div className="loading-spinner"></div>
          <p>Loading your orders...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="orders-container">
      <div className="section-header">
        <h2>My Orders</h2>
        {user && (
          <button
            onClick={handleRefresh}
            className="btn btn-outline"
            disabled={refreshing}
          >
            {refreshing ? (
              <>
                <span className="refresh-spinner"></span>
                Refreshing...
              </>
            ) : (
              <>
                <span>🔄</span>
                Refresh Orders
              </>
            )}
          </button>
        )}
      </div>

      {error && (
        <div className="alert alert-error">
          <strong>Error:</strong> {error}
          <button
            onClick={handleRefresh}
            className="retry-btn"
          >
            Try Again
          </button>
        </div>
      )}

      {!user ? (
        <div className="empty-orders">
          <div className="empty-icon">🔐</div>
          <h3>Authentication Required</h3>
          <p>Please log in to view your order history and track your deliveries.</p>
        </div>
      ) : orders.length === 0 ? (
        <div className="empty-orders">
          <div className="empty-icon">📦</div>
          <h3>No Orders Found</h3>
          <p>You haven't placed any orders yet. Start exploring our menu to place your first order!</p>
          <button onClick={handleRefresh} className="btn btn-primary">
            Check for Orders
          </button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order, index) => (
            <div
              key={order.id}
              className="order-card"
              style={{ animationDelay: `${index * 0.1}s` }}
            >
              <div className="order-header">
                <div className="order-info">
                  <h3>Order #{order.id}</h3>
                  <span className="order-date">{formatDate(order.createdAt)}</span>
                </div>
                <div className="order-status">
                  <span className={`status-badge ${getStatusColor(order.status)}`}>
                    {formatStatus(order.status)}
                  </span>
                </div>
              </div>

              <div className="order-details">
                <div className="order-items">
                  <h4>Order Items</h4>
                  {order.items && order.items.length > 0 ? (
                    order.items.map(item => (
                      <div key={item.id} className="order-item">
                        <span>
                          {item.quantity}x {item.menuItemName || 'Menu Item'}
                        </span>
                        <span>
                          ${((item.price || 0) * (item.quantity || 1)).toFixed(2)}
                        </span>
                      </div>
                    ))
                  ) : (
                    <div className="order-item">
                      <span>No items available</span>
                      <span>$0.00</span>
                    </div>
                  )}
                </div>

                <div className="order-summary">
                  <div className="summary-row">
                    <span>Subtotal:</span>
                    <span>${order.totalAmount?.toFixed(2) || '0.00'}</span>
                  </div>
                  <div className="summary-row">
                    <span>Delivery Fee:</span>
                    <span>$2.99</span>
                  </div>
                  <div className="summary-row total">
                    <strong>Total:</strong>
                    <strong>${((order.totalAmount || 0) + 2.99).toFixed(2)}</strong>
                  </div>
                </div>
              </div>

              {(order.deliveryAddress || order.customerName || order.customerPhone || order.specialInstructions) && (
                <div className="order-footer">
                  <div className="delivery-info">
                    {order.customerName && (
                      <div className="info-row">
                        <strong>Customer:</strong>
                        <span>{order.customerName}</span>
                      </div>
                    )}
                    {order.customerPhone && (
                      <div className="info-row">
                        <strong>Phone:</strong>
                        <span>{order.customerPhone}</span>
                      </div>
                    )}
                    {order.deliveryAddress && (
                      <div className="info-row">
                        <strong>Delivery Address:</strong>
                        <span>{order.deliveryAddress}</span>
                      </div>
                    )}
                    {order.specialInstructions && (
                      <div className="info-row">
                        <strong>Instructions:</strong>
                        <span>{order.specialInstructions}</span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;