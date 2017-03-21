"use strict";

require('./modules/loading.js');
require('./modules/jquery.cookie.js');
require('./modules/select2.full.js');
var Menu = require('./modules/menu.js');

var mgr = {
    deployPath: location.pathname.search('/publish') !== -1 ? '/publish' : '',
    init: function () {
        var me = this;

        me.initMenu();
    },
    initMenu: function () {
        var me = this;
        var userInfo = ($.cookie('userinfo') || '').split(':');
        var dftData = [];

        $('#user-name-btn').html(
            userInfo[1]
            || '<a href="https://acl.admin.jituancaiyun.com/power/user/view/login.html" ' +
            'target="_self"><span class="cor-w">登录</span></a>'
        );

        switch ( +userInfo[0] ) {
            case 0:
                break;
            case 2:
                dftData.push({
                    id: 1,
                    text: '用户管理',
                    icon: 'iconfont icon-jiaoseguanlisanji',
                    href: me.deployPath + '/assets/fragment/user.html'
                });
            case 1:
                dftData.push({
                    id: 2,
                    text: '项目管理',
                    icon: 'iconfont icon-mokuaiguanlierji',
                    href: me.deployPath + '/assets/fragment/pro-list.html'
                });
                break;
            // no default;
        };


        dftData = dftData.concat([{
            id: 3,
            text: '发布单',
            icon: 'iconfont icon-edit',
            children: [{
                id: 31,
                selected: true,
                text: '我的发布单',
                icon: 'iconfont icon-menu',
                href: me.deployPath + '/assets/fragment/publish-order.html'
            },{
                id: 32,
                text: '发布单审核',
                icon: 'iconfont icon-confirm1',
                href: me.deployPath + '/assets/fragment/publish-audit.html'
            },{
                id: 33,
                text: '发布单日志',
                icon: 'iconfont icon-code',
                href: me.deployPath + '/assets/fragment/operate-log.html'
            }]
        },{
            id: 4,
            text: 'web shell',
            icon: 'iconfont icon-code',
            href: me.deployPath + '/assets/fragment/web-shell.html'
        },{
            id: 5,
            text: '日志文件',
            icon: 'iconfont icon-rizhi',
            href: me.deployPath + '/assets/fragment/log-file.html'
        },{
            id: 6,
            text: '健康检查',
            icon: 'iconfont icon-success',
            href: me.deployPath + '/assets/fragment/health-check.html'
        }]);

        Util.menuTreeIns = me.menuIns = new Menu({
            container: $('#menu-tree-container'),
            data: dftData,
            events: {
                onUnselected: function () {
                    //me.getMainBodyContainer().off().html('');
                    var oldTab = Util.getCurrentTab();

                    oldTab && oldTab.off().remove();
                },
                onSelected: function ( itemData ) {
                    // 生成一个新Tab container;清空主容器并插入到主容器中；
                    var newTab = Util.generateTab();

                    me.getMainBodyContainer().html('').append(newTab);
                    me.getPageAjax = Util.baseAjax({
                        url: itemData.href,
                        dataType: 'html'
                    }, function ( data ) {
                        newTab.append(data);
                    });
                },
                /**
                 *
                 * @param e
                 * @param itemData
                 * @param forces
                 * @returns {boolean}
                 */
                onItemClick: function (e, itemData, forces) {
                    var menutree = this;

                    // 如果 点击项的id 为undefined 或 null 或者有子选项；则忽略本次点击；
                    if ( itemData.id == undefined || $.isArray(itemData.children) ) {
                        return false;
                    }

                    // 强制【forces】 == false 并且 当前已打开的项 == 当前点击项；
                    // 则取消本次点击事件；
                    if ( !forces && menutree.getSelectedId() == itemData.id ) {
                        return false;
                    }

                    me.getPageAjax && me.getPageAjax.abort();
                    this.select(itemData.id);
                },
                onLoadSuccess: function () {
                    //this.getSelectElem().trigger('click');
                    var selectedItem = this.getSelectedItemData();

                    selectedItem &&
                    this.events.onItemClick.call(this,{},selectedItem, true);
                }
            }
        });
    },
    getMainBodyContainer: function () {
        var d = $('#main-tab-container');

        this.getMainBodyContainer = function () {
            return d;
        }
        return d;
    }
};

mgr.init();