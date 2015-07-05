module.exports = function(grunt) {
  
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json')
    
    bower: {
      install: {
        options: {
          install: true,
          copy: false,
          targetDir: 'src/main/webapp/app/components',
          cleanTargetDir: true
        }
      }
    }

    html2js: {
      dist: {
        src: [ 'src/main/webapp/**/*.html' ],
        dest: 'tmp/templates.js'
      }
    }

    jade: {
       html: {
        files: {
          'src/main/webapp/views/': ['src/main/webapp/views/**/*.jade'],
          'src/main/webapp/': ['src/main/webapp/*.jade']
        },
        options: {
          pretty: true,
          client: false
        }
      }
    }

    concat: {
      options: {
        separator: ';'
      },
      dist: {
        src: [ 'src/main/webapp/**/*.js', 'tmp/*.js' ],
        dest: 'dist/app.js'
      }
    }

    clean: {
      temp: {
        src: [ 'tmp' ]
      }
    }
    
    watch: {
      dev: {
        files: [ 'Gruntfile.js', 'src/main/webapp/**/*.js', 'src/main/webapp/**/*.html' ],
        tasks: [ 'html2js:dist', 'concat:dist', 'clean:temp' ],
        options: {
          atBegin: true
        }
      },
      min: {
        files: [ 'Gruntfile.js', 'src/main/webapp/**/*.js', 'src/main/webapp/**/*.html' ],
        tasks: [ 'concat:dist', 'clean:temp', 'uglify:dist' ],
        options: {
          atBegin: true
        }
      }
    }

    connect: {
      server: {
        options: {
          hostname: 'localhost',
          port: 8080
        }
      }
    }

    compress: {
      dist: {
        options: {
          archive: 'dist/<%= pkg.name %>-<%= pkg.version %>.zip'
        },
        files: [{
          src: [ 'index.html' ],
          dest: '/'
        }, {
          src: [ 'dist/**' ],
          dest: 'dist/'
        }, {
          src: [ 'assets/**' ],
          dest: 'assets/'
        }, {
          src: [ 'libs/**' ],
          dest: 'libs/'
        }]
      }
    }

    
  });

  grunt.loadNpmTasks('grunt-livescript');
    grunt.loadNpmTasks('grunt-jade');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-connect');
    grunt.loadNpmTasks('grunt-contrib-compress');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-html2js');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-bower-task');
};