import React from 'react';
import PropTypes from 'prop-types';
import './logout.css';

class Logout extends React.Component {
  constructor(props) {
    super(props);

    this.handleLogoutClick = this.handleLogoutClick.bind(this);
  }

  handleLogoutClick(event) {
    event.preventDefault();
    fetch('/logout', {
      method: 'GET',
      credentials: 'include'
    })
    .then(response => {
      if (response.ok) {
        this.props.onLogout();
      } else {
        throw new Error('Failed to logout');
      }
    })
    .catch(err => console.log(err));
  }

  render() {
    return (
      <input
        type="submit"
        value="Logout"
        className="logout-button button-primary"
        onClick={this.handleLogoutClick}
      />
    );
  }
}

Logout.propTypes = {
  onLogout: PropTypes.func.isRequired
};

export default Logout;
