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
      signupAttempted: false,
      takenUsernames: [],
      takenEmails: []
    };

    this.handleClick = this.handleClick.bind(this);
    this.validatePasswordMatch = this.validatePasswordMatch.bind(this);
    this.validateEmail = this.validateEmail.bind(this);
    this.validatePassword = this.validatePassword.bind(this);
    this.validateUsername = this.validateUsername.bind(this);
    this.usernameAvailable = this.usernameAvailable.bind(this);
    this.emailAvailable = this.emailAvailable.bind(this);
    this.submitEnabled = this.submitEnabled.bind(this);
  }

  componentWillMount() {
    this.setState({email: this.props.email});
  }

  validatePasswordMatch() {
    return this.state.password === this.state.passwordConfirm;
  }

  validateUsername() {
    return this.state.signupAttempted ? this.state.username !== '' : true;
  }

  validateEmail() {
    const regexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regexp.test(this.state.email) || ((this.state.email === '') && !this.state.signupAttempted);
  }

  validatePassword() {
    return this.state.signupAttempted ? this.state.password !== '' : true;
  }

  usernameAvailable() {
    return this.state.takenUsernames.indexOf(this.state.username) === -1;
  }

  emailAvailable() {
    return this.state.takenEmails.indexOf(this.state.email) === -1;
  }

  submitEnabled() {
    return this.validateEmail() && this.validatePasswordMatch() && this.validatePassword() &&
      this.emailAvailable() && this.usernameAvailable();
  }

  handleClick(event) {
    event.preventDefault();
    this.setState({signupAttempted: true}, () => {
      if (this.validateEmail() && this.validatePasswordMatch() && this.validatePassword()) {
        fetch('/signup', {
          method: 'POST',
          body: JSON.stringify({
            email: this.state.email,
            username: this.state.username,
            password: this.state.password,
            csrfToken: document.head.querySelector('[name=csrfToken]').content
          }),
          headers: {
            'Content-Type': 'application/json',
            'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
          }
        })
        .then(response => {
          if (response.ok) {
            this.props.onSignup(this.state.email);
          } else if (response.status === 409) { // Conflict HTTP code
            response.json().then(json => {
              if (json.error === 'username_uniqueness') {
                const newTakenUsernames = this.state.takenUsernames.concat(this.state.username);
                this.setState({takenUsernames: newTakenUsernames});
              } else if (json.error === 'email_uniqueness') {
                const newTakenEmails = this.state.takenEmails.concat(this.state.email);
                this.setState({takenEmails: newTakenEmails});
              }
            });
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
          {this.validateUsername() ? null :
          <p className="signup-error">Username cannot be empty</p>}
          {this.usernameAvailable() ? null :
          <p className="signup-error">Username is not available</p>}
          {this.validateEmail() ? null :
          <p className="signup-error">Invalid email address</p>}
          {this.emailAvailable() ? null :
          <p className="signup-error">Email address is already in use</p>}
          {this.validatePassword() ? null :
          <p className="signup-error">Password cannot be empty</p>}
          {this.validatePasswordMatch() ? null :
          <p className="signup-error">Passwords do not match</p>}
        </div>
        <div className="row">
          <input
            disabled={!this.submitEnabled()}
            type="submit"
            value="Signup"
            className={this.submitEnabled() ? 'button-primary signup-button' : 'button-primary signup-button disabled'}
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
