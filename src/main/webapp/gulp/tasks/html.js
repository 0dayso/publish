
const gulp = require('gulp');
const htmlmin = require('gulp-htmlmin');
const rev = require('gulp-rev');
const revReplace = require('gulp-rev-replace');
//const rename = require('gulp-rename');
const useref = require('gulp-useref');

// copy develop html to temp folder;
gulp.task('copy-html', function () {
	return gulp.src('./develop/**/*.html')
		.pipe(useref())
		.pipe(gulp.dest('./temp/'));
});

// copy temp html to dist folder;
gulp.task('html', ['fragment'], function () {
	var manifest = gulp.src(['./WEB-INF/templates/rev-js.json','./WEB-INF/templates/rev-css.json']);
	return gulp.src('./temp/*.html')
		//.pipe(htmlmin({collapseWhitespace: true,processConditionalComments: true}))
		.pipe(revReplace({manifest: manifest}))
		//.pipe(rename({ extname: '.vm'}))
		.pipe(gulp.dest('./WEB-INF/templates/'));
});

// copy temp fragment html to dist folder;
gulp.task('fragment', function () {
	var manifest = gulp.src(['./WEB-INF/templates/rev-js.json','./WEB-INF/templates/rev-css.json']);
	return gulp.src('./temp/assets/fragment/*.html')
		//.pipe(htmlmin({collapseWhitespace: true,processConditionalComments: true}))
		.pipe(revReplace({manifest: manifest}))
		.pipe(gulp.dest('./assets/fragment/'));
});