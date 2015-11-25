package com.phonegap.bossbolo.plugin.xgpush;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import com.phonegap.bossbolo.plugin.CustomGlobal;
import com.tencent.android.tpush.XGBasicPushNotificationBuilder;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGIOperateCallback;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.util.Log;

public class BoloPush extends CordovaPlugin {
    private static final String TAG = "BoosboloPush";
    private CordovaWebView webView;
    private Activity activity;
    private Context appContext;
    
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
        this.appContext = activity.getApplicationContext();
        //初始化push配置项
        XGPushConfig.enableDebug = false;
        try {
			this.registerPush(null, null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
	public void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);		
    	webView.onNewIntent(intent);
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
    	if (action.equals("registerPush")) {
        	registerPush(args, callbackContext);
        } else if (action.equals("unregisterPush")){
        	unregisterPush();
        } else if(action.equals("setBuild")){
        	setBuild(args);
        } else if(action.equals("setTag")){
        	XGPushManager.setTag(appContext, args.getString(0));
        } else{
            return false;
        }
		return true;
    }
    
    public void registerPush(JSONArray args, final CallbackContext callbackContext) throws JSONException{
    	XGIOperateCallback callback = new XGIOperateCallback() {
    		public void onSuccess(Object data, int flag){
    			String tocken = XGPushConfig.getToken(appContext);
    			CustomGlobal.getInstance().setTocken(tocken);
    			if(callbackContext!=null){
    				callbackContext.success(tocken);
    			}
    		}
    		public void onFail(Object data, int errCode, String msg) {
    			if(callbackContext!=null){
    				callbackContext.error(msg);
    			}
    			Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
    		}
    	};
    	
    	if(args==null){
    		XGPushManager.registerPush(appContext, callback);
    	}else{
    		String account = args.getString(0);
    		XGPushManager.unregisterPush(appContext);
        	XGPushManager.registerPush(appContext, account, callback);
    	}
    }
    
    public void unregisterPush(){
    	XGPushManager.unregisterPush(appContext);
    }
    
    /**
     * 设置消息提醒样式
     * @param args				格式：[int buildID, String iconName, String smallIconName, Boolean sound, Boolean vibrate]
     * @throws JSONException
     */
    public void setBuild(JSONArray args) throws JSONException{
    	int buildID = args.getInt(0);
    	String iconName = args.getString(1);
    	String smallIconName = args.getString(2);
    	Boolean sound = args.getBoolean(3);
    	Boolean vibrate = args.getBoolean(4);
    	
    	Resources activityRes = cordova.getActivity().getResources();
    	int iconID = activityRes.getIdentifier(iconName, "drawable", cordova.getActivity().getPackageName());
    	int smallIconID = activityRes.getIdentifier(smallIconName, "drawable", cordova.getActivity().getPackageName());
    	
    	XGBasicPushNotificationBuilder build = new XGBasicPushNotificationBuilder();
    	// 设置自定义样式属性，该属性对对应的编号生效，指定后不能修改。
    	build.setIcon(iconID)
    		.setSmallIcon(smallIconID)
    		.setDefaults(Notification.DEFAULT_LIGHTS)	//设置提示灯
    		.setFlags(Notification.FLAG_NO_CLEAR); 		// 是否可清除
		if(sound)build.setSound(RingtoneManager.getActualDefaultRingtoneUri(appContext,RingtoneManager.TYPE_ALARM)); // 设置声音
		if(vibrate)build.setVibrate(new long[]{0,600,100,300,100,100}); // 振动,设置方式：停震停震停...
    	// 设置通知样式，绑定样式编号为buildID，不同提醒设置项，只能通过不同buildID进行识别
    	XGPushManager.setPushNotificationBuilder(appContext, buildID, build);
    }
}
