package com.example.fantasyfootballrankings.InterfaceAugmentations;

import com.example.fantasyfootballrankings.Pages.DraftHistory;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.example.fantasyfootballrankings.Pages.News;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

public class NonListSwipeDetector implements View.OnTouchListener {
	 private Activity activity;
	 public String origin;
	 static final int MIN_DISTANCE = 140;
	 private float downX, upX;
	 public boolean isFarLeft;

	 public NonListSwipeDetector(final Activity activity, String source) { 
		  this.activity = activity;
		  origin = source;
	 }
	
	 public void handleToggle(){
	    	if(origin.equals("Home"))
	    	{
	    		Home.sideNavigationView.toggleMenu();
	    	}
	    	else if(origin.equals("History"))
	    	{
	    		DraftHistory.sideNavigationView.toggleMenu();
	    	}
	    	else if(origin.equals("Rankings"))
	    	{
	    		Rankings.sideNavigationView.toggleMenu();
	    	}
	    	else if(origin.equals("News"))
	    	{
	    		News.sideNavigationView.toggleMenu();
	    	}
	    	else if(origin.equals("Trending"))
	    	{
	    		Trending.sideNavigationView.toggleMenu();
	    	}
	    	else if(origin.equals("Import"))
	    	{
	    		ImportLeague.sideNavigationView.toggleMenu();
	    	}
	    }

	 public boolean onTouch(View v, MotionEvent event) {
		 float deltaX = 0;
		 switch(event.getAction()){
			  case MotionEvent.ACTION_DOWN: {
				   downX = event.getX();
				   if(downX < 10.0)
				   {
					   isFarLeft = true;
				   }
				   else
				   {
					   isFarLeft = false;
				   }
				   return true;
			  }
			  case MotionEvent.ACTION_UP: {
				   upX = event.getX();
				   deltaX = downX - upX;
				   if(Math.abs(deltaX) > MIN_DISTANCE){
					   if(deltaX < 0 && isFarLeft) { this.handleToggle(); return true; }
				   } 
			  }
		 }
		 return false;
	 }
}