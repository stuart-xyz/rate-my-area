import React from 'react';
import PropTypes from 'prop-types';
import './login.css';

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: '',
      password: '',
      invalidEmail: false,
      loginAttempted: false,
      invalidCredentialsSupplied: false
    };

    this.handleClick = this.handleClick.bind(this);
    this.validateEmail = this.validateEmail.bind(this);
    this.validatePassword = this.validatePassword.bind(this);
    this.submitEnabled = this.submitEnabled.bind(this);
  }

  componentWillMount() {
    this.setState({email: this.props.email});
  }

  validateEmail() {
    const regexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regexp.test(this.state.email) || ((this.state.email === '') && !this.state.loginAttempted);
  }

  validatePassword() {
    return this.state.loginAttempted ? this.state.password !== '' : true;
  }

  submitEnabled() {
    return this.validateEmail() && this.validatePassword();
  }

  handleClick(event) {
    event.preventDefault();
    this.setState({loginAttempted: true}, () => {
      if (this.validateEmail() && this.validatePassword()) {
        fetch('/login', {
          method: 'POST',
          body: JSON.stringify({
            email: this.state.email,
            password: this.state.password,
            csrfToken: document.head.querySelector('[name=csrfToken]').content
          }),
          credentials: 'include',
          headers: {
            'Content-Type': 'application/json',
            'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
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
    });
  }

  render() {
    const self = this;
    return (
      <form className="login-form">
        <div className="form-input row">
          <input
            type="email"
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
          {this.validateEmail() ? null :
          <p className="signup-error">Invalid email address</p>}
          {this.validatePassword() ? null :
          <p className="signup-error">Password cannot be empty</p>}
          {this.state.invalidCredentialsSupplied ?
            <p className="login-error">Invalid email address or password provided</p> : null}
        </div>
        <div className="row">
          <input
            disabled={!this.submitEnabled()}
            type="submit"
            value="Login"
            className={this.submitEnabled() ? 'button-primary login-button' : 'button-primary login-button disabled'}
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
