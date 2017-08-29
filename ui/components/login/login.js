import React from 'react';
import PropTypes from 'prop-types';
import './login.css';

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: undefined,
      password: undefined,
      invalidCredentialsSupplied: false
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleLoginError(error) {
    console.log(error);
  }

  handleClick(event) {
    event.preventDefault();
    fetch('/login', {
      method: 'POST',
      body: JSON.stringify({email: this.state.email, password: this.state.password}),
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (response.ok) {
        this.props.onAuthentication();
      } else if (response.status === 401) {
        this.setState({invalidCredentialsSupplied: true});
      } else {
        throw new Error('Unexpected HTTP response');
      }
    })
    .catch(this.handleLoginError);
  }

  render() {
    const self = this;
    return (
      <form>
        <input
          type="text"
          placeholder="Email address"
          onChange={function (event) {
            self.setState({email: event.target.value});
          }}
        />
        <input
          type="password"
          placeholder="Password"
          onChange={function (event) {
            self.setState({password: event.target.value});
          }}
        />
        <input
          type="submit"
          value="Login"
          className="button-primary"
          onClick={this.handleClick}
        />
      </form>
    );
  }
}

Login.propTypes = {
  onAuthentication: PropTypes.func.isRequired
};

export default Login;
