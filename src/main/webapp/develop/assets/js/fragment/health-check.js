"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    listContainer: $('#project-list-container'),
    proxy: {
        // '/assets/mock/wars.json',    //'/assets/mock/checkAllStatus.json',
        // '/assets/mock/checkStatus.json'     //
        getProjectWar: '/publish/monitor/wars.do',
        checkAllProjectStatus: '/publish/monitor/checkAllWars.do',
        checkStatus: '/publish/monitor/checkStatus' //?id=11'
    },
    _cacheConfigErrorList: [],
    init: function () {
        var me = this;

        me.renderProjectList();
        me.bindEvents();
    },
    bindEvents: function () {
        var me = this,
            serForm = me.getSearchFormElem();

        me.listContainer.on('click', '.-check-status-btn', function () {
            var $this = $(this);

            if ( $this.hasClass('disabled') ) {
                return false;
            }
            me.doCheckStatus($this.data('id'), $this.closest('li'));
        });
        me.container.on('click', '.-ser-btn', function () {

            me.filterItems(serForm[0].dir.value || '');
        }).on('click', '.-check-all-status', function () {
            me.doCheckAllStatus();
        });

        serForm.on('keydown', function ( e ) {
            if ( e.keyCode === 13 ) {
                e.preventDefault();
                serForm.find('.-ser-btn').trigger('click');

                return false;
            }
        });
    },
    filterItems: function ( key ) {
        var me = this,
            filterData = [];

        if ( key ) {
            if ( key.toLowerCase() === 'configerror' ) {
                return me.fillProjectWars(me._cacheConfigErrorList);
            }

            $.each( me._cacheData, function (i, it) {
                it.name.search(new RegExp(key, 'i')) !== -1 && filterData.push(it);
            });

            filterData.length
                ? me.fillProjectWars(filterData, $.isPlainObject(filterData[0].host))
                : me.fillProjectWars([]);
        } else {
            me.fillProjectWars(me._cacheData, $.isPlainObject(me._cacheData[0].host));
        }
    },
    doCheckAllStatus: function () {
        var me = this;

        var loader = me.addLoading( me.listContainer );
        Util.baseAjax(
            {
                url: me.proxy.checkAllProjectStatus,
                complete: function () {
                    loader.remove();
                }
            }, function ( json ) {
                var data;

                json = json || {};
                data = json.data;
                if ( !json.success || !data ) {
                    return Util.baseSuccessTips(json, '', json.msg || '检查失败');
                }

                me._cacheData = data;
                me.fillProjectWars(data, true);
            }
        );
    },
    addLoading: function ( container ) {
        return container.loading(
            {
                modal: {
                    opacity: 40
                },
                loader: {
                    cls: 'loader-circle',
                    width: 30,
                    height: 30
                }
            }
        );
    },
    doCheckStatus: function ( id, container ) {
        var me = this;

        // 添加loading 到当前项；
        var loader = me.addLoading( container );
        Util.baseAjax(
            {
                url: me.proxy.checkStatus,
                data: { id: id },
                complete: function () {
                    loader.remove();
                }
            }, function ( json ) {
                var data;

                json = json || {};
                data = json.data;
                if ( !json.success || !data ) {
                    return Util.baseSuccessTips(json, '', json.msg || '操作失败');
                }

                var allSuccess = true,
                    hosts = [];

                $.each(data, function ( key, val ) {
                    hosts.push({
                                   host: key,
                                   status: val.status
                               });
                    !val.status && (allSuccess = false);
                });

                container.addClass(allSuccess ? 'check-success' : 'check-error')
                         .removeClass(allSuccess ? 'check-error' : 'check-success');
                container.find('.host-container').html(me.fillHostItems(hosts));
            });
    },
    renderProjectList: function () {
        var me = this;

        Util.baseAjax(
            {
                url: me.proxy.getProjectWar
            }, function ( json ) {
                var data;

                json = json || {};
                data = json.data;
                if ( !json.success || !data ) {
                    return Util.baseSuccessTips(json, '', json.msg || '获取项目失败');
                }

                me._cacheData = data;
                me.fillProjectWars(data);
            });
    },
    fillHostItems: function ( hosts ) {
        var hostItemTpl = '<div class="{% return data.status ? \'cor-success\' : data.status === false? \'cor-error\' : \'\' %}">' +
                '<span class="d-ib va-m w-25 ta-r">host-{index}:</span>' +
                '<span class="d-ib va-m ml-10px">{host}</span>' +
                '</div>',
            retHtmls = '',
            i = 0;

        $.each(hosts || [], function ( key, h ) {
            retHtmls += Util.renderTpl(hostItemTpl, { index: i++, host: h.host, status: h.status });
        });

        return retHtmls;
    },
    fillProjectWars: function ( data, checked ) {
        var me = this,
            cacheConfigErrorArr = [],
            retHtmls = '';

        $.each(data || [], function ( i, it ) {
            var allSuccess = true,
                configSuccess = true,
                hosts = [];

            if ( $.isArray(it.host) ) {
                hosts = $.map(it.host, function ( v ) { return { host: v }});
            } else {
                $.each(it.host, function ( key, val ) {
                    hosts.push({
                                   host: key,
                                   status: val.status
                               });
                    !val.status && (allSuccess = false);
                })
            }

            if ($.isEmptyObject(it.host) || !it.checkUri ) {
                allSuccess = false;
                it.disabled = 'disabled';
                it.configError = '<span class="cor-error d-ib ml-20px va-m  mt-5px">配置不正确</span>';
                configSuccess = false;
                cacheConfigErrorArr.push(it);
            }

            it.hosts = me.fillHostItems(hosts);
            it.checkResultClass = checked
                ? allSuccess ? 'check-success' : 'check-error'
                : configSuccess ? '' : 'check-error';
            retHtmls += Util.renderTpl(me.getProjectItemTpl(), it);
        });

        me._cacheConfigErrorList = cacheConfigErrorArr;
        me.listContainer.html(retHtmls);
    },
    getProjectItemTpl: function () {
        var tpl = $('#project-status-item-tpl').html();

        this.getProjectItemTpl = function () {
            return tpl;
        };
        return tpl;
    },
    getSearchFormElem: function () {
        var e = this.container.find('.-search-form');

        this.getSearchFormElem = function () {
            return e;
        };

        return e;
    }
};

mgr.init();