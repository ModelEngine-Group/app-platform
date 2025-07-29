/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const path = require('path');
const webpack = require('webpack');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const TerserWebpackPlugin = require('terser-webpack-plugin');
const { SubresourceIntegrityPlugin } = require('webpack-subresource-integrity');
const { merge } = require('webpack-merge');
const baseConfig = require('./webpack.common.single.js');
module.exports = merge(baseConfig, {
  mode: 'production',
  output: {
    filename: '[name].[contenthash:8].js',
    chunkFilename: 'chunk.[contenthash:8].js',
    crossOriginLoading: 'anonymous',
    publicPath: '/apps/appengine/',
  },
  plugins: [
    new SubresourceIntegrityPlugin({
      hashFuncNames: ['sha256', 'sha384'],
    }),
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('production'),
      'process.env.PACKAGE_MODE': JSON.stringify('spa')
    }),
  ],
  optimization: {
    minimize: true,
    minimizer: [
      new CssMinimizerPlugin(),
      new TerserWebpackPlugin({
        extractComments: false,
        terserOptions: {
          format: {
            comments: false,
          },
          compress: {
            drop_console: true,
            drop_debugger: true,
            unsafe_math: true,
          },
        },
      }),
      '...',
    ],
  },
});
