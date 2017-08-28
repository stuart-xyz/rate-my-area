import React from 'react';
import Login from '../login/login';
import Main from '../main/main';
import './index.css';

class Index extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      authenticated: undefined
    };

    this.handleAuthentication = this.handleAuthentication.bind(this);
    this.setAuthenticated = this.setAuthenticated.bind(this);
  }

  componentWillMount() {
    this.setState({authenticated: this.handleAuthentication()});
  }

  setAuthenticated(authenticated) {
    console.log(authenticated);
    this.setState({authenticated});
  }

  handleAuthentication() {
    fetch('/user', {
      method: 'GET',
      credentials: 'include'
    })
    .then(response => {
      if (response.ok) {
        this.setAuthenticated(true);
      } else if (response.status === 401) {
        this.setAuthenticated(false);
      } else {
        throw new Error('Unexpected HTTP response');
      }
    })
    .catch(err => console.log(err));
  }

  render() {
    let view;
    if (this.state.authenticated === undefined) {
      view = null;
    } else if (this.state.authenticated) {
      view = <Main/>;
    } else {
      view = (
        <Login
          onAuthentication={this.handleAuthentication}
        />
      );
    }
    return (
      <div>
        {view}
      </div>
    );
  }
}

export default Index;
