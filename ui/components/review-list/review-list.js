import React from 'react';
import PropTypes from 'prop-types';
import RefreshIcon from './img/refresh-icon.svg';
import BinIcon from './img/bin-icon.svg';
import PencilIcon from './img/pencil-icon.svg';
import './review-list.css';

class ReviewList extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      newTitle: undefined,
      newAreaName: undefined,
      newDescription: undefined,
      focus: {},
      unexpectedError: false
    };

    this.handleRemoveClick = this.handleRemoveClick.bind(this);
    this.handleInputFocus = this.handleInputFocus.bind(this);
    this.updateReview = this.updateReview.bind(this);
    this.handleError = this.handleError.bind(this);
  }

  handleError(err) {
    this.setState({unexpectedError: true});
    console.log(err);
  }

  handleRemoveClick(reviewId) {
    fetch('/reviews/' + reviewId, {
      method: 'DELETE',
      body: JSON.stringify({
        csrfToken: document.head.querySelector('[name=csrfToken]').content
      }),
      credentials: 'include',
      headers: {
        'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
      }
    })
    .then(response => {
      if (response.ok) {
        this.setState({unexpectedError: false});
        this.props.onRefreshClick();
      } else {
        throw new Error('Review failed to delete');
      }
    })
    .catch(this.handleError);
  }

  updateReview(reviewId, updatedField, updatedValue) {
    const body = {
      csrfToken: document.head.querySelector('[name=csrfToken]').content
    };
    body[updatedField] = updatedValue;
    fetch('/reviews/' + reviewId, {
      method: 'PATCH',
      credentials: 'include',
      body: JSON.stringify(body),
      headers: {
        'Content-Type': 'application/json',
        'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
      }
    })
    .then(response => {
      if (response.ok) {
        this.setState({unexpectedError: false});
        this.props.onRefreshClick();
      } else {
        throw new Error('Review failed to update');
      }
    })
    .catch(this.handleError);
  }

  handleInputFocus(inputClassName, hasFocus, reviewId) {
    if (!hasFocus) {
      switch (inputClassName) {
        case 'review-title-input':
          if (this.state.newTitle) {
            this.updateReview(reviewId, 'title', this.state.newTitle);
            this.setState({newTitle: undefined});
          }
          break;
        case 'review-area-name-input':
          if (this.state.newAreaName) {
            this.updateReview(reviewId, 'areaName', this.state.newAreaName);
            this.setState({newAreaName: undefined});
          }
          break;
        case 'review-description-input':
          if (this.state.newDescription) {
            this.updateReview(reviewId, 'description', this.state.newDescription);
            this.setState({newDescription: undefined});
          }
          break;
        default: break;
      }
    }

    const newState = {};
    const innerState = {};
    innerState[inputClassName] = hasFocus;
    newState[reviewId] = innerState;
    const updatedFocusState = Object.assign(this.state.focus, newState);
    this.setState({focus: updatedFocusState});
  }

  render() {
    const self = this;
    return (
      <div>
        {
          this.props.reviews.length > 0 ?
            <img
              className="refresh-icon"
              src={RefreshIcon}
              onClick={function () {
                self.props.onRefreshClick();
              }}
            /> : null
        }

        {this.state.unexpectedError ?
          <p className="form-error">Action failed due to unexpected error</p> : null}

        <div className="review-list">
          {this.props.reviews.map(review => {
            const reviewBelongsToUser = this.props.username === review.username;
            const pencilIcon = reviewBelongsToUser ? <img className="pencil-icon" src={PencilIcon}/> : null;
            return (
              <div key={review.id} className="review">
                <div className={this.state.focus[review.id] && this.state.focus[review.id]['review-title-input'] ? 'review-title' : 'review-title-static'}>
                  <div className="review-title-primary">
                    {pencilIcon}
                    {
                      this.state.focus[review.id] && this.state.focus[review.id]['review-title-input'] && reviewBelongsToUser ?
                        <input
                          className="review-title-input"
                          onClick={function () {
                            self.handleInputFocus('review-title-input', true, review.id);
                          }}
                          onFocus={function () {
                            self.handleInputFocus('review-title-input', true, review.id);
                          }}
                          onBlur={function () {
                            self.handleInputFocus('review-title-input', false, review.id);
                          }}
                          onChange={function (event) {
                            self.setState({newTitle: event.target.value});
                          }}
                          type="text"
                          defaultValue={review.title}
                          autoFocus
                          disabled={!reviewBelongsToUser}
                        /> :
                        <span
                          className={'review-title-display' + (reviewBelongsToUser ? '' : ' no-border')}
                          onClick={function () {
                            if (reviewBelongsToUser) {
                              self.handleInputFocus('review-title-input', true, review.id);
                            }
                          }}
                        >
                          {review.title}
                        </span>
                    }
                  </div>
                  {reviewBelongsToUser ?
                    <img
                      className="remove-button"
                      src={BinIcon}
                      onClick={function () {
                        self.handleRemoveClick(review.id);
                      }}
                    /> : null}
                </div>
                <div className="username-display">
                  <span> - {review.username}</span>
                </div>
                <div className={this.state.focus[review.id] && this.state.focus[review.id]['review-area-name-input'] ? 'review-area-name' : 'review-area-name-static'}>
                  {
                    this.state.focus[review.id] && this.state.focus[review.id]['review-area-name-input'] && reviewBelongsToUser ?
                      <input
                        className="review-area-name-input"
                        onFocus={function () {
                          self.handleInputFocus('review-area-name-input', true, review.id);
                        }}
                        onBlur={function () {
                          self.handleInputFocus('review-area-name-input', false, review.id);
                        }}
                        onChange={function (event) {
                          self.setState({newAreaName: event.target.value});
                        }}
                        type="text"
                        defaultValue={review.areaName}
                        autoFocus
                        disabled={!reviewBelongsToUser}
                      /> :
                      <span
                        className={'review-area-name-display' + (reviewBelongsToUser ? '' : ' no-border')}
                        onClick={function () {
                          if (reviewBelongsToUser) {
                            self.handleInputFocus('review-area-name-input', true, review.id);
                          }
                        }}
                      >
                        {review.areaName}
                      </span>
                  }
                </div>
                <div className={this.state.focus[review.id] && this.state.focus[review.id]['review-description-input'] ? 'review-description' : 'review-description-static'}>
                  {
                    this.state.focus[review.id] && this.state.focus[review.id]['review-description-input'] && reviewBelongsToUser ?
                      <textarea
                        className="review-description-input"
                        onFocus={function () {
                          self.handleInputFocus('review-description-input', true, review.id);
                        }}
                        onBlur={function () {
                          self.handleInputFocus('review-description-input', false, review.id);
                        }}
                        onChange={function (event) {
                          self.setState({newDescription: event.target.value});
                        }}
                        type="text"
                        defaultValue={review.description}
                        autoFocus
                        disabled={!reviewBelongsToUser}
                      /> :
                      <span
                        className={'review-description-display' + (reviewBelongsToUser ? '' : ' no-border')}
                        onClick={function () {
                          if (reviewBelongsToUser) {
                            self.handleInputFocus('review-description-input', true, review.id);
                          }
                        }}
                      >
                        {review.description}
                      </span>
                  }
                </div>
                <div className="review-photos">
                  {review.imageUrls.map(imageUrl => {
                    return <img key={imageUrl} src={imageUrl} className="review-photo"/>;
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
