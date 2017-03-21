
/**
 *  根据给定的json对象生成树状menu 菜单；
 *  Need jQuery lib 1.8.3+
 *  @author Dennis.
 *  @Time 2016/06/13 11:00
 *  @version 0.2.0
 */

function MenuTree ( opts ) {
    $.extend(true, this, {
        container: null,
        // cls 添加到 menu 容器块上的样式；
        cls: '',
        subCls: '',
        /**
         * item 菜单项的全局设置；
         */
        item: {
            textCls: '',
            expandIcon: 'iconfont  icon-top',
            cls: '',
            text: '',
            icon: ''
        },
        /**
         * data {array} 数据中的每一项为一个菜单项的描述对象；
         * 需要渲染的数据，数据为一个对象；
         * {
         *    id: '',
         *    text: '',
         *    href: 'javascript:void(0);',
         *    icon: '',
         *    cls: '',
         *    textCls: '',
         *    selected: true, // 表示选中当前项；
         *    // children {array} 数据和data 配置对象一样； children 中还可以有子children;
         *    children: []
         * }
         */
        data: null,
        offerCommonAjax: $.noop,
        proxy: '',
        queryParams: false,
        /**
         * loading {boolean/object} 可选配置项，默认在请求数据之前会添加loading;
         * 设置为false 禁用loading;
         * 远程加载数据 在发送请求之前 会调用 beforeSend 方法；
         * 接收到数据之后会调用afterReceived 方法；
         * delay 创建延迟； 默认 100ms;
         */
        loading: {
            delay: 100,
            beforeSend: $.noop,
            afterReceived: $.noop
        },
        filterData: function ( data ) { return data; },
        singlePage: true,
        events: {
            onBeforeLoad: $.noop,
            onItemClick: $.noop,
            onLoadSuccess: $.noop,
            onUnselected: $.noop,
            onSelected: $.noop
        }
    }, opts);

    pMt.init( this );
}

