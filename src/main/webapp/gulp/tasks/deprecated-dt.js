
const gulp = require('gulp');
const useref = require('gulp-useref');
const spritesmith = require('gulp.spritesmith');
const sourcemaps = require('gulp-sourcemaps');
const pngquant = require('imagemin-pngquant');
const globby = require('globby');
const sass = require('gulp-sass');
const autoprefixer = require('gulp-autoprefixer');
const browserify = require('browserify');
const handleErrors = require('../utils/handleErrors.js');

gulp.task('copy-imgs-dt', ['sprite-dt'], function () {
    return gulp.src(['./develop/assets/imgs/**/*','!**/sprite','!**/sprite/**/*'])
        .pipe(gulp.dest('./assets/imgs/'));
});


// sprite imgs to temp folder;
gulp.task('sprite-dt', function ( cb ) {
    var spriteData = gulp.src('./develop/assets/imgs/sprite/icon/*.png')
        .pipe(spritesmith({
            imgName: 'icon-sprite.png',
            cssName: 'icon-sprite.scss',
            imgPath: 'imgs/icon-sprite.png',
            padding: 10
        }));

    spriteData.img.pipe(gulp.dest('./assets/imgs/'));
    spriteData.css.pipe(gulp.dest('./develop/assets/css/modules/'));

    cb();
});


// From .scss to .css;
gulp.task('generate-css-to-dt', ['copy-font-dt'], function ( cb ) {
    return gulp.src(['./develop/assets/css/**/*.scss','!.develop/assets/css/modules','!./develop/assets/css/modules/**/*'])
        .pipe(sourcemaps.init())
        .pipe(sass().on('error', sass.logError))
        .pipe(autoprefixer({
            browsers: ['last 2 versions', 'IE 8', 'IOS 7', '> 5%']
        }))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('./assets/css/'));
});

// copy fonts to temp folder;
gulp.task('copy-font-dt',function () {
    return gulp.src(['./develop/assets/css/modules/font/*','!./develop/assets/css/modules/font/*.scss'])
        .pipe(gulp.dest('./assets/css/'));
});


gulp.task('browserify-dt', ['copy-lib-dt'], function( cb ) {
    globby(['./develop/assets/js/**/*.js','!./develop/assets/js/modules/**/*','!./develop/assets/js/lib/**']).then(function (paths) {
        console.log(paths);
        paths.forEach(function ( path ) {
            browserify({
                debug: true,
                entries: path
            }).bundle()
                .on('error',handleErrors)
                .pipe(source(path.split('develop/assets/js/')[1]))
                .pipe(gulp.dest('./assets/js'));
        });

        cb();
    });
});

// copy lib folder to temp folder;
gulp.task('copy-lib-dt', function () {
    return gulp.src('./develop/assets/js/lib/**/*')
        .pipe(gulp.dest('./assets/js/lib/'));
});



// copy temp fragment html to dist folder;
gulp.task('fragment', function () {
    return gulp.src('./develop/assets/fragment/*.html')
        //.pipe(htmlmin({collapseWhitespace: true,processConditionalComments: true}))
        .pipe(gulp.dest('./assets/fragment/'));
});

// copy develop html to temp folder;
gulp.task('copy-html-dt',['fragment'], function () {
    return gulp.src('./develop/*.html')
        .pipe(gulp.dest('./WEB-INF/templates/'));
});
