//
//  BoloPlugin.h
//
//  Created by lihh on 15-3-20.
//  Copyright (c) 2015年 bolo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <Cordova/CDV.h>
#import "AppDelegate.h"

@interface BoloPush : CDVPlugin

- (void)enable:(CDVInvokedUrlCommand*)command;

- (void)disable:(CDVInvokedUrlCommand*)command;

- (void)setBuild:(CDVInvokedUrlCommand*)command;

- (void)setTag:(CDVInvokedUrlCommand*)command;

@end