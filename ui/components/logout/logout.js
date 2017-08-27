import React from 'react';
import PropTypes from 'prop-types';
import './logout.css';

class Logout extends React.Component {
  constructor(props) {
    super(props);

    this.handleClick = this.handleClick.bind(this);
  }

  handleLogoutError(error) {
    console.log(error);
  }

  handleClick() {

  }

  render() {
    return (
      <div/>
    );
  }
}

Logout.propTypes = {
  onLogout: PropTypes.func.isRequired
};

export default Logout;
