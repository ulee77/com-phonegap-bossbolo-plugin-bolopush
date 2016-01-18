cordova.define("com-phonegap-bossbolo-plugin-bolopush.BoloPush", function(require, exports, module) {     var exec = require('cordova/exec');

    var defaults = {
        buildID : 85186230,
        iconName : "icon_notification",
        smallIconName : "icon_small"
    };

    var BoloPush = {
        /**
         * 注册消息推送服务   win：返回tocken； error：返回错误消息
         * @param account
         * @param win
         * @param error
         */
        enable : function(account, win, error){
            if($.os.ios)return;
            win = win || function(toccken){};
            error = error || function(errorMessage){};
            exec(win, error, "BoloPush", "enable", []);
        },

        /**
         * 反注册消息推送服务
         * 通常用于用户重新登录
         */
        disable : function(){
            if($.os.ios)return;
            exec(null, null, "BoloPush", "disable", []);
        },

        /**
         * 设置消息提醒参数
         * 每次调用将会生成新的 notification 对象
         * @param sound             是否开启声音，声音使用系统默认
         * @param vibrate           是否开启震动，默认震动3次间隔100毫秒，第一次600毫秒，第二次500毫秒，第三次100毫秒
         */
        setBuild : function(sound, vibrate){
            if($.os.ios)return;
            exec(null, null, "BoloPush", "setBuild", [!!sound, !!vibrate]);
        },

        /**
         * 设置tag,tag用于推送用户分组
         * @param tag
         */
        setTag : function(tag){
            if($.os.ios)return;
            exec(null, null, "BoloPush", "setTag", [tag]);
        }
    };

    module.exports = BoloPush;
});