var pMt = {
    init: function ( me ) {
        me.dataMap = {};
        me.reload( me.data );
        this.bindEvents(me);
    },
    bindEvents: function ( me ) {
        me.container.on('click.MenuTree','.'+ this.defaultCls.menuItemCls, me, function ( e ) {
            // 移除下一行，因为需要把具体的逻辑分发到对应的处理模块；
            //me.select($(this).attr('data-field'));

            var $this = $(this),
                itemData = me.getItemDataFromId($this.attr('data-field')) || {};

            if ( $.isArray(itemData.children) ) {
                $this.nextAll('.' + pMt.defaultCls.subMenuCls).slideToggle();
                $this.find('.' + pMt.defaultCls.expandIconCls).toggleClass(pMt.defaultCls.expand);
            }

            me.events.onItemClick.call(me, e , itemData);
        });
    },
    defaultCls: {
        menuItemCls: '-menu-item',
        subMenuCls: 'sub-menu',
        expandIconCls: 'menu-expand-icon',
        expand: 'icon-bottom',
        itemActive: 'active'
    },
    generateTree: function ( me ) {
        me.menuContainer = $('<div class="menu-container '+ me.cls +'" ></div>');
        me.menuContainer.html(this.getMenuHtml(me, me.data));

        me.container.html(me.menuContainer);
    },
    getMenuHtml: function ( me, data, sub ) {
        var i = 0, len,
            html = '',
            selected = false;

        if ( !($.isArray(data) && data.length) ) {
            return '';
        }

        $.each(data, function () {
            selected = !!this.selected;

            return !this.selected;
        });

        for (len = data.length; i < len; ++i) {
            html += this.generateItemHtml( me, data[i]);
        }

        return this.renderTpl(this.tpls.menuBlock, {
            // 子菜单默认都是闭合状态，如果子菜单中有选中的项则展开；
            cls: sub ? (me.subCls + ' '+ this.defaultCls.subMenuCls + (selected ?  '' : ' d-n')) : '',
            content: html
        });
    },
    unique: 1,
    generateItemHtml: function ( me, iData ) {
        var childHasSelected = false;

        // 对于没有传递ID 的项自动生成一个唯一的ID；
        iData.id = iData.id == undefined ? (+new Date).toString(36) + this.unique++ : iData.id;
        if ( iData.selected ) {
            iData.cls = (iData.cls || '') + ' ' + this.defaultCls.itemActive;
            me.selectId = iData.id;
        }
        // 创建 一个DataMap 用于存放每项的参数；
        me.dataMap[iData.id] = iData;

        /**
         * 使用递归的方式循环生成菜单项；
         * 只针对 有子菜单项的并且子菜单中有选中项的添加 展开图标；
         * 获取子菜单中有没有选中项；
         */
        $.isArray(iData.children) && $.each(iData.children, function () {
            childHasSelected = !!this.selected;

            return !this.selected;
        });
        return this.renderTpl(this.tpls.menuItem,
            $.extend(true,{}, me.item, iData, {
                menuItemCls: this.defaultCls.menuItemCls,
                href: me.singlePage ? 'javascript:void(0);' : iData.href,
                expandIcon: iData.children
                        ? this.renderTpl(this.tpls.expandIcon, {
                                expandIcon: this.defaultCls.expandIconCls + ' ' +
                                        (childHasSelected ? '' : this.defaultCls.expand) +' '+ me.item.expandIcon
                            })
                        : '',
                subMenu: this.getMenuHtml(me, iData.children, true)
            }));
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
    tpls: {
        menuBlock: '<ul class="{cls}">{content}</ul>',
        menuItem: '<li class="{cls} pos-r"><a class="{menuItemCls}" data-field="{id}" href="{href}"><i class="{icon}"></i>\
                        <span class="{textCls}">{text}</span>{expandIcon}</a>{subMenu}</li>',
        expandIcon: '<i class="{expandIcon}"></i>'

    }
};

$.extend(MenuTree.prototype, {
    getItemDataFromId: function ( id ) {
        return this.dataMap[id];
    },
    getSelectElem: function () {
        return this.container.find('li.' + pMt.defaultCls.itemActive).find('.' + pMt.defaultCls.menuItemCls)
    },
    getSelectedItemData: function () {
        return this.getItemDataFromId(this.getSelectedId());
    },
    getSelectedId: function () {
        return this.selectId;
    },
    /**
     * 通过传入对应的id 或者 选项的文本选择指定的菜单项；
     * @param k {number/string}
     */
    select: function ( it ) {
        var mItems = this.container.find('.'+ pMt.defaultCls.menuItemCls),
            fromId, itemData, k,
            target = $();

        // 通过给定一个菜单项的数据对象选择指定的菜单项；
        k = $.isPlainObject(it) ?  it.id || it.text : it;

        // 如果传入的值为undefined / null; 则直接出发unSelected/selected 事件；
        if ( k == undefined ) {
            this.unSelected();
            this.events.onSelected.call(this, $.isPlainObject(it) ? it : {});

            return ;
        }
        fromId = !!this.getItemDataFromId(k);

        mItems.each(function () {
            target = $(this);

            return !($.trim(fromId ? target.attr('data-field') : target.text()) == k);
        });

        itemData = this.getItemDataFromId(target.attr('data-field')) || {};

        if ( $.isArray(itemData.children) ) {
            target.nextAll('.' + pMt.defaultCls.subMenuCls).slideToggle();
            target.find('.' + pMt.defaultCls.expandIconCls).toggleClass(pMt.defaultCls.expand);
        } else {
            this.unSelected();
            target.parent().addClass(pMt.defaultCls.itemActive);
            this.selectId = target.attr('data-field');
            this.events.onSelected.call(this, $.isPlainObject(it) ? it : itemData);
        }
    },
    unSelected: function () {
        if ( this.getSelectedId() ) {
            this.getSelectElem().parent().removeClass(pMt.defaultCls.itemActive);
            this.selectId = undefined;
            this.events.onUnselected.call(this);
        }
    },
    /**
     * reload 重新生成菜单树；
     */
    reload: function ( data ) {
        var me = this,
            _reload = function ( d ) {
                me.data = me.filterData( d );
                me.events.onBeforeLoad.call(me);
                pMt.generateTree(me);
                /** 生成table 完成之后 调用 用户配置的onLoadSuccess;  */
                me.events.onLoadSuccess.call(me, me.data);
                clearTimeout(me.loadingTimer);
                !me.loadingTimer && me.loading.afterReceived.call(me);
                me.lockGenerate = false;
            };

        if ( !me.lockGenerate ) {
            me.lockGenerate = true;
        } else  {
            return false;
        }

        if (data) {
            _reload( data );
        } else if ( $.isFunction(me.offerCommonAjax) ) {
            me.loading && (me.loadingTimer = setTimeout(function () {
                me.loading.beforeSend.call(me);
                me.loadingTimer = undefined;
            }, me.loading.delay));

            me.getRemoteData(_reload);
        } else {
            _reload( me.data );
        }
    },
    /**
     * @private
     * 根据opts获取远程数据；
     * 请求完成调用 cb ；
     * @param cb
     */
    getRemoteData: function ( cb ) {
        var me = this,
            data = {},
            readOpts = typeof me.proxy === 'string' ? { url: me.proxy } :  me.proxy;

        if ( $.isFunction( me.queryParams ) ) {
            $.extend(data, me.queryParams());
        } else if ( $.isPlainObject( me.queryParams ) ) {
            $.extend(data, me.queryParams);
        }

        /** 如果queryParams 是字符串 */
        typeof me.queryParams === 'string' &&
        (data = me.queryParams + '&' + $.param(data));

        me.offerCommonAjax( $.extend(true,{}, readOpts, {
            data: data,
            dataType: 'json',
            complete: function ( xhr ) {
                var json = xhr.responseJSON;

                json = json || {};
                $.isFunction( cb ) && cb( json );
                if ( $.isFunction(readOpts.complete) ) {
                    return readOpts.complete.apply(this, arguments);
                }
            }
        }) );
    }
});

module.exports = MenuTree;