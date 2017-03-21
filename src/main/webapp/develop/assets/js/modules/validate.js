
;void function () {
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
                rule = rules[m[1]];

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
}();
