import React from 'react';
import Login from '../login/login';
import Signup from '../signup/signup';
import Main from '../main/main';
import '../../styles/vendor/skeleton.css';
import '../../styles/vendor/normalize.css';
import './index.css';

class Index extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      authenticated: undefined,
      username: undefined,
      showSignupForm: false,
      email: ''
    };

    this.handleAuthentication = this.handleAuthentication.bind(this);
    this.setAuthenticated = this.setAuthenticated.bind(this);
    this.handleSignupClick = this.handleSignupClick.bind(this);
    this.handleSignup = this.handleSignup.bind(this);
    this.handleLoginClick = this.handleLoginClick.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
  }

  componentWillMount() {
    this.setState({authenticated: this.handleAuthentication()});
  }

  setAuthenticated(authenticated, username = undefined) {
    this.setState({authenticated, username});
  }

  handleSignupClick(email) {
    this.setState({showSignupForm: true, email});
  }

  handleLoginClick(email) {
    this.setState({showSignupForm: false, email});
  }

  handleLogout() {
    this.setState({
      authenticated: false,
      username: '',
      email: '',
      showSignupForm: false
    });
  }

  handleSignup(email) {
    this.setState({showSignupForm: false, email});
  }

  handleAuthentication() {
    fetch('/user', {
      method: 'GET',
      credentials: 'include'
    })
    .then(response => {
      if (response.ok) {
        const jsonPromise = response.json();
        jsonPromise
        .then(json => this.setAuthenticated(true, json.username))
        .catch(err => console.log(err));
      } else if (response.status === 401) {
        this.setAuthenticated(false);
      } else {
        throw new Error('User authentication check failed');
      }
    }).catch(err => console.log(err));
  }

  render() {
    let view;
    if (this.state.authenticated === undefined) {
      view = null;
    } else if (this.state.authenticated) {
      view = (
        <Main
          username={this.state.username}
          onLogout={this.handleLogout}
        />
      );
    } else if (this.state.showSignupForm) {
      view = (
        <Signup
          onSignup={this.handleSignup}
          onLoginClick={this.handleLoginClick}
          email={this.state.email}
        />
      );
    } else {
      view = (
        <Login
          onAuthentication={this.handleAuthentication}
          onSignupClick={this.handleSignupClick}
          email={this.state.email}
        />
      );
    }
    return <div>{view}</div>;
  }
}

export default Index;
