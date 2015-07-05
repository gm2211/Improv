#global module:false

module.exports = (grunt) ->

  # Project configuration.
  grunt.init-config {}=

    common:
      staticDirs: ['src/main/webapp']

    meta:
      version: \0.0.1

    livescript:
      src:
        files:
          "src/main/webapp/js/main.js": "src/main/webapp/app/app.ls"

    jade:
       html:
        files:
          'src/main/webapp/views/': ['src/main/webapp/views/**/*.jade'],
          'src/main/webapp/': ['src/main/webapp/*.jade']
        options:
          pretty: true,
          client: false

    connect:
        options:
            port: 9000,
            hostname: 'localhost',
            livereload: 35729

        proxies: <[context: '/api' host: 'localhost' changeOrigin: false port: 8080]>

        livereload:
            options:
                open: true,
                base: src/main/webapp,
                middleware: (connect, options) ->
                   return [
                     (require 'grunt-connect-proxy/lib/utils').proxyRequest
                     connect.static 'src/main/webapp'
                     connect!.use '/components', connect.static 'src/main/webapp/app/components'
                     connect.static appConfig.app
                   ]

    watch:
     jade:
        files: <[src/main/webapp/**/*.jade]>
        tasks: <[jade]>
        options: {+livereload}

     livescript:
        files: <[src/main/webapp/**/*.ls]>
        tasks: <[livescript]>
        options: {+livereload}

  # load tasks
  grunt.loadNpmTasks \grunt-livescript
  grunt.loadNpmTasks \grunt-jade
  grunt.loadNpmTasks \grunt-contrib-watch
  #grunt.loadNpmTasks \grunt-serve

  # register tasks
  grunt.registerTask \default, <[livescript jade]>

  grunt.registerTask \server, (target) ->
      if target is 'dist'
        return grunt.task.run [
          'build'
          'configureProxies'
          'connect:dist'
        ]
      grunt.task.run [
        'configureProxies'
        'connect:livereload'
        'startLivereloadServer'
        'watch'
      ]
