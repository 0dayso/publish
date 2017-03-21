
var Util = {
    /** 静态变量：
     *  请求验证码的时间间隔
     *  Bops.GETCODE_INTERVAL = 30;
     *  设置为 30s
     *  Ajax 请求超时时间 设置为 15s
     *  Bops.AJAX_TIMEOUT = 15000;
     */
    GETCODE_INTERVAL: 30,
    AJAX_TIMEOUT: 15000,
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
    }
};

/**
 * 设置全局Ajax请求超时间；
 */
$.ajaxSetup({
    timeout: Util.AJAX_TIMEOUT
});

Util.baseAjax = function ( opts, callback ) {
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
        success: function (json) {
            $.isFunction(callback) && callback(json);
            $.isFunction(opts.success) && opts.success(json);
        },
        complete: function (xhr) {
            var json = xhr.responseJSON;

            json = json || {};

            if (json.code === 60000) {
                // 登录超时；
                Util.sessionOutdated();
            } else if (json.code === 60004) {
                // 其它需要提示信息的情况；
                // TODO: some custom defined tip msg;
            }

            if ($.isFunction(opts.complete)) {
                return opts.complete.apply(this,arguments);
            }
        }
    }));
}

Util.parseParam = function (str) {
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
Util.Date.format = function (utc,f) {
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
Util.Form.loadData = function ($targetForm, data) {
    var form, item, i = 0;

    $targetForm instanceof jQuery ? (form = $targetForm[0])
        :  $.isArray($targetForm) ? $targetForm[0] : (form = $targetForm);

    while ( (item = form[i++]) ) {
        // 下面的条件会过滤掉undefined 和 null;
        data[item.name] != undefined &&
        (item.value = data[item.name]);
    }
};

Util.Form.loadSelectOptions = function (select, data) {
    var str = '',
        i = 0,
        len = data && data.length;
    for (; i < len; ++i) {
        str += '<option value="' + data[i].value + '">' + data[i].key + '</option>'
    }

    $(select).append(str);
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

module.exports = Util;