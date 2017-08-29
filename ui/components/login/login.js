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

  handleClick() {
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
      <div>
        <div className="row">
          <input
            placeholder="Email address"
            onChange={function (event) {
              self.setState({email: event.target.value});
            }}
          />
        </div>
        <div className="row">
          <input
            placeholder="Password"
            onChange={function (event) {
              self.setState({password: event.target.value});
            }}
          />
        </div>
        <div className="row">
          <button
            onClick={this.handleClick}
          >
            Login
          </button>
        </div>
      </div>
    );
  }
}

Login.propTypes = {
  onAuthentication: PropTypes.func.isRequired
};

export default Login;
