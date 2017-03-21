"use strict";
require('../modules/select.js');
var mgr = {
    container: Util.getCurrentTab(),
    // 保存所有未被添加进项目的成员；
    unSetMembers: [],
    proxy: {
        read: '/publish/project/list.do',
        create: '/publish/project/add.do',
        update: '/publish/project/edit.do',
        delProject: '/publish/project/del.do',
        getMembersFromProject: '/publish/projectuser/getProjectUsers.do',
        editUserType: '/publish/projectuser/edit.do',
        delUserFromProject: '/publish/projectuser/del.do',
        addUserToProject: '/publish/projectuser/addBatch.do',
        validationProjectSetting: '/publish/shell/checkProject.do',
        copyProject: '/publish/project/copy.do',
        getMembers: '/publish/user/selectAll.do'
    },
    init: function () {
        var me = this;

        me.getAllMemberData(me.initTable);
        me.bindEvents();
        $.validate.addRules(
            {
                appNameRule: {
                    method: function ( val ) {
                        return !/[^a-zA-Z.\-_]/.test(val);
                    },
                    message: '只能填写a-z A-Z -_ .这些字符'
                }
            });
    },
    destroy: function () {
        this.grid.destroy();
        this.container.off('.ProjectController');
    },
    bindEvents: function () {
        var me = this;

        me.container.on(
            'click.ProjectController', '.-edit-btn', me, function () {
                me.grid.container.find('.-toolbar-edit-btn').trigger('click');
            });


        me.container.on(
            'click.ProjectController', '.-ser-btn', function () {
                me.grid.reload();
            });
    },
    getSearchFormElem: function () {
        var e = this.container.find('.-search-form');

        this.getSearchFormElem = function () {
            return e;
        }

        return e;
    },
    getAllMemberData: function ( cb ) {
        var me = this;

        Util.baseAjax(
            {
                url: me.proxy.getMembers,
                complete: function () {
                    $.isFunction(cb) && cb.call(me, me.allMembers);
                }
            }, function ( json ) {
                json = json || {};
                json.success && $.isArray(json.data) &&
                ( me.allMembers = json.data.map(
                    function ( it ) {
                        return { id: it.value, text: it.text };
                    }));
            });
    },
    setProjectDialogShow: function ( target, type ) {
        // value == 0  JAVA 资源发布； 开启日志字段；
        // value == 1 前端资源发布；禁用日志字段；
        if ( type == 0 ) {
            target.removeClass('disabled-field').find('textarea').removeAttr('disabled')
        } else if ( type == 1 ) {
            target.addClass('disabled-field').find('textarea').attr('disabled', true);
        }
    },
    initNewProjectDialog: function ( container ) {
        var me            = this,
            javaFieldElem = container.find('.-java-project');

        container.on(
            'change.ProjectController', '.-project-type', function () {
                me.setProjectDialogShow(javaFieldElem, this.value);
            });
    },
    loadSelect2: function ( form ) {
        var me = this;

        if ( me.allMembers ) {
            form.find('.-select-m').select2(
                {
                    placeholder: {
                        id: '',
                        text: '请选择'
                    },
                    tag: true,
                    data: me.allMembers
                });
        } else {
            me.getAllMemberData(
                function ( data ) {
                    form.find('.-select-m').select2(
                        {
                            placeholder: {
                                id: '',
                                text: '请选择'
                            },
                            tag: true,
                            data: data
                        });
                });
        }
    },
    initTable: function ( members ) {
        var me = this;

        me.grid = me.container.children('.-page-container').grid(
            {
                proxy: {
                    read: me.proxy.read,
                    create: me.proxy.create,
                    update: me.proxy.update,
                    del: me.proxy.delProject
                },
                offerCommonAjax: Util.baseAjax,
                queryParams: function () {
                    return Util.parseParam(me.getSearchFormElem().serialize());
                },
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

                toolbar: [{
                    name: 'addMembers',
                    text: '添加成员',
                    icon: 'iconfont icon-add',
                    handle: function ( e ) {
                        e.data.createDialog('addMembers');
                    }
                }, {
                    name: 'validation',
                    text: '检测项目配置',
                    icon: 'iconfont icon-confirm1',
                    handle: function ( e ) {
                        e.data.createDialog('validation');
                    }
                }, {
                    name: 'copy',
                    text: '复制项目',
                    icon: 'iconfont icon-add01',
                    handle: function () {
                        var sData = me.grid.getSelectedData();

                        if ( !sData ) {
                            noty(
                                {
                                    text: '请选中条数据再操作！',
                                    type: 'warning',
                                    layout: 'topCenter',
                                    theme: 'defaultTheme',
                                    timeout: 4 * 1000
                                });

                            return false;
                        }

                        Util.baseAjax(
                            {
                                url: me.proxy.copyProject,
                                data: { id: sData.id }
                            }, function ( json ) {
                                Util.baseSuccessTips(json, '复制成功', '复制失败');
                                json.success && me.grid.reload();
                            });
                    }
                }],
                paginationQueryKey: {
                    page: 'pageCount',
                    size: 'pageSize'
                },
                pagination: true,
                loading: {
                    delay: 500,
                    beforeSend: function () {
                        var grid = this;

                        me.loading = grid.tableBodyContainer.loading(
                            {
                                loader: {
                                    intervalText: ['加载中 . ', '加载中 . . ', '加载中 . . . '],
                                    width: 10,
                                    height: 10,
                                    cls: 'loader-circle',
                                    textCls: 'fz-14px fw-b',
                                    text: '加载中...'
                                }
                            });
                    },
                    afterReceived: function () {
                        me.loading.remove();
                    }
                },
                gridHeader: {
                    title: '项目信息列表',
                    icon: 'iconfont icon-mokuaiguanlierji',
                    toolbar: [{
                        icon: 'iconfont icon-refresh1',
                        handle: function ( e ) {
                            var grid = e.data;

                            grid.reload();
                        }
                    }]
                },
                beforeCreateDialog: function ( name ) {
                    var rowData = this.getSelectedData();

                    if ( name === 'edit' || name === 'delete'
                        || name === 'addMembers' || name === 'validation' ) {
                        !rowData && noty(
                            {
                                text: '请选中条数据再操作！',
                                type: 'warning',
                                layout: 'topCenter',
                                theme: 'defaultTheme',
                                timeout: 4 * 1000
                            });

                        return !!rowData;
                    }
                },
                dialogOpts: [{
                    name: 'add',
                    style: {
                        width: '80%',
                        height: '95%'
                    },
                    header: {
                        title: '创建新项目',
                        icon: 'iconfont icon-user mr-5px'
                    },
                    content: me.container.find('.-add-dialog-template'),
                    events: {
                        onBeforeShow: function ( notyIns ) {
                            var container = notyIns.$bar;

                            me.initNewProjectDialog(container);
                        }
                    },
                    loadData: function ( form ) {
                        me.loadSelect2(form);
                    },
                    beforeSubmitFilterData: function ( data ) {

                        return data;
                    }
                }, {
                    name: 'delete',
                    header: {
                        title: '确认删除项目',
                        icon: 'iconfont icon-user mr-5px'
                    }
                }, {
                    name: 'edit',
                    style: {
                        width: '80%',
                        height: '80%'
                    },
                    header: {
                        title: '修改项目',
                        icon: 'iconfont icon-user mr-5px'
                    },
                    content: me.container.find('.-edit-dialog-template'),
                    events: {
                        onBeforeShow: function ( notyIns ) {
                            var container = notyIns.$bar;

                            me.initNewProjectDialog(container);
                        }
                    },
                    loadData: function ( form ) {
                        var sData = this.gridIns.getSelectedData() || {};

                        me.loadSelect2(form);
                        Util.Form.loadData(form, sData);
                        form.find('.-select-m').trigger('change');
                        me.setProjectDialogShow(form.find('.-java-project'), sData.type);
                    }
                }, {
                    name: 'addMembers',
                    header: {
                        title: '添加新成员',
                        icon: 'iconfont icon-user mr-5px'
                    },
                    style: {
                        width: '600',
                        height: '60%'
                    },
                    buttons: [{
                        text: '关闭',
                        icon: 'iconfont icon-cancel',
                        cls: 'btn btn-danger',
                        handle: function ( $noty ) {
                            $noty.close();
                        }
                    }],
                    content: me.container.find('.-add-member-dialog'),
                    events: {
                        onBeforeShow: function ( notyIns ) {
                            var container = notyIns.$bar;

                            me.initMembersTable(container, me.grid.getSelectedData().id);

                            container.on(
                                'click.ProjectController', '#add-member', function () {
                                    var v = container.find('.-select-m').val();

                                    v && me.addMembersToProject(
                                        {
                                            userIds: v,
                                            projectId: me.grid.getSelectedData().id
                                        });
                                }).on(
                                'click.ProjectController', '.del-member', function () {
                                    me.delUserFromProject(
                                        {
                                            userId: me.membersGrid.getSelectedData().userId,
                                            projectId: me.grid.getSelectedData().id
                                        });
                                });

                            /** 初始化 选择框 **/
                            container.find('.-select-m').select2(
                                {
                                    placeholder: {
                                        id: '',
                                        text: '请选择项目人员'
                                    },
                                    //allowClear: true,
                                    tags: true,
                                    data: me.allMembers
                                });
                        }
                    },
                    loadData: function ( form ) {
                    },
                    beforeSubmitFilterData: function ( data ) {
                        return data;
                    }
                }, {
                    name: 'validation',
                    header: {
                        title: '检测项目配置',
                        icon: 'iconfont icon-user mr-5px'
                    },
                    content: me.container.find('.-validation-dialog'),
                    buttons: [{
                        text: '关闭',
                        icon: 'iconfont icon-cancel',
                        cls: 'btn btn-danger',
                        handle: function ( $noty ) {
                            $noty.close();
                        }
                    }],
                    events: {
                        onBeforeShow: function ( $noty ) {
                            // 发送请求检测项目配置是否符合要求；
                            Util.baseAjax(
                                {
                                    loading: {
                                        target: $noty.$bar.find('.dialog-content')
                                    },
                                    url: me.proxy.validationProjectSetting,
                                    data: { projectId: this.gridIns.getSelectedData().id }
                                }, function ( json ) {
                                    $noty.$bar.find('.dialog-content')
                                         .append(
                                             '<div class="h-100 p-10px cor-w ' +
                                             (json.success ? 'bg-success' : 'bg-error')
                                             + '">' + json.msg + '</div>');
                                });
                        }
                    },
                    beforeSubmitFilterData: function () {
                        var rowData = this.gridIns.getSelectedData();

                        return {
                            id: rowData.id
                        };
                    }
                }],
                columns: [{
                    title: '工程名',
                    field: 'name',
                    width: 60,
                    hcls: 'ta-l',
                    cls: 'ta-l'
                }, {
                    title: '标题',
                    field: 'title',
                    width: 60,
                    hcls: 'ta-l',
                    cls: 'ta-l'
                }, {
                    title: '环境',
                    field: 'env',
                    width: 40,
                    fixed: true,
                    formatter: function ( val ) {
                        if ( val === -1 ) {
                            return '<span style="color:green" >讯盟</span>';
                        } else if ( val === 0 ) {
                            return '<span style="color:maroon" >优办</span>';
                        } else if ( val === 1 ) {
                            return '<span style="color:red" >彩云</span>';
                        } else if ( val === 2 ) {
                            return '<span style="color:blue" >麻绳</span>';
                        }
                    }
                }, {
                    title: '资源类型',
                    field: 'type',
                    width: 80,
                    fixed: true,
                    formatter: function ( val ) {
                        if ( val === 1 ) {
                            return '<span style="color:green" >静态资源</span>';
                        } else if ( val === 0 ) {
                            return '<span style="color:maroon" >Java</span>';
                        }
                    }
                }, {
                    title: '描述',
                    field: 'descr',
                    width: 150,
                    hcls: 'ta-l',
                    cls: 'ta-l'
                }, {
                    title: 'Git',
                    field: 'git',
                    width: 100
                }, {
                    title: '开发负责人',
                    field: 'ownerId',
                    width: 80,
                    fixed: true,
                    formatter: function ( val ) {
                        var ret = '';

                        $.each(
                            members, function ( i, n ) {
                                if ( n.id == val ) {
                                    ret = n.text;

                                    return false;
                                }
                            });

                        return ret ? ret : val;
                    }
                }, {
                    title: '测试负责人',
                    field: 'testId',
                    width: 80,
                    fixed: true,
                    formatter: function ( val ) {
                        var ret = '';

                        $.each(
                            members, function ( i, n ) {
                                if ( n.id == val ) {
                                    ret = n.text;

                                    return false;
                                }
                            });

                        return ret ? ret : val;
                    }
                }]
            });
    },
    renderSplitBtn: function ( text, data ) {
        var fillList = function ( d ) {
            var listHtml = '<ul class="pos-a list d-n">';

            $.each(
                d, function ( i, n ) {
                    listHtml += '<li data-val="' + n.value + '">' + n.text + '</li>'
                });

            return listHtml + '</ul>';
        };

        return '<div class="pos-r d-ib split-btn">' +
            '<div class="split-btn-body pos-r d-ib">' +
            '<span class="split-text d-ib">' + text + '</span>' +
            '<i class="arrow d-ib"></i>' +
            '</div>' + fillList(data) +
            '</div>';
    },
    delUserFromProject: function ( data ) {
        var me = this;

        Util.baseAjax(
            {
                url: this.proxy.delUserFromProject,
                data: data
            }, function ( json ) {
                Util.baseSuccessTips(json, '删除用户成功', '删除用户失败');
                json.success && me.membersGrid.reload();
            });
    },
    changeUserType: function ( data, cb ) {
        var me = this;

        Util.baseAjax(
            {
                url: me.proxy.editUserType,
                type: 'post',
                data: data
            }, function ( json ) {
                Util.baseSuccessTips(json, '修改用户类型成功', '修改用户类型失败');
                $.isFunction(cb) && cb(json.success);
            });
    },
    addMembersToProject: function ( data ) {
        var me = this;

        Util.baseAjax(
            {
                url: this.proxy.addUserToProject,
                data: data,
                type: 'post'
            }, function ( json ) {
                Util.baseSuccessTips(json, '添加成功', '添加失败');

                json.success && me.membersGrid.reload();
            });
    },
    initMembersTable: function ( container, id ) {
        var me = this;

        me.membersGrid = container.find('.-m-grid-container').jtable(
            {
                proxy: {
                    read: me.proxy.getMembersFromProject
                },
                queryParams: {
                    projectId: id
                },
                offerCommonAjax: Util.baseAjax,
                rowNumbers: true,
                height: 180,
                fixedRowHeight: false,
                filterData: function ( data ) {
                    if ( data.success && data.data ) {
                        return {
                            data: data.data
                        };
                    } else {
                        return {};
                    }
                },
                events: {
                    onLoadSuccess: function ( data ) {
                        //var membersArr = $.map(data.data, function ( n, i ) {
                        //    return n.userId;
                        //});

                        // 过滤掉所有已经添加的人员；更新添加select 列表；
                        //me.unSetMembers = $.grep(me.allMembers, function ( n, i) {
                        //    return $.inArray( n.id,membersArr) === -1;
                        //});

                        //container.find('.-select-m').select2({
                        //    data:  $.map( me.allMembers, function ( n, i ) {
                        //        $.inArray(n.id, membersArr) !== -1 ? (n.disabled = true) : (n.disabled = false);
                        //
                        //        return n;
                        //    })
                        //}).select2();

                        // init split-btn action;
                        container.find('.split-btn').on(
                            'click', '.arrow', function () {
                                $(this).parents('.split-btn').find('.list').toggleClass('d-n');
                            }).on(
                            'click', '.list li', function () {
                                var $this        = $(this),
                                    originalText = $this.text();

                                $this.parent().addClass('d-n');
                                $this.parents('.split-btn').find('.split-text').text($this.text());

                                me.changeUserType(
                                    {
                                        type: $this.attr('data-val'),
                                        id: me.membersGrid.getSelectedData().id
                                    }, function ( success ) {
                                        !success && $this.text(originalText);
                                    });
                            });
                    }
                },
                columns: [{
                    title: '名字',
                    field: 'userId',
                    formatter: function ( val ) {
                        var ret = '';

                        $.each(
                            me.allMembers, function ( i, n ) {
                                if ( n.id == val ) {
                                    ret = n.text;

                                    return false;
                                }
                            });

                        return ret ? ret : val;
                    }
                }, {
                    title: '类型',
                    field: 'type',
                    formatter: function ( val ) {
                        var dataMap = [{
                            text: '用户',
                            value: 0
                        }, {
                            text: '管理员',
                            value: 1
                        }];

                        return me.renderSplitBtn(val == 0 ? '用户' : val == 1 ? '管理员' : '超级管理员', dataMap);
                    }
                }, {
                    title: '操作',
                    formatter: function () {
                        return '<i class="del-member iconfont icon-delete cur-p" title="删除" ></i>';
                    }
                }]
            });
    }
};

mgr.init();