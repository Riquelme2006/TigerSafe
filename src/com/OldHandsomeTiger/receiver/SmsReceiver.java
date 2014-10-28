﻿package com.OldHandsomeTiger.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.OldHandsomeTiger.engine.GpsInfo_Service;
import com.OldHandsomeTiger.util.Config;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取所有的短信
		//这里是否有问题？每次收到新短信都会遍历所有的短信……
		//想想更好的方法……
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
			String content = smsMessage.getMessageBody();
			String senderNum = smsMessage.getOriginatingAddress();
			Log.i(TAG, "短信内容是：" + content);
			Log.i(TAG, "短信发送者是：" + senderNum);
			if (content.equals(Config.COMMAND_LOCATION)) {
				// 中止广播
				abortBroadcast();
				GpsInfo_Service gpsInfoService = GpsInfo_Service
						.getGpsInfoService(context);
				String location = gpsInfoService.getLocation();

				SmsManager smsManager = SmsManager.getDefault();
				if ("".equals(location)) {
					Log.i(TAG, "尚未获取到Location信息" );
				} else {
					smsManager.sendTextMessage(senderNum, null, "当前手机的位置是:"+location,
							null, null);
				}

			}else if (content.equals(Config.COMMAND_DELETE)) {
					//	出厂设置
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				abortBroadcast();
			}else if (content.equals(Config.COMMAND_ALARM)) {
					//播放警报
				MediaPlayer player = MediaPlayer.create(context, com.OldHandsomeTiger.tigersafe.R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if (content.equals(Config.COMMAND_LOCKSCREEN)) {
				//强制锁屏
			}

		}

	}

}
