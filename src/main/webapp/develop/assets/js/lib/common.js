
;void (function () {
    'use strict';

    // Home page buttons action;
    var componentMgr = {
        pageContainer: $('#page-container'),
        initLayout: true,
        // 左侧菜单显示隐藏标记；
        showMenuFlag: false,
        /** 标记是否宽度全屏 默认 宽度不是全屏  */
        maxWidthFlag: false,
        /** fixedLayoutFlag 标记当前layout 已经锁定 */
        fixedLayoutFlag: false,
        /**  标记是否全屏显示页面 默认非全屏 */
        fullScreenFlag: false,
        init: function () {
            this.bindEvents();
        },
        bindEvents: function () {
            var me = this;

            me.pageContainer.on('click', '.menu-dropdown', me, me.initMenuHandle)
                .on('click', '#toggler-menu-bar',me, me.toggleMenuBarHandle)
                .on('click', '#fullscreen-toggler', me, me.fullScreenHandle)
                .on('click', '#refresh-toggler', me, me.refreshHandle)
                .on('click', '#fixed-toggler', me, me.fixedLayoutHandle);
        },
        fixedLayoutHandle: function (e) {
            var me = e.data;

            // 赋值 + 判断； fixedLayoutFlag {boolean}
            (me.fixedLayoutFlag = !me.fixedLayoutFlag) ||
            me.getToggleMenuBtnElem().trigger('click', e);

            $(this).toggleClass('active');
        },
        getToggleWidthBtnElem: function () {
            var d = $('#max-width-toggler');

            this.getToggleWidthBtnElem =  function () {
                return d;
            }
            return d;
        },
        getFixedBtnElem: function () {
            var d = $('#fixed-toggler');

            this.getFixedBtnElem =  function () {
                return d;
            }
            return d;
        },
        getAccordionElem: function () {
            var d = this.pageContainer.find('.menu-dropdown');
            this.getAccordionElem = function () {
                return d;
            }

            return d;
        },
        getToggleMenuBtnElem: function () {
            var d = $('#toggler-menu-bar');
            this.getToggleMenuBtnElem = function () {
                return d;
            }
            return d;
        },
        getToolbarHeaderElem: function () {
            var d = $('#main-toolbar-header');
            this.getToolbarHeaderElem = function () {
                return d;
            }
            return d;
        },
        getToggleFullScreenBtnElem: function () {
            var d = $('#fullscreen-toggler');
            this.getToggleFullScreenBtnElem = function () {
                return d;
            }
            return d;
        },
        refreshHandle: function () {
            location.reload();
        },
        toggleMenuBarHandle: function (e) {
            var $this = $(this);
            var me = e.data;

            if ( me.fixedLayoutFlag ) {
                return false;
            }

            if ( me.showMenuFlag = !me.showMenuFlag ) {
                $this.next().addClass('d-n');
            } else {
                $this.next().removeClass('d-n');
            }
            me.togglePageWidthHandle();
        },
        togglePageWidthHandle: function () {
            var me = this;

            if ( me.fixedLayoutFlag ) {
                return false;
            }

            this.containerElem = this.containerElem || $('main');

            // 赋值和判断集合在一起 => '='；
            if ( me.maxWidthFlag = !me.maxWidthFlag ) {
                this.containerElem.addClass('w-100-i').css('padding-left', 0);
                me.getToolbarHeaderElem().addClass('pl-50px');
                me.showMenuFlag = true;
            } else {
                this.containerElem.removeClass('w-100-i').css('padding-left', '202px');
                me.getToolbarHeaderElem().removeClass('pl-50px');
            }

            $(window).trigger('resize.Table');
        },
        fullScreenHandle: (function () {
            var  b = document.body;

            b.requestFullscreen = b.requestFullscreen || b.webkitRequestFullScreen
                || b.msRequestFullscreen || b.mozRequestFullScreen
                || $.noop;
            document.cancelFullScreen = document.cancelFullScreen || document.webkitCancelFullScreen
                || document.mozCancelFullScreen || document.msCancelFullScreen
                || $.noop;

            return function ( e ) {
                var me = e.data;

                if ( me.fullScreenFlag = !me.fullScreenFlag ) {
                    b.requestFullscreen();
                } else {
                    document.cancelFullScreen();
                }

                $(this).toggleClass('active');
            };
        })(),
        initMenuHandle: function (e) {
            var $this = $(this);
            var expand = $this.find('.menu-expand-icon').toggleClass('menu-expand').hasClass('menu-expand');
            var otherDropdownElems = e.data.getAccordionElem().not(this);

            otherDropdownElems.next().slideUp();
            otherDropdownElems.find('.menu-expand-icon').removeClass('menu-expand');

            if ( expand ) {
                $this.next('.sub-menu').slideDown();
            } else {
                $this.next('.sub-menu').slideUp();
            }
        }
    };

    componentMgr.init();
})();