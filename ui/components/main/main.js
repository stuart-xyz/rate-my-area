import React from 'react';
import PropTypes from 'prop-types';
import AddReviewForm from '../add-review-form/add-review-form';
import ReviewList from '../review-list/review-list';
import './main.css';

class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      reviews: []
    };

    this.handleReviewSubmit = this.handleReviewSubmit.bind(this);
    this.handleRefreshClick = this.handleRefreshClick.bind(this);
    this.getReviews = this.getReviews.bind(this);

    this.getReviews();
  }

  handleReviewSubmit() {
    this.getReviews();
  }

  handleRefreshClick() {
    this.getReviews();
  }

  getReviews() {
    fetch('/reviews', {
      method: 'GET',
      credentials: 'include'
    })
    .then(response => {
      if (response.ok) {
        response.json().then(reviews => {
          this.setState({reviews});
        });
      } else {
        throw new Error('Unexpected HTTP response: ' + response.statusText);
      }
    })
    .catch(err => console.log(err));
  }

  render() {
    return (
      <div className="main-container">
        <AddReviewForm onSubmit={this.handleReviewSubmit} userId={this.props.userId}/>
        <ReviewList reviews={this.state.reviews} onRefreshClick={this.handleRefreshClick}/>
      </div>
    );
  }
}

Main.propTypes = {
  userId: PropTypes.number.isRequired
};

export default Main;
