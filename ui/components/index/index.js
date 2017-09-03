import React from 'react';
import Login from '../login/login';
import Main from '../main/main';
import '../../styles/vendor/skeleton.css';
import '../../styles/vendor/normalize.css';
import './index.css';

class Index extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      authenticated: undefined,
      userId: undefined
    };

    this.handleAuthentication = this.handleAuthentication.bind(this);
    this.setAuthenticated = this.setAuthenticated.bind(this);
  }

  componentWillMount() {
    this.setState({authenticated: this.handleAuthentication()});
  }

  setAuthenticated(authenticated, userId = undefined) {
    this.setState({authenticated, userId});
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
        .then(json => this.setAuthenticated(true, json.id))
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
      view = <Main userId={this.state.userId}/>;
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
