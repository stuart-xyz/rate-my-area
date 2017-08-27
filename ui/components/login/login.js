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
    console.log(error);
  }

  handleClick() {
    fetch('/login', {
      method: 'POST',
      body: JSON.stringify(this.state),
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(this.props.onAuthentication)
    .catch(this.handleLoginError);
  }

  render() {
    const self = this;
    return (
      <div className="loginForm">
        <input
          placeholder="Email address"
          onChange={function (event) {
            self.setState({email: event.target.value});
          }}
        />
        <input
          placeholder="Password"
          onChange={function (event) {
            self.setState({password: event.target.value});
          }}
        />
        <button
          onClick={this.handleClick}
        >
          Login
        </button>
      </div>
    );
  }
}

Login.propTypes = {
  onAuthentication: PropTypes.func.isRequired
};

export default Login;
