
const path = require("path");
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");

module.exports = merge(common, {
  entry: {
    index: "./src/index.tsx",
  },
  output: {
    filename: "[name].js",
    path: path.resolve(__dirname, "output/6befc536-7e6d-48b5-8dcb-1c4d04ca4e92/build")
  },
  plugins: [],
});
