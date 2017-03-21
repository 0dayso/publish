
const gulp = require('gulp');
const runSequence = require('run-sequence');

gulp.task('default', function ( callback ) {
	// browserify 任务如果放到并列任务中，执行dt 任务的时候有些文件复制不到目标文件；
	runSequence('clean-temp','copy-imgs-to-temp','browserify',['generate-css','copy-html'], callback);
});

gulp.task('dt', function ( callback ) {
	runSequence('clean-dist','default','copy-temp-to-dist',callback);
});

gulp.task('copy-temp-to-dist', function ( cb ) {
	gulp.src('./temp/assets/**/*').pipe(gulp.dest('./assets/'));
	return gulp.src('./temp/*.html').pipe(gulp.dest('./WEB-INF/templates/'));
});

gulp.task('watch', function () {

	const watcher = gulp.watch('./develop/**/*',['default']);

	watcher.on('change', function(event) {
		console.log('File ' + event.path + ' was ' + event.type + ', running tasks...');
	});
});