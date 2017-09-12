const path = require('path');
const webpack = require('webpack');
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
      },
      {
        test: /\.(jpg|png|gif|eot|svg|ttf|woff|woff2)(\?.*)?$/,
        loader: 'url-loader'
      }
    ]
  },
  plugins: [
    new ExtractTextPlugin('styles.css'),
    new webpack.ProvidePlugin({
      Promise: 'es6-promise',
      fetch: 'imports-loader?this=>global!exports-loader?global.fetch!whatwg-fetch'
    })
  ]
};
