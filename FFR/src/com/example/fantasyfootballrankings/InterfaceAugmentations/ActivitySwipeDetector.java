package com.example.fantasyfootballrankings.InterfaceAugmentations;

import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;

import com.ffr.fantasyfootballrankings.R;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.StorageAsyncTask;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ProgressBar;

public class ActivitySwipeDetector implements View.OnTouchListener {

	private Activity activity;
	public String origin;
	static final int MIN_DISTANCE = 275;
	private float downX, upX;
	public PlayerInfo hold;

	public ActivitySwipeDetector(final Activity activity, PlayerInfo playerInfo) {
		this.activity = activity;
		hold = playerInfo;
	}

	public final void onRightToLeftSwipe() {
		if (origin.equals("Popup")) {
			hold.swipeRightToLeft(activity);
		}
	}

	public void onLeftToRightSwipe() {
		if (origin.equals("Popup")) {
			hold.swipeLeftToRight(activity);
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		float deltaX = 0;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			// return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			deltaX = downX - upX;
			// swipe horizontal?
			if (Math.abs(deltaX) > MIN_DISTANCE) {
				// left or right
				if (deltaX < 0) {
					this.onLeftToRightSwipe();
					return true;
				}
				if (deltaX > 0) {
					this.onRightToLeftSwipe();
					return true;
				}
			} else {

			}

			// return true;
		}
		}

		return false;
	}
}