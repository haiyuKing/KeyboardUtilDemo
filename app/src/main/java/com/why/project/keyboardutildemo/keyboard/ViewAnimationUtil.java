package com.why.project.keyboardutildemo.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Used 界面上升下降动画效果工具类（全）
 */
public class ViewAnimationUtil {
	
	private static ViewAnimationListener mViewAnimationListener = null;
	public ViewAnimationUtil() {
	}

	/**区域的上升动画效果*/
	public static void editAreaAnimator(View view, float translationFromY, float translationToY, float scalingFromRatio, float scalingToRatio, final boolean doEnd) {
		ObjectAnimator animTY = ObjectAnimator.ofFloat(view, "translationY", new float[] {translationFromY, translationToY});
		ObjectAnimator animTSX = ObjectAnimator.ofFloat(view, "scaleX", new float[] {scalingFromRatio, scalingToRatio});
		ObjectAnimator animTSY = ObjectAnimator.ofFloat(view, "scaleY", new float[] {scalingFromRatio, scalingToRatio});
		AnimatorSet editAreaSet = new AnimatorSet();
		int EDIT_DURATION = 500;
		editAreaSet.setDuration((long)EDIT_DURATION);
		editAreaSet.playTogether(new Animator[] {animTY, animTSX, animTSY});
		editAreaSet.addListener(new AnimatorListenerAdapter() {

			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				if((doEnd) && mViewAnimationListener != null) {
					mViewAnimationListener.endAnimation();
				}
			}
		});
		editAreaSet.start();
	}


	public static void setViewAnimationListener(ViewAnimationListener mmViewAnimationListener) {
		mViewAnimationListener = mmViewAnimationListener;
	}

	public static abstract interface ViewAnimationListener
	{
		public abstract void endAnimation();
	}
}
