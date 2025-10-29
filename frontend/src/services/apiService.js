import AuthService from './authService';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

class ApiService {
  // Generic API call with authentication
  static async callApi(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const authHeader = AuthService.getAuthHeader();

    const config = {
      headers: {
        'Content-Type': 'application/json',
        ...authHeader,
        ...options.headers,
      },
      ...options,
    };

    // Add body if present (and not a GET request)
    if (options.body && config.method !== 'GET') {
      config.body = JSON.stringify(options.body);
    }

    try {
      const response = await fetch(url, config);

      // Handle unauthorized (token expired)
      if (response.status === 401) {
        AuthService.logout();
        throw new Error('Session expired. Please login again.');
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || `API error: ${response.status}`);
      }

      // For DELETE requests that might not return content
      if (response.status === 204) {
        return null;
      }

      return await response.json();
    } catch (error) {
      console.error('API call error:', error);
      throw error;
    }
  }

  // Order API methods
  static async createOrder(orderData) {
    return this.callApi('/orders', {
      method: 'POST',
      body: orderData,
    });
  }

  static async getMyOrders() {
    // Use the new my-orders endpoint that uses authentication
    return this.callApi('/orders/my-orders');
  }

  /*  static async getMyOrders() {
      // Get current user from token to fetch their orders
      const currentUser = AuthService.getCurrentUser();
      if (!currentUser || !currentUser.email) {
        throw new Error('User not authenticated');
      }

      // Use the email endpoint from your backend
      return this.callApi(`/orders/user/email/${currentUser.email}`);
    }*/

  static async getAllOrders() {
    return this.callApi('/orders');
  }

  static async getOrder(orderId) {
    return this.callApi(`/orders/${orderId}`);
  }

  static async updateOrderStatus(orderId, newStatus) {
    return this.callApi(`/orders/${orderId}/status`, {
      method: 'PUT',
      body: newStatus,
    });
  }

  // Menu API methods
  static async getMenu() {
    return this.callApi('/menu');
  }

  static async getMenuByCategory(category) {
    return this.callApi(`/menu/category/${category}`);
  }
}

export default ApiService;