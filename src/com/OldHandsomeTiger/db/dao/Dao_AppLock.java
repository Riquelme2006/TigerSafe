﻿package com.OldHandsomeTiger.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.OldHandsomeTiger.db.DB_LockAppInfo;
/**
 * 锁定的APP，数据库类
 * @author tang
 *
 */
public class Dao_AppLock {
	private Context context;
	private DB_LockAppInfo dbHelper;

	public Dao_AppLock(Context context) {
		this.context = context;
		dbHelper = new DB_LockAppInfo(context);
	}

	/**
	 * 查询
	 */
	public boolean find(String packname) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select packname from applock where packname=?",
					new String[] { packname });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();

			db.close();
		}
		return result;
	}

	/**
	 * 添加
	 */
	public void add(String packname){
		if(find(packname)){
			return ;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into applock (packname) values (?)", new Object[]{packname});
			db.close();
		}
	}
	/**
	 * 删除
	 */
	public void delete(String packname){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from applock where packname=?", new Object[]{packname});
			db.close();
		}
	}
	

	
	
	/**
	 * 查找全部包名
	 */
	public List<String> getAllApps(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<String> packnames = new ArrayList<String>();
		if (db.isOpen()) {
		  Cursor cursor =	db.rawQuery("select packname from applock", null);
			while (cursor.moveToNext()) {
				String packname = cursor.getString(0);
				packnames.add(packname);
			}
			cursor.close();
			db.close();
		}
		return packnames;
	}
}
