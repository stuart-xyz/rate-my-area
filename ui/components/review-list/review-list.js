import React from 'react';
import PropTypes from 'prop-types';
import RefreshIcon from './img/refresh-icon.svg';
import BinIcon from './img/bin-icon.svg';
import './review-list.css';

class ReviewList extends React.Component {

  constructor(props) {
    super(props);
    this.handleRemoveClick = this.handleRemoveClick.bind(this);
  }

  handleRemoveClick(reviewId) {

  }

  render() {
    const self = this;
    return (
      <div>
        <img
          className="refresh-icon"
          src={RefreshIcon}
          onClick={function () {
            self.props.onRefreshClick();
          }}
        />
        <div className="review-list">
          {this.props.reviews.map(review => {
            return (
              <div key={review.id} className="review">
                <div className="review-title">
                  <div>{review.title}<span className="username">-{review.username}</span></div>
                  {this.props.username === review.username ?
                    <img
                      className="remove-button"
                      src={BinIcon}
                      onClick={function () {
                        self.handleRemoveClick(review.id);
                      }}
                    /> : null}
                </div>
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
      </div>
    );
  }

}

ReviewList.propTypes = {
  reviews: PropTypes.array.isRequired,
  onRefreshClick: PropTypes.func.isRequired,
  username: PropTypes.string.isRequired
};

export default ReviewList;
