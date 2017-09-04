import React from 'react';
import PropTypes from 'prop-types';
import './review-list.css';

class ReviewList extends React.Component {
  render() {
    return (
      <div className="review-list">
        {this.props.reviews.map(review => {
          return (
            <div key={review.id} className="row review">
              <div className="review-title">{review.title}<span className="username">-{review.username}</span></div>
              <div className="review-areaName">{review.areaName}</div>
              <div className="review-description">{review.description}</div>
              <div className="review-photos">
                {review.imageUrls.map(imageUrl => {
                  return <img key={imageUrl} src={imageUrl}/>;
                })}
              </div>
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
