/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const { merge } = require('webpack-merge');
const path = require('path');
const rootPath = path.resolve(__dirname, '../');
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');
const baseConfig = require('./webpack.common.single');
const proxyConfig = require('./proxy.conf.json');
const webpack = require('webpack');

const chromeName = (function () {
  switch (process.platform) {
    case 'win32':
      return 'chrome';
    case 'linux':
      return 'google-chrome';
    case 'darwin':
      return 'Google Chrome';
    default:
      return 'google-chrome';
  }
})();

const devStatics = [
  {
    directory: path.join(__dirname, './src/assets/images/'),
    publicPath: '/apps/appengine/src/assets/images',
  },
];

module.exports = merge(baseConfig, {
  mode: 'development',
  devtool: 'eval-cheap-module-source-map',
  devServer: {
    static: devStatics,
    allowedHosts: 'all',
    port: 5210,
    open: {
      app: {
        name: chromeName,
      },
    },
    proxy: proxyConfig,
    hot: true,
    historyApiFallback: true,
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept',
    },
  },
  plugins: [
    new ReactRefreshWebpackPlugin(),
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
      'process.env.PACKAGE_MODE': JSON.stringify('spa'),
    }),
  ],
});
