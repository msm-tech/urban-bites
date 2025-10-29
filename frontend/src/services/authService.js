const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
const DEBUG = process.env.NODE_ENV !== 'production';

class AuthService {
  // Store token in localStorage
  static setToken(token) {
    localStorage.setItem('jwtToken', token);
  }

  // Get token from localStorage
  static getToken() {
    return localStorage.getItem('jwtToken');
  }

  // Remove token (logout)
  static removeToken() {
    localStorage.removeItem('jwtToken');
  }

  // Check if user is authenticated
  static isAuthenticated() {
    return !!this.getToken();
  }

  // Get authorization header for API calls
  static getAuthHeader() {
    const token = this.getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
  }

  // Register new user
static async register(userData) {
  try {
    if (DEBUG) console.log('Sending registration data:', userData);

    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    // First get the response as text
    const responseText = await response.text();
    let responseData;

    // Try to parse as JSON, if it fails, use the text as error message
    try {
      responseData = JSON.parse(responseText);
    } catch {
      // If it's not JSON, create an error object from the text
      responseData = { message: responseText };
    }

    if (!response.ok) {
      const errorMessage = responseData.message ||
                          responseData.error ||
                          responseText ||
                          `Registration failed: ${response.status}`;
      throw new Error(errorMessage);
    }

    if (DEBUG) console.log('Registration successful:', responseData);
    return responseData;

  } catch (error) {
    console.error('Registration error:', error);
    throw error; // Just re-throw the error as-is
  }
}

  // Login user
  static async login(credentials) {
    try {
      if (DEBUG) console.log('Sending login credentials:', credentials);

      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      const responseData = await response.json();

      if (!response.ok) {
        const errorMessage = responseData.message ||
                            responseData.error ||
                            `Login failed: ${response.status}`;
        throw new Error(errorMessage);
      }

      // Store the token
      if (responseData.token) {
        this.setToken(responseData.token);
        if (DEBUG) console.log('Token stored successfully');
      }

      return responseData;

    } catch (error) {
      console.error('Login error:', error);
      throw new Error(error.message || 'Login failed. Please check your credentials.');
    }
  }

  // Logout user
  static logout() {
    this.removeToken();
    // Redirect to login page
    window.location.href = '/login';
  }

  // Get current user info from token (basic decoding)
  static getCurrentUser() {
    const token = this.getToken();
    if (!token) return null;

    try {
      // Simple JWT decoding (for frontend display only)
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (DEBUG) console.log('JWT Payload:', payload); // Debug log

      // Handle different possible field names in JWT
      return {
        id: payload.userId || payload.id || payload.sub,
        email: payload.email || payload.sub,
        fullName: payload.fullName || payload.name,
        phone: payload.phone,
        role: payload.role || 'CUSTOMER'
      };
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }
}

export default AuthService;