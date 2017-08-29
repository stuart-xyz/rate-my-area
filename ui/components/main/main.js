import React from 'react';
import AddReviewForm from '../add-review-form/add-review-form';
// import ReviewList from '../review-list/review-list';
import './main.css';

class Main extends React.Component {
  constructor(props) {
    super(props);

    this.handleReviewSubmit = this.handleReviewSubmit.bind(this);
  }

  handleReviewSubmit() {
    console.log('hello');
  }

  render() {
    return (
      <div>
        <h1>Welcome!</h1>
        <AddReviewForm onSubmit={this.handleReviewSubmit}/>
        {/* <ReviewList/> */}
      </div>
    );
  }
}

export default Main;
