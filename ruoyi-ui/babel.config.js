module.exports = {
  presets: [
    '@babel/preset-env',
    '@vue/cli-plugin-babel/preset',
  ],
  'env': {
    'development': {
      'plugins': ['dynamic-import-node']
    }
  }
}
