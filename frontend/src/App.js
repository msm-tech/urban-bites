import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './components/Login';
import Register from './components/Register';
import Menu from './components/Menu';
import OrderForm from './components/OrderForm';
import Orders from './components/Orders'; // This should now work
import Header from './components/Header';
import './App.css';

// Main app content that uses authentication
const AppContent = () => {
  const { user, logout } = useAuth();
  const [currentView, setCurrentView] = useState('menu');
  const [authMode, setAuthMode] = useState('login');
  const [cartItems, setCartItems] = useState([]);

  const addToCart = (item) => {
    setCartItems(prev => {
      const existing = prev.find(i => i.id === item.id);
      if (existing) {
        return prev.map(i =>
          i.id === item.id
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }
      return [...prev, { ...item, quantity: 1 }];
    });
  };

  const updateQuantity = (itemId, newQuantity) => {
    if (newQuantity === 0) {
      setCartItems(prev => prev.filter(item => item.id !== itemId));
    } else {
      setCartItems(prev =>
        prev.map(item =>
          item.id === itemId ? { ...item, quantity: newQuantity } : item
        )
      );
    }
  };

  const clearCart = () => {
    setCartItems([]);
  };

  const handleOrderSuccess = () => {
    setCartItems([]); // Clear cart on successful order
    setCurrentView('orders'); // Switch to orders view
  };

  // If user is not authenticated, show auth pages
  if (!user) {
    return (
      <div className="App">
        {authMode === 'login' ? (
          <Login onSwitchToRegister={() => setAuthMode('register')} />
        ) : (
          <Register onSwitchToLogin={() => setAuthMode('login')} />
        )}
      </div>
    );
  }

  // User is authenticated, show main app
  return (
    <div className="App">
      <Header
        user={user}
        onLogout={logout}
        currentView={currentView}
        onViewChange={setCurrentView}
        cartItemCount={cartItems.reduce((total, item) => total + item.quantity, 0)}
      />

      <main className="app-main">
        {currentView === 'menu' && (
          <div className="menu-order-layout">
            <div className="menu-section">
              <Menu onAddToCart={addToCart} />
            </div>
            <div className="order-section">
              <OrderForm
                selectedItems={cartItems}
                onUpdateQuantity={updateQuantity}
                onOrderSuccess={handleOrderSuccess}
                onClearCart={clearCart}
              />
            </div>
          </div>
        )}

        {currentView === 'orders' && (
          <Orders />
        )}
      </main>
    </div>
  );
};

// Main App component with AuthProvider
function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;