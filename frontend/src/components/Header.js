import React from 'react';
import './Header.css';

const Header = ({ user, onLogout, currentView, onViewChange }) => {
  return (
    <header className="app-header">
      <div className="header-content">
        <div className="header-brand">
          <h1>🍽️ Urban Bites</h1>
          <p>Delicious Food, Delivered Fresh</p>
        </div>

        <nav className="header-nav">
          <button
            className={`nav-btn ${currentView === 'menu' ? 'active' : ''}`}
            onClick={() => onViewChange('menu')}
          >
            Menu
          </button>
          <button
            className={`nav-btn ${currentView === 'orders' ? 'active' : ''}`}
            onClick={() => onViewChange('orders')}
          >
            My Orders
          </button>
        </nav>

        <div className="header-user">
          <span className="user-email">Welcome, {user.email}</span>
          <button className="btn btn-outline" onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;