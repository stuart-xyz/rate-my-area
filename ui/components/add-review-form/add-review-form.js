import React from 'react';
import PropTypes from 'prop-types';
import Dropzone from 'react-dropzone';
import crossIcon from './img/cross.svg';
import './add-review-form.css';

class AddReviewForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      title: '',
      areaName: '',
      description: '',
      files: [],
      formSubmitPending: false
    };

    this.handleClick = this.handleClick.bind(this);
    this.handleDrop = this.handleDrop.bind(this);
    this.handleRemoveClick = this.handleRemoveClick.bind(this);
    this.handleError = this.handleError.bind(this);
  }

  componentWillUnmount() {
    this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
  }

  handleError(error) {
    this.setState({formSubmitPending: false});
    console.log(error);
  }

  postForm(imageUrls) {
    fetch('/reviews', {
      method: 'POST',
      body: JSON.stringify({
        title: this.state.title,
        areaName: this.state.areaName,
        description: this.state.description,
        imageUrls
      }),
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      if (response.ok) {
        this.props.onSubmit();
        this.setState({
          title: '',
          areaName: '',
          description: '',
          files: []
        });
        this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
        this.setState({formSubmitPending: false});
      } else {
        this.setState({formSubmitPending: false});
        throw new Error('Review failed to post');
      }
    })
    .catch(this.handleError);
  }

  handleClick() {
    this.setState({formSubmitPending: true});
    const imagesPromise = Promise.all(this.state.files.map(file => {
      return new Promise((resolve, reject) => {
        fetch(file.preview, {
          method: 'GET'
        })
        .then(response => {
          if (response.ok) {
            resolve(response.blob());
          } else {
            reject(new Error('Error retrieving image'));
          }
        });
      });
    }));

    imagesPromise.then(imageBlobs => {
      const formData = new FormData();
      imageBlobs.forEach(blob => formData.append('photo', blob));
      fetch('/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include'
      })
      .then(response => {
        if (response.ok) {
          const jsonPromise = response.json();
          jsonPromise
          .then(json => this.postForm(json.urls))
          .catch(this.handleError);
        } else {
          this.setState({formSubmitPending: false});
          throw new Error('Image upload failed');
        }
      })
      .catch(this.handleError);
    })
    .catch(this.handleError);
  }

  handleDrop(files) {
    const newFiles = this.state.files.concat(files);
    this.setState({files: newFiles});
  }

  handleRemoveClick(file) {
    window.URL.revokeObjectURL(file.preview);
    const index = this.state.files.indexOf(file);
    if (index > -1) {
      const files = this.state.files;
      files.splice(index, 1);
      this.setState({files});
    }
  }

  render() {
    const self = this;
    let imageKey = 0;
    return (
      <div>
        <div className="form">
          <form>
            <div className="form-input">
              <input
                className="form-input-title"
                type="text"
                placeholder="Title"
                value={this.state.title}
                onChange={function (event) {
                  self.setState({title: event.target.value});
                }}
              />
            </div>
            <div className="form-input">
              <input
                className="form-input-area-name"
                type="text"
                placeholder="Area name"
                value={this.state.areaName}
                onChange={function (event) {
                  self.setState({areaName: event.target.value});
                }}
              />
            </div>
            <div className="form-input">
              <textarea
                className="form-input-description"
                type="text"
                placeholder="Description"
                value={this.state.description}
                onChange={function (event) {
                  self.setState({description: event.target.value});
                }}
              />
            </div>
            <div className="form-input">
              <Dropzone
                accept="image/jpeg, image/png"
                onDrop={this.handleDrop}
              >
                <p className="dropzone-text"><a href="">Click or drop photos here to upload</a></p>
              </Dropzone>
            </div>
            <div className="preview-image-container">
              {this.state.files.map(file => {
                const image = (
                  <div key={imageKey} className="preview">
                    <img
                      src={crossIcon}
                      className="preview-cross"
                      onClick={function () {
                        self.handleRemoveClick(file);
                      }}
                    />
                    <img
                      className="preview-image"
                      src={file.preview}
                    />
                  </div>
                );
                imageKey += 1;
                return image;
              })}
            </div>
            <div>
              {
                this.state.formSubmitPending ? <p>posting...</p> :
                <input
                  type="submit"
                  value="Post"
                  className="button-primary"
                  onClick={function (event) {
                    event.preventDefault();
                    self.handleClick();
                  }}
                />
              }
            </div>
          </form>
        </div>
      </div>
    );
  }
}

AddReviewForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  userId: PropTypes.number.isRequired
};

export default AddReviewForm;
