import React, { useState, useEffect } from 'react'; // Don't forget to import useEffect
import { useAuth } from '../context/AuthContext';
import './Auth.css';

const Register = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    phone: '',
  });
  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(5); // 5 seconds countdown
  const { register, loading, error, clearError } = useAuth();

  // Auto-redirect effect - PUT THIS HERE
  useEffect(() => {
    if (success) {
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            onSwitchToLogin();
            return 0;
          }
          return prev - 1;
        });
      }, 1000);

      return () => clearInterval(timer);
    }
  }, [success, onSwitchToLogin]);

  // Rest of your component code remains the same...
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    if (error) clearError();
  };

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      return "Passwords don't match";
    }
    if (formData.password.length < 6) {
      return "Password must be at least 6 characters";
    }
    if (!formData.phone.match(/^\d{10}$/)) {
      return "Phone number must be 10 digits";
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      clearError();
      alert(validationError);
      return;
    }

    const { confirmPassword, ...registrationData } = formData;

    try {
      await register(registrationData);
      setSuccess(true); // This triggers the auto-redirect
    } catch (error) {
      // Error handled by context
    }
  };

  const handleBackToLogin = () => {
    onSwitchToLogin();
  };

  // Success Page with countdown
  if (success) {
    return (
      <div className="auth-container">
        <div className="auth-card success-card">
          <div className="success-icon">🎉</div>
          <h2>Account Created Successfully!</h2>
          <p className="success-message">
            Welcome to Urban Bites, <strong>{formData.fullName}</strong>!
          </p>
          <p className="success-details">
            Redirecting to login page in <strong>{countdown}</strong> seconds...
          </p>
          <div className="success-actions">
            <button
              onClick={handleBackToLogin}
              className="btn btn-primary btn-block"
            >
              Continue to Login Now
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Rest of your registration form JSX...
  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Create Your Account</h2>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          {/* Your form fields remain exactly the same */}
          <div className="form-group">
            <label htmlFor="fullName">Full Name</label>
            <input
              type="text"
              id="fullName"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              required
              placeholder="Enter your full name"
              autoComplete="name"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="Enter your email"
              autoComplete="email"
            />
          </div>

          <div className="form-group">
            <label htmlFor="phone">Phone Number</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              required
              placeholder="Enter 10-digit phone number"
              pattern="[0-9]{10}"
              title="Please enter a 10-digit phone number"
              autoComplete="tel"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Enter password (min 6 characters)"
              minLength="6"
              autoComplete="new-password"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              placeholder="Confirm your password"
              autoComplete="new-password"
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-block"
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-switch">
          <p>Already have an account? </p>
          <button
            type="button"
            className="btn-link"
            onClick={onSwitchToLogin}
          >
            Login here
          </button>
        </div>
      </div>
    </div>
  );
};

export default Register;