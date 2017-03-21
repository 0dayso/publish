"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    // 线上发布机器默认下标 ，从0 开始； 如果获取项目发布信息下的idx 替换该值；
    onlinePublishHostIdx: 0,
    publishId: $('#main-tab-container').data('publishId'),
    proxy: {
        publishInfo: '/publish/apply/getAll.do',
        publishHistory: '/publish/shell/scripts.do',//?projectId=12',
        getBuildLog: '/publish/shell/getBuildLog.do',//?applyId=1',
        getAppLog: '/publish/shell/getRemotelog.do',//?applyId=1&host=1.1.1.1&port=9092'
        applyOnline: '/publish/apply/submit.do',    //?id=1
        check: '/publish/shell/checkProject.do',//?projectId=1',
        updateResource: '/publish/shell/git', //?applyId=1',
        build: '/publish/shell/build.do',//?applyId=1'
        beforeSync: '/publish/shell/preSync.do',//?applyId=1&online=true&idx=1'
        syncFile: '/publish/shell/syncfile.do',//?applyId=1&online=true&idx=1',//?applyId=1&online=true&idx=1'
        afterSync: '/publish/shell/afterSync.do',//?applyId=1&online=true&idx=1'
        setTag: '/publish/shell/gitTag.do',//?applyId=1'
        finishPublish: '/publish/apply/finish.do',//?id=1'
        revertPublish: '/publish/shell/rollback.do', //?applyId=1'
        checkHealth: '/publish/shell/checkHealth.json' //?applyId=872&idx=0'
    },
    init: function () {
        var me = this;

        Util.Tab.init(me.getLogTabElem(), {
            onClick: function ( th, tabContent ) {
                $.trim(th.text()) === '编译日志' && !th[0].hasGettedLog && me.setBuildLog(tabContent);
                th[0].hasGettedLog = true;
            },
            onRefresh: function ( th, tabContent ) {
                $.trim(th.text()) ===  '编译日志' &&  me.setBuildLog(tabContent);
            }
        });

        me.initPublishInfo(function () {
            me.renderPublishInfo.apply(me, arguments);
            me.setPublishHistory(function () {
                me.setPageShow(me.publishInfoData);
                me.bindEvents();
            });
        });
    },
    bindEvents: function () {
        var me = this;

        me.container.on('click.PublishAction', '.-collapsed', function () {
            $(this).toggleClass('icon-minus');
            $(this).closest('.-pc-list').find('.-info-body').slideToggle();
        }).on('click.PublishAction','.-pre-publish', function () {
            var loader = $(this).parent().loading({
                modal: {
                    opacity: 40
                },
                loader: {
                    cls: 'small-circle',
                    width: 15,
                    height: 15
                }
            });

            // 执行预发发布；
            me.publishAction({
                pre: true,
                host: me.publishInfoData.preHost,
                check: true,
                update: true,
                // type == 1 表示静态资源发布；
                build: !(me.publishInfoData.type == 1),
                beforeSync: me.publishInfoData.beforeSync,
                syncFile: true,
                afterSync: me.publishInfoData.afterSync
            }, function () {
                // 发布完成后执行回调；
                loader.remove();
            },{
                build: {
                    data: {  online: false }
                }
            }, function () {
                // 预发执行成功以后设置发布上线按钮可以用；
                me.publishInfoData.idx = 0;
                me.container.find('.-apply-online-btn').find('span').text('发布上线');
            });
        }).on('click.PublishAction','.-apply-online-btn', function () {
            var loader = $(this).parent().loading();

            me.publishBtnHandle( function () {
                loader.remove();
            });
        }).on('click.PublishAction','.-revert-btn', function () {
            var $this = $(this),
                loader = $(this).parent().loading({
                modal: {
                    opacity: 40
                },
                loader: {
                    cls: 'small-circle',
                    width: 15,
                    height: 15
                }
            });

            Util.baseAjax({
                url: me.proxy.revertPublish,
                data: { applyId: me.publishId },
                complete: function () {
                    loader.remove();
                }
            }, function ( json ) {
                Util.baseSuccessTips(json, '回滚成功', '回滚失败');

                if ( json.success ) {
                    this.container.find('.-pre-publish').parent().loading();
                    this.container.find('.-apply-online-btn').parent().loading();
                    $this.parent().loading();
                }
            });
        }).on('click.PublishAction','.-app-log', function () {
            var code = $(this).closest('.pc-info-header').find('.-ip-code-container')
                .text().split(':') || ['',''];

            Util.baseAjax({
                url: me.proxy.getAppLog,
                data: {
                    applyId: me.publishId,
                    host: code[0],
                    port: code[1]
                }
            }, function ( json ) {
                Util.baseSuccessTips(json, '获取app 日志成功', '获取app 日志失败');

                Util.Tab.add(me.getLogTabElem(), {
                    text: code.join(':'),
                    closed: true,
                    selected: true
                }, function ( tContent ) {
                    tContent.html('<pre>'+ (json.success ? json.data : json.msg )+'</pre>');
                });
            });
        }).on('click.PublishAction','.-go-on-next-host.btn-success', function () {
            var btnContainer = $(this).parent();

            btnContainer.remove();
            me.onlinePublish(null, false);
        }).on('click.PublishAction','.-health-check:not(.disabled)', function () {
            var $this = $(this),
                thisParentEl = $this.parent();

            var loader = thisParentEl.loading(
                {
                  modal: {
                      opacity: 40
                  },
                  loader: {
                      cls: 'small-circle',
                      width: 15,
                      height: 15
                  }
              }
            );
            $this.addClass('disabled');
            Util.baseAjax(
                {
                    url: me.proxy.checkHealth,
                    data: {
                        applyId: me.publishId,
                        idx: me.onlinePublishHostIdx - 1
                    },
                    complete: function () {
                        $this.removeClass('disabled');
                        loader.remove();
                    }
                }, function ( json ) {
                    json = json || {};

                    if ( !json.success ) {
                        Util.baseSuccessTips(json, json.msg || '发布成功正在继续发布中', json.msg || '尚未完成发布，请稍后重新发布');
                        return false;
                    }

                    $this.addClass('success-cor').append('<i class="iconfont icon-success d-ib ml-10px  va-t"></i>');
                    thisParentEl.nextAll('.-go-on-next-host').removeClass('disabled').addClass('btn-success');
                });
        });
    },
    isEmptyStep: function ( data ) {
        var ret = true;

        $.each(data, function () {
            ret = !(this === true);

            return ret;
        });

        return ret;
    },
    setPublishHistory: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.publishHistory,
            data: { applyId: me.publishId }
        }, function ( json ) {
            var htmls = '';

            Util.baseSuccessTips(json, '获取发布历史成功','获取发布历史失败');
            if ( !(json.success && json.data) ) {
                return false;
            }

            me.publishInfoData.hisOnline = json.data.online;
            me.publishInfoData.hisPre = json.data.pre;

            // 执行回调；
            $.isFunction(cb) && cb.call(me, json.data);
            !$.isEmptyObject(json.data.pre)
            && (htmls += me.isEmptyStep(json.data.pre) ? '' : me.getStepTpl({
                    pre: true,
                    host: json.data.pre.host,
                    check: json.data.pre.check,
                    update: json.data.pre.update,
                    build: json.data.pre.build,
                    beforeSync: json.data.pre.beforeSync,
                    syncFile: json.data.pre.syncFile,
                    afterSync: json.data.pre.afterSync
                }, true));

            !$.isEmptyObject(json.data.online)
            && $.each(json.data.online.hosts, function () {
                    htmls += me.isEmptyStep(this) ? '' :  me.getStepTpl({
                        pre: false,
                        host: this.host,
                        build: this.build,
                        beforeSync: this.beforeSync,
                        syncFile: this.syncFile,
                        afterSync: this.afterSync
                    }, true);
                });
            me.getStepStatusContainerElem().html(htmls);
        });
    },
    setBuildLog: function ( container ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getBuildLog,
            data: { applyId : me.publishId }
        }, function ( json ) {
            Util.baseSuccessTips(json, '获取编译日志成功', '获取编译日志失败');
            container.html('<pre>'+ (json.success ? json.data : json.msg )+'</pre>');
        });
    },
    initPublishInfo: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.publishInfo,
            data: { id: me.publishId }
        }, function ( json ) {
            json.success && json.data
            && $.isFunction(cb) && cb.call(me, json.data);
        });
    },
    renderPublishInfo: function ( data ) {
        var rData = $.extend(true, {}, data.apply, data.project);

        this.publishInfoData = $.extend(true,{}, rData);
        this.onlineHostArr = (this.publishInfoData.onlineHost || '').split('\n');
        this.onlinePublishHostIdx = rData.idx;

        rData.publishTitle = data.apply.title;
        rData.gmtModified = Util.Date.format(rData.gmtModified, 'yyyy/mm/dd hh:mm:ss');
        rData.type = rData.type === 1 ?  '静态资源' : 'JAVA 资源';
        rData.status = this.statusMap(rData.status);
        Util.Form.loadData(this.container.find('form'), rData);
    },
    setPageShow: function ( data ) {
        var preBtnElem = this.container.find('.-pre-publish'),
            revertBtnElem = this.container.find('.-revert-btn'),
            onlinePublishBtnElem =  this.container.find('.-apply-online-btn');
        var publishSuccess = false;

        // 如果是前端资源，去掉编译日志TAB;
        data.type === 1 && Util.Tab.close(this.getLogTabElem(), 1);
        // 如果已经审批通过则可以发布上线； flag == 1
        data.flag === 1 && onlinePublishBtnElem.removeClass('btn-success').find('span').text('发布线上');

        // 代码如果回滚了 则禁用 预发和线上发布和回滚 按钮；
        if ( data.status === 17 ) {
            onlinePublishBtnElem.parent().loading();
            revertBtnElem.parent().loading();
            revertBtnElem.text('已回滚');
            preBtnElem.parent().loading();
        } else if ( data.status === 15 ) {  // 如果已完成发布则禁用 预发 和线上发布按钮；
            preBtnElem.parent().loading();
            onlinePublishBtnElem.text('发布已完成');
            onlinePublishBtnElem.parent().loading();
        } else {
            data.idx >= data.hisOnline.hosts.length
            && onlinePublishBtnElem.find('span').text('完成发布');
        }

        if ( data.hisOnline.hosts ) {
            data.hisOnline.hosts[0].syncFile
            && revertBtnElem.parent().removeClass('d-n');

            if ( data.type === 0 ) {
                $.each(data.hisOnline.hosts[0], function () {
                    return publishSuccess = !!this;
                });

                // 如果当前资源类型为JAVA 且有一台线上的机器发布成功，则显示回滚按钮；
                publishSuccess && revertBtnElem.removeClass('d-n');
            }
        }
    },
    flagMap: function ( val ) {
        switch ( +val ) {
            case -1 :
                return '删除';
            case 0 :
                return '未审核';
            case 1 :
                return '正常';
            case 2 :
                return '审核未通过';

            default :
                return val;
        }
    },
    statusMap: function ( val ) {
        switch ( +val ) {
            case 0 :
                return '创建完成';
            case 1 :
                return '更新';
            case 2 :
                return '编译通过';
            case 3 :
                return '编译失败';
            case 4 :
                return '同步预发成功';
            case 5 :
                return '同步预发失败';
            case 6 :
                return '重置预发成功';
            case 7 :
                return '重置预发失败';
            case 8 :
                return '等待发布';
            //case 9 :
            //    return '发布中';
            case 10 :
                return '线上编译成功';
            case 11 :
                return '线上编译失败';
            case 12 :
                return '线上文件同步';
            case 13 :
                return '线上发布成功';
            case 15:
                return '发布单已完成';
            case 17:
                return '发布单已回滚';

            default :
                return val;
        }
    },
    getLogTabElem: function () {
        var e = this.container.find('.-log-tab-container');

        this.getLogTabElem = function () {
            return e;
        };

        return e;
    },
    getStepStatusContainerElem: function () {
        var e = this.container.find('.-step-status-container');

        this.getStepStatusContainerElem = function () {
            return e;
        };

        return e;
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.ProjectController');
    },
    publishBtnHandle: function ( cb, isBuild ) {
        var me = this,
            onlineInfo = me.publishInfoData.hisOnline,
            hostsArr = $.isArray(onlineInfo.hosts) ? onlineInfo.hosts : [];

        /**
         * case 1. 如果线上发布过所有的机器，且当前状态 ！= 15 【确认过发布完成】 ！= 17 【已回滚】 则执行发布完成；
         * case 2. 如果当前发布单状态正常，则发布；
         * case 3. 执行申请发布；
         */
        if ( me.publishInfoData.idx >= me.publishInfoData.hisOnline.hosts.length
            && me.publishInfoData.status != 15
            && me.publishInfoData.status != 17 ) {
            me.confirmPublishFinish();
        } else if ( me.publishInfoData.flag == 1 ) {
            // 如果是静态资源发布则去掉编译步骤；
            me.onlinePublish(cb,
                isBuild == undefined
                    ? me.publishInfoData.type == 1 ? false : !(hostsArr[0] && hostsArr[0].build)
                    : isBuild);
        } else {
            Util.baseAjax({
                url: me.proxy.applyOnline,
                data: { id: me.publishId },
                complete: function () {
                    $.isFunction(cb) && cb();
                }
            }, function ( json ) {
                Util.baseSuccessTips(json, '申请上线提交成功，等待审核', '申请上线提交失败');
            });
        }
    },
    confirmPublishFinish: function () {
        var me = this;

        new $.Dialog({
            header:  {
                title: '确认发布完成'
            },
            content: '<div class="dialog-content text-base-red p-20px fz-14px"> ' +
            '<i class="iconfont icon-info mr-5px"></i>' +
            '<span class="d-ib va-t ml-10px">您确定该发布单发布完成吗？<br/>确认完成后不能再次预发？</span></div>',

            style: {
                width: 310
            },
            buttons: [{
                text: '确定',
                icon: 'iconfont icon-confirm',
                cls: 'btn btn-success',
                handle: function ($noty) {
                    Util.baseAjax({
                        url: me.proxy.finishPublish,
                        data: 'id=' + me.publishId
                    }, function ( json ) {
                        Util.baseSuccessTips(json, '确认发布完成成功','确认发布完成失败');

                        if ( json.success ) {
                            $noty.close();
                            me.container.find('.-pre-publish').parent().loading();
                            me.container.find('.-apply-online-btn').text('发布已完成');
                        }
                    });
                }
            },{
                text: '取消',
                icon: 'iconfont icon-cancel',
                cls: 'btn btn-danger',
                handle: function ($noty) { $noty.close(); }
            }]
        });
    },
    onlinePublish: function ( cb, isBuild ) {
        var me = this;

        // 执行线上发布
        me.publishAction({
            host: me.onlineHostArr[me.onlinePublishHostIdx], //me.publishInfoData.onlineHost,
            // @overwrite 开启线上发布的检查、更新 步骤；
            check: true, // false,
            update: true, // false,
            build: isBuild,
            beforeSync: me.publishInfoData.beforeSync,
            syncFile: true,
            afterSync: me.publishInfoData.afterSync
        }, function () {
            $.isFunction(cb) && cb();
        }, {
            build: {
                data: {  online: true }
            },
            beforeSync: {
                data: {
                    online: true,
                    idx: me.onlinePublishHostIdx
                }
            },
            syncFile: {
                data: {
                    online: true,
                    idx: me.onlinePublishHostIdx
                }
            },
            afterSync: {
                data: {
                    online: true,
                    idx: me.onlinePublishHostIdx
                }
            }
        }, function ( container ) { // 发布成功以后的回调；
            // 如果是JAVA 资源发布，成功后，显示回滚按钮；
            me.publishInfoData.type === 0 && me.container.find('.-revert-btn').removeClass('d-n');
            if ( me.onlinePublishHostIdx < me.publishInfoData.hisOnline.hosts.length ) {
                container
                    .append(' <div class="cf p-10px ta-c"><div class="d-ib"><span class="btn -health-check mr-10px">健康检查</span></div>' +
                            '<span class="btn -go-on-next-host disabled">继续发布</span></div></div>')
                    .get(0).scrollIntoView();
            } else {
                me.container.find('.-apply-online-btn').parent().loading();
                me.confirmPublishFinish();
            }
        });

        ++me.onlinePublishHostIdx;
    },
    getBaseTpl: function () {
        var tpl = this.container.find('.-publish-status-tpl').html();

        this.getBaseTpl = function () {
            return tpl;
        };

        return tpl;
    },
    stepItemTpl: '<li class="{history} p-5px fz-14px wait-cor {cls}"><span class="fl-l w-90">{text}</span>' +
                    '<i class="iconfont {status} d-ib"></i></li>',
    getStepTpl: function ( pStep, history ) {
        return Util.renderTpl(this.getBaseTpl(), {
            preOrOnline: pStep.pre ? '预发' : '线上',
            host: pStep.host,
            check: pStep.check
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-check',
                        text: '检查',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : '',
            update: pStep.update
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-update',
                        text: '更新',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : '',
            build: pStep.build
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-build',
                        text: '编译',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : '',
            beforeSync: pStep.beforeSync
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-before-sync',
                        text: '同步前执行脚本',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : '',
            syncFile: pStep.syncFile
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-sync-file',
                        text: '同步文件',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : '',
            afterSync: pStep.afterSync
                ? Util.renderTpl(this.stepItemTpl, {
                        cls: '-step-after-sync',
                        text: '同步后执行脚本',
                        history: history ? 'success-cor': 'd-n',
                        status: history ? 'icon-success' : 'small-circle'
                    })
                : ''
        });
    },
    /**
     * 发布执行步骤
     * @param list {Array}
     *  @item obj {Object}
     *      url: {String} 请求地址；
     *      data: {String/Object}请求参数，
     *      target：{String} 请求成功返回后的操作目标选择对象；
     * @param container {jQuery}
     * @param cb {Function}
     * @param [successCallback] {Function} optional 全部成功后的回调；
     */
    publishStepAction: function ( list, container, cb, successCallback ) {
        var me = this,
            i = 0,
            tabContainer = $(),
            _action = function () {
                Util.baseAjax({
                    url: list[i].url,
                    data: list[i].data,
                    timeout: 0,
                    beforeSend: function () {
                        container.find('.'+ list[i].target).removeClass('d-n');
                    },
                    complete: function ( xhr, ts) {

                        if ( ts !== 'success' ) {
                            $.isFunction(cb) && cb();
                            container.find('.'+ list[i].target)
                                .addClass('error-cor')
                                .find('.small-circle')
                                .removeClass('small-circle')
                                .addClass('icon-error');
                        }
                    }
                }, function ( json ) {
                    tabContainer.append( '<pre>' +
                            (json.success ? json.data : '<span class="error-cor">'+ json.msg + '</span>') +
                            '</pre>');

                    container.find('.'+ list[i].target)
                        .addClass( json.success ? 'success-cor' : 'error-cor')
                        .find('.small-circle')
                        .removeClass('small-circle')
                        .addClass(json.success ? 'icon-success' : 'icon-error');

                    ++i;
                    json.success && i < list.length
                        ? _action(i)
                        : $.isFunction(cb) && cb();

                    i === list.length
                    && json.success
                    && $.isFunction(successCallback) && successCallback(container)
                });
            };

        Util.Tab.select(me.getLogTabElem(), 'first', function ( tContent ) {
            tabContainer = tContent;
        });
        $.isArray(list) && list.length && _action();
    },
    filterStep: function ( pStep, pOpts ) {
        var me = this,
            retStep = [], tempOpt,
            _getOptObj = function ( it ) {
                tempOpt = null;

                return pStep[it] && (tempOpt = $.extend(true, {}, pOpts[it]), true);
            };

        //($.each(opts, function () {// 找到以后不会再继续循环；return !(this.name === it && $.extend(true, tempOpt, {}, this));})

        pOpts = pOpts || {};
        _getOptObj('check') && retStep.push($.extend(true, {
                                url: me.proxy.check,
                                target: '-step-check',
                                data: { projectId: me.publishInfoData.projectId }
                            }, tempOpt));
        _getOptObj('update') && retStep.push($.extend(true, {
                                url: me.proxy.updateResource,
                                target: '-step-update',
                                data: { applyId: me.publishId }
                            }, tempOpt));
        _getOptObj('build') && retStep.push($.extend(true, {
                                url: me.proxy.build,
                                target: '-step-build',
                                data: { applyId: me.publishId }
                            }, tempOpt));
        _getOptObj('beforeSync') && retStep.push($.extend(true, {
                                url: me.proxy.beforeSync,
                                target: '-step-before-sync',
                                data: {
                                    applyId: me.publishId,
                                    online: false,
                                    idx: 0
                                }
                            }, tempOpt));
        _getOptObj('syncFile') && retStep.push($.extend(true, {
                                url: me.proxy.syncFile,
                                target: '-step-sync-file',
                                data: {
                                    applyId: me.publishId,
                                    online: false,
                                    idx: 0
                                }
                            }, tempOpt));
        _getOptObj('afterSync') && retStep.push($.extend(true, {
                                url: me.proxy.afterSync,
                                target: '-step-after-sync',
                                data: {
                                    applyId: me.publishId,
                                    online: false,
                                    idx: 0
                                }
                            }, tempOpt));
        return retStep;
    },
    /**
     * 根据配置数据生成对应的模板，填充到的发布状态块中；过滤发布步骤后执行具体的发布步骤；
     * @param preStepObj {object} 发布步骤配置对象；
     * @param cb  {function} 发布成功的回调；
     * @param [opts] {object} optional 重写默认预发的请求参数；
     * @param [successCallback] {function} 发布成完成后的回调函数；
     * eg.{
     *          build: {
     *              data: {
     *                  online: true
     *              }
     *          }
     *      }
     */
    publishAction: function ( preStepObj, cb, opts, successCallback ) {
        var me = this,
            stepContainer = $(me.getStepTpl(preStepObj));

        if ( arguments.length < 4 && $.isFunction(opts) ) {
            successCallback = opts;
            opts = {};
        } else if ( arguments.length === 4 ) {
            $.type(successCallback) === 'object'
            && (opts = [successCallback,successCallback = opts][0]);
        }

        me.getStepStatusContainerElem().append(stepContainer);
        me.publishStepAction(me.filterStep(preStepObj, opts), stepContainer, cb, successCallback);
    }
};

mgr.init();