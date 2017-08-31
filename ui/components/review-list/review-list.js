import React from 'react';
import PropTypes from 'prop-types';
import './review-list.css';

class ReviewList extends React.Component {
  render() {
    return (
      <div>
        {this.props.reviews.map(review => {
          return (
            <div key={review.id} className="row review">
              <div className="review-title">{review.title}</div>
              <div className="review-areaName">{review.areaName}</div>
              <div className="review-description">{review.description}</div>
            </div>
          );
        })}
      </div>
    );
  }
}

ReviewList.propTypes = {
  reviews: PropTypes.array.isRequired
};

export default ReviewList;
