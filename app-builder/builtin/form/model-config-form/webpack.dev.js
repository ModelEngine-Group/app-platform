const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  plugins: [],
  devServer: {
    historyApiFallback: true,
    hot: true,
    open: true,
    https: false,
    proxy: {}
  },
});
