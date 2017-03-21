"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    proxy: {
        getDir: '/publish/shell/listDir.do',//?dir=/usr/local&hostinfo=shinemo-safe:10.132.21.123:9092',
        getFileContent: '/publish/shell/getlog.do',//?filename=/usr/local/1_tomcat/logs/catalina.out&lines=100&hostinfo=shinemo-safe:10.132.21.123:9092',
        getMachines: '/publish/remoteshell/allHost.do'
    },
    init: function () {
        var me = this;
        var serForm = me.getSearchFormElem()[0];
        var maxH = $('#page-main').height() - 110 - me.getSearchFormElem().height();

        me.container.find('.-container-height').height(maxH);
        me.getLogContainerElem().height(maxH - 36);
        me.getMachineData(function ( data ) {
            Util.Form.loadSelectOptions(serForm.hostinfo, data, '请选择操作机器');
        });

        me.bindEvents();
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.LogFile');
    },
    bindEvents: function () {
        var me = this,
            serForm = me.getSearchFormElem();

        me.container.on('click.LogFile', '.-ser-btn', function () {
            if ( !serForm.validate('validation') ) {
                return false;
            }

            me.renderPath(serForm[0].dir.value || '');
            me.renderDir({
                hostinfo: serForm[0].hostinfo.value,
                dir: serForm[0].dir.value
            });
        }).on('click.LogFile', '.-file', function () {
            var dir = serForm[0].dir.value,
                fileName = $(this).attr('title'),
                fullPath = dir + (dir.slice(-1) === '/' ? fileName : '/' + fileName);

            if ( !serForm.validate('validation') ) {
                return false;
            }

            me.renderLogFile({
                filename: fullPath,
                lines: serForm[0].lines.value,
                hostinfo: serForm[0].hostinfo.value
            });
            me.renderPath(fullPath || '');
        }).on('click.LogFile', '.-folder', function () {
            var dir = serForm[0].dir.value,
                folderName = $(this).attr('title');

            serForm[0].dir.value = dir + (dir.slice(-1) === '/' ? folderName : '/' + folderName);
            if ( !serForm.validate('validation') ) {
                return false;
            }

            me.renderDir({
                hostinfo: serForm[0].hostinfo.value,
                dir: serForm[0].dir.value
            });

            me.renderPath(serForm[0].dir.value || '');
        }).on('click.LogFile','.path>i:not(:first)', function () {
            var pathArr = [$(this).text()],
                path = '';

            me.getLogContainerElem().html('');
            $(this).nextAll('i').each(function () {
                pathArr.unshift($(this).text())
            });

            path = pathArr.join('/');
            me.renderDir({
                hostinfo: serForm[0].hostinfo.value,
                dir: path
            });
            me.renderPath(path || '');
            serForm[0].dir.value = path;
        });

        serForm.on('keydown', function ( e ) {
            if ( e.keyCode === 13 ) {
                e.preventDefault();
                serForm.find('.-ser-btn').trigger('click');

                return false;
            }
        });
    },
    renderLogFile: function ( data ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getFileContent,
            data: data
        },function ( json ) {
            json = json || {};
            Util.baseSuccessTips(json, '获取目录成功','获取目录失败');

            if ( json.success && json.data ) {
                me.getLogContainerElem().html('<pre class="pre-line">' + json.data + '</pre>');
            }
        });
    },
    getDirData: function ( data, cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getDir,
            data: data
        }, function (json) {
            json = json || {};
            if ( json.success && json.data ) {
                $.isFunction(cb) && cb(json.data);
            } else {
                Util.baseSuccessTips(json, '获取目录成功','获取目录失败');
            }
        });
    },
    renderPath: function ( basePath ) {
        var me = this,
            spArr = basePath.split('/').reverse(),
            str = '';

        $.each(spArr, function ( i, n ) {
            str += '<i class="iconfont '+ (i === 0 ? '': 'icon-slash') +'"><b class="cur-p">'+ n +'</b></i>';
        });

        me.getPathContainerElem().html(str);
    },
    renderDir: function ( q ) {
        var me = this;
        var tpl = '<li class="relative {fileOrFolder}" title="{text}"><i class="success-cor iconfont icon-file {folder}">' +
            '</i><span class="ml-5px ov-e w-150px d-ib">{text}</span></li>';

        me.getDirData(q, function ( data ) {
            var htmls = '';

            $.each(data, function () {
                htmls += Util.renderTpl(tpl, {
                            fileOrFolder: this.type == 'dir' ? '-folder' : '-file',
                            folder: this.type == 'dir' ? 'icon-folder text-base-cor' : '',
                            text: this.name
                        });
            });

            me.getFolderTreeElem().html(htmls);
        });
    },
    getFolderTreeElem: function () {
        var e = this.container.find('.tree');

        this.getFolderTreeElem = function () {
            return e;
        }

        return e;
    },
    getPathContainerElem: function () {
        var e = this.container.find('.path');

        this.getPathContainerElem = function () {
            return e;
        }

        return e;
    },
    getLogContainerElem: function () {
        var e = this.container.find('.-log-file-container');

        this.getLogContainerElem = function () {
            return e;
        }

        return e;
    },
    getSearchFormElem: function () {
        var e = this.container.find('.-search-form');

        this.getSearchFormElem = function () {
            return e;
        }

        return e;
    },
    getMachineData: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getMachines
        }, function (json) {
            var ret = [],
                p, d;

            json = json || {};
            if ( json.success && json.data ) {
                d = json.data;

                for ( p in d ) {
                    ret.push({
                        key: d[p],
                        value: p
                    });
                }

                $.isFunction(cb) && cb(ret);
            }

        });
    }
};

mgr.init();