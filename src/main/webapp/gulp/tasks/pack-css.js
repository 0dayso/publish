
const gulp = require('gulp');
const sass = require('gulp-sass');
const autoprefixer = require('gulp-autoprefixer');
const sourcemaps = require('gulp-sourcemaps');
const csso = require('gulp-csso');
const filter = require('gulp-filter');
const gulpIf = require('gulp-if');
const rev = require('gulp-rev');
const revReplace = require('gulp-rev-replace');

// From .scss to .css;
gulp.task('generate-css', ['copy-font'], function ( cb ) {
	return gulp.src(['./develop/assets/css/**/*.scss','!.develop/assets/css/modules','!./develop/assets/css/modules/**/*'])
		.pipe(sourcemaps.init())
		.pipe(sass().on('error', sass.logError))
		.pipe(autoprefixer({
			browsers: ['last 2 versions', 'IE 8', 'IOS 7', '> 5%']
		}))
		.pipe(sourcemaps.write())
		.pipe(gulp.dest('./temp/assets/css/'));
});

// copy fonts to temp folder;
gulp.task('copy-font',function () {
	return gulp.src(['./develop/assets/css/modules/font/*','!./develop/assets/css/modules/font/*.scss'])
		.pipe(gulp.dest('./temp/assets/css/'));
});

// compress css to dist folder;
gulp.task('min-css', function () {
	return gulp.src('./temp/assets/css/**/*')
		.pipe(gulpIf('*.css',csso({
			restructure: false
		})))
		.pipe(rev())
		.pipe(revReplace())
		.pipe(gulp.dest('./assets/css'))
		.pipe(rev.manifest('rev-css.json'))
		.pipe(gulp.dest('./WEB-INF/templates/'));
});