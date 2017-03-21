const gulp = require('gulp');
const browserify = require('browserify');
const source = require('vinyl-source-stream');
const handleErrors = require('../utils/handleErrors.js');
const uglify = require('gulp-uglify');
const globby = require('globby');
const rev = require('gulp-rev');

gulp.task('browserify', ['copy-lib'], function( cb ) {
	globby(['./develop/assets/js/**/*.js','!./develop/assets/js/modules/**/*','!./develop/assets/js/lib/**']).then(function (paths) {
		console.log(paths);
		paths.forEach(function ( path ) {
			browserify({
				debug: true,
				entries: path
			}).bundle()
			.on('error',handleErrors)
			.pipe(source(path.split('develop/assets/js/')[1]))
			.pipe(gulp.dest('./temp/assets/js'));
		});
		
		cb();
	});
});

// copy lib folder to temp folder;
gulp.task('copy-lib', function () {
	return gulp.src('./develop/assets/js/lib/**/*')
		.pipe(gulp.dest('./temp/assets/js/lib/'));
});

// compress js to dist folder;
gulp.task('min-js', function () {
	return gulp.src('./temp/assets/js/**/*.js')
		.pipe(uglify())
		.pipe(rev())
		.pipe(gulp.dest('./assets/js'))
		.pipe(rev.manifest('rev-js.json'))
		.pipe(gulp.dest('./WEB-INF/templates/'));
});
