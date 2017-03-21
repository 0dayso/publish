"use strict";
var mgr = {
    container: Util.getCurrentTab(),
    proxy: {
        read:       '/publish/user/list.do',
        update:     '/publish/user/edit.do'
    },
    init: function () {
        var me = this;

        me.initTable();
        me.bindEvents();
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.UserController');
    },
    bindEvents: function () {
        var me = this;


            me.container.on('click.UserController', '.-edit-btn', me, function () {
                setTimeout(function () {
                    me.grid.container.find('.-toolbar-edit-btn').trigger('click');
                }, 100);
            });
    },
    initTable: function () {
        var me = this;

        me.grid =  me.container.children('.-page-container').grid({
            proxy: {
                read: me.proxy.read,
                update: me.proxy.update
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
            hiddenTools: ['delete','add'],
            paginationQueryKey: {
                page: 'pageCount',
                size: 'pageSize'
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
                title: '用户信息列表',
                icon: 'iconfont icon-mokuaiguanlierji',
                toolbar: [{
                    icon: 'iconfont icon-refresh1',
                    handle: function (e) {
                        var grid = e.data;

                        grid.reload();
                    }
                }]
            },
            overwriteToolbar: [{
                name: 'edit',
                cls: 'd-n -toolbar-edit-btn'
            }],
            dialogOpts: [{
                name: 'edit',
                header: {
                    title:  '修改用户',
                    icon: 'iconfont icon-user mr-5px'
                },
                content: me.container.find('.-edit-dialog-template'),
                loadData: function ( form ) {
                    var sData = this.gridIns.getSelectedData();

                    form.find('.-user-name').val(sData.name);
                    Util.Form.loadData(form, sData);
                }
            }],
            columns: [{
                title: '用户名',
                field: 'name',
                width: '100'
            },{
                title: '角色',
                field: 'role',
                width: '100'
            },{
                title: '手机号',
                field: 'mobile',
                width: '100'
            },{
                title: '邮箱',
                field: 'mail',
                width: '200'
            },{
                title: '状态',
                field: 'flag',
                width: 60,
                formatter: function (val, row) {
                    if ( val === -1 ) {
                        return '<span style="color:red" >删除</span>';
                    } else if ( val === 1 ) {
                        return '<span style="color:green" >正常</span>';
                    } else if ( val === 0 ) {
                        return '<span style="color:grey" >禁用</span>';
                    }
                }
            }
                ,{
                title: '用户类型',
                field: 'type',
                width: '100',
                formatter: function (val, row) {
                    switch ( +val ) {
                        case 0 :
                            return '<span style="color: #777">用户</span>';
                        case 1 :
                            return '<span style="color: darkorange">管理员</span>';
                        case 2 :
                            return '<span style="color: green">超级管理员</span>';

                        default:
                            return val;
                    }
                }
            },{
                title: '创建时间',
                field: 'gmtCreate',
                width: '120',
                formatter: function (val, row) {
                    return Util.Date.format(val,'yyyy-mm-dd hh:mm:ss');
                }
            },{
                title: '操作',
                width: 100,
                formatter: function (val, row) {
                    return '<a href="javascript:void(0)" class="h-line -edit-btn">修改</a>'
                }
            }]
        });
    }
};

mgr.init();