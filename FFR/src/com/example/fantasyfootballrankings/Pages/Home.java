package com.example.fantasyfootballrankings.Pages;



import java.io.IOException;








import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.HandleExport;
import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCSC;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFA;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNFL;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseNames;
import AsyncTasks.ParsingAsyncTask.ParsePermanentDataSets;
import FileIO.ReadFromFile;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
 
/**
 * The home class, sets up the three main buttons to go to 
 * trending players, team view, and/or rankings themselves
 * @author Jeff
 *
 */ 
public class Home extends Activity{
	//Some global variables, context and a few buttons
	Storage holder = new Storage();
	final Context cont = this;
	Dialog dialog;
	Button rankings;
	Button trending;
	Button news;
	
	
	long start; 
	
	  
	/**  
	 * Makes the buttons and sets the listeners for them
	 */   
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        rankings = (Button)findViewById(R.id.rankings);
        rankings.setOnClickListener(rankHandler);
        trending = (Button)findViewById(R.id.trending);
        trending.setOnClickListener(trendHandler);
        news = (Button)findViewById(R.id.news_button); 
        news.setOnClickListener(newsHandler);
        start = System.nanoTime();
        handleInitialRefresh();
		ManageInput.setUpScoring(cont, new Scoring(), false, holder);
		ManageInput.setUpRoster(cont, holder);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        try { 
        	HighLevel.parseRedZoneStats(new Storage());
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
		return true;
	}
	
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{  
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		switch (item.getItemId()) 
		{
			case R.id.export_names:
				if(holder.players.size() > 0)
				{
					callExport();
				}
				else
				{
					Toast.makeText(cont, "Can't export rankings until they are fetched", Toast.LENGTH_SHORT).show(); 
				}
				return true;
			case R.id.refresh_names:
				nameRefresh(dialog);
		    	return true;			
			case R.id.start_scoring:
				if(holder.players.size() > 0)
				{
					ManageInput.passSettings(cont, new Scoring(), true, holder);
				}
				else
				{
					Toast.makeText(cont, "Can't update scoring until the rankings are fetched", Toast.LENGTH_SHORT).show();
				}
		    	return true;	
			case R.id.start_roster:
				if(holder.players.size() > 0)
				{
					ManageInput.getRoster(cont, true, holder);
				}
				else
				{
					Toast.makeText(cont, "Can't update roster settings until the rankings are fetched", Toast.LENGTH_SHORT).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Calls the handle export fn
	 */
	public void callExport()
	{
		HandleExport.driveInit(HandleExport.orderPlayers(holder), dialog, cont);
	}
	
	/**
	 * Handles the initial fetching of player names
	 * and permanent data if they haven't been
	 * fetched (initial opening of the app)
	 */
	public void handleInitialRefresh()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		try {
			if(holder.players.size() < 5)
			{
				ReadFromFile.fetchPlayers(holder,cont, false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String checkExists = prefs.getString("O Line Ranks", "Not Set");
		if(checkExists.equals("Not Set"))
		{
			final ParsingAsyncTask stupid = new ParsingAsyncTask();
			ParseNames task = stupid.new ParseNames((Activity)cont);
		    task.execute(cont);
		    
		    ParsePermanentDataSets advancedData = stupid.new ParsePermanentDataSets((Activity) cont);
		    advancedData.execute(cont, new Storage());
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
				dialog.dismiss();
				ParseNames task = stupid.new ParseNames((Activity)cont);
			    task.execute(cont);	
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
}
