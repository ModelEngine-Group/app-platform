/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const HappyPack = require('happypack');
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  entry: {
    index: './src/index.tsx',
  },
  output: {
    filename: '[name].bundle[hash].js',
    publicPath: './',
    path: path.resolve(__dirname, 'build'),
  },
  module: {
    rules: [{
      test: /\.(js|jsx)$/,
      exclude: /node_modules/,
      include: [path.resolve(__dirname, 'src')],
      use: {
        loader: 'happypack/loader?id=babel',
      },
    },
    {
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
      use: ['style-loader', 'css-loader']
    },
    {
      test: /\.(ts|tsx)$/,
      exclude: /node_modules/,
      use: [{
        loader: 'babel-loader',
        options: {
          presets: [
            '@babel/preset-env',
            '@babel/preset-react',
            '@babel/preset-typescript',
          ],
        },
      }],
    },
    {
      test: /\.less$/,
      use: [{
        loader: 'style-loader',
      }, {
        loader: 'css-loader',
      }, {
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
              'label-color': 'rgb(77, 77, 77)',
            },
            javascriptEnabled: true,
          },
        },
      }],
    },
    {
      test: /\.(sc|sa)ss$/,
      include: [path.resolve(__dirname, 'src')],
      exclude: /node_modules/,
      use: [
        MiniCssExtractPlugin.loader,
        {
          loader: 'css-loader',
          options: {
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
    }
    ],
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    },
    extensions: ['.ts', '.tsx', '.js', '.jsx'],
  },
  plugins: [
    new HappyPack({
      id: 'babel',
      loaders: ['babel-loader?cacheDirectory'],
    }),
    new MiniCssExtractPlugin({
      filename: '[name].[hash:8].css',
      chunkFilename: '[name].[hash:8].css',
    }),
    new HtmlWebpackPlugin({
      title: 'ModeEngine',
      template: path.join(process.cwd(), './src/index.html'),
      filename: 'index.html',
    }),
    new CleanWebpackPlugin(),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'src/assets', // 静态资源目录
          to: 'src/assets', // 打包后的静态资源目录
          globOptions: {
            ignore: ['**/icon.*'] // 忽略的文件
          }
        },
        {
          from: 'plugins', // 插件目录
          to: 'plugins', // 打包后的插件目录
        }
      ]
    })
  ],
  optimization: {
    splitChunks: {
      chunks: 'all',
    }
  },
};
