/**
 * Define Loading Object
 *
 * this Object require jQuery 1.7+
 * @author Dennis.li;
 * @time 2016/6/15  10:00; GMT+8
 * @version 0.2.2
 * bug fixed for auto set target height ，may be not removed!
 */
void function () {
    'use strict';
    /**
     *  Loading Object;
     *	Loading 对象快捷方便的创建使用；
     */
    function Loading ( opts ) {
        $.extend(true, this, {
            /**
             * width optional {number/string}default: 100%;
             */
            width: '100%',
            height: '100%',
            top: 0,
            /**
             * loaderContainer 是loading 内部使用的容器，不可以配置，但是可以在外部使用；
             * 建议把所有事件委托到该容器上；因为在移除loading 的时候回自动移除loaderContainer
             * 上面绑定的事件；
             */
            /** container 是用户配置对象 **/
            container: null,
            cls: '',
            /**
             * relativeTarget optional default 'self' , container will set position to 'relative'
             * you can set other values like : a DOM , a jQuery Element or a valid jquery selector;
             */
            relativeTarget: 'self',
            zIndex: 999,
            show: true,
            /**
             * optinal ,default false ,no timeout removed ;
             * unit ms;
             */
            timeout: false,
            /**
             * modal optinal {boolean/object} default: true
             *	set to false , no modal;
             *
             *  {
             *      width: '',
             *      height: '',
             *      cls: '',
             *      opacity: 60,
             *      bgColor: '#fff'
             *  }
             */
            modal: {
                width: '100%',
                height: '100%',
                cls: '',
                opacity: 60,
                bgColor: '#fff',
                touchClosed: false
            },
            /**
             * loader {object} Loading 配置项；
             */
            loader: {
                /**
                 * intervalText {array} optional default: false;
                 * expect receive an array with text item;
                 * ['.','..','...','....']
                 * automatic change the giving text;
                 */
                intervalText: false,
                // unit ms ; default: 500ms;
                interval: 500,
                text: '',
                textCls: '',
                width: 'auto',
                height: 'auto',
                /** content {string/function } if set to function ,then the function will return a content string */
                content: '',//function () {}
                cls: '',
                // align {string} optional default: 'center' you can set to 'left'/'right';
                align: 'center',
                // verticalAlign {string} optional default 'middle' other values: 'top'/'bottom'
                verticalAlign: 'middle'
            },
            events: {
                /**
                 * 在显示之后调用；
                 */
                afterShow: $.noop,
                /** 在移除之前调用 如果该方法返回false 取消移除 **/
                beforeRemove: $.noop
            }
        }, opts);

        pLoading.init(this);
    }


    /**
     * The private for Loading Object;alpha(opacity=85); BACKGROUND-COLOR: #fff
     */
    var pLoading = {
        tpls: {
            base:'<div id="{loaderId}" class="{loadingCls}" style="position:absolute;top:{top};left:0;width:{width};height:{height};display:table;z-index:{zIndex}"> {modal}\
                        <div style="display:table-cell;text-align:{align};vertical-align:{verticalAlign}; overflow: hidden; width: 100%; height: 100%">\
                            <div style="display:inline-block;width:{loaderWidth};height:{loaderHeight}" class="{cls}">{content}</div><div class="{loadTextCls} {textCls}">{text}</div>\
                        </div></div>',
            modal: '<div class="-modal" style="position:absolute;left:0;top:0;width:{width};height:{height};filter:alpha(opacity={msOpacity}); opacity:{opacity};background-color:{bgColor};z-index: -1"></div>'
        },
        renderTpl: function ( str, data ) {
            var indexMap = {};

            return str.replace(/\{[^\{\}]*\}/g, function ( m ) {
                var key = $.trim(m.slice(1,-1)),
                    renderData = data[key];

                if ($.isArray(renderData)) {
                    // 下面的判断会过滤掉 undefined 和 null;
                    indexMap[key] == undefined ? (indexMap[key] = 0) : (indexMap[key] += 1);

                    return renderData[indexMap[key]];
                } else {
                    return renderData == undefined ?  '' : renderData;
                }
            });
        },
        idStart: 0,
        generateId: function ( prefix ) {
            return (prefix || 'Loading-') + (+new Date).toString(36) + this.idStart++;
        },
        /**
         *  Loading 实例对象初始化；
         * @param me {object} 当前Loading 对象；
         */
        init: function ( me ) {
            var opts = this.packOptions(me);

            if( me.modal ) {
                opts.modal = this.renderTpl(this.tpls.modal, $.extend({}, me.modal, { opacity: me.modal.opacity/100, msOpacity: me.modal.opacity}));
            }

            this.setRelative(me);
            me.loaderContainer = $(this.renderTpl(this.tpls.base, opts));
            !me.show && me.hidden();
            $.isArray(me.loader.intervalText) && this.intervalText(me);
            me.container.append(me.loaderContainer);

            me.events.afterShow.call(me);
            me.timeout && setTimeout(function() {
                me.remove();
            }, me.timeout);

            me.modal.touchClosed && me.loaderContainer.on('click', '.-modal', function () {
                me.remove();
            })
        },
        setRelative: function ( me ) {
            var target;

            if ( me.relativeTarget === 'self' ) {
                target = me.container;
            } else {
                target = $(me.relativeTarget);
                target = target.length ? target : $('body');
            }

            me.originalCssText = target[0].style.cssText;
            me.originalHeightValue = (me.originalCssText.match(/[^-\w]height:\s*([^;]+);?/) || [])[1];
            me.originalPositionValue = target.css('position');

            (!me.originalHeightValue || me.originalHeightValue == 'auto')
            && target.css('height',target.height());
            (!me.originalPositionValue || me.originalPositionValue == 'static')
            && target.css('position','relative');

            me.relativeTarget = target;
        },
        packOptions: function ( me ) {
            me.loaderId = this.generateId();
            me.loadTextCls = this.generateId('-loading-text');
            return {
                loadTextCls: me.loadTextCls,
                loaderId: me.loaderId,
                loadingCls: me.cls,
                cls: me.loader.cls,
                top: +me.top ? me.top + 'px' : me.top ,
                width: +me.width ? me.width + 'px' : me.width ,
                height: +me.height ? me.height + 'px' : me.height,
                loaderWidth: +me.loader.width ? me.loader.width + 'px' : me.loader.width,
                loaderHeight: +me.loader.height ? me.loader.height + 'px' : me.loader.height,
                zIndex: me.zIndex,
                align: $.inArray(me.loader.align,['left','right','center']) !== -1 ? me.loader.align : 'center',
                verticalAlign: $.inArray(me.loader.verticalAlign, ['middle', 'top', 'bottom']) !== -1 ? me.loader.verticalAlign : 'middle',
                text: me.loader.text,
                textCls: me.loader.textCls,
                content: $.isFunction(me.loader.content) ? me.loader.content() : me.loader.content
            };
        },
        /** if you forget remove loading or other reason no remove the loading object
         *  after default 3mins it automatic remove itself;
         */
        safeTime: 1 * 10 * 1000,
        intervalText: function ( me ) {
            var target = me.loaderContainer.find('.'+ me.loadTextCls),
                len = me.loader.intervalText.length,
                i = 0, allTime = 0;

            me.timerInterval = setInterval(function () {
                allTime += me.loader.interval
                if ( allTime > pLoading.safeTime ) {
                    clearInterval(me.timerInterval);
                    return false;
                }
                target.text(me.loader.intervalText[i++ % len]);
            }, me.loader.interval);
        }
    };

    $.extend(Loading.prototype, {
        show: function () {
            this.loaderContainer.css('display','table');
        },
        hidden: function () {
            this.loaderContainer.css('display','none');
        },
        updateText: function ( text ) {
            this.loaderContainer.find(this.loadTextCls).text(text);
        },
        remove: function () {
            if ( this.events.beforeRemove.call(this) === false ) {
                return false;
            }


            (!this.originalHeightValue || this.originalHeightValue == 'auto')
            && this.relativeTarget.css('height', this.originalHeightValue || '');

            (!this.originalPositionValue || this.originalPositionValue == 'static')
            && this.container.css('position',this.originalPositionValue);

            clearInterval(this.timerInterval);
            this.loaderContainer.remove();
            this.loaderContainer.off();
        }
    });

    // expose to jQuery;
    $.fn.loading = function ( opts ) {
        opts = opts || {};
        opts.container = $(this);

        return new Loading(opts);
    };
}();