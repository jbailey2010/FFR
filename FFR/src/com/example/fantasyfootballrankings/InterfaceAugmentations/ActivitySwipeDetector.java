package com.example.fantasyfootballrankings.InterfaceAugmentations;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

public class ActivitySwipeDetector implements View.OnTouchListener {

 private Activity activity;
 public String origin;
 static final int MIN_DISTANCE = 300;
 private float downX, upX;
 public PlayerInfo hold;

 public ActivitySwipeDetector(final Activity activity, PlayerInfo playerInfo) { 
  this.activity = activity;
  hold = playerInfo;
 }

 public final void onRightToLeftSwipe() {
  	if(origin.equals("Popup"))
  	{
  		hold.swipeRightToLeft();
  	}
 }

 public void onLeftToRightSwipe(){
  	if(origin.equals("Popup"))
  	{
  		hold.swipeLeftToRight();
  	}
 }

 public boolean onTouch(View v, MotionEvent event) {
  switch(event.getAction()){
  case MotionEvent.ACTION_DOWN: {
   downX = event.getX();
   //   return true;
  }
  case MotionEvent.ACTION_UP: {
   upX = event.getX();
   float deltaX = downX - upX;

   // swipe horizontal?
   if(Math.abs(deltaX) > MIN_DISTANCE){
    // left or right
    if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
    if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
   } else {  }

   //     return true;
  }
  }
  return false;
 }
}