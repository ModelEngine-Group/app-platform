const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const HappyPack = require("happypack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  module: {
    rules: [{
      test: /\.(js|jsx)$/,
      exclude: /node_modules/,
      include: [path.resolve(__dirname, "src")],
      use: {
        loader: "happypack/loader?id=babel",
      },
    },
    {
      test: /\.(png|jpg|gif)$/,
      type: 'javascript/auto',
      include: [path.resolve(__dirname, "src")],
      exclude: /node_modules/,
      use: [{
        loader: "url-loader",
        options: {
          esModule: false
        }
      }],
    },
    {
      test: /\.css$/,
      use: ['style-loader', 'css-loader']
    },
    {
      test: /\.(woff|woff2|eot|otf|ttf)$/,
      loader: "file-loader",
      options: {
        name: "[name]-[hash:5].min.[ext]"
      }
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
              'border-radius-base': '4px'
            },
            javascriptEnabled: true,
          },
        },
      }],
    },
      {
        test: /\.s(c|a)ss$/,
        include: [path.resolve(__dirname, "src")],
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
      id: "babel",
      loaders: ["babel-loader?cacheDirectory"]
    }),
    new MiniCssExtractPlugin({
      filename: "[name].[hash:8].css",
      chunkFilename: "[name].[hash:8].css",
    }),
    new HtmlWebpackPlugin({
      title: "module",
      template: path.join(process.cwd(), './src/index.html'),
      filename: "index.html",
    }),
    new CleanWebpackPlugin(),
    new CopyWebpackPlugin({
      patterns: [
        {
          from: 'src/assets',
          to: 'src/assets'
        }
      ]
    }),
  ],
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all',
        },
      },
    },
  },
};
