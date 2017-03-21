"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    deployPath: location.pathname.search('/publish') !== -1 ? '/publish' : '',
    proxy: {
        read: '/publish/apply/search.do',
        getMembers: '/publish/user/selectAll.do',
        getProjects: '/publish/project/selectAll.do'
    },
    init: function () {
        var me = this;
        var serForm = me.getSearchFormElem()[0];

        me.getAllMemberData(function ( data ) {
            $(serForm.userId).select2({
                data: [{id: '',text: ''}].concat(data),
                allowClear: true,
                placeholder: {
                    id: '',
                    text: '请选择用户'
                }
            });

            me.getAllProjectData(function ( data ) {
                me.initTable(data);
                $(serForm.projectId).select2({
                    data: [{id: '',text: ''}].concat(data),
                    allowClear: true,
                    placeholder: {
                        id: '',
                        text: '请选择项目'
                    }
                });
            });
        });

        $.datetimepicker.setLocale('zh');
        // init datepicker;
        $(serForm.startDate).datetimepicker({
            format: 'Y-m-d',
            timepicker:false,
            onShow: function () {
                var endDate = $(serForm.endDate).val();

                this.setOptions({
                    maxDate: endDate ? endDate : false
                })
            }
        });

        $(serForm.endDate).datetimepicker({
            format: 'Y-m-d',
            timepicker:false,
            onShow: function () {
                var startDate = $(serForm.startDate).val();

                this.setOptions({
                    minDate: startDate ? startDate : false
                });
            }
        });

        me.bindEvents();
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.OperateLog');
    },
    bindEvents: function () {
        var me = this;

        me.container.on('click.OperateLog', '.-ser-btn', function () {
            if ( !me.getSearchFormElem().validate('validation') ) {
                return false;
            }

            me.grid.reload();
        });
        me.container.on('click.OperateLog', '.-operate-log-btn', function () {
            // 也可以绑定事件捕获处理；
            setTimeout(function () {
                var sData = me.grid.getSelectedData();

                if ( sData ) {
                    $('#main-tab-container').data('publishOrderId', sData.id);
                    Util.menuTreeIns.select({
                        href:  me.deployPath + '/assets/fragment/publish-order-log.html',
                    });
                } else {
                    noty({
                        text: '请选中条数据再操作！',
                        type: 'warning',
                        layout: 'topCenter',
                        theme: 'defaultTheme',
                        timeout: 4 * 1000
                    });
                }
            },10);
        });
    },
    getSearchFormElem: function () {
        var e = this.container.find('.-search-form');

        this.getSearchFormElem = function () {
            return e;
        }

        return e;
    },
    getAllProjectData: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getProjects,
            data: 'self=false',
            complete: function () {
                $.isFunction(cb) && cb.call( me, me.allProjects );
            }
        }, function (json) {
            json = json || {};

            json.success
            && $.isArray(json.data)
            && (me.allProjects = json.data.map(function ( it) { return { id: it.value, text: it.text}; }));
        });
    },
    getAllMemberData: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getMembers,
            complete: function () {
                $.isFunction(cb) && cb.call( me, me.allMembers );
            }
        }, function (json) {
            json = json || {};
            json.success && $.isArray(json.data) &&
            (me.allMembers = json.data.map(function ( it) { return { id: it.value, text: it.text}; }));
        });
    },
    initTable: function () {
        var me = this;

        me.grid =  me.container.children('.-page-container').jtable({
            proxy: {
                read: me.proxy.read
            },
            offerCommonAjax: Util.baseAjax,
            filterData: function ( data ) {
                if ( data.success && data.data ) {
                    return {
                        totalCount: data.data.count,
                        data: data.data.list
                    };
                } else {
                    return {};
                }
            },
            paginationQueryKey: {
                page: 'pageCount',
                size: 'pageSize'
            },
            queryParams: function () {
                return Util.parseParam(me.getSearchFormElem().serialize());
            },
            rowNumber: true,
            pagination: true,
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
                    me.loading instanceof jQuery && me.loading.remove();
                }
            },
            gridHeader: {
                title: '发布单列表',
                icon: 'iconfont icon-mokuaiguanlierji',
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
                field: 'userId',
                width: 120,
                fixed: true,
                formatter: function ( val ) {
                    var ret = val;

                    $.each(me.allMembers, function () {
                        this.id == val && (ret = this.text);

                        return ret == val;
                    });

                    return ret;
                }
            },{
                title: '所属项目',
                field: 'projectId',
                width: 120,
                cls: 'ta-l',
                hcls: 'ta-l',
                formatter: function ( val ) {
                    var ret = val;

                    $.each(me.allProjects, function () {
                        this.id == val && (ret = this.text);

                        return ret == val;
                    });

                    return ret;
                }
            },{
                title: '标题',
                field: 'title',
                cls: 'ta-l',
                hcls: 'ta-l',
                width: 120
            },{
                title: 'Git 分支',
                field: 'gitBranch',
                width: 160
            },{
                title: '发布单状态',
                field: 'flag',
                width: 80,
                fixed: true,
                formatter: function (val, row) {
                    switch ( +val ) {
                        case -1 :
                            return '<span style="color:red" >删除</span>';
                        case 0 :
                            return '<span style="color: #FFBF2A" >未审核</span>';
                        case 1 :
                            return '<span style="color:green" >正常</span>';
                        case 2 :
                            return '<span style="color:grey" >审核未通过</span>';

                        default :
                            return val;
                    }
                }
            },{
                title: '发布状态',
                field: 'status',
                width: 80,
                formatter: function ( val ) {
                    switch ( +val ) {
                        case 0 :
                            return '<span style="color: green" >创建完成</span>';
                        case 1 :
                            return '<span style="color: darkgreen" >更新</span>';
                        case 2 :
                            return '<span style="color: orange" >编译通过</span>';
                        case 3 :
                            return '<span style="color: red" >编译失败</span>';
                        case 4 :
                            return '<span style="color: green" >同步预发成功</span>';
                        case 5 :
                            return '<span style="color: red" >同步预发失败</span>';
                        case 6 :
                            return '<span style="color: green" >重置预发成功</span>';
                        case 7 :
                            return '<span style="color: red" >重置预发失败</span>';
                        case 8 :
                            return '<span style="color: orange" >等待审核</span>';
                        //case 9 :
                        //    return '<span style="color: orange" >发布中</span>';
                        case 10 :
                            return '<span style="color: green" >线上编译成功</span>';
                        case 11 :
                            return '<span style="color: red" >线上编译失败</span>';
                        case 12 :
                            return '<span style="color: orange" >线上文件同步</span>';
                        case 13 :
                            return '<span style="color: green" >线上发布成功</span>';
                        case 15:
                            return '<span style="color: green" >发布单已完成</span>';
                        case 17:
                            return '<span style="color: red" >发布单已回滚</span>';

                        default :
                            return val;
                    }
                }
            },{
                title: '创建时间',
                field: 'gmtCreate',
                width: 140,
                fixed: true,
                formatter: function (val, row) {
                    return Util.Date.format(val,'yyyy-mm-dd hh:mm:ss');
                }
            },{
                title: '操作',
                width: 100,
                fixed: true,
                formatter: function (val, row) {
                    return '<a href="javascript:void(0)" class="h-line -operate-log-btn">操作日志</a>'
                }
            }]
        });
    }
};

mgr.init();