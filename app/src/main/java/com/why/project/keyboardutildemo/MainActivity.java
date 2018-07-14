package com.why.project.keyboardutildemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.why.project.keyboardutildemo.keyboard.KeyboardUtil;
import com.why.project.keyboardutildemo.keyboard.ViewAnimationUtil;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private RelativeLayout rootLayout;//整体View

	private EditText edt_send;//输入框view
	private EditText edt_user;//输入框view

	/**区域是否上升了*/
	private boolean editAreaIsUp = false;
	/**软键盘的高度值*/
	private int keyboardHeight = 0;
	/**需要上升的高度值*/
	private int textBottom = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	private void initViews() {
		rootLayout = (RelativeLayout) findViewById(R.id.layoutroot);
		edt_send = (EditText) findViewById(R.id.edt_send);
		edt_user = (EditText) findViewById(R.id.edt_user);

		//计算整体view需要移动的高度值（总高度 - 可见区域高度 + top（标题栏高度） = 隐藏区域高度（软键盘高度值））
		((RelativeLayout)rootLayout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				//当保存的高度值小于300的时候执行
				// 【当APP第一次打开的时候，这里的代码也会执行，那么此时keyboardHeight==0，那么会继续执行下面代码，但是keyboardHeight计算后的值一般会小于300，所以此时不能保存keyboardHeight！！！】
				// 【当触摸输入框的时候，不管输入框在软键盘上方还是下方，此时keyboardHeight计算后的值>300】【也就是弹出系统软键盘后整体view向上移动的距离（rect.bottom值变小了），也就可以理解为系统软键盘的高度】
				if(keyboardHeight < 300) {
					Rect rect = new Rect();
					rootLayout.getWindowVisibleDisplayFrame(rect);//rect指可见区域
					Log.e(TAG, "{onGlobalLayout}rootLayout.getRootView().getHeight()=" + rootLayout.getRootView().getHeight());//【移动前的rootLayout的bottom】
					Log.e(TAG, "{onGlobalLayout}rect.bottom=" + rect.bottom);//【移动后的rootLayout的bottom】
					Log.e(TAG, "{onGlobalLayout}rect.top=" + rect.top);//【标题栏的高度值】
					keyboardHeight = rootLayout.getRootView().getHeight() - rect.bottom + rect.top;
					Log.e(TAG, "{onGlobalLayout}keyboardHeight=" + keyboardHeight);//
					if (keyboardHeight > 300) {
						KeyboardUtil.saveKeyboardHeight(MainActivity.this, keyboardHeight);
					}
				}else {//方案一
					Rect rect = new Rect();
					rootLayout.getWindowVisibleDisplayFrame(rect);//rect指可见区域
					Log.e(TAG, "{onGlobalLayout}rect.bottom=" + rect.bottom);//【移动后的rootLayout的bottom】
					Log.e(TAG, "{onGlobalLayout}keyboardHeight=" + keyboardHeight);//
					if(rect.bottom != keyboardHeight){//代表软键盘隐藏了，当软键盘显示的时候，rect.bottom == keyboardHeight
						downEditRect();
					}
				}
			}
		});
		//整个界面区域的触摸事件
		rootLayout.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.v(TAG, "{initialize}rootLayout=onTouch");
				//隐藏自定义的软键盘区域
				hideEditRect();
				return true;
			}
		});

		//输入框触摸的监听事件
		edt_user.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//【注意：edt_user.getBottom()是指系统软键盘弹出来后的输入框的bottom值，，缺少顶部标题栏的区域高度，而且连续点击后值不变】
				Log.i(TAG, "{initViews}edt_user.getBottom()=" + edt_user.getBottom());
				//计算相对于Windows的坐标
				int[] locationW = new int[2];
				edt_user.getLocationInWindow(locationW);
				Log.i(TAG, "{onTouch}locationW=" + locationW[0] +";" + locationW[1]);
				//计算相对于Screen的坐标
				int[] locationS = new int[2];
				edt_user.getLocationOnScreen(locationS);
				Log.i(TAG, "{onTouch}locationS=" + locationS[0] +";" + locationS[1]);
				Log.i(TAG, "{onTouch}edt_user.getMeasuredHeight()=" + edt_user.getMeasuredHeight());//输入框的高度

				int edtBottom = locationW[1] + edt_user.getMeasuredHeight();//输入框的底部的Y坐标值== topY + Height;
				showEditRect(edt_user,edtBottom);
				return false;
			}
		});

		//输入框触摸的监听事件
		edt_send.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//【注意：edt_send.getBottom()是指系统软键盘弹出来后的输入框的bottom值，缺少顶部标题栏的区域高度，而且连续点击后值不变】
				Log.i(TAG, "{initViews}edt_send.getBottom()=" + edt_send.getBottom());
				//计算相对于Windows的坐标
				int[] locationW = new int[2];
				edt_send.getLocationInWindow(locationW);
				Log.i(TAG, "{initViews}locationW1=" + locationW[0] +";" + locationW[1]);
				//计算相对于Screen的坐标
				int[] locationS = new int[2];
				edt_send.getLocationOnScreen(locationS);
				Log.i(TAG, "{initViews}locationS1=" + locationS[0] +";" + locationS[1]);

				int edtBottom = locationW[1] + edt_send.getMeasuredHeight();//输入框的底部的Y坐标值== topY + Height;
				showEditRect(edt_send,edtBottom);

				return false;
			}
		});
	}


	/**
	 * 显示自定义的软键盘区域
	 * @param view - 输入框view
	 * @param bottom  输入框的bottom【弹出系统软键盘后的值】*/
	public void showEditRect(final View view, final int bottom) {

		//实现编辑区域的上升动画效果
		view.postDelayed(new Runnable() {
			public void run() {
				Log.w(TAG, "(showEditRect)bottom="+bottom);
				Log.w(TAG, "(showEditRect)keyboardHeight="+keyboardHeight);
				if(keyboardHeight != 0 && bottom - keyboardHeight > 0){//为什么需要判断bottom - keyboardHeight > 0??因为当已经弹出软键盘后继续点击输入框的时候，就不需要在上移了，而可以通过bottom值变小了来解决继续上移的问题。
					textBottom = view.getMeasuredHeight();

					Log.w(TAG, "(showEditRect)textBottom="+textBottom);
					makeEditAreaUpAndSmall(((float)textBottom));
				}
			}
		}, 300);

	}

	/**隐藏自定义软键盘区域*/
	private void hideEditRect(){
		KeyboardUtil.hideKeyboard(this);
		downEditRect();
	}
	/**下移*/
	private void downEditRect(){
		if(textBottom > 0) {
			makeEditAreaOriginal((float)textBottom);
			textBottom = 0;
		}
	}

	/**编辑区域上升的动画效果：指定高度*/
	public void makeEditAreaUpAndSmall(float to)
	{
		if (!this.editAreaIsUp)
		{
			ViewAnimationUtil.editAreaAnimator(rootLayout, 0.0F, -to, 1.0F, 1.0F,false);
			this.editAreaIsUp = true;
		}
	}
	/**编辑区域回到原始的动画效果*/
	public void makeEditAreaOriginal(float from)
	{
		if(this.editAreaIsUp) {
			ViewAnimationUtil.editAreaAnimator(rootLayout, -from, 0.0F, 1.0F, 1.0F,false);
			this.editAreaIsUp = false;
		}
	}
}
