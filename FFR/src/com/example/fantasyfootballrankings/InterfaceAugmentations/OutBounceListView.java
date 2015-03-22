package com.example.fantasyfootballrankings.InterfaceAugmentations;

import com.example.fantasyfootballrankings.Pages.Rankings;
import com.ffr.fantasyfootballrankings.R;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OutBounceListView extends ListView {

	 /**
	  * Implements the below established maximum
	  */
	 public OutBounceListView(Context context) {
		  super(context);
	 }

	 /**
	  * Implements the below established maximum
	  */
	 public OutBounceListView(Context context, AttributeSet attrs) {
		  super(context, attrs);
	 }

	 /**
	  * Implements the below established maximum
	  */
	 public OutBounceListView(Context context, AttributeSet attrs, int defStyle) {
		  super(context, attrs, defStyle);
	 }


    private final Camera mCamera = new Camera();
    private final Matrix mMatrix = new Matrix();
    /** Paint object to draw with */
    private Paint mPaint;


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    	String tv = ((TextView)((RelativeLayout)child).findViewById(R.id.text1)).getText().toString();
    	if((tv.equals(Rankings.swipedText) && Rankings.isSwiping)){
    		super.drawChild(canvas, child, drawingTime);
    		return true;
    	}
        // get top left coordinates
        final int top = child.getTop();
        final int left = child.getLeft();
        Bitmap bitmap = child.getDrawingCache(); 
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }

        final int centerY = child.getHeight() / 2;
        final int centerX = child.getWidth() / 2;
        final int radius = getHeight() / 2;
        final int absParentCenterY = getTop() + getHeight() / 2;
        final int absChildCenterY = child.getTop() + centerX;
        final int distanceY = absParentCenterY - absChildCenterY;
        final int absDistance = Math.min(radius, Math.abs(distanceY));

        final float translateZ = (float) Math.sqrt((radius * radius) - (absDistance * absDistance));

        double radians = Math.acos((float) absDistance / radius);
        double degree = 90 - (180 / Math.PI) * radians;

        mCamera.save();
        mCamera.translate(0, 0, (radius - translateZ)/6);
        if (distanceY < 0) {
            degree = 360 - degree;
        }
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
 
        // create and initialize the paint object
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
        }

        mMatrix.preTranslate(-centerX, -centerY);
        mMatrix.postTranslate(centerX, centerY);
        mMatrix.postTranslate(left, top);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        return false;
    } 

}
