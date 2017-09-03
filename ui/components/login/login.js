import React from 'react';
import PropTypes from 'prop-types';
import './login.css';

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: '',
      password: '',
      invalidCredentialsSupplied: false
    };

    this.handleClick = this.handleClick.bind(this);
  }

  componentWillMount() {
    this.setState({email: this.props.email});
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
    .catch(err => console.log(err));
  }

  render() {
    const self = this;
    return (
      <form>
        <div className="form-input row">
          <input
            type="text"
            placeholder="Email address"
            value={this.state.email}
            onChange={function (event) {
              self.setState({email: event.target.value});
            }}
          />
        </div>
        <div className="form-input row">
          <input
            type="password"
            placeholder="Password"
            value={this.state.password}
            onChange={function (event) {
              self.setState({password: event.target.value});
            }}
          />
          {this.state.invalidCredentialsSupplied ?
            <p className="login-error">Invalid email address or password provided</p> : null}
        </div>
        <div className="row">
          <input
            type="submit"
            value="Login"
            className="button-primary"
            onClick={this.handleClick}
          />
          <button
            value="Signup"
            className="button signup-button"
            onClick={function () {
              self.props.onSignupClick(self.state.email);
            }}
          >
            Signup
          </button>
        </div>
      </form>
    );
  }
}

Login.propTypes = {
  onAuthentication: PropTypes.func.isRequired,
  onSignupClick: PropTypes.func.isRequired,
  email: PropTypes.string.isRequired
};

export default Login;
