const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
  entry: ['whatwg-fetch', './app.js'],
  output: {path: path.resolve(__dirname, '../public/compiled'), filename: 'bundle.js'},
  devtool: 'source-map',
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          presets: ['es2015', 'stage-0', 'react']
        }
      },
      {
        test: /\.scss$/,
        loaders: ExtractTextPlugin.extract('css-loader!sass-loader')
      },
      {
        test: /\.css$/,
        loaders: ExtractTextPlugin.extract('css-loader')
      },
      {
        test: /\.json$/,
        loader: 'json-loader'
      }
    ]
  },
  plugins: [
    new ExtractTextPlugin('styles.css')
  ]
};
