"use strict";
require('../modules/select2.full.js');
var mgr = {
    container: Util.getCurrentTab(),
    unSetMembers: [],
    proxy: {
        read: '/publish/apply/getApplys.do',
        audit: '/publish/apply/audit.do',
        getProjects: '/publish/project/selectAll.do?self=false',
        memberList: '/publish/user/selectAll.do'
    },
    init: function () {
        var me = this;

        me.getAllMemberData(function () {
            me.getAllProjectData(me.initTable);
        });
        me.bindEvents();
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.PublishAuditController');
    },
    bindEvents: function () {
        var me = this;

        me.container.on('click.PublishAuditController', '.-audit-btn-approve', me, function () {
            setTimeout(function() {
                me.auditAction(1);
            }, 10);
        }).on('click.PublishAuditController','.-audit-btn-refuse', me, function () {
            setTimeout(function() {
                me.auditAction(2);
            }, 10);
        });
    },
    auditAction: function ( flag ) {
        var me = this;

        Util.baseAjax({
            url: this.proxy.audit,
            type: 'post',
            data: {
                id: me.grid.getSelectedData().id,
                flag: flag
            }
        }, function ( json ) {
            Util.baseSuccessTips(json);

            me.grid.reload();
        });
    },
    getAllMemberData: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.memberList,
            complete: function () {
                $.isFunction(cb) && cb.call( me, me.allMembers );
            }
        }, function (json) {
            json = json || {};
            json.success && $.isArray(json.data) &&
            ( me.allMembers = json.data.map(function ( it) { return { id: it.value, text: it.text}; }));
        });
    },
    getAllProjectData: function ( cb ) {
        var me = this;

        Util.baseAjax({
            url: me.proxy.getProjects,
            complete: function () {
                $.isFunction(cb) && cb.call( me, me.allProjects );
            }
        }, function (json) {
            json = json || {};

            json.success
            && $.isArray(json.data)
            && (me.allProjects = json.data.map(function ( it) { return { value: it.value, key: it.text}; }));
        });
    },
    initTable: function ( members ) {
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
                    me.loading.remove();
                }
            },
            gridHeader: {
                title: '待审核发布单列表',
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
                formatter: function ( val ) {
                    var ret = val;

                    $.each(me.allProjects, function () {
                        this.value == val && (ret = this.key);

                        return ret == val;
                    });

                    return ret;
                }
            },{
                title: '标题',
                field: 'title',
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
                            return '<span style="color: orange" >等待发布</span>';
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
                title: 'Git版本',
                field: 'gitVersion',
                width: 100
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
                width: 120,
                fixed: true,
                formatter: function (val) {
                    return '<a href="javascript:void(0)" class="-audit-btn-approve mr-10px">通过</a>' +
                        '<a href="javascript:void(0)" class="-audit-btn-refuse">拒绝</a>'
                }
            }]
        });
    }
};

mgr.init();