package com.why.project.keyboardutildemo.keyboard;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.why.project.keyboardutildemo.R;


/**
 * Used 软键盘的操作类（显示、隐藏软件盘、保存软键盘的高度值——用于控制输入框的上升）
 */
public class KeyboardUtil {

	/**最后一次保存的键盘高度*/
	private static int LAST_SAVE_KEYBOARD_HEIGHT = 0;

	/**输入法软键盘区域的最大高度*/
	private static int MAX_PANEL_HEIGHT = 0;
	/**输入法软键盘区域的最小高度*/
	private static int MIN_PANEL_HEIGHT = 0;

	/**显示软键盘*/
	public static void showKeyboard(View view) {
		if(view != null){
			view.requestFocus();
			InputMethodManager inputManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputManager != null) {
				inputManager.showSoftInput(view, 0);
			}
		}
	}

	/**隐藏软键盘
	 * 第一个参数中的windowToken应当是之前请求显示软键盘的View的windowToken，也就是执行showSoftInput()时第一个参数中的View的windowToken。
	 * 但是实际情况是，用任意一个当前布局中的已经加载的View的windowToken都可以隐藏软键盘，哪怕这个View被设置为INVISIBLE或GONE。
	 * 因此，如果不知道之前是谁请求显示的软键盘，可以随便传入一个当前布局中存在的View的windowToken。
	 * 特别的，可以传入一个Activity的顶层View的windowToken，即getWindow().getDecorView().getWindowToken()，来隐藏当前Activity中显示的软键盘，
	 * 而不用管之前调用showSoftInput()的究竟是哪个View。*/
	public static void hideKeyboard(Activity mActivity) {
		InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0);
		}
	}

	/**隐藏软键盘*/
	public static void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		view.clearFocus();
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**是否正在显示软键盘*/
	public static boolean isShowKeyboard(Context context, View view)
	{
		boolean bool = false;
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.hideSoftInputFromWindow(view.getWindowToken(), 0))
		{
			imm.showSoftInput(view, 0);
			bool = true;
		}
		return bool;
	}

	/**保存软键盘的高度值*/
	public static boolean saveKeyboardHeight(Context context, int keyboardHeight) {

		Log.d("KeyboardUtil", "keyboardHeight="+keyboardHeight);
		if(keyboardHeight <= 300 || LAST_SAVE_KEYBOARD_HEIGHT == keyboardHeight) {
            return false;
        }
		LAST_SAVE_KEYBOARD_HEIGHT = keyboardHeight;

		return KeyboardSharedPreferences.save(context, keyboardHeight);
	}
	
	/**获取软键盘的高度值*/
	public static int getKeyboardHeight(Context context) {
		if(LAST_SAVE_KEYBOARD_HEIGHT == 0) {
			LAST_SAVE_KEYBOARD_HEIGHT = KeyboardSharedPreferences.get(context, getMinPanelHeight(context.getResources()));
		}
		return LAST_SAVE_KEYBOARD_HEIGHT;
	}

	/**获取面板的最大高度值-自定义的*/
	private static int getMaxPanelHeight(Resources res) {
		if(MAX_PANEL_HEIGHT == 0) {
			MAX_PANEL_HEIGHT = res.getDimensionPixelSize(R.dimen.keyboard_content_panel_max_height);
		}
		return MAX_PANEL_HEIGHT;
	}

	/**获取面板的最小高度值-自定义的*/
	private static int getMinPanelHeight(Resources res) {
		if(MIN_PANEL_HEIGHT == 0) {
			MIN_PANEL_HEIGHT = res.getDimensionPixelSize(R.dimen.keyboard_content_panel_min_height);
		}
		return MIN_PANEL_HEIGHT;
	}
}
