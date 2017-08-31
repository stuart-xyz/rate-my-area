import React from 'react';
import PropTypes from 'prop-types';
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
    }).then(response => {
      if (response.ok) {
        this.props.onSubmit();
        this.setState({
          title: undefined,
          areaName: undefined,
          emojiCode: undefined,
          description: undefined
        });
      } else {
        throw new Error('Unexpected HTTP response');
      }
    })
    .catch(this.handleLoginError);
  }

  render() {
    const self = this;
    return (
      <form>
        <div className="row form-input">
          <input
            type="text"
            placeholder="Title"
            value={this.state.title}
            onChange={function (event) {
              self.setState({title: event.target.value});
            }}
          />
        </div>
        <div className="row form-input">
          <input
            type="text"
            placeholder="Area name"
            value={this.state.areaName}
            onChange={function (event) {
              self.setState({areaName: event.target.value});
            }}
          />
        </div>
        <div className="row form-input">
          <input
            type="text"
            placeholder="Description"
            value={this.state.description}
            onChange={function (event) {
              self.setState({description: event.target.value});
            }}
          />
        </div>
        <div className="row">
          <input
            type="submit"
            value="Post"
            className="button-primary"
            onClick={function (event) {
              event.preventDefault();
              self.handleClick();
            }}
          />
        </div>
      </form>
    );
  }
}

AddReviewForm.propTypes = {
  onSubmit: PropTypes.func.isRequired
};

export default AddReviewForm;
