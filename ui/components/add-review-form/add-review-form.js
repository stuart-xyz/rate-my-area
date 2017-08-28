import React from 'react';
import './add-review-form.css';

class AddReviewForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      title: undefined,
      areaName: undefined,
      emojiCode: undefined,
      description: undefined
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick() {
    fetch('/reviews', {
      method: 'POST',
      body: JSON.stringify(this.state),
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  render() {
    return (
      <div/>
    );
  }
}

export default AddReviewForm;
