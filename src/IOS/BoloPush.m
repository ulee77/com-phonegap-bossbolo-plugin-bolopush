//
//  BoloPlugin.h
//
//  Created by lihh on 15-3-20.
//  Copyright (c) 2015年 bolo. All rights reserved.
//

#import "BoloPush.h"
#import "UMessage.h"
#import "BoloCustomGlobal.h"

@implementation BoloPush

bool remoteNotification  = NO;
float systemVersion = 0;
NSNotificationCenter* center;

bool foreground = NO;

#pragma mark -
#pragma mark 对外开放接口

//注册push服务
- (void)enable:(CDVInvokedUrlCommand*)command
{
    if([BoloCustomGlobal getInstance].deviceToken != nil){
        [self initForReregister];
    }
}

//反注册push服务
- (void)disable:(CDVInvokedUrlCommand*)command
{
    [UMessage unregisterForRemoteNotifications];
    remoteNotification = NO;
}

//设置提醒样式
- (void)setBuild:(CDVInvokedUrlCommand*)command{
    
}

//设置tag分组
- (void)setTag:(CDVInvokedUrlCommand *)command
{
    NSString *tag = [command argumentAtIndex:0];
    [UMessage addTag:tag response:^(id addCallback, NSInteger remain, NSError *error) {
        NSLog(@"Add done");
    }];
}

//设置应用角标
- (void)setAppIconBadgeNumber:(CDVInvokedUrlCommand *)command
{
    int num = [[command argumentAtIndex:0] intValue];
    [self.app setApplicationIconBadgeNumber:num];
}


#pragma mark -
#pragma mark 初始化方法
/**
 * 插件初始化时注册 UIApplication 委托
 */
//应用完成加载委托事件


- (void) pluginInitialize
{
    systemVersion = [[[UIDevice currentDevice] systemVersion] floatValue];
    center = [NSNotificationCenter defaultCenter];
    [center addObserver:self
               selector:@selector(finishLaunchingWithOptions:)
                   name:UIApplicationDidFinishLaunchingNotification
                 object:nil];
    
    //获取deviceToken委托事件
    //deviceToken 为NSString类型
    [center addObserver:self
               selector:@selector(registerForRemoteNotificationsWithDeviceToken:)
                   name:CDVRemoteNotification
                 object:nil];
    //deviceToken 为NSData类型
    [center addObserver:self
               selector:@selector(registerForRemoteNotificationsWithDeviceTokenData:)
                   name:@"BoloRemoteNotificationsWithDeviceToken"
                 object:nil];
    [center addObserver:self
               selector:@selector(registerForRemoteNotificationsWithDeviceTokenError:)
                   name:CDVRemoteNotificationError
                 object:nil];
    
    
    //得到通知委托事件
    //远程通知
    [center addObserver:self
               selector:@selector(didReceiveRemoteNotification:)
                   name:@"BoloReceiveRemoteNotification"
                 object:nil];
    //本地通知
    [center addObserver:self
               selector:@selector(receiveLocalNotification:)
                   name:CDVLocalNotification
                 object:nil];
    
    //前台、后台切换委托事件
    [center addObserver:self
               selector:@selector(didEnterBackground:)
                   name:UIApplicationDidEnterBackgroundNotification
                 object:nil];
    [center addObserver:self
               selector:@selector(willEnterForeground:)
                   name:UIApplicationWillEnterForegroundNotification
                 object:nil];
}

/**
 * 定义UIApplication实例
 */
- (UIApplication*) app
{
    return [UIApplication sharedApplication];
}

#pragma mark -
#pragma mark 生命周期控制


/**
 *  应用载入完成
 */
- (void) finishLaunchingWithOptions:(NSNotification*)launchOptions
{
    foreground = YES;
    [UMessage startWithAppkey:@"5684ee17e0f55ae6240011a2" launchOptions:nil];
    [self initForReregister];
    [UMessage setLogEnabled:YES];
}


