package com.phonegap.bossbolo.plugin.bolopush;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import com.phonegap.bossbolo.plugin.CustomGlobal;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.local.UmengLocalNotification;
import com.umeng.message.local.UmengNotificationBuilder;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.os.Handler;
import android.util.Log;

public class BoloPush extends CordovaPlugin {
    private static final String TAG = "BoloPush";
    private CordovaWebView webView;
    private Activity activity;
    private Context appContext;
    
    public static BoloPush bolopush;
    private PushAgent mPushAgent;

	public int buildID;
	public int sound;
	public int vibrate;
    
    private Resources activityRes;
    private String pkgName;
    
    private Boolean enable = true;
	
	public Handler handler = new Handler();

	/**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        Log.v(TAG, "StatusBar: initialization");
        super.initialize(cordova, webView);
        this.webView = webView;
        this.activity = cordova.getActivity();
        this.setAppContext(activity.getApplicationContext());
        this.activityRes = this.activity.getResources();
    	this.pkgName = this.activity.getPackageName();
    	
    	mPushAgent = PushAgent.getInstance(this.activity);
    	mPushAgent.setNotificaitonOnForeground(false);
    	mPushAgent.setMuteDurationSeconds(1);
    	mPushAgent.setResourcePackageName("com.bossbolo.powerbjy");
//    	mPushAgent.setPushCheck(true);
//    	mPushAgent.setMergeNotificaiton(true);
    }
    
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
    	if(!enable)
    		return true;
    	if (action.equals("enable")) {
        	setEnable(callbackContext);
        } else if (action.equals("disable")){
        	setDisable();
//        } else if (action.equals("isEnabled")){
//        	isEnabled(callbackContext);
//    	} else if (action.equals("addAlias")){
//    		addAlias(args);
    	} else if(action.equals("setBuild")){
        	setBuild(args);
        } else if(action.equals("setTag")){
        	setTags(args);
        } else if (action.equals("delTag")){
        	delTags(args);
        } else {
            return false;
        }
		return true;
    }
    /**
     * 启动服务
     */
    public void setEnable(CallbackContext callbackContext){
    	mPushAgent.enable(new IUmengRegisterCallback() {
			@Override
			public void onRegistered(String token) {
				CustomGlobal.getInstance().setTocken(token);
			}
		});
    	String device_token =UmengRegistrar.getRegistrationId(this.appContext);
    	CustomGlobal.getInstance().setTocken(device_token);
    	PushAgent.getInstance(this.activity).onAppStart();
    }
    /**
     * 停止服务
     */
    public void setDisable(){
    	mPushAgent.disable();
    }
    /**
     * 设置标签
     * @param args				tags字符串每个tag以逗号隔开
     * @throws JSONException 
     */
    public void setTags(JSONArray args) throws JSONException{
    	String tagString = args.getString(0);
//    	mPushAgent.getTagManager().add(tagString, "sport");
    }
    /**
     * 移除标签
     * @param args				tags字符串每个tag以逗号隔开
     * @throws JSONException 
     */
    public void delTags(JSONArray args) throws JSONException {
    	String tagStr = args.getString(0);
//        mPushAgent.getTagManager().delete(tagString, "sport");
    }
    
    /**
     * 设置消息提醒样式
     * @param args				格式：[int buildID, String iconName, String smallIconName, Boolean sound, Boolean vibrate]
     * @throws JSONException
     */
    public void setBuild(JSONArray args) throws JSONException{
    	this.buildID = args.getInt(0);
    	this.sound = args.getBoolean(1) ? MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE : MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE;
    	this.vibrate = args.getBoolean(2) ? MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE : MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE;
    	
    	mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
    	mPushAgent.setNotificationPlaySound(this.sound);
    	mPushAgent.setNotificationPlayVibrate(this.vibrate);
    }

	public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}
    
    public int getBuildID() {
		return buildID;
	}

	public void setBuildID(int buildID) {
		this.buildID = buildID;
	}

	public void updateStatus(){
		//TODO 通知前端
	}
}
