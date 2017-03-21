
const gulp = require('gulp');
const runSequence = require('run-sequence');

gulp.task('build', function ( callback ) {
	runSequence('clean-dist',['min-css', 'min-js','min-imgs'],'html', callback);
});

