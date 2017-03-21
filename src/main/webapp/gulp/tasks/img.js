
const gulp = require('gulp');
const imgMin = require('gulp-imagemin');
const spritesmith = require('gulp.spritesmith');
const gulpIf = require('gulp-if');
const pngquant = require('imagemin-pngquant');
const globby = require('globby');

// sprite imgs to temp folder;
gulp.task('sprite', function ( cb ) {
	globby('./develop/assets/imgs/sprite/').then( function () {
		console.log(arguments);
	});
	var spriteData = gulp.src('./develop/assets/imgs/sprite/icon/*.png')
		.pipe(spritesmith({
			imgName: 'icon-sprite.png',
			cssName: 'icon-sprite.scss',
			imgPath: 'imgs/icon-sprite.png',
			padding: 10
		}));

	spriteData.img.pipe(gulp.dest('./temp/assets/imgs/'));
	spriteData.css.pipe(gulp.dest('./develop/assets/css/modules/'));

	cb();
});

gulp.task('copy-imgs-to-temp', ['sprite'], function () {
	return gulp.src(['./develop/assets/imgs/**/*','!**/sprite','!**/sprite/**/*'])
		.pipe(gulp.dest('./temp/assets/imgs/'));
});

// compress imgs form temp folder to dist;
gulp.task('min-imgs', function () {
	return gulp.src(['./temp/assets/imgs/**/*'])
		.pipe(imgMin({
			progressive: true,
			optimizationLevel: 0,
			use: [pngquant()]
		}))
		.pipe(gulp.dest('./assets/imgs/'));
});
