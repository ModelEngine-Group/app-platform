/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const path = require('path');
const { merge } = require('webpack-merge');
const webpack = require('webpack');
const common = require('./webpack.common.js');
const proxyConfig = require('./proxy.conf.json');

const devStatics = [
  {
    directory: path.join(__dirname, './src/assets/images/'),
    publicPath: '/apps/appengine/src/assets/images',
  },
];
module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  output: {
    filename: '[name].bundle.js',
    publicPath: '/',
    path: path.resolve(__dirname, 'build'),
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
      'process.env.PACKAGE_MODE': JSON.stringify('common'),
    }),
  ],
  devServer: {
    historyApiFallback: true,
    static: devStatics,
    hot: true,
    open: true,
    proxy: proxyConfig
  },
});
