/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const { SubresourceIntegrityPlugin } = require('webpack-subresource-integrity');
const webpack = require('webpack');
const {
  merge
} = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
  output: {
    crossOriginLoading: 'anonymous',
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('production'),
      'process.env.PACKAGE_MODE': JSON.stringify('common'),
    }),
    new SubresourceIntegrityPlugin({ hashFuncNames: ['sha256', 'sha384'] }),
  ],
});
