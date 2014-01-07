package com.example.fantasyfootballrankings.Pages;



import java.util.Random;
import java.util.Set;

import com.ffr.fantasyfootballrankings.R;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.HandleExport;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseNames;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
 
/**
 * The home class, sets up the three main buttons to go to 
 * trending players, team view, and/or rankings themselves
 * @author Jeff
 *
 */ 
public class Home extends Activity{
	//Some global variables, context and a few buttons
	public static Storage holder = new Storage(null);
	Context cont = this;
	Dialog dialog;
	Button rankings; 
	Button trending;
	Button news;
	Button drafts;
	Button importLeague;
	long start; 
	Menu m;
	SideNavigationView sideNavigationView;
	  
	/**  
	 * Makes the buttons and sets the listeners for them
	 */   
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ActionBar ab = getActionBar();
		cont = this;
		//ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		ISideNavigationCallback sideNavigationCallback = new ISideNavigationCallback() {
		    @Override
		    public void onSideNavigationItemClick(int itemId) {
		    	switch (itemId) {
	            case R.id.side_navigation_menu_item1:
	            	Intent intent = new Intent(cont, Home.class);
	    	        cont.startActivity(intent);	
	                break;
	            case R.id.side_navigation_menu_item2:
	            	Intent intent2 = new Intent(cont, Rankings.class);
	    	        cont.startActivity(intent2);	
	                break;
	            case R.id.side_navigation_menu_item3:
	            	Intent intent5 = new Intent(cont, ImportLeague.class);
	    	        cont.startActivity(intent5);
	                break;
	            case R.id.side_navigation_menu_item4:
	            	Intent intent3 = new Intent(cont, Trending.class);
	    	        cont.startActivity(intent3);		
	                break;
	            case R.id.side_navigation_menu_item5:
	            	Intent intent4 = new Intent(cont, News.class);
	    	        cont.startActivity(intent4);
	                break;
	            case R.id.side_navigation_menu_item6:
	            	Intent intent6 = new Intent(cont, DraftHistory.class);
	    	        cont.startActivity(intent6);
	                break;
	            default:
	                return;
		    	}
		    }
		};
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
	    sideNavigationView.setMenuItems(R.menu.side_navigation_view);
	    sideNavigationView.setMenuClickCallback(sideNavigationCallback);
	   // sideNavigationView.setMode(/*SideNavigationView.Mode*/);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rankings = (Button)findViewById(R.id.rankings);
        rankings.setOnClickListener(rankHandler);
        trending = (Button)findViewById(R.id.trending);
        trending.setOnClickListener(trendHandler);
        news = (Button)findViewById(R.id.news_button); 
        news.setOnClickListener(newsHandler);
        drafts = (Button)findViewById(R.id.draft_history);
        drafts.setOnClickListener(draftHandler);
        importLeague = (Button)findViewById(R.id.import_league_btn); 
        importLeague.setOnClickListener(importHandler);
        WindowManager wm = (WindowManager) cont.getSystemService(Context.WINDOW_SERVICE);
		 Display display = wm.getDefaultDisplay();
		 Resources r = cont.getResources();
		 int height = display.getHeight();
		 int newHeight = height / 14;
		 float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newHeight, r.getDisplayMetrics());
		 		 
		ImageView pic = (ImageView)findViewById(R.id.football_icon_home);
		final Random message = new Random();
		pic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int random = message.nextInt();
				if(random%2 == 0)
				{
					Toast.makeText(cont, "Thanks for downloading the app!", Toast.LENGTH_SHORT).show();
				}
				else if(random%3 == 0 || random%7 == 0)
				{
					Toast.makeText(cont, "Stop clicking the image.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(cont, "Clicking this image does nothing.", Toast.LENGTH_SHORT).show();
				}
			} 
		});
		 android.view.ViewGroup.LayoutParams params1 = rankings.getLayoutParams();
		 params1.height = (int) px;
		 rankings.setLayoutParams(params1);
		 android.view.ViewGroup.LayoutParams params2 = trending.getLayoutParams();
		 params2.height = (int) px;
		 trending.setLayoutParams(params2);
		 android.view.ViewGroup.LayoutParams params3 = news.getLayoutParams();
		 params3.height = (int) px;
		 news.setLayoutParams(params3);
		 android.view.ViewGroup.LayoutParams params4 = drafts.getLayoutParams();
		 params4.height = (int) px;
		 drafts.setLayoutParams(params4);
		 android.view.ViewGroup.LayoutParams params5 = importLeague.getLayoutParams();
		 params5.height = (int)px;
		 importLeague.setLayoutParams(params5);
        start = System.nanoTime();
        handleInitialRefresh();
        if(ReadFromFile.readFirstOpen(cont))
        {
        	helpPopUp();
        	WriteToFile.writeFirstOpen(cont);
			if(ManageInput.confirmInternet(cont))
			{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				final ParsingAsyncTask stupid = new ParsingAsyncTask();
				ParseNames task = stupid.new ParseNames((Activity)cont, true);
			    task.execute(cont);
			}
			else
			{
				Toast.makeText(cont, "No Internet Connection Available. The Names List Must Be Fetched, So Please Connect and Refresh it Manually to Avoid Problems With The Rankings", Toast.LENGTH_LONG).show();
			}
        }
        
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        try { 
        	HighLevel.getROSRankingsWrapper(new Storage(cont), cont);
		} catch (IOException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}*/
	}  
	 
	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		m = menu;
		return true;
	}
	
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{  
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		boolean isStored = false;
		if(holder.players.size() > 0)
		{
			isStored = true;
		}
		switch (item.getItemId()) 
		{
			case R.id.export_names:
				if(isStored)
				{ 
					if(!holder.isRegularSeason)
					{
						callExport();
					}
					else
					{
						Toast.makeText(cont, "This is a preseason only feature", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(cont, "Can't export rankings until they are fetched", Toast.LENGTH_SHORT).show(); 
				}
				return true;
			case R.id.refresh_names:
				nameRefresh(dialog);
		    	return true;	
			case android.R.id.home:
		        sideNavigationView.toggleMenu();
		        return true;
			case R.id.credit_home:
				helpDialog(cont);
				return true;
			case R.id.start_scoring:
				ManageInput.passSettings(cont, new Scoring(), isStored, holder);
		    	return true;	
			case R.id.start_roster:
				ManageInput.getRoster(cont, isStored, holder);
				return true;
			case R.id.auction_or_snake:
				ManageInput.isAuctionOrSnake(cont, holder);
				return true;
			case R.id.help_home:
				helpPopUp();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	
	/**
	 * Handles the displaying of the help popup for the home page. Nothing else, very simple
	 * @param cont2
	 */
	private void helpDialog(Context cont2) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.home_credit);
		dialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button close = (Button)dialog.findViewById(R.id.credit_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * Handles the help dialog popup
	 */
	public void helpPopUp()
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.help_home);
		dialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button close = (Button)dialog.findViewById(R.id.help_home_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * Calls the handle export fn
	 */
	public void callExport()
	{
		HandleExport.driveInit(HandleExport.orderPlayers(holder, cont), dialog, cont, holder);
	} 
	
	/**
	 * Handles the initial fetching of player names
	 * and permanent data if they haven't been
	 * fetched (initial opening of the app)
	 */
	public void handleInitialRefresh()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		Set<String> checkExists = prefs.getStringSet("Player Values", null);
		if(checkExists != null &&( prefs.getBoolean("Rankings Update Home", false) || holder.players.size() < 5))
		{
			ReadFromFile.fetchPlayers(checkExists, holder,cont, 1);
			SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
			editor.putBoolean("Rankings Update Home", false).commit();
		}
	}
	
	/**
	 * Comes back here after re-loading the data to see if the menu needs altering
	 */
	public void seeIfInvalid()
	{
		if(m != null && holder.isRegularSeason)
		{
			MenuItem a = (MenuItem)m.findItem(R.id.auction_or_snake);
			a.setVisible(false);
			a.setEnabled(false);
		}
	}
	
	/**
	 * Handles the name refreshing given the prompt
	 * @param dialog
	 */
	public void nameRefresh(final Dialog dialog)
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.refresh_list);
		dialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button cancel = (Button)dialog.findViewById(R.id.cancel_list_refresh);
		Button submit = (Button)dialog.findViewById(R.id.confirm_list_refresh);
		final ParsingAsyncTask stupid = new ParsingAsyncTask();
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		submit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				if(ManageInput.confirmInternet(cont))
				{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					dialog.dismiss();
					ParseNames task = stupid.new ParseNames((Activity)cont, false);
				    task.execute(cont);	
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection", Toast.LENGTH_SHORT).show();
				}
	    	}	
		});	
	}
	

	/**
	 * Sends the rank button to the ranking page
	 */
	View.OnClickListener rankHandler = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
	        Intent intent = new Intent(cont, Rankings.class);
	        cont.startActivity(intent);		
		}
	};	
	
	/**
	 * Sends the trending button to the ranking page
	 */
	View.OnClickListener trendHandler = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
	        Intent intent = new Intent(cont, Trending.class);
	        cont.startActivity(intent);		
		}	
	};
	
	/**
	 * Sends the news button to the news page
	 */
	View.OnClickListener newsHandler = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
	        Intent intent = new Intent(cont, News.class);
	        cont.startActivity(intent);		
		}	
	};
	
	/**
	 * Sends the user to the draft saved page
	 */
	View.OnClickListener draftHandler = new View.OnClickListener() 
	{
		public void onClick(View v) 
		{
	        Intent intent = new Intent(cont, DraftHistory.class);
	        cont.startActivity(intent);		
		}	
	};
	
	/**
	 * Sends the user to the import league page
	 */
	View.OnClickListener importHandler = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			Intent intent = new Intent(cont, ImportLeague.class);
			cont.startActivity(intent);
		}
	};
}
