import React from 'react';
import PropTypes from 'prop-types';
import './login.css';

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: undefined,
      password: undefined
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleLoginError(error) {
    console.log("login error");
  }

  handleClick(e) {
    fetch('/login', {
      method: "POST",
      body: JSON.stringify(this.state),
      headers: {
        'Content-Type': 'application/json'
      }
    }).catch(this.handleLoginError);
  }
  
  render() {
    return (
      <div className = "loginForm">
        <input
          placeholder = "Email address"
          className = "emailInput"
          onChange = {event => this.setState({email: event.target.value})}
          />
        <input
          placeholder = "Password"
          className = "passwordInput"
          onChange = {event => this.setState({password: event.target.value})}
          />
        <button
          type = "input"
          onClick = {this.handleClick}>
          Login
        </button>
      </div>
    );
  }
}

export default Login;