/**
 *  得到设备唯一标识,并向信鸽服务器发起推送注册
 *  注：发起注册条件 1:未注册 2:account变更 3:距离上次注册成功超过15小时。
 */
- (void) registerForRemoteNotificationsWithDeviceToken:(NSNotification*)deviceToken
{
    remoteNotification = YES;
    NSString *token = (NSString *)[deviceToken object];
    [BoloCustomGlobal getInstance].deviceToken = token;
    
}
- (void) registerForRemoteNotificationsWithDeviceTokenData:(NSNotification*)deviceToken
{
    NSData *token = (NSData *)[deviceToken object];;
    [UMessage registerDeviceToken:token];
}
- (void) registerForRemoteNotificationsWithDeviceTokenError:(NSError*)error
{
    //注册失败处理
    remoteNotification = NO;
    [BoloCustomGlobal getInstance].deviceToken = nil;
}

/**
 *  远程通知被激活。
 */
- (void) didReceiveRemoteNotification:(NSNotification*)userInfo
{
    //TODO:后期需要对推送消息内容做更多处理时再做代码补充，目前以系统默认方式显示
    NSDictionary *info = [userInfo object];
    NSLog(@"收到推送消息:%@",[[info objectForKey:@"aps"] objectForKey:@"alert"]);
    if(!foreground){
        [UMessage didReceiveRemoteNotification:info];
    }
}
/**
 *  本地通知被激活，得到通知消息内容
 */
- (void) receiveLocalNotification:(NSNotification*)localNotification
{
    //TODO:后期需要对推送消息内容做更多处理时再做代码补充，目前以系统默认方式显示
}

/**
 * 前后台切换监听
 */
- (void) didEnterBackground:(NSNotification*)notification
{
    foreground = NO;
    NSLog(@"前后台状态：%d", foreground);
}

- (void) willEnterForeground:(NSNotification*)notification
{
    foreground = YES;
    NSLog(@"前后台状态：%d", foreground);
}


#pragma mark -
#pragma mark push注册实体方法：

//
//小于IOS8版本的注册方法
//
- (void)registerPushForLowerIOS8{
//    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound)];
    [UMessage registerForRemoteNotificationTypes:UIRemoteNotificationTypeBadge
     |UIRemoteNotificationTypeSound
     |UIRemoteNotificationTypeAlert];
}

//
// IOS8以上的注册方法
//
- (void)registerPushForHigherIOS8{
    //Types
    UIUserNotificationType types = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
    
    //锁屏状态下通知消息左滑动操作按钮
    //Actions
    UIMutableUserNotificationAction *acceptAction = [[UIMutableUserNotificationAction alloc] init];
    acceptAction.identifier = @"ACCEPT_IDENTIFIER";
    acceptAction.title = @"查看";
    acceptAction.activationMode = UIUserNotificationActivationModeForeground;
    acceptAction.destructive = NO;//是否可以取消
    acceptAction.authenticationRequired = YES;//是否需要解锁才能处理
    
    //Categories
    UIMutableUserNotificationCategory *inviteCategory = [[UIMutableUserNotificationCategory alloc] init];
    inviteCategory.identifier = @"INVITE_CATEGORY";
    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextDefault];
//    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextMinimal];
    
    NSSet *categories = [NSSet setWithObjects:inviteCategory, nil];
    
    UIUserNotificationSettings *mySettings = [UIUserNotificationSettings settingsForTypes:types categories:categories];
    
    [UMessage registerRemoteNotificationAndUserNotificationSettings:mySettings];
    
//    [self.app registerUserNotificationSettings:mySettings];
//    [self.app registerForRemoteNotifications];
}

- (void)initForReregister{
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_8_0
    if(systemVersion < 8){
        [self registerPushForLowerIOS8];
    }
    else{
        [self registerPushForHigherIOS8];
    }
#else
    [self registerPushForLowerIOS8];
#endif
    
}

@end
