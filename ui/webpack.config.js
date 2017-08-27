var webpack = require('webpack')

module.exports = {
  entry: ['whatwg-fetch', './app.js'],
  output: { path: __dirname + '/../public/compiled', filename: 'bundle.js' },
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel-loader',
        query: { presets: ['es2015', 'stage-0', 'react'] }
      }
    ]
  }
}
