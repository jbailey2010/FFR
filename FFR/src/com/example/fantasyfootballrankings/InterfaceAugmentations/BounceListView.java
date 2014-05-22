package com.example.fantasyfootballrankings.InterfaceAugmentations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;
/**
 * Adds bouncing of the listview
 * @author Jeff
 *
 */
public class BounceListView extends ListView {
	 private static final int MAX_Y_OVERSCROLL_DISTANCE = 250;
	
	 private Context mContext;
	 private int mMaxYOverscrollDistance;

	 /**
	  * Implements the below established maximum
	  */
	 public BounceListView(Context context) {
		  super(context);
		  mContext = context;
		  initBounceListView();
	 }

	 /**
	  * Implements the below established maximum
	  */
	 public BounceListView(Context context, AttributeSet attrs) {
		  super(context, attrs);
		  mContext = context;
		  initBounceListView();
	 }

	 /**
	  * Implements the below established maximum
	  */
	 public BounceListView(Context context, AttributeSet attrs, int defStyle) {
		  super(context, attrs, defStyle);
		  mContext = context;
		  initBounceListView();
	 }

	 /**
	  * Establishes how far to overscroll
	  */
	 private void initBounceListView() {
		  // get the density of the screen and do some maths with it on the max
		  // overscroll distance
		  // variable so that you get similar behaviors no matter what the screen
		  // size
		
		  final DisplayMetrics metrics = mContext.getResources()
		    .getDisplayMetrics();
		  final float density = metrics.density;
		
		  mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	 }
	
	 @Override
	 protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
		   int scrollY, int scrollRangeX, int scrollRangeY,
		   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		  // This is where the magic happens, we have replaced the incoming
		  // maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
		  return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
		    scrollRangeX, scrollRangeY, maxOverScrollX,
		    mMaxYOverscrollDistance, isTouchEvent);
	 }

}