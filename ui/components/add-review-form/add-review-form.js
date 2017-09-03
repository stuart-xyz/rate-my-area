import React from 'react';
import PropTypes from 'prop-types';
import Dropzone from 'react-dropzone';
import './add-review-form.css';

class AddReviewForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      title: undefined,
      areaName: undefined,
      description: undefined,
      files: []
    };

    this.handleClick = this.handleClick.bind(this);
    this.handleDrop = this.handleDrop.bind(this);
  }

  componentWillUnmount() {
    this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
  }

  handleError(error) {
    console.log(error);
  }

  postForm(imageUrl) {
    fetch('/reviews', {
      method: 'POST',
      body: JSON.stringify({
        title: this.state.title,
        areaName: this.state.areaName,
        description: this.state.description,
        imageUrls: [imageUrl]
      }),
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
          description: undefined,
          files: []
        });
        this.state.files.forEach(file => window.URL.revokeObjectURL(file.preview));
      } else {
        throw new Error('Review failed to post');
      }
    })
    .catch(this.handleError);
  }

  handleClick() {
    const imagePromise = new Promise((resolve, reject) => {
      fetch(this.state.files[0].preview, {
        method: 'GET'
      }).then(response => {
        if (response.ok) {
          resolve(response.blob());
        } else {
          reject(new Error('Error retrieving image'));
        }
      });
    });
    imagePromise.then(blob => {
      const formData = new FormData();
      formData.append('photo', blob);
      fetch('/upload', {
        method: 'POST',
        body: formData,
        credentials: 'include'
      }).then(response => {
        if (response.ok) {
          debugger;
          this.postForm(response.body.url);
        } else {
          throw new Error('Image upload failed');
        }
      }).catch(this.handleError);
    }).catch(this.handleError);
  }

  handleDrop(files) {
    const newFiles = this.state.files.concat(files);
    this.setState({files: newFiles});
  }

  render() {
    const self = this;
    let dropzoneRef;
    let imageKey = 0;
    return (
      <div className="row">
        <div className="one-half column">
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
            <div className="row form-input">
              <Dropzone
                ref={function (node) {
                  dropzoneRef = node;
                }}
                accept="image/jpeg, image/png"
                onDrop={this.handleDrop}
              >
                <p className="dropzone-text">Drop photos here to upload or pick photos</p>
              </Dropzone>
              <button
                type="button"
                className="file-upload-button"
                onClick={function () {
                  dropzoneRef.open();
                }}
              >
                Pick photos
              </button>
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
        </div>
        <div className="one-half column">
          <div className="row">
            {this.state.files.map(file => {
              console.log(file);
              const image = <img key={imageKey} src={file.preview} className="preview-image"/>;
              imageKey += 1;
              return image;
            })}
          </div>
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
