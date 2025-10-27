import React, { useState, useEffect } from 'react';
import ApiService from '../services/apiService';
import './Menu.css';

const Menu = ({ onAddToCart }) => {
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  useEffect(() => {
    fetchMenu();
  }, []);

  const fetchMenu = async () => {
    try {
      setLoading(true);
      const items = await ApiService.getMenu();

      // Ensure all items have availability set (default to true if missing)
      const itemsWithAvailability = items.map(item => ({
        ...item,
        available: item.available !== false // Default to true if not specified
      }));

      setMenuItems(itemsWithAvailability);
    } catch (error) {
      console.error('Error fetching menu:', error);
      setError('Failed to load menu');
    } finally {
      setLoading(false);
    }
  };

  const categories = ['all', ...new Set(menuItems.map(item => item.category))];

  const filteredItems = selectedCategory === 'all'
    ? menuItems
    : menuItems.filter(item => item.category === selectedCategory);

  const handleAddToCart = (item) => {
    if (onAddToCart && item.available) {
      onAddToCart(item);
      // Optional: Show a quick confirmation
      alert(`Added ${item.name} to cart!`);
    }
  };

  if (loading) {
    return <div className="menu-loading">Loading menu...</div>;
  }

  if (error) {
    return (
      <div className="menu-error">
        <p>{error}</p>
        <button onClick={fetchMenu} className="btn btn-primary">
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="menu-container">
      <div className="menu-header">
        <h2>Our Menu</h2>
        <div className="category-filters">
          {categories.map(category => (
            <button
              key={category}
              className={`category-btn ${selectedCategory === category ? 'active' : ''}`}
              onClick={() => setSelectedCategory(category)}
            >
              {category.charAt(0).toUpperCase() + category.slice(1)}
            </button>
          ))}
        </div>
      </div>

      <div className="menu-items">
        {filteredItems.map(item => (
          <div key={item.id} className="menu-item-card">
            <div className="menu-item-image">
              {item.imageUrl ? (
                <img src={item.imageUrl} alt={item.name} />
              ) : (
                <div className="menu-item-placeholder">
                  {item.category === 'PIZZA' ? '🍕' :
                   item.category === 'SALAD' ? '🥗' :
                   item.category === 'DESSERT' ? '🍰' :
                   item.category === 'BEVERAGE' ? '🥤' : '🍽️'}
                </div>
              )}
            </div>

            <div className="menu-item-info">
              <h3>{item.name}</h3>
              <p className="menu-item-description">{item.description}</p>
              <div className="menu-item-meta">
                <span className="price">${item.price?.toFixed(2)}</span>
                <span className={`availability ${item.available ? 'available' : 'unavailable'}`}>
                  {item.available ? 'Available' : 'Unavailable'}
                </span>
              </div>

              <button
                className={`btn ${item.available ? 'btn-primary' : 'btn-disabled'} add-to-cart-btn`}
                onClick={() => handleAddToCart(item)}
                disabled={!item.available}
              >
                {item.available ? 'Add to Cart' : 'Out of Stock'}
              </button>
            </div>
          </div>
        ))}
      </div>

      {filteredItems.length === 0 && (
        <div className="no-items">
          <p>No items found in this category.</p>
        </div>
      )}
    </div>
  );
};

export default Menu;