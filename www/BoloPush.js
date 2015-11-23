    var exec = require('cordova/exec');

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
        registerPush : function(account, win, error){
            win = win || function(toccken){};
            error = error || function(errorMessage){};
            exec(win, error, "BoloPush", "registerPush", [account]);
        },

        /**
         * 反注册消息推送服务
         * 通常用于用户重新登录
         */
        unregisterPush : function(){
            exec(null, null, "BoloPush", "registerPush", []);
        },

        /**
         * 设置消息提醒参数
         * 每次调用将会生成新的 notification 对象
         * @param buildID           notification对象id
         * @param iconName          大图标名称，图标需要放在（ android：drawable ）资源文件夹中
         * @param smallIconName     小图标名称，图标需要放在（ android：drawable ）资源文件夹中
         * @param sound             是否开启声音，声音使用系统默认
         * @param vibrate           是否开启震动，默认震动3次间隔100毫秒，第一次600毫秒，第二次500毫秒，第三次100毫秒
         */
        setBuild : function( buildID, iconName, smallIconName, sound, vibrate){
            buildID = typeof buildID=="number" ? buildID : ++defaults.buildID;
            iconName = iconName ? iconName : defaults.iconName;
            smallIconName = smallIconName ? smallIconName : defaults.smallIconName;
            exec(null, null, "BoloPush", "setBuild", [buildID, iconName, smallIconName, !!sound, !!vibrate]);
        },

        /**
         * 设置tag,tag用于推送用户分组
         * @param tag
         */
        setTag : function(tag){
            exec(null, null, "BoloPush", "setTag", [tag]);
        }
    };

    module.exports = BoloPush;