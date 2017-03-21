
/**
 * Select plugin
 * This plugin need jQuery 1.7+
 * @author Dennis;
 * @time 2016/04/27 16:18
 */

void function () {
    function Select ( opts ) {
        $.extend(true, this, {
            target: null,
            defaultValue: null,
            disabled: false,
            tag: false,
            editable: false,
            multiple: false,
            cls: '',
            iconCls: '',
            queryParams: false,
            proxy: '',
            loading: {
                delay: 100,
                beforeSend: $.noop,
                afterReceived: $.noop
            },
            arrow: true,
            offerCommonAjax: null,
            filterData: function ( data ) { return data; },
            /**
             *  panel 只生成一次 当select 获取焦点的时候动态显示隐藏；
             */
            panel: {
                cls: '',
                groupCls: '',
                descCls: '',
                itemCls: '',
                width: 'auto'
            },
            /**
             * 该方法接受选项中前一项 数据对象 { key: '', value: '' ...}
             * 如果前一项排在前面 返回一个负数，
             * 如果希望前一项排在后面， 返回一个正数；
             * @param pre
             * @param next
             */
            sortBy: function ( pre,next ) {
                return pre.text.localeCompare(next.text)
            },
            filterItem: function ( data ) {

            },
            events: {
                onBeforeLoad: $.noop,
                onFocus: $.noop,
                onBlur: $.noop,
                onSelect: $.noop,
                onLoadSuccess: $.noop
            }
        }, opts);

        pSelect.init(this);
    }

    var pSelect = {
        init: function ( me ) {
            me.defaultCls = {
                dSId: '-DS' + (+new Date).toString(36) + this.unique++,
                dSCls: 'DS',
                lineWrap: 'ds-line-wrap',
                lineIconContainer: 'ds' + (-new Date).toString(36) + this.unique++,
                dsShowLine: 'ds-out-show-line',
                arrowIcon: 'ds-arrow-icon',
                showIcon: 'ds-show-icon',
                textContainer: 'ds-text',
                itemCls: 'ds-option',
                itemTextCls: 'ds-item-text',
                icon: 'ds-itemIcon',
                panelCls: 'ds-panel',
                groupCls: 'ds-group',
                groupItemCls: 'ds-group-item',
                descCls: 'ds-desc'
            };

            me.target.addClass('d-n');
            this.generateSelect(me);
            pSelect.bindEvents( me )
        },
        tpls: {
            select: '<div id="{dSId}" class="{cls}" style="position:relative;"><div class="{lineWrap} w-100 h-100" style="position: relative;overflow: hidden">' +
            '<div class="{dsShowLine} pos-a z-1" style="left:0"><i class="{icon}"></i><span class="{textContainer}"></span></div>{iconBlock}' +
            '</div>{panelContent}</div>',
            showIconContainer: '<div class="{lineIconContainer} pos-a z-99" style="right:0">{arrowContent}{iconContent}</div>',
            options: '<li class="{itemCls}" data-item="{index}" data-field="{dataField}">{desc}<div class="pos-r">' +
            '<a href="javascript:;" class=""><i class="{optionIcon}"></i><span class="{itemTextCls}">{text}</span></a></div></li>',
            panel: '<ul class="{panelCls}" style="display:none;position:absolute;">{options}</ul>',
            group: '<div class="{groupCls}"><div class="{groupItemCls}">{groupTitle}</div>{groupItems}</div>',
            desc: '<div class="{descCls}">{descText}</div>',
            ipt: '<input type="text" style="border:none;background: none; margin:0;padding:0; width: 100%;' +
            'height: 100%;display:inline-block;color:#000; background-image: url(data:image/png;base64,)\\9;" />',
            arrowContent: '<i class="{arrowIcon}"></i>',
            icon: '<i class="{iconCls}"></i>'
        },
        generateSelect: function ( me ) {
            me.selectContainer = $(this.generateSelectHtml(me));
            me.panelContainer = me.selectContainer.find('.'+me.defaultCls.panelCls);
            me.textContainer = me.selectContainer.find('.'+me.defaultCls.textContainer);
            me.targetInput = $(this.tpls.ipt);
            me.dataFieldInput = $('<input type="hidden" name="'+ me.target.attr('name') +'"/>');
            me.textContainer.after(me.targetInput);
            me.target.removeAttr('name');
            me.target.before(me.dataFieldInput);

            me.defaultValue = me.defaultValue || me.target.val();
            me.defaultValue && me.select();
            me.disabled && me.disable();
            !me.editable && me.targetInput.attr('readonly','true')

            me.target.after(me.selectContainer);
            me.reload(me.data);
        },
        generateSelectHtml: function ( me ) {
            var arrHtml = me.arrow ? this.renderTpl(this.tpls.arrowContent, { arrowIcon: me.defaultCls.arrowContent }) : undefined;
            var iconHtml = me.iconCls ? this.renderTpl(this.tpls.icon, { iconCls: me.iconCls }) : undefined;
            var iconContent = arrHtml || iconHtml
                                ?  this.renderTpl(this.tpls.showIconContainer,{
                                        lineIconContainer: me.defaultCls.lineIconContainer,
                                        arrowContent: arrHtml,
                                        iconContent: iconHtml
                                    })
                                : undefined;
            return this.renderTpl(this.tpls.select, {
                dSId: me.defaultCls.dSId,
                dsShowLine: me.defaultCls.dsShowLine,
                cls: me.defaultCls.dSCls + me.cls,
                icon: me.defaultCls.icon,
                iconBlock: iconContent,
                textContainer: me.defaultCls.textContainer,
                panelContent: this.generatePanelHtml(me)
            })
        },
        generatePanelHtml: function ( me ) {
            return this.renderTpl(this.tpls.panel, {
                panelCls: me.defaultCls.panelCls + ' ' +me.panel.cls,
                options: this.generateGroupHtml(me, me.data)
            })
        },
        generateGroupHtml: function ( me, data ) {
            var html = '',
                i = 0, len;

            if ( !($.isArray(data) && (len = data.length)) ) {
                return this.renderTpl(this.tpls.options, {
                    itemCls: me.defaultCls.itemCls +' '+ me.panel.itemCls,
                    itemTextCls: me.defaultCls.itemTextCls,
                    text: '无数据'
                })
            }

            for (; i < len; ++i) {
                html += $.isPlainObject(data.group)
                        ? this.renderTpl(this.tpls.group, {
                                groupCls: me.defaultCls.groupCls +' '+ me.panel.groupCls,
                                groupItemCls: me.defaultCls.groupItemCls,
                                groupTitle: data[i].group.title,
                                groupItems: this.generateOptionsHtml(me, data[i].group.data)
                            })
                        : this.generateOptionsHtml(me, [data[i]]);
            }

            return html;
        },
        generateOptionsHtml: function ( me, data ) {
            var html = '',
                i = 0, len;

            if ( !($.isArray(data) && (len = data.length)) ) {
                return '';
            }

            for (; i < len; ++i) {
                html += this.renderTpl(this.tpls.options, {
                            itemCls: me.defaultCls.itemCls + ' '+ me.panel.itemCls,
                            text: data[i].text,
                            itemTextCls: me.defaultCls.itemTextCls,
                            dataField: data[i].value,
                            index: i,
                            optionIcon: data[i].icon,
                            desc: data[i].desc == undefined ? this.renderTpl(this.tpls.desc, { descCls: me.panel.descCls, descText: data[i].desc}) : ''
                        });
            }

            return html;
        },
        bindEvents: function ( me ) {
            var isItemClicked = false,
                safeTime = 200,  ms = 0,
                /** 让blur 失焦延迟到点击完成； */
                bulrHandle = function ( e ) {
                    setTimeout(function() {
                        if ( isItemClicked || ms > safeTime) {
                            isItemClicked = false;
                            ms = 0;
                            me.events.onBlur.call(me.selectContainer, e);
                            !me.multiple && me.panelContainer.css('display','none');
                        } else {
                            ms += 50;
                            bulrHandle(e);
                        }
                    }, 50);

                    return false;
                };

            me.selectContainer.on('click.DSelect', me, function (e) {
                if ( me.disabled ) {
                    return false;
                }

                me.panelContainer.css('display','block');
                me.targetInput.focus();

                return false;
            }).on('click.DSelect','.'+ me.defaultCls.itemCls,function () {
                // 选中值填充到DS 中；
                var $this = $(this);

                isItemClicked = true;
                //me.textContainer.text($this.find('.'+ me.defaultCls.itemTextCls).text());
                me.targetInput.val($this.find('.'+ me.defaultCls.itemTextCls).text());
                me.dataFieldInput.val($this.attr('data-field'));
                me.multiple ? $this.addClass('disabled') : me.panelContainer.css('display','none');

                return false;
            }).on({
            	'blur.DSelect': bulrHandle,
	            'focus.DSelect': function (e) {
	            	me.events.onFocus.call(me.selectContainer, e);

                    return false;
	            },
	            'keyup.DSelect': function ( e ) {
	            	// 处理 target input 中的内容转入到select 中；
                    //me.selectContainer.find('.'+me.defaultCls.textContainer)[0].childNodes
                    if ( !me.editable ) {
                        return false;
                    }
                    var v = this.value;

                    //var oText = me.textContainer.text();
                    //
                    //if ( e.keyCode === 8 ) {
                    //    oText = oText.slice(0, -1);
                    //} else {
                    //    oText += this.value;
                    //    this.value = '';
                    //}
                    //
                    //me.textContainer.text(oText);

                    window.clearTimeout(me.updateTimer);
                    me.updateTimer = setTimeout(function () {
                        me.update(me.data, v);
                    }, 50);

                    return false
                }
        	}, 'input');

        },
        unique: 1,
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
        },
        filterDataArr: function ( data, f ) {
            if ( !($.isArray(data) && data.length && f != undefined && f != '' ) ) {
                return data;
            }
            var i = 0, len = data.length, ret = [];

            for(; i < len; ++i) {
                data[i].text.toLowerCase().indexOf(f.toLowerCase()) !== -1 && ret.push(data[i]);
            }

            return ret;
        },
        sortData: function ( me, data, f ) {
            if ( !($.isArray(data) && data.length) ) {
                return data;
            }
            var i = 0, len = data.length,
                groupArr = [],
                kVArr = [];

            for (; i < len; ++i) {
                $.isPlainObject(data[i].group) && $.isArray(data[i].group.data)
                    ? groupArr.push(this.filterDataArr(data[i].group.data, f).sort(me.sortBy))
                    : kVArr.push(data[i]);
            }

            return groupArr.concat(this.filterDataArr(kVArr, f).sort(me.sortBy));
        },
        resizeShowLine: function ( me ) {
            var outW = me.selectContainer.children(this.defaultCls.lineWrap).width();
            var iW = me.selectContainer.find('.'+ this.defaultCls.lineIconContainer).width();
            var showBlock = me.selectContainer.find('.'+ this.defaultCls.dsShowLine);
            var ml = outW - iW - showBlock.width();

            // TODO://
            ml < 0 && showBlock.css('left',ml);
        }
    };

    $.extend(Select.prototype, {
        doLayout: function () {
            var me = this,
                dsHig = parseFloat(me.selectContainer.height()),
                outerH = parseFloat(me.selectContainer.outerHeight(true)),
                dsMinH = outerH > 0 ? outerH : parseFloat(me.selectContainer.css('min-height'));

            // 计算target 元素和模型宽高；
           me.panelContainer.css({
               // -1 panel 有1px 的宽度； 后面做定位上下的时候在动态计算具体边线宽度；
               top: dsMinH - 1 - parseFloat(me.selectContainer.css('border-bottom-width')),
               left: '-1px'
           });
            me.selectContainer.css('height',dsHig > 0 ? dsHig : dsMinH);
            //
            //me.panelContainer.find('li.'+ me.defaultCls.itemCls)
            //    .css('padding-left',me.selectContainer.find('.'+me.defaultCls.dsShowLine).position().left);
        },
        getValue: function () {
            return this.dataFieldInput.val();
        },
        getSelectedItemData: function () {
            return this.data && this.data[this.panelContainer.find('li.selected').attr('data-item')];
        },
        disable: function () {
            var me = this;

            me.targetInput.attr('disabled',true);
            me.selectContainer.addClass('disabled');
            me.disabled = true;
        },
        /**
         *  通过给定值选中一个项；
         * @param value
         */
        select: function ( value ) {
            var me = this;

            me.panelContainer.find('li.' + me.defaultCls.itemCls).each(function () {
                var elem = $(this);

                elem.removeClass('selected');
                // == => 0 == '0' true; 不能使用全等；
                value == elem.attr('data-field') && elem.addClass('selected') && me.dataFieldInput.val(value);
            });
        },
        /**
         * 使用给定参数 data 更新select 选项；
         * @param data
         */
        update: function ( data, f ) {
            data = pSelect.sortData(this, data || this.data, f);
            this.panelContainer.html(pSelect.generateGroupHtml(this, data));
        },
        /**
         * reload 重新生成Select;
         */
        reload: function ( data ) {
            var me = this,
                _reload = function ( d ) {
                    me.data = me.filterData( d );
                    me.events.onBeforeLoad.call(me);
                    me.update(me.data);
                    /** 生成Select 完成之后 调用 用户配置的onLoadSuccess;  */
                    me.events.onLoadSuccess.call(me, me.data);
                    clearTimeout(me.loadingTimer);
                    !me.loadingTimer && me.loading.afterReceived.call(me);
                    me.lockGenerate = false;
                    me.doLayout();
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
        }
    });

    $.fn.dselect = function ( opts ) {
        var ret = [];

        if ( this.length > 1 ) {
            this.each(function () {
                opts.target = $(this);
                ret.push(new Select(opts));
            });

            return ret;
        } else {
            opts.target = this;
            return new Select(opts);
        }
    };
}();

