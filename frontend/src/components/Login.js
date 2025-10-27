import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

const Login = ({ onSwitchToRegister }) => {
  const [formData, setFormData] = useState({
    email: '',
    phone: '',
    password: '',
  });
  const [useEmail, setUseEmail] = useState(true);
  const { login, loading, error, clearError } = useAuth();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Prepare credentials based on login method
    const credentials = useEmail
      ? { email: formData.email, password: formData.password }
      : { phone: formData.phone, password: formData.password };

    try {
      await login(credentials);
      // Redirect or show success - handled by context
    } catch (error) {
      // Error handled by context
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Login to Your Account</h2>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        <div className="login-method-toggle">
          <button
            type="button"
            className={`toggle-btn ${useEmail ? 'active' : ''}`}
            onClick={() => setUseEmail(true)}
          >
            Use Email
          </button>
          <button
            type="button"
            className={`toggle-btn ${!useEmail ? 'active' : ''}`}
            onClick={() => setUseEmail(false)}
          >
            Use Phone
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {useEmail ? (
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleChange}  // ✅ Correct
                required
                placeholder="Enter your email"
                autoComplete="email"
              />
            </div>
          ) : (
            <div className="form-group">
              <label htmlFor="phone">Phone Number</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}  // ✅ Correct
                required
                placeholder="Enter your phone number"
                pattern="[0-9]{10}"
                title="Please enter a 10-digit phone number"
                autoComplete="tel"
              />
            </div>
          )}

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}  // ✅ FIXED - using handleChange instead of setPassword
              required
              placeholder="Enter your password"
              autoComplete="current-password"
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        <div className="auth-switch">
          <p>Don't have an account? </p>
          <button
            type="button"
            className="btn-link"
            onClick={onSwitchToRegister}
          >
            Sign up here
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;