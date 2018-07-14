package com.why.project.keyboardutildemo.keyboard;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Used SharedPreferences存储软键盘的高度值
 */
public class KeyboardSharedPreferences {
	/**存储文件名*/
	private static final String FILE_NAME = "KeyboardSharedPreferences";
	/**存储Key值*/
	private static final String KEY_KEYBORD_HEIGHT = "keyboardHeight";

	private static volatile SharedPreferences SP;

	/**实例化SharedPreferences*/
	private static SharedPreferences with(Context context) {
		if(SP == null) {
			synchronized(KeyboardSharedPreferences.class) {
				if(SP == null) {
					SP = context.getSharedPreferences(FILE_NAME, 0);
				}
			}
		}
		return SP;
		
	}
	/**存储软键盘的高度*/
	public static boolean save(Context context, int keyboardHeight) {
		return with(context).edit().putInt(KEY_KEYBORD_HEIGHT, keyboardHeight).commit();
	}
	/**读取存储软键盘的高度（带默认值）*/
	public static int get(Context context, int defaultHeight) {
		return with(context).getInt(KEY_KEYBORD_HEIGHT, defaultHeight);
	}

}
