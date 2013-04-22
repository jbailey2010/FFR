package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.id;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.menu;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Sets up the trending page
 * @author Jeff
 *
 */ 
public class Trending extends Activity {
	//Some globals, the dialog and the context
	Dialog dialog;
	final Context cont = this;
	Button day;
	Button week;
	Button month;
	Button all;
	final Storage holder = new Storage();
	/**
	 * Sets up the dialog to show up immediately
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		
		//Fetch the date of the posts, and convert it to a date
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	String storedDate = prefs.getString("Date of Posts", "Doesn't Matter");
    	Date date = new Date();
    	if(storedDate != "Doesn't Matter")
    	{
    		try {
    			date = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(storedDate);
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	//Get the posts, if they're not set, fetch them. If it's been 4+ days since
    	//they were fetched, fetch them. Otherwise, get from storage.
    	String checkExists = prefs.getString("Posts", "Not Set");
    	if(checkExists.equals("Not Set") || ((int)((new java.util.Date()).getTime()
    			- date.getTime()) / (1000 * 60 * 60 * 24) > 3))
    	{
    		fetchTrending(holder);
    	}
    	else
    	{
    		Storage.fetchPostsLocal(holder, cont);
    	} 
    	getFilterForPosts(holder);
	}

	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trending, menu);
		return true;
	}

	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		//All intents. Easy peasy.
		switch (item.getItemId()) 
		{
			case R.id.refresh:
				holder.posts.clear();
				fetchTrending(holder);
				return true;
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
		        return true;
			case R.id.view_rankings:
		        Intent intent = new Intent(cont, Rankings.class);
		        cont.startActivity(intent);		
 		        return true;
			case R.id.view_team:
		        Intent team_intent = new Intent(cont, Team.class);
		        cont.startActivity(team_intent);		
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Just because this has to be called twice, abstracted.
	 * Makes the loading dialog, and in a side thread calls the back
	 * function so both could happen. Yay, hackiness.
	 * @param holder
	 */
	public void fetchTrending(final Storage holder)
	{
		dialog = new Dialog(cont);
		dialog.setContentView(R.layout.trending_loading);
		dialog.show();
		dialog.setCancelable(false); 
		Thread thread=new Thread(new Runnable(){
			public void run(){
				try {
					ParseTrending.trendingPlayers(holder, cont);
					Storage.writePosts(holder, cont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(dialog.isShowing())
							dialog.dismiss();
					}
				});
			}
		});
		thread.start();		
	}
	
	/**
	 * Waits for a click of a button to get a filter
	 * timeframe
	 * @param holder
	 */
	private void getFilterForPosts(final Storage holder) 
	{
        day = (Button)findViewById(R.id.filter_day);
        day.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
        		dialog = new Dialog(cont);
        		dialog.setContentView(R.layout.trending_filter);
        		dialog.show();
        		dialog.setCancelable(false); 
        		Thread thread=new Thread(new Runnable(){
        			public void run(){
                    	try {
        					try {
        						ParseTrending.setUpLists(holder, 1, cont);
        				    	handleParsed(holder);
        					} catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        				} catch (ParseException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				runOnUiThread(new Runnable(){
        					@Override
        					public void run() {
        						if(dialog.isShowing())
        							dialog.dismiss();
        					}
        				});
        			}
        		});
        		thread.start();	
            }
        });       
        week = (Button)findViewById(R.id.filter_week);
        week.setOnClickListener(new View.OnClickListener() 
        {   
            @Override
            public void onClick(View v) 
            {
        		dialog = new Dialog(cont);
        		dialog.setContentView(R.layout.trending_filter);
        		dialog.show();
        		dialog.setCancelable(false); 
        		Thread thread=new Thread(new Runnable(){
        			public void run(){
                    	try {
        					try {
        						ParseTrending.setUpLists(holder, 7, cont);
        				    	handleParsed(holder);
        					} catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        				} catch (ParseException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				runOnUiThread(new Runnable(){
        					@Override
        					public void run() {
        						if(dialog.isShowing())
        							dialog.dismiss();
        					}
        				});
        			}
        		});
        		thread.start();	
            }
        });     
        month = (Button)findViewById(R.id.filter_month);
        month.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
        		dialog = new Dialog(cont);
        		dialog.setContentView(R.layout.trending_filter);
        		dialog.show();
        		dialog.setCancelable(false); 
        		Thread thread=new Thread(new Runnable(){
        			public void run(){
                    	try {
        					try {
        						ParseTrending.setUpLists(holder, 30, cont);
        				    	handleParsed(holder);
        					} catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        				} catch (ParseException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				runOnUiThread(new Runnable(){
        					@Override
        					public void run() {
        						if(dialog.isShowing())
        							dialog.dismiss();
        					}
        				});
        			}
        		});
        		thread.start();	
            } 
        }); 
        all = (Button)findViewById(R.id.filter_all);
        all.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
        		dialog = new Dialog(cont);
        		dialog.setContentView(R.layout.trending_filter);
        		dialog.show();
        		dialog.setCancelable(false); 
        		Thread thread=new Thread(new Runnable(){
        			public void run(){
                    	try {
        					try {
        						ParseTrending.setUpLists(holder, 365, cont);
        				    	handleParsed(holder);
        					} catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        				} catch (ParseException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				runOnUiThread(new Runnable(){
        					@Override
        					public void run() {
        						if(dialog.isShowing())
        							dialog.dismiss();
        					}
        				});
        			}
        		});
        		thread.start();	
            }
        }); 
	}
	
	/**
	 * Still needs to be done...
	 * @param holder
	 */
	private void handleParsed(Storage holder)
	{
		System.out.println("DONEZO " + holder.postedPlayers.size());
		for(PostedPlayer e:holder.postedPlayers)
		{
			System.out.println(e.name + ": " + e.count);
		}
	}
}