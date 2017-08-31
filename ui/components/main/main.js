import React from 'react';
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
    this.getReviews = this.getReviews.bind(this);

    this.getReviews();
  }

  handleReviewSubmit() {
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
      <div>
        <h1>Welcome!</h1>
        <AddReviewForm onSubmit={this.handleReviewSubmit}/>
        <ReviewList reviews={this.state.reviews}/>
      </div>
    );
  }
}

export default Main;
