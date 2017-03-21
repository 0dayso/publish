
const gulp = require('gulp');
const del = require('del');

gulp.task('clean-dist', del.bind(null, ['./assets/**','./WEB-INF/templates/**']));
gulp.task('clean-temp', del.bind(null, ['./temp/**']));
