import React from 'react';
import PropTypes from 'prop-types';
import './signup.css';

class Signup extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      email: '',
      password: '',
      passwordConfirm: '',
      invalidEmail: false,
      signupAttempted: false
    };

    this.handleClick = this.handleClick.bind(this);
    this.validatePasswordMatch = this.validatePasswordMatch.bind(this);
    this.validateEmail = this.validateEmail.bind(this);
    this.validatePassword = this.validatePassword.bind(this);
  }

  componentWillMount() {
    this.setState({email: this.props.email});
  }

  validatePasswordMatch() {
    return this.state.password === this.state.passwordConfirm;
  }

  validateEmail() {
    const regexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regexp.test(this.state.email) || ((this.state.email === '') && !this.state.signupAttempted);
  }

  validatePassword() {
    return this.state.signupAttempted ? this.state.password !== '' : true;
  }

  handleClick(event) {
    event.preventDefault();
    this.setState({signupAttempted: true}, () => {
      if (this.validateEmail() && this.validatePasswordMatch()) {
        fetch('/signup', {
          method: 'POST',
          body: JSON.stringify({email: this.state.email, password: this.state.password}),
          headers: {
            'Content-Type': 'application/json'
          }
        })
        .then(response => {
          if (response.ok) {
            this.props.onSignup(this.state.email);
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
      <form className="signup-form">
        <div className="form-input row">
          <input
            type="text"
            placeholder="Username"
            onChange={function (event) {
              self.setState({username: event.target.value});
            }}
          />
        </div>
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
            onChange={function (event) {
              self.setState({password: event.target.value});
            }}
          />
        </div>
        <div className="form-input row">
          <input
            type="password"
            placeholder="Confirm password"
            onChange={function (event) {
              self.setState({passwordConfirm: event.target.value});
            }}
          />
          {this.validateEmail() ? null :
          <p className="signup-error">Invalid email address provided</p>}
          {this.validatePasswordMatch() ? null :
          <p className="signup-error">Passwords do not match</p>}
          {this.validatePassword() ? null :
          <p className="signup-error">Invalid password</p>}
        </div>
        <div className="row">
          <input
            disabled={!this.validateEmail() || !this.validatePasswordMatch()}
            type="submit"
            value="Signup"
            className={!this.validateEmail() || !this.validatePasswordMatch() ? 'button-primary signup-button disabled' : 'button-primary signup-button'}
            onClick={this.handleClick}
          />
          <button
            value="Login"
            className="button login-button"
            onClick={function () {
              self.props.onLoginClick(self.state.email);
            }}
          >
            Login
          </button>
        </div>
      </form>
    );
  }
}

Signup.propTypes = {
  onSignup: PropTypes.func.isRequired,
  onLoginClick: PropTypes.func.isRequired,
  email: PropTypes.string.isRequired
};

export default Signup;
