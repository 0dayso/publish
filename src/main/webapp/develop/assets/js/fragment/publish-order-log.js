"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    publishOrderId: $('#main-tab-container').data('publishOrderId'),
    proxy: {
        read: '/publish/apply/detail.do'
    },
    init: function () {
        var me = this;

        me.initTable()
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.PublishOrderLog');
    },
    initTable: function () {
        var me = this;

        me.grid =  me.container.children('.-page-container').jtable({
            proxy: {
                read: me.proxy.read + '?id=' + me.publishOrderId
            },
            offerCommonAjax: Util.baseAjax,
            events: {
                onRowClick: function () {
                    me.grid.toggleRowHeight($(this));
                    me.grid.setRowHeightFixed($(this).siblings())
                }
            },
            filterData: function ( data ) {
                if ( data.success && data.data ) {
                    return {
                        data: data.data.applylog
                    };
                } else {
                    return {};
                }
            },
            rowNumber: true,
            loading: {
                delay: 500,
                beforeSend: function () {
                    var grid = this;

                       me.loading = grid.tableBodyContainer.loading({
                                        loader: {
                                            intervalText: ['加载中 . ','加载中 . . ','加载中 . . . '],
                                            width: 10,
                                            height: 10,
                                            cls: 'loader-circle',
                                            textCls: 'fz-14px fw-b',
                                            text: '加载中...'
                                        }});
                },
                afterReceived: function () {
                    me.loading.remove();
                }
            },
            gridHeader: {
                title: '发布单操作日志',
                icon: 'iconfont icon-code',
                toolbar: [{
                    icon: 'iconfont icon-refresh1',
                    handle: function (e) {
                        var grid = e.data;

                        grid.reload();
                    }
                }]
            },
            columns: [{
                title: '用户',
                field: 'username',
                width: 120,
                fixed: true
            },{
                title: '动作',
                field: 'stepName',
                width: 280,
                cls: 'ta-l',
                hcls: 'ta-l'
            },{
                title: '日志',
                field: 'log',
                width: 480,
                cls: 'ta-l',
                hcls: 'ta-l',
                formatter: function ( v ) {
                    return '<pre class="pre-line">' + v + '</pre>';
                }
            },{
                title: '操作时间',
                field: 'gmtModified',
                width: 140,
                fixed: true,
                formatter: function (val, row) {
                    return Util.Date.format(val,'yyyy-mm-dd hh:mm:ss');
                }
            }]
        });
    }
};

mgr.init();