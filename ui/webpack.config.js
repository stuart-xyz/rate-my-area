const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
  entry: ['whatwg-fetch', './app.js'],
  output: {path: path.resolve(__dirname, '../public/compiled'), filename: 'bundle.js'},
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel-loader',
        query: {presets: ['es2015', 'stage-0', 'react']}
      },
      {
        test: /\.scss$/,
        loaders: ExtractTextPlugin.extract('css-loader!sass-loader')
      },
      {
        test: /\.css$/,
        loaders: ExtractTextPlugin.extract('css-loader')
      }
    ]
  },
  plugins: [
    new ExtractTextPlugin('styles.css')
  ]
};
