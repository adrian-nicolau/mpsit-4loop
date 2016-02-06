var path = require('path');

module.exports = function(grunt) {

  grunt.initConfig({
    jshint: {
      files: ['Gruntfile.js', '*.js', 'frontend/**/*.js'],
    },
    jsdoc: {
        dist: {
            src: ['app.js', 'elastic.js', 'routes.js', 'frontend/*.js', '../README.md'],
            options: {
                destination: 'doc',
                template : 'node_modules/ink-docstrap/template',
            }
        }
    },
    watch: {
      linter: {
        files: ['<%= jshint.files %>'],
        tasks: ['jshint']
      },
      builder: {
        files: ['frontend/**/*.js'],
        tasks: ['browserify', 'concat_css'],
      }
    },
    browserify: {
        dist: {
            files: {
                'public/bundle.js': ['frontend/app.js']
            },
        }
    },
    concat_css: {
        all: {
            src: ['node_modules/angular-material/angular-material.min.css'],
            dest: 'public/bundle.css'
        }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-express-server');
  grunt.loadNpmTasks('grunt-concat-css');
  grunt.loadNpmTasks('grunt-jsdoc');

  grunt.registerTask('default', ['jshint', 'browserify']);
};
