var path = require('path');

module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      files: ['Gruntfile.js', '*.js', 'frontend/**/*.js'],
    },
    watch: {
      linter: {
        files: ['<%= jshint.files %>'],
        tasks: ['jshint']
      },
      builder: {
        files: ['frontend/**/*.js'],
        tasks: ['browserify'],
      }
    },
    browserify: {
        dist: {
            files: {
                'public/bundle.js': ['frontend/app.js']
            },
        }
    },
  });

  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-express-server');

  grunt.registerTask('default', ['jshint', 'browserify']);
};
