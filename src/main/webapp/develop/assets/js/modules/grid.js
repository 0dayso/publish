/**
 * @author Dennis;
 * @time 2016/4/14
 *
 * 依赖插件；
 *      jquery.table /
 *      noty/
 *      toolbar/
 *      Util 公用方法；
 *  自带增删改查；并附带对话框；
 */
;void (function ($) {
    "use strict";

    function CRUDGrid ( opts ) {
        this.options = $.extend(true, {}, {
            /**
             *  扩展 Table 中的 proxy 配置项；
             */
            proxy: {
                read: '',
                create: '',
                update: '',
                del: ''
            }
        }, opts);

        $.Grid.call(this, this.options);

        /** 扩展配置项 */
        $.extend(true, this, {
            /**
             *  hiddenTools {array} 隐藏默认的按钮，
             *  该参数接受一个数组，数组中为要删除按钮的name ： ‘add’/'edit'/'delete'
             */
            hiddenTools: false,
            /**
             * overwriteToolbar  {array} / {object} 接受一个对象或者一个数组；
             * 重写默认工具按钮功能，根据配置项的名字（name） 重写对应项；
             */
            overwriteToolbar: false,
            /**
             * beforeCreateDialog {function} 期望接受一个function ,
             * 该方法会传递当前要创建的dialog 名称作为回调函数的第一个参数；this 指向 CRUDGrid;
             * 如果返回 false 则取消创建dialog，其它值忽略；
             */
            beforeCreateDialog: false,
            ///**
            // * overwriteDialogButton 重写对话框中按钮，根据对应对话框名重写 （name）
            // * 暂时不做隐藏默认按钮；只能通过该配置项重写按钮功能；
            // * {
            // *  name: 'add',
            // *  buttons:
            // * }
            // */
            //overwriteDialogButton: {}
        },this.options);
        pGrid.initGrid.call(this);
    }

    var pGrid = {
        /**
         *  所有diaoyong pGrid 下面 非handles 下的属性时都要绑定CRUDGrid 实例
         *  initGrid / createDialog 中的this都指向 CRUDGrid 的实例；
         */
        initGrid: function () {

        },
        defaultOpts: {
            toolbar: [{
                name: 'add',
                text: '添加',
                icon: 'iconfont icon-add',
                handle: function ( e ) {
                    e.data.createDialog('add');
                }
            },{
                name: 'edit',
                text: '修改',
                icon: 'iconfont icon-edit',
                handle: function ( e ) {
                    e.data.createDialog('edit');
                }
            },{
                name: 'delete',
                text: '删除',
                icon: 'iconfont icon-edit',
                handle: function ( e ) {
                    e.data.createDialog('delete');
                }
            }],
            dialogOpts: [{
                name: 'add',
                header: {
                    title: '添加'
                },
                content: '',
                buttons: [{
                    text: '保存继续',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var form = $noty.$bar.find('form'),
                            me = event.data;

                        if ( !form.validate('validation', { parentPositionR: false }) ) {
                           return false;
                        }

                        me.submit($.type(me.gridIns.proxy.create) === 'string' ? { url: me.gridIns.proxy.create, type: 'post' } : me.gridIns.proxy.create,
                            Util.parseParam(form.serialize()),
                            function ( json ) {
                                if ( json.success ) {
                                    form[0].reset();
                                    me.gridIns.reload();
                                }
                        });
                    }
                },{
                    text: '保存',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var form = $noty.$bar.find('form'),
                            me = event.data;

                        if ( !form.validate('validation', { parentPositionR: false }) ) {
                            return false;
                        }

                        me.submit($.type(me.gridIns.proxy.create) === 'string' ? { url: me.gridIns.proxy.create, type: 'post' } : me.gridIns.proxy.create,
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
            },{
                name: 'edit',
                header:  {
                    title: '修改'
                },
                buttons: [{
                    text: '保存',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var form = $noty.$bar.find('form'),
                            me = event.data;

                        if ( !form.validate('validation', { parentPositionR: false }) ) {
                            return false;
                        }

                        me.submit($.type(me.gridIns.proxy.update) === 'string' ? { url: me.gridIns.proxy.update, type: 'post' } : me.gridIns.proxy.update,
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
            },{
                name: 'delete',
                header:  {
                    title: '删除'
                },
                buttons: [{
                    text: '确定',
                    icon: 'iconfont icon-confirm',
                    cls: 'btn btn-success',
                    handle: function ($noty, event) {
                        var form = $noty.$bar.find('form'),
                            me = event.data;

                        if ( !form.validate('validation', { parentPositionR: false }) ) {
                            return false;
                        }

                        me.submit($.type(me.gridIns.proxy.del) === 'string' ? { url: me.gridIns.proxy.del, type: 'post' } : me.gridIns.proxy.del,
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
            }]
        },
        packOpts: function ( opts ) {
            var defaultOpts = $.extend(true, {}, pGrid.defaultOpts),
                isEqual = false;

            // 根据overwrite 中配置的名字重写对应的默认配置项；
            $.isPlainObject(opts.overwriteToolbar) && (opts.overwriteToolbar = [opts.overwriteToolbar]);
            if ( $.isArray(opts.overwriteToolbar) ) {
                $.each(opts.overwriteToolbar, function ( i, it ) {
                    $.each(defaultOpts.toolbar, function ( j ) {
                        it.name === this.name && $.extend(true, defaultOpts.toolbar[j], it)
                    });
                });
            }

            opts.toolbar = opts.toolbar ? defaultOpts.toolbar.concat(opts.toolbar) : defaultOpts.toolbar;

            /** 对于需要隐藏的元素 根据提供的名称删除掉 */
            if ( $.isArray(opts.hiddenTools) ) {
                opts.toolbar = $.grep(opts.toolbar, function (i) {
                    return $.inArray(i.name, opts.hiddenTools) === -1;
                })
            }

            /** 根据dialogOpts 中配置的名字重写对应的默认配置项；*/
            $.isPlainObject(opts.dialogOpts) && (opts.dialogOpts = [opts.dialogOpts]);
            if ( $.isArray(opts.dialogOpts) ) {
                $.each(opts.dialogOpts, function ( i, it ) {
                    isEqual = false;
                    defaultOpts.dialogOpts = $.map(defaultOpts.dialogOpts, function ( oIt ) {
                        if ( it.name === oIt.name ) {
                            isEqual = true;
                            return $.extend(true, oIt, it);
                        }
                        return oIt;
                    });

                    !isEqual && defaultOpts.dialogOpts.push(it);
                });
            }
            opts.dialogOpts = defaultOpts.dialogOpts;

            return opts;
        },
        createDialog: function ( dOpts ) {

            return new Dialog(dOpts);
        },
        showDialog: function ( name ) {
            var me = this,
                dOpts = {};

            $.each(me.dialogOpts, function () {
                if ( this.name === name ) {
                    $.extend(true, dOpts, this);

                    return false;
                }
            });

            dOpts.gridIns = me;
            dOpts.offerCommonAjax = me.offerCommonAjax;
            pGrid.createDialog(dOpts);
            // 创建一个Dialog 对象并且保存到当前对象中；
            //if ( dOpts.name ) {
            //    !me.dialogs[name]
            //        ? (me.dialogs[name] =  pGrid.createDialog.call(me, dOpts))
            //        : me.dialogs[name].show();
            //}
        },
        handles: {

        }
    };

    Util.inherit($.Grid, CRUDGrid);

    // CRUDGrid 所有公用方法接口；
    $.extend(CRUDGrid.prototype, {
        createDialog: function ( name ) {
            if ( $.isFunction(this.beforeCreateDialog) && this.beforeCreateDialog(name) === false ) {
                return false;
            }
            pGrid.showDialog.call(this, name);
        }
    });

    /**
     *  对话框对象；
     * @constructor
     */
    function Dialog ( opts ) {
        $.extend(true, this, {
            /**
             * 默认创建的Dialog 存放在Body；
             */
            container: false,
            /**
             * content 可以为一个选择器，也可以是DOM 元素 jQuery 对象；
             * content 包含的内容会被填充到Dialog 中；
             */
            content: '',
            /**
             *  Header Dialog 的头部；
             *  {string} / {Object} 可以为一个字符串，填充Dialog 的标题；
             *  如果为对象：
             *  {
             *      title: '',
             *      icon: '',
             *      toolbar: []
             *  }
             */
            header: '',
            timeout: false,
            offerCommonAjax: false,
            /**
             * beforeSubmitFilterData {function}发送请求之前过滤 发送数据；
             * 该配置项期望接收一个函数；如果dialog 有form 表单，则会去form表单的数据作为第一个参数传递给回调函数；
             * 如果没有则会传递undefined;该回调发会的值会作为发送给后台的数据参数；
             */
            beforeSubmitFilterData: false,
            /**
             * loadData 期望接受一个函数，如果传递了该函数，会在dialog 显示之前自动调用；
             * 并 会把dialog 中的form 元素作为回调函数的第一个参数传入；
             */
            loadData: false,
            style: {
                type: 'notification',
                layout: 'center',
                theme: 'defaultTheme',
                modal: true
            },
            buttons: [],
            events: {
                onSaveSuccess: $.noop,
                onBeforeShow: $.noop,
                onShow: $.noop,
                onClose: $.noop,
                afterClose: $.noop
            },
            // noty 实例
            notyIns: null
        },opts);

        pDialog.init.call(this);
    }

    var pDialog = {
        init: function () {
            var me = this,
                opts = pDialog.packOpts.call(this);

            me.notyIns = me.container ?  me.container.noty(opts) : noty(opts);
        },
        packOpts: function () {
            var me = this;

            return {
                type: me.style.type,
                modal: me.style.modal,
                layout: me.style.layout,
                theme: me.style.theme,
                header: me.header,
                text: me.text,
                template: typeof me.content === 'string' ? /^(\.|#)[-_A-Za-z][-_A-Za-z0-9]+/.test(me.content) ? $(me.content).html() : me.content
                                    : me.content instanceof jQuery ? me.content.html() : me.content.innerHTML,
                timeout: me.timeout,
                callback: {
                    onShow: function () {
                        this.$bar.addClass('dialog-base-theme');
                        me.events.onBeforeShow.call(me, this);
                        this.$bar.find('form').validate({ parentPositionR: false });
                        $.isFunction(me.loadData) && me.loadData.call(me, this.$bar.find('form'));
                    },
                    afterShow: me.events.onShow,
                    onClose: me.events.onClose,
                    afterClose: me.events.afterClose
                },
                buttons: $.map(me.buttons, function (it) {
                    return {
                        addClass: it.cls,
                        onClick: it.handle,
                        text: it.text,
                        eventData: me
                    }
                })
            };
        }
    };
    /** Dialog 暴露公共方法 */
    $.extend(Dialog.prototype, {
        close: function () {
            this.notyIns.close();
        },
        /**
         * Dialog  自带提交方法；
         * @param opts {string/object} ajax 发送请求的配置项，如果为字符串，则为请求地址；
         * @param data {string/object} 请求提交的参数，
         * @param cb   {function} 请求成功的回调函数；[status = 200]
         */
        submit: function ( opts, data, cb ) {
            var me = this;

            /** TODO: 把下面两条判断提到submit之前 在用户重写submit 之后不需要做这个判断，submit 只用于提交 */
            $.type(opts) === 'string' && (opts = { url: opts });
            $.isFunction(me.beforeSubmitFilterData) && (data = me.beforeSubmitFilterData.call(me, data));
            $.isFunction(me.offerCommonAjax) && me.offerCommonAjax($.extend(true,{
                data: data,
                dataType: 'json',
                success: function (json) {
                    json = json || {};

                    $.isFunction(cb) && cb.call(me, json);
                    // TODO: 这里暂时判断返回值的success ，标准情况应该使用xhr status 状态值来判断；
                    json.success && $.isFunction(me.events.onSaveSuccess) && me.events.onSaveSuccess.call(me, json);

                    noty({
                        text: json.msg,
                        type: json.success ? 'success' : 'error',
                        timeout: 5 * 1000,
                        layout: 'topCenter',
                        theme: 'defaultTheme'
                    });
                }
            },opts));
        }
    });

    /** 把CRUDGrid 绑定到 $ 对象*/
    $.fn.grid = function ( opts ) {
        opts.container = this;

        return new CRUDGrid( pGrid.packOpts( opts ) );
    }

    // 导出 Dialog 模块
    module.exports = Dialog;
})(jQuery);