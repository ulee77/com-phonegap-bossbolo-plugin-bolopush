# com-phonegap-bossbolo-plugin-xgpush
布络phonegap 消息推送插件，插件基于腾讯信鸽推送

## 插件的安装、卸载
#安装命令

```sh
phonegap plugin add https://github.com/ulee77/com-phonegap-bossbolo-plugin-xgpush.git
```
#卸载命令
```sh
phonegap plugin rm com-phonegap-bossbolo-plugin-xgpush
```

##插件依赖
依赖于主框架插件：https://github.com/ulee77/com-phonegap-bossbolo-plugin

##平台支持
- phoengap 5+
- Android 4+
- IOS 5+

# IOS平台环境配置
- __1__ : 添加 a 库：将plugin/com-phonegap-bossbolo-plugin-xgpush/libUMessage_Sdk.a 导入Frameworks中；
- __2__ : 添加远程通知委托：application: application didReceiveRemoteNotification:
```sh
//在AppDelegate.m中
- (void)application:(UIApplication*)application didReceiveRemoteNotification:(NSDictionary*)userInfo
{
    //添加代码
    [[NSNotificationCenter defaultCenter] postNotificationName:@"BoloReceiveRemoteNotification" object:userInfo];
}
```
- __3__: deviceToken委托：application:application didRegisterForRemoteNotificationsWithDeviceToken:
```sh
//在AppDelegate.m中
- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken
{
    //添加代码
    [[NSNotificationCenter defaultCenter] postNotificationName:@"BoloRemoteNotificationsWithDeviceToken" object:deviceToken];
}
```

# Android平台环境配置
- __1__ : 应用信息设置
    注册信鸽推送应用信息，并将 应用 ACCESS_ID、ACCESS_KEY 替换掉 AndroidManifest.xml 中的 "ACCESS_ID"、"ACCESS_KEY"

- __2__ : 消息接收服务配置
    将 AndroidManifest.xml 中  的 "应用包名" 改为当前应用的主包名。


##通用接口说明

# 注册消息推送服务，按照用户账号注册
```sh
var win = function(tocken){console.log("Device tocken:"+tocken);}
var error = function(message){console.log("错误信息:"+message);}
BoloPush.registerPush(account, win, error);
```

# 反注册消息推送服务，取消注册后将会在接收推送消息
```sh
BoloPush.unregisterPush();
```

# 设置消息提醒参数
```sh
/**
 * 设置消息提醒参数
 * 每次调用将会生成新的 notification 对象
 * @param buildID           notification对象id
 * @param iconName          大图标名称，图标需要放在（ android：drawable ）资源文件夹中
 * @param smallIconName     小图标名称，图标需要放在（ android：drawable ）资源文件夹中
 * @param sound             是否开启声音，声音使用系统默认
 * @param vibrate           是否开启震动，默认震动3次间隔100毫秒，第一次600毫秒，第二次500毫秒，第三次100毫秒
 */
BoloPush.setBuild(buildID, iconName, smallIconName, sound, vibrate);
```

# 设置tag,tag用于推送用户分组
IOS支持添加多个
```sh
BoloPush.setTag("四川");
```

# 默认build设置信息
```sh
var defaults = {
    buildID : 12345678,
    iconName : "icon_notification",
    smallIconName : "icon_small"
};
```
