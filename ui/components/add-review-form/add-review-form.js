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
      formSubmitPending: false,
      dropRejected: false,
      uploadTooLarge: false,
      validationFailed: false,
      unexpectedError: false
    };

    this.handleClick = this.handleClick.bind(this);
    this.handleDrop = this.handleDrop.bind(this);
    this.handleRemoveClick = this.handleRemoveClick.bind(this);
    this.handleError = this.handleError.bind(this);
    this.handleDropRejected = this.handleDropRejected.bind(this);
  }

  componentWillUnmount() {
    this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
  }

  handleError(error) {
    this.setState({formSubmitPending: false, unexpectedError: true});
    console.log(error);
  }

  postForm(imageUrls) {
    fetch('/reviews', {
      method: 'POST',
      body: JSON.stringify({
        title: this.state.title,
        areaName: this.state.areaName,
        description: this.state.description,
        imageUrls,
        csrfToken: document.head.querySelector('[name=csrfToken]').content
      }),
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
      }
    })
    .then(response => {
      if (response.ok) {
        this.props.onSubmit();
        this.setState({
          title: '',
          areaName: '',
          description: '',
          files: [],
          dropRejected: false,
          uploadTooLarge: false
        });
        this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
        this.setState({formSubmitPending: false, unexpectedError: false});
      } else {
        throw new Error('Review failed to post');
      }
    })
    .catch(this.handleError);
  }

  handleClick() {
    if ((this.state.title === '') || (this.state.areaName === '') || (this.state.description === '')) {
      this.setState({validationFailed: true});
      return;
    }

    this.setState({formSubmitPending: true, validationFailed: false});
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
      formData.append('csrfToken', document.head.querySelector('[name=csrfToken]').content);
      fetch('/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include',
        headers: {
          'Csrf-Token': document.head.querySelector('[name=csrfToken]').content
        }
      })
      .then(response => {
        if (response.ok) {
          const jsonPromise = response.json();
          jsonPromise
          .then(json => this.postForm(json.urls))
          .catch(this.handleError);
        } else if (response.status === 413) {
          this.setState({formSubmitPending: false, uploadTooLarge: true});
        } else {
          throw new Error('Image upload failed');
        }
      })
      .catch(this.handleError);
    })
    .catch(this.handleError);
  }

  handleDrop(files) {
    const newFiles = this.state.files.concat(files);
    this.setState({files: newFiles, dropRejected: false});
  }

  handleDropRejected() {
    this.setState({dropRejected: true});
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
      <div className="add-review-form-container">
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

            {(this.state.title === '') && this.state.validationFailed ?
              <p className="form-error">Title cannot be empty</p> : null}
            {(this.state.areaName === '') && this.state.validationFailed ?
              <p className="form-error">Area name cannot be empty</p> : null}
            {(this.state.description === '') && this.state.validationFailed ?
              <p className="form-error">Description cannot be empty</p> : null}

            <div className="form-input">
              <Dropzone
                accept="image/jpeg, image/png"
                maxSize={2 * 1024 * 1024}
                onDrop={this.handleDrop}
                onDropRejected={this.handleDropRejected}
              >
                <p className="dropzone-text">Click or drop photos here to upload</p>
              </Dropzone>
            </div>

            {this.state.dropRejected ?
              <p className="form-error">Files must be jpeg or png format, maximum size 2MB</p> : null}

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

            {this.state.uploadTooLarge ?
              <p className="form-error">Maximum total size of pictures is 20MB per review</p> : null}

            <div>
              {
                this.state.formSubmitPending ? <p>uploading & posting...</p> :
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

            {this.state.unexpectedError ?
              <p className="form-error">Review failed to post due to unexpected error</p> : null}
          </form>
        </div>
      </div>
    );
  }
}

AddReviewForm.propTypes = {
  onSubmit: PropTypes.func.isRequired
};

export default AddReviewForm;
