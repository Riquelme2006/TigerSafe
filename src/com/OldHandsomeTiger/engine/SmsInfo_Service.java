﻿package com.OldHandsomeTiger.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import com.OldHandsomeTiger.domain.SmsInfo;

public class SmsInfo_Service {

	private Context context;

	public SmsInfo_Service(Context context) {
		this.context = context;
	}

	public List<SmsInfo> getSmsInfo() {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "_id", "address",
				"date", "type", "body" }, null, null, "date desc");
		List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			String address = cursor.getString(1);
			String date = cursor.getString(2);
			String type = cursor.getString(3);
			String body = cursor.getString(4);
			System.out.println(body);
			SmsInfo smsInfo = new SmsInfo(id, address, date, type, body);
			smsInfos.add(smsInfo);
			smsInfo = null;
		}

		return smsInfos;
	}

	/**
	 * 还原短信信息 
	 * 
	 * @param path
	 *            短信备份文件对应的路径
	 */
	public void restoreSms(String path, ProgressDialog pd) throws Exception {
		File file = new File(path);
		ContentValues values = null;
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		int type = parser.getEventType();
		int currentcount = 0;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {

			case XmlPullParser.START_TAG:
				if ("count".equals(parser.getName())) {
					String count = parser.nextText();
					pd.setMax(Integer.parseInt(count));
				}

				if ("sms".equals(parser.getName())) {
					values = new ContentValues();
				} else if ("address".equals(parser.getName())) {
					values.put("address", parser.nextText());
				} else if ("date".equals(parser.getName())) {
					values.put("date", parser.nextText());
				} else if ("type".equals(parser.getName())) {
					values.put("type", parser.nextText());
				} else if ("body".equals(parser.getName())) {
					values.put("body", parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					ContentResolver resolver = context.getContentResolver();
					resolver.insert(Uri.parse("content://sms/"), values);
					values = null;
					currentcount++;
					pd.setProgress(currentcount);
				}
				break;
			}
			type = parser.next();
		}
	}
}
