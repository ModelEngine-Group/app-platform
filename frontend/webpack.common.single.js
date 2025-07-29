/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const path = require('path');
const rootPath = path.resolve(__dirname, './');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  context: rootPath,
  entry: {
    main: path.resolve(rootPath, 'src/index.tsx'),
  },
  output: {
    publicPath: '/',
    clean: true,
    path: path.resolve(rootPath, 'build'),
    filename: '[name].js',
    chunkFilename: '[name].js',
    library: 'appengine',
    libraryTarget: 'umd',
    assetModuleFilename: 'assets/images/[name][ext]',
  },
  module: {
    rules: [{
      test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
      type: 'asset',
      parser: {
        dataUrlCondition: {
          maxSize: 200 * 1024, // 200kb
        },
      },
    },
    {
      test: /\.css$/,
      use: ['style-loader', 'css-loader'],
    },
      {
        test: /\.(j|t)sx?$/,
        exclude: /node_modules/,
        include: [path.resolve(rootPath, 'src')],
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env', '@babel/preset-react',
              '@babel/preset-typescript',
            ],
            plugins: [
              [
                require.resolve('babel-plugin-import'),
                {
                  libraryName: '@iux/live-react',
                  style: true,
                },
              ],
            ],
          },
        },
      },
      {
        test: /\.s(c|a)ss$/,
        include: [path.resolve(rootPath, 'src')],
        exclude: /node_modules/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              url: false,
              sourceMap: true,
              importLoaders: 2,
              modules: {
                auto: true,
                exportLocalsConvention: 'dashesOnly',
                localIdentName: '[local]__[hash:base64:5]',
              },
            },
          },
          'postcss-loader',
          {
            loader: 'sass-loader',
          },
        ],
      },
      {
        test: /\.less$/,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              esModule: true,
              modules: {
                auto: true,
                exportLocalsConvention: 'dashesOnly',
                localIdentName: '[local]__[hash:base64:5]',
              },
              importLoaders: 2,
            },
          },
          'postcss-loader',
          {
            loader: 'less-loader',
            options: {
              lessOptions: {
                modifyVars: {
                  'primary-color': '#2673e5',
                  'border-radius-base': '4px',
                  'tag-default-bg': '#f2f2f2',
                  'disabled-color': '#808080',
                  'primary-color-hover': '#2673e5',
                  'select-item-selected-color': '#2673e5',
                  'select-item-selected-bg': '#2673e50f',
                  'select-item-selected-font-weight': 'normal',
                  'outline-blur-size': '0',
                  'outline-width': '0',
                  'input-hover-border-color': '#4d4d4d',
                  'error-color': '#c63939',
                  'drawer-body-padding': '16px 24px',
                },
                javascriptEnabled: true,
              },
            },
          },
        ],
      },
  ],
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.js', '.jsx'],
    alias: {
      '@': path.resolve(rootPath, 'src'),
    },
    fallback: {
      'react/jsx-runtime': 'react/jsx-runtime.js',
      'react/jsx-dev-runtime': 'react/jsx-dev-runtime.js',
    },
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'ModeEngine',
      template: path.resolve(rootPath, 'src/index.html'),
      publicPath: '',
      chunks: ['main'],
    }),

    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'src/assets', // 静态资源目录
          to: 'src/assets', // 打包后的静态资源目录
          globOptions: {
            ignore: ['**/icon.*'], // 忽略的文件
          },
        },
        {
          from: 'plugins', // 插件目录
          to: 'plugins', // 打包后的插件目录
        }
      ],
    }),
  ],
};
