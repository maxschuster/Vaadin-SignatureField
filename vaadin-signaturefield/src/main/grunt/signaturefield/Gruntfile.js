module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        gruntMavenProperties: grunt.file.readJSON('grunt-maven.json'),
        banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
                '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
                '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
                '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
                ' Licensed <%= _.pluck([pkg.license], "type") %> */\n',
        mavenPrepare: {
            options: {
                resources: ['**']
            },
            prepare: {}
        },
        mavenDist: {
            options: {
                warName: '<%= gruntMavenProperties.warName %>',
                deliverables: [
                    'node_modules/signature_pad/signature_pad.js',
                    'src/**',
                    'dist/**'
                ],
                gruntDistDir: 'maven-dist'
            },
            dist: {}
        },
        watch: {
            maven: {
                files: ['<%= gruntMavenProperties.filesToWatch %>'],
                tasks: 'default'
            }
        },
        clean: {
            files: ['dist']
        },
        concat: {
            options: {
                banner: '<%= banner %>',
                stripBanners: true
            },
            dist: {
                src: [
                    'node_modules/signature_pad/signature_pad.js',
                    'src/js/<%= pkg.name %>.js'
                ],
                dest: 'dist/<%= pkg.name %>.js'
            }
        },
        sass: {
            dist: {
                options: {
                    banner: '<%= banner %>',
                    style: 'compressed'
                },
                files: {
                    'dist/<%= pkg.name %>.css': 'src/scss/<%= pkg.name %>.scss'
                }
            }
        },
        uglify: {
            options: {
                banner: '<%= banner %>',
                sourceMap: true
            },
            dist: {
                src: '<%= concat.dist.src %>',
                dest: 'dist/<%= pkg.name %>.min.js'
            }
        },
        compress: {
            dist: {
                options: {
                    mode: 'gzip'
                },
                files: [
                    {
                        expand: true,
                        src: ['dist/*.js'],
                        dest: './',
                        ext: '.js.gz',
                        extDot: 'last'
                    },
                    {
                        expand: true,
                        src: ['dist/*.css'],
                        dest: './',
                        ext: '.css.gz',
                        extDot: 'last'
                    },
                    {
                        expand: true,
                        src: ['dist/*.map'],
                        dest: './',
                        ext: '.map.gz',
                        extDot: 'last'
                    }
                ]
            }
        },
        qunit: {
            files: ['test/js/**/*.html']
        },
        jshint: {
            options: {
                jshintrc: true
            },
            gruntfile: {
                src: 'Gruntfile.js'
            },
            src: {
                src: ['src/js/**/*.js']
            },
            test: {
                src: ['test/js/**/*.js']
            }
        }
    });

    grunt.loadNpmTasks('grunt-maven');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-qunit');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-compress');

    grunt.registerTask('default', ['mavenPrepare', 'jshint', 'clean', 'concat', 'uglify', 'sass', 'compress', 'mavenDist']);
    grunt.registerTask('watch', ['default']);

};