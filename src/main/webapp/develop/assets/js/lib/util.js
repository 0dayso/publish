;void (function () {
    "use strict";

    if (window.Util || typeof Util !== "undefined") {
        return false;
    }

    var Util = {
        /** 静态变量：
         *  请求验证码的时间间隔
         *  Bops.GETCODE_INTERVAL = 30;
         *  设置为 30s
         * // Ajax 请求超时时间  临时去掉；
         */
        GETCODE_INTERVAL: 30,
        AJAX_TIMEOUT: 0,
        namespace: function (ns) {	 // 对象命名空间;
            var nsArr = ns.split("."),
                objNs = this,
                i = 0;

            for (; i < nsArr.length; ++i) {

                if (typeof objNs[nsArr[i]] === "undefined") {
                    objNs[nsArr[i]] = {};
                }

                objNs = objNs[nsArr[i]];
            }
            return objNs;
        },
        /**  公用继承方法； inherit;
         *   功能：实现子类原型继承父类，子类的构造函数指向子类；
         *   参数1： superClass {object}  父类；
         *   参数2： subclass {object}  子类
         */
        inherit: function (superClass, subclass) {
            var prototype;

            function temp () {};
            temp.prototype = superClass.prototype;
            prototype = new temp();

            prototype.constructor = subclass;
            subclass.prototype = prototype;
        },
        baseSuccessTips: function ( json, sText, fText ) {
            noty({
                text: json.msg || (json.success ?  sText || '操作成功' :  fText || '操作失败'),
                type:  json.success ? 'success' : 'error',
                layout: 'top',
                theme: 'defaultTheme',
                timeout: 3 * 1000
            });
        },
        addLoading: function ( opts ) {
            var loader = false, delay;

            return $.extend(true, {}, opts, {
                beforeSend: function () {
                    delay = true;
                    setTimeout(function () {
                        delay && (loader = (opts.loading.target ? $(opts.loading.target) : $('body')).loading({
                            zIndex: 10000003,
                            loader: {
                                cls: 'loader-circle'
                            }
                        }));
                    }, opts.loading.delay);

                    if ( $.isFunction(opts.beforeSend) ) {
                        return opts.beforeSend.apply(this,arguments);
                    }
                },
                complete: function () {
                    delay = false;
                    loader && loader.remove();
                }
            });
        },
        /**
         *
         * 简单模板渲染方法；接收一个模板字符串，根据提供的渲染对象 data，填充对应的字段；
         * 对象中的属性值为null/undefined 则会使用'' 替换；
         *
         * - 支持数组渲染；模板格式：{arr}      数据源： data.arr = [1,2,3,4]
         * - 支持数据对象嵌套 模板格式：{.subObj.text} 数据源：data.subObj.text = 'hello';
         * 注意： 模板内部的对象取值必须是和对象可连接； 上面的模板内的字符必须以 '.' 开始；
         *        也可以是 {[1].obj.test} 数据源[{obj: {test: 'test nest' }}]
         *
         * 语句格式： {% if ( true ) ..  %}
         * 变量格式： #price#
         * @param {String} str 需要渲染的字符串
         * @param {Object} data 数据对象源
         * @param {Object} [contentState] (optional) 数据对象源
         * @returns {String}
         */
        renderTpl: function ( str, data, contentState ) {
            var me = this,
                indexMap = {},
                _doStrStatement = function ( s, data ) {
                    try {
                        return (new Function('data', $.trim(s)))(data);
                    } catch ( e ) {
                        return undefined;
                    }
                },
                st = str.match(/\{%(.+)%}/);

            return st && st[1]
                ? me.renderTpl(
                    str.replace(/\{%(.+)%}/m, function ( m ) {
                        var stText = _doStrStatement(me.renderTpl(st[1], data, true), data);

                        return stText == undefined ? '' : stText;
                    }), data)
                : str.replace(/\{[^{}]*}/g, function ( m ) {
                    var key = $.trim(m.slice(1, -1)),
                        renderData = data[key],
                        variableKey = key.match(/^#(.+)#$/);

                    if ( variableKey && variableKey[1] ) {
                        return (variableKey[1].search(/[.[]/) === 0 ? 'data' : 'data.') + variableKey[1];
                    } else if ( contentState ) {
                        return m;
                    }

                    if ( renderData === undefined && key.search(/[.[]/) === 0 ) {
                        renderData = _doStrStatement('return data' + key, data);
                    }

                    if ( $.isArray(renderData) ) {
                        // 下面的判断会过滤掉 undefined 和 null;
                        indexMap[key] == undefined ? (indexMap[key] = 0) : (indexMap[key] += 1);

                        return renderData[indexMap[key]];
                    } else {
                        return renderData == undefined && !contentState ? '' : renderData;
                    }
                });
        }

        // renderTpl: function ( str, data ) {
        //     var indexMap = {};
        //
        //     return str.replace(/\{[^\{\}]*\}/g, function ( m ) {
        //         var key = $.trim(m.slice(1,-1)),
        //             renderData = data[key];
        //
        //         if ($.isArray(renderData)) {
        //             // 下面的判断会过滤掉 undefined 和 null;
        //             indexMap[key] == undefined ? (indexMap[key] = 0) : (indexMap[key] += 1);
        //
        //             return renderData[indexMap[key]];
        //         } else {
        //             return renderData == undefined ?  '' : renderData;
        //         }
        //     });
        // }
    };

    /**
     * 设置全局Ajax请求超时间；
     */
    $.ajaxSetup({
        timeout: Util.AJAX_TIMEOUT
    });

    Util.baseAjax = function ( opts,callback ) {
        if ( opts.loading ) {
            opts = $.extend(true, {
                loading: {
                    delay: 300
                }
            },opts);

            opts = Util.addLoading(opts);
        }

        return $.ajax($.extend({
            type: 'get',
            dataType: 'json',
            cache: false,
            error: function (xhr) {
                noty({
                    text: '存在异常 - Error Code: ' + xhr.status + ' &nbsp;请刷新当前页面重新操作！',
                    type: 'error',
                    layout: 'top',
                    theme: 'defaultTheme',
                    //modal: true,
                    timeout: 2 * 1000
                });
            }
        },opts,{
            success: function ( json ) {
                $.isFunction(callback) && callback(json);
                $.isFunction(opts.success) && opts.success(json);
            },
            complete: function ( xhr ) {
                var json = xhr.responseJSON;

                json = json || {};

                if ( xhr.status === 0 && xhr.state() === 'rejected' ) {
                    // 登录超时；
                    //location.reload();
                } else if ( json.code === 60004 ) {
                    // 其它需要提示信息的情况；
                    // TODO: some custom defined tip msg;
                }

                if ( $.isFunction(opts.complete) ) {
                    return opts.complete.apply(this,arguments);
                }
            }
        }));
    }


    void function ( opts ) {
        var validateSelect = ":text, [type='password'], select, textarea, [type='number'], " +
            "[type='tel'], [type='url'], [type='email'], [type='datetime'], [type='date'],  " +
            "[type='week'], [type='time'], [type='datetime-local'], [type='range'], [type='color'], " +
            "[type='search'], [type='month'], [contenteditable]",
             rules = {
                required: {
                    method: function (val) {
                        return !!val;
                    },
                    message: '该字段必填！'
                },
                phoneCN: {
                    method: function (v) {
                        v = (v + '').replace(/[\(\)\s\-]/g, '');
                        return v.length === 11 &&  /^1[3-9](\d{9})$/.test(v);
                    },
                    message: '请输入正确的手机号码'
                },
                password: {
                    method: function (v) {
                        var pwReg = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\s)(?=.*[^A-Za-z0-9]).{8,20}$/;

                        return pwReg.test(v);
                    },
                    message: '密码必须包含大小写字符、数字和特殊符号的 8 - 20个字符'
                },
                numeric: {
                    method: function (v) {
                        return !isNaN( parseFloat( v ) ) && isFinite( v );
                    },
                    message:  '请输入数字！'
                },
                 number: {
                     method: function (v) {
                         return  /^[\+\-]?\d+$/.test(v);
                     },
                     message: '请输入整数！'
                 },
                minLength: {
                    method: function (v, def) {
                        return v.length >= def[0];
                    },
                    message: '该字段长度必须大于等于 {0}'
                },
                maxLength: {
                    method: function (v, def) {
                        return v.length <= def[0];
                    },
                    message: '该字段长度必须小于等于 {0}'
                },
                 email: {
                     method: function ( v ) {
                        return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(v);
                     },
                     message: '请输入正确邮箱地址！'
                 }
            };

        function validationHandle( event ) {
            var me = $(this),
                isValidate = true,
                rule = {},
                validations = me.attr('validations');

            // 如果元素data: validateIgnore 属性 取消验证；
            if ( me.data('validateIgnore') || me.attr('disabled') || !validations) {
                setTips.call(me, isValidate);
                return isValidate;
            }

            validations = validations.replace(/\s/g, '').split(';') || [];

            $.each(validations, function (i, m) {
                // 验证方法可以传递参数，所有参数都放在[] 中；
                var params;

                m = m.match(/(\w+)(\[.+\])?/) || [];

                /** 如果内容为空 切验证规则不是required 忽略该验证规则 */
                if ( m[1] !== 'required' && me.val() === '' ) {
                    return true;
                }

                if ( rules[m[1]] ) {
                    isValidate = rules[m[1]].method.call(me, me.val(), params = eval(m[2]));
                    $.extend(true, rule, rules[m[1]]);

                    // 对于消息中有模板的替换其中的值；
                    rule.message = rule.message.replace(/\{(\d+)\}/g, function ( p ) {
                        return params[p.slice(1,-1)];
                    });
                }
                return isValidate;
            });

            setTips.call(me, isValidate, rule, event.data);

            return isValidate;
        }

        function setTips (isValidate, rule, data) {
            // 如果未通过验证，弹出提示；
            var me = this,
                tipsIns;

            tipsIns = me.get(0).toolbarIns;
            if ( !isValidate ) {
                if( tipsIns ) {
                    tipsIns.toolbar.find('.-error-msg').text(rule.message);
                    tipsIns.show()
                } else {
                    createTips.call(me, rule.message, data);
                }
            } else if ( tipsIns ) {
                me.removeClass('pressed');
                tipsIns.toolbar.off().remove();
                me.get(0).toolbarIns = null;
            }
        }

        function createTips ( msg, opts ) {
            var me = this;

            me.toolbar($.extend({
                content: '<div><a class="-error-msg ov-e fz-12px" style="color: #fff" href="javascript:void(0);">'+ msg +'</a></div>',
                position: 'right',
                style: 'danger',
                //event: 'click',
                parentPositionR: true,
                parentContainer: true
            }, opts));

            /**
             * @opts.show 自定义配置
             * 如果设置该配置项则在创建的提示的时候显示该提示；
             * 这里设置定时器，是因为toolbar 内部显示的时候使用了定时器，150ms 后会隐藏；
             * 这里设定300ms 以后再显示；
             */
            opts.show && setTimeout(function () {
                me.get(0).toolbarIns.show();
            }, 300);
        }

        function init ( opts ) {
            opts = $.extend(true, opts, { show: true });
            $(this).on( "focusin.validate focusout.validate keyup.validate",
                validateSelect, opts, validationHandle )
                // 兼容IE；
                .on( "click.validate", "select, option", opts, validationHandle );
        }

        // export interface;
        var methods = {
            /**
             * 接受一个验证对象，验证名对应 method 和 message 两个属性；
             * 可以重写以前验证规则的method 和 message;
             * @return this;
             */
            addRules: function ( addRules ) {
                $.extend(true, rules, addRules);

                return this;
            },

            /**
             * validation / enableValidation
             *  调用 validation 的可以是单个需要验证的元素，如 input;
             *  也可以是包含验证元素的数组，如 form;
             */
            validation: function ( opts ) {
                var filter = this.filter(validateSelect),
                    isValidate = true;

                !filter.length && (filter = this.find(validateSelect));
                filter.each(function (i, dom) {
                    return isValidate = validationHandle.call(dom, { data: $.extend(opts,{ show: true }) });
                });

                return isValidate;
            },
            /**
             * 开启验证只 设置 元素validateIgnore 属性，
             * 对于元素有disabled 属性的不做修改；
             */
            enableValidation: function () {
                var filter = this.filter(validateSelect);

                !filter.length && (filter = this.find(validateSelect));

                filter.each(function (i, dom) {
                    $(dom).data('validateIgnore', false);
                });

                return this;
            },
            disableValidation: function () {
                var filter = this.filter(validateSelect);

                !filter.length && (filter = this.find(validateSelect));

                filter.each(function (i, dom) {
                    $(dom).data('validateIgnore', true);
                    setTips.call($(dom), true);
                });

                return this;
            }
        };

        $.fn.validate = function ( opts ) {
            if ( typeof opts === "string" ) {
                return methods[ opts ].apply(this, [].slice.call(arguments,1));
            }

            return this.each(function (i, elem) {
                init.call(elem, opts)
            });
        };

        // 挂载在$.validate 上的静态方法；
        $.validate = {
            addRules: function ( addRules ) {
                $.extend(true, rules, addRules);

                return this;
            }
        };
    }();

    Util.parseParam = function ( str ) {
        var ret = {},
            parts = (str + "").replace(/\+/g," ").split("&"),
            len = parts.length,
            i = 0, p,
            add = function (k,v) {
                typeof ret[k] !== "undefined" ?
                    $.isArray(ret[k]) ? ret[k].push(v) : ret[k] = [ret[k],v]
                    : ret[k] = v;
            };

        for (; i < len; i++) {
            if (parts[i]) {
                p = parts[i].split("=");
                add(decodeURIComponent(p[0]),decodeURIComponent(p[1]));
            }
        }
        return ret;
    }
    /* 时间 格式解析 */
    Util.namespace('Date');
    Util.Date.format = function ( utc, f ) {
        if (!+utc) {
            return "";
        }

        var cDate = new Date(utc),
            preZero = function (num) {
                return num < 10 ? "0" + num : num;
            };

        var fy =  cDate.getFullYear(),
            m = cDate.getMonth() + 1,
            d = cDate.getDate(),
            h = cDate.getHours(),
            minutes = cDate.getMinutes(),
            sec = cDate.getSeconds();


        return f.replace(/(yyyy|yy)|(mm)|(dd)|(hh)|(\:mm)|(ss)/ig,function (site) {

            switch (site.substr(0,1)) {
                case "y" :
                    return (fy + "").substr(-site.length);
                case "m" :
                    return site.length >= 2 ? preZero(m) : m;
                case "d" :
                    return site.length >= 2 ? preZero(d) : d;
                case "h" :
                    return site.length >= 2 ? preZero(h) : h;
                case ":" :
                    return ":" + (site.length >= 2 ? preZero(minutes) : minutes);
                case "s" :
                    return site.length >= 2 ? preZero(sec) : sec;

                // 无匹配制定格式，返回元字符串；
                default:
                    return site;
            }
        });
    };

    Util.namespace('Form');
    Util.Form.loadData = function ( $targetForm, data ) {
        var form, item, i = 0;

        $targetForm instanceof jQuery
            ? (form = $targetForm[0])
            :  $.isArray($targetForm) ? $targetForm[0] : (form = $targetForm);

        while ( (item = form[i++]) ) {
            // 下面的条件会过滤掉undefined 和 null;
            data[item.name] != undefined &&
            (item.value = data[item.name]);
        }
    };

    Util.Form.loadSelectOptions = function ( select, data, dOpt ) {
        var str = dOpt != undefined ? ('<option value="">' + dOpt + '</option>') : '',
            i = 0,
            len;

        if ( $.isArray(data) && !$.isEmptyObject(data) ) {
            for ( len = data.length; i < len; ++i) {
                str += '<option value="' + data[i].value + '">' + data[i].key + '</option>'
            }
        } else {
            str += '<option value="">无数据项</option>';
        }

        $(select).html(str);
    }


    /**
     * setLocalStorage 方法；
     *  用于存储的 localStorage 方法；
     *  可以给该方法传递一个对象，该方法会把对应的值写入到本地存储；
     */
    Util.setLocalStorage = (function () {
        if (window.localStorage && (typeof window.localStorage.setItem === "function")) {
            return function (map) {
                for (var key in map) {
                    window.localStorage.setItem(key,map[key]);
                }
                return true;
            }
        } else {
            return function () {
                return false;
            }
        }
    })();

    /**	获取本地存储；lazy function;
     *  参数：key {array} / {string}
     *  返回值为一个数组；
     *  getLocalStorage @return {array} ;
     */
    Util.getLocalStorage = (function () {
        if (window.localStorage && (typeof window.localStorage.getItem === "function")) {
            return function (key) {
                var ret = [];

                if ($.isArray(key)) {
                    $.each(key,function (index,e) {
                        ret.push(window.localStorage.getItem(e));
                    });
                } else {
                    ret.push(window.localStorage.getItem(key));
                }

                return ret;
            }
        } else {
            return function () {
                return [];
            }
        }
    })();


    /**
     * 初始化 指定容器下的 tab 组件；
     * @param container {jQuery}
     * @param opts  {object}
     *  onClick 容器下的tab 触发点击事件是的回调函数；【如果当前Tab 为活动状态则不会触发该回调】
     *  onClose 容器下的tab 触发关闭事件是的回调函数；
     */

    Util.namespace('Tab');
    Util.Tab.init = function ( container, opts ) {
        container.on('click.Tab', '.tab .tab-header', function () {
            var $this = $(this),
                tabContent = $this.closest('.tab').find('.tab-body').eq($this.index());

            if ( $this.hasClass('active') ) {
                return false;
            }

            $this.addClass('active').siblings().removeClass('active');
            tabContent.removeClass('d-n').siblings().addClass('d-n');
            opts && $.isFunction(opts.onClick) && opts.onClick($this, tabContent);
        }).on('click.Tab', '.tab .tab-close', function () {
            var $this = $(this),
                th = $this.closest('.tab-header'),
                tabContent =  $this.closest('.tab').find('.tab-body').eq(th.index());

            // 触发用户自定义事件；
            opts && $.isFunction(opts.onClose) && opts.onClose(th, tabContent);

            Util.Tab.select(th.prev());
            tabContent.remove();
            th.remove();
        }).on('click.Tab', '.tab .tab-refresh', function () {
            var $this = $(this),
                th = $this.closest('.tab-header'),
                tabContent =  $this.closest('.tab').find('.tab-body').eq(th.index());

            // 触发用户自定义事件；
            opts && $.isFunction(opts.onRefresh) && opts.onRefresh(th, tabContent);
        });
    };

    Util.Tab.tpls = {
        tabBody: '<div class="tab-body p-10px d-n"></div>',
        tabItem: '<li class="fl-l tab-header"><span>{text}</span>{closeIcon}{refreshIcon}</li>',
        closeIcon: '<i class="iconfont icon-close tab-close"></i>',
        refreshIcon: '<i class="iconfont icon-refresh1 tab-refresh fz-12px-i"></i>',
    };
    Util.Tab.add = function ( container, opts, cb ) {
        var tabBody = $(this.tpls.tabBody);

        container.find('.header-container').append(Util.renderTpl(this.tpls.tabItem, {
            text: opts.text,
            closeIcon: opts.closed ? this.tpls.closeIcon : '',
            refreshIcon: opts.refresh ? this.tpls.refreshIcon : ''
        }));

        container.find('.tab-container').append(tabBody);
        opts.selected && this.select(container, 'last');
        $.isFunction(cb) && cb(tabBody);
    };

    Util.Tab.select = function ( container, target, cb ) {
        var th = $();

        $.type(target) == 'function' && (cb = target, target = null);
        if ( target === 'last' || target === 'first' ) {
            th = container.find('.tab-header')[target]().trigger('click');
        } else if ( $.isNumeric(target) ) {
            th = container.find('.tab-header').eq(target).trigger('click');
        } else if ( target == undefined ) {
            th = container.trigger('click');
        }

        $.isFunction(cb) && cb(th.closest('.tab').find('.tab-body').eq(th.index()));
    }

    Util.Tab.close = function ( container, target ) {
        var th = $(),
            tabContent;

        if ( target === 'last' || target === 'first' ) {
            th = container.find('.tab-header')[target]();
        } else if ( $.isNumeric(target) ) {
            th = container.find('.tab-header').eq(target);
        } else if ( target == undefined ) {
            th = container;
        }

        tabContent = th.closest('.tab').find('.tab-body').eq(th.index());
        Util.Tab.select(th.prev());
        tabContent.remove();
        th.remove();
    };

    /**
     * 动态生成一个new Tab 并返回新生成的Tab 容器；
     * @type
     */
    Util.generateTab = function () {
        return Util.currentTabContainer = $('<div class="h-100 ov-a -tab-'+ Math.random().toString(16).substr(2) +'"></div>');
    };
    Util.getCurrentTab = function () {
       return Util.currentTabContainer;
    };

    window.Util = Util;
})();