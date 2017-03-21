"use strict";
require('../modules/jquery.ztree.core.js');
require('../modules/jquery.ztree.excheck.js');
var mgr = {
    container: Util.getCurrentTab(),
    // allProjects 存放 所有项目的映射 map 对象；
    deployPath: location.pathname.search('/publish') !== -1 ? '/publish' : '',
    allProjects: [],
    proxy: {
        read: '/publish/apply/getUserApplys.do',
        create: '/publish/apply/add.do',
        update: '/publish/apply/edit.do',
        del: '/publish/apply/del.do',
        gitVersion: '/publish/shell/gitlog.do',
        gitBranch: '/publish/shell/gitbranch.do',
        submitPublish: '/publish/apply/submit.do',
        getProjects: '/publish/project/selectAll.do',
        getMembers: '/publish/user/selectAll.do',
        // 获取最近3天 同一git 仓库下是否有发布单未结束；
        getProjectInfo: '/publish/apply/projectInfo.do', //?projectId=1'
        getTreeFolder: '/publish/apply/ls.do'

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
        this.container.off('.PublishOrder');
    },
    bindEvents: function () {
        var me = this;

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
    toggleFieldEnable: function ( container, enable ) {
        container.find('[name]').each(function () {
            enable ? this.removeAttribute('disabled') : this.setAttribute('disabled','disabled');
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
    getGitInfo: function ( id, form ,cb ) {
        var me = this,
            finished = 0;

        Util.baseAjax({
            url: me.proxy.getProjectInfo,
            data: { projectId: id }
        }, function ( json ) {
            json = json || {};

            Util.baseSuccessTips(json, json.msg, json.msg);
            // data == 1 表示静态资源；
            if ( json.data == 1 ) {
                me.toggleFieldEnable($(form).find('.-chosen-container').slideDown(), true);
            } else if ( json.data == 0 ) {
                me.toggleFieldEnable($(form).find('.-chosen-container').slideUp(), false);
            }
        });

        Util.baseAjax({
            url: me.proxy.gitBranch,
            data: { projectId: id },
            complete: function () {
                ++finished;
                finished === 2 && $.isFunction(cb) && cb();
            }
        }, function ( json ) {
            Util.baseSuccessTips(json, '获取Git 分支成功','获取Git 分支失败，请重新选择项目');
            Util.Form.loadSelectOptions(form.gitBranch,
                $.map(json.data,function ( it ) { return { value: it.value, key: it.text}; }),
                '请选择分支');
        });

        Util.baseAjax({
            url: me.proxy.gitVersion,
            data: { projectId: id },
            complete: function () {
                ++finished;
                finished === 2 && $.isFunction(cb) && cb();
            }
        }, function ( json ) {
            Util.baseSuccessTips(json, '获取Git 版本成功','获取Git 版本失败，请重新选择项目');
            Util.Form.loadSelectOptions(form.gitVersion,
                $.map(json.data,function ( it ) { return { value: it.value, key: it.text}; }),
                '请选择版本');
        });
    },
    initChosenFile: function ( container ) {
        var me = this;

        container.on('click', '.-chosen-file', function () {
            new $.Dialog({
                header:  {
                    title: '选择指定文件',
                    icon: 'iconfont icon-file mr-5px'
                },
                content: '<div id="ztree-id" class="dialog-content text-base-red p-10px fz-14px -tree-container h-400px ztree ov-a"></div>',
                style: {
                    height: 480
                },
                custom: $('body'),
                events: {
                    onBeforeShow: function ( notyIns ) {
                        var thisDialogElem = notyIns.$bar;
                        var dir = '/';

                        $.fn.zTree.init(thisDialogElem.find('.-tree-container'), {
                            async: {
                                enable: true,
                                type: 'get',
                                url: me.proxy.getTreeFolder,
                                otherParam: {
                                    id: container.find('[name=projectId]').val(),
                                    dir: dir
                                },
                                dataFilter: function ( treeId, parentNode, childNodes) {
                                    return $.map(childNodes.data || [], function ( it ) {
                                        return {
                                            name: it.name,
                                            isParent: it.type == 'dir',
                                            pId: dir,
                                            id: dir + it.name + '/'
                                        }
                                    });
                                }
                            },
                            check: {
                                enable: true
                            },
                            data: {
                                simpleData: {
                                    enable: true,
                                    rootPId: '/'
                                }
                            },
                            callback: {
                                beforeAsync: function ( treeId, treeNode ) {
                                    if ( $.isPlainObject(treeNode) ){
                                        dir = treeNode.id;
                                        me.zTreeIns.setting.async.otherParam.dir = dir;
                                    }

                                    return true;
                                }
                            }
                        });

                        me.zTreeIns = $.fn.zTree.getZTreeObj('ztree-id');
                    }
                },
                buttons: [{
                    text: '确定',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var selectedNodes = me.zTreeIns.getCheckedNodes() || [],
                            checkAllParentsNodeMap = [],
                            ret = [];

                        $.each(selectedNodes, function () {
                            // 如果当前的节点所有子节点都已经选中 == 2；
                            // 并且当前节点的父节点已经存在；则忽略该节点
                            if ( $.inArray(this.parentTId, checkAllParentsNodeMap) !== -1 ) {
                                // 所有节点选中状态为2 的节点都放父节点MAP 对象中；
                                this.check_Child_State === 2 && checkAllParentsNodeMap.push(this.tId);

                                return true;
                            } else {
                                // 如果把下面一句放到在检测父节点之前， 那第一个父节点就获取不了数据；
                                // 所有节点选中状态为2 的节点都放父节点MAP 对象中；
                                this.check_Child_State === 2 && checkAllParentsNodeMap.push(this.tId);
                            }

                            if ( this.check_Child_State === -1 || this.check_Child_State === 2 ) {
                                if ( this.isParent ) {
                                    ret.push(this.id);
                                } else {
                                    ret.push(this.pId + this.name);
                                }
                            }
                        });

                        container.find('[name=fileList]').val(ret.join());
                        $noty.close();
                    }
                },{
                    text: '取消',
                    icon: 'iconfont icon-cancel',
                    cls: 'btn btn-danger',
                    handle: function ($noty) { $noty.close(); }
                }]
            });
        });
    },
    initTable: function () {
        var me = this;

        me.grid =  me.container.children('.-page-container').grid({
            proxy: {
                read: me.proxy.read,
                update: me.proxy.update,
                create: me.proxy.create,
                del: me.proxy.del
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
                    me.loading.remove();
                }
            },
            gridHeader: {
                title: '发布单信息列表',
                icon: 'iconfont icon-menu',
                toolbar: [{
                    icon: 'iconfont icon-refresh1',
                    handle: function (e) {
                        var grid = e.data;

                        grid.reload();
                    }
                }]
            },
            beforeCreateDialog: function ( name ) {
                var rowData = this.getSelectedData();

                if ( name === 'edit' || name === 'delete' || name === 'go-publish' ) {
                    !rowData && noty({
                        text: '请选中条数据再操作！',
                        type: 'warning',
                        layout: 'topCenter',
                        theme: 'defaultTheme',
                        timeout: 4 * 1000
                    });

                    return !!rowData;
                }
            },
            toolbar: [{
                name: 'go-publish',
                text: '去发布',
                icon: 'iconfont icon-diqu',
                handle: function ( e ) {
                    var sData = me.grid.getSelectedData();

                    if ( sData ) {
                        if ( sData.flag != 0 && sData.flag != 1 ) {
                            noty({
                                text: '改发布单的状态不能执行发布！！',
                                type: 'warning',
                                layout: 'topCenter',
                                theme: 'defaultTheme',
                                timeout: 5 * 1000
                            });

                            return false;
                        }

                        $('#main-tab-container').data('publishId', sData.id);
                        Util.menuTreeIns.select({
                            href: me.deployPath + '/assets/fragment/publish-action.html'
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
                }
            }],
            dialogOpts: [{
                name: 'add',
                header: {
                    title:  '添加发布单',
                    icon: 'iconfont icon-user mr-5px'
                },
                content: me.container.find('.-add-dialog-template'),
                events: {
                    onBeforeShow: function ( notyIns ) {
                        var container = notyIns.$bar;

                        container.on('change', 'select[name=projectId]', function () {
                            me.getGitInfo(this.value, this.form);
                        });

                        me.initChosenFile(container);
                    }
                },
                loadData: function ( form ) {
                    var  v;

                    Util.Form.loadSelectOptions(form[0].projectId,me.allProjects, '请选择项目');
                    v = form[0].projectId.value;
                    v != undefined && v !== '' && me.getGitInfo(v, form[0]);
                }
            },{
                name: 'edit',
                header: {
                    title:  '修改发布单',
                    icon: 'iconfont icon-user mr-5px'
                },
                content: me.container.find('.-edit-dialog-template'),
                events: {
                    onBeforeShow: function ( notyIns ) {
                        var container = notyIns.$bar;

                        container.on('change', 'select[name=projectId]', function () {
                            me.getGitInfo(this.value, this.form);
                        });
                    }
                },
                loadData: function ( form ) {
                    var sData = this.gridIns.getSelectedData();
                    var  v;

                    Util.Form.loadSelectOptions(form[0].projectId, me.allProjects, '请选择项目');
                    Util.Form.loadData(form, sData);
                    v = form[0].projectId.value;
                    v != undefined && v !== '' && me.getGitInfo(v, form[0] , function () {
                        Util.Form.loadData(form, sData);
                    });
                }
            },{
                name: 'delete',
                header: {
                    title:  '确认删除该发布单',
                    icon: 'iconfont icon-user mr-5px'
                }
            },{
                name: 'submit-dialog',
                header: {
                    title: '发布单上线',
                    icon: 'iconfont icon-diqu mr-5px'
                },
                content: '<div class="dialog-content text-base-red p-20px fz-14px"> ' +
                            '<i class="iconfont icon-info mr-5px"></i>' +
                            '<span>确定提交上线？</span></div>',
                style: {
                    width: 310
                },
                beforeSubmitFilterData: function ( data ) {
                    var rowData = this.gridIns.getSelectedData();

                    return {
                        id: rowData.id
                    };
                },
                buttons: [{
                    text: '确定',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var me = event.data;

                        me.submit($.type(me.proxy.submitPublish) === 'string' ? { url: me.proxy.submitPublish, type: 'post' } : me.proxy.submitPublish,
                            Util.parseParam(form.serialize()),
                            function ( json ) {
                                if ( json.success ) {
                                    me.close();
                                    me.gridIns.reload();
                                }
                            });
                    }
                },{
                    text: '取消',
                    icon: 'iconfont icon-cancel',
                    cls: 'btn btn-danger',
                    handle: function ($noty) { $noty.close(); }
                }]
            }],
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
                hcls: 'ta-l',
                cls: 'ta-l',
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
                width: 120,
                hcls: 'ta-l',
                cls: 'ta-l'
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
            }]
        });
    }
};

mgr.init();