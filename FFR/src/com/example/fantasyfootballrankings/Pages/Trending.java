package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.id;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.menu;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
	Storage holder = new Storage();
	ListView listview;
	/**
	 * Sets up the dialog to show up immediately
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		
		//Fetch the date of the posts, and convert it to a date
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	String storedPosts = prefs.getString("Posted Players", "Not Posted");
    	listview = (ListView)findViewById(R.id.listview_trending);
    	if(storedPosts != "Not Posted")
    	{
    		String[] posts = storedPosts.split("##");
    		List<String>postsList = Arrays.asList(posts);
    		ManageInput.handleArray(postsList, listview, this);
    	}
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
    			- date.getTime()) / (1000 * 60 * 60 * 24) > 4))
    	{
    		fetchTrending(holder);
    	}
    	else
    	{
    		ReadFromFile.fetchPostsLocal(holder, cont);
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
		try {
			ParseTrending.trendingPlayers(holder, cont);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
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
				try {
					ParseTrending.setUpLists(holder, 1, cont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });       
        week = (Button)findViewById(R.id.filter_week);
        week.setOnClickListener(new View.OnClickListener() 
        {   
            @Override
            public void onClick(View v) 
            {
            	try {
					ParseTrending.setUpLists(holder, 7, cont);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });     
        month = (Button)findViewById(R.id.filter_month);
        month.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
				try {
					ParseTrending.setUpLists(holder, 30, cont);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } 
        }); 
        all = (Button)findViewById(R.id.filter_all);
        all.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
				try {
					ParseTrending.setUpLists(holder, 365, cont);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }); 
	}
	
	/**
	 * Does sexy things
	 * @param holder
	 */
	public void handleParsed(Storage holder, Activity cont)
	{
		System.out.println("DONE " + holder.postedPlayers.size());
		for(PostedPlayer e:holder.postedPlayers)
		{
			System.out.println(e.name + ": " + e.count);
		}
	    listview = (ListView) cont.findViewById(R.id.listview_trending);
	    listview.setAdapter(null);
	    List<String> trendingPlayers = new ArrayList<String>(350);
	    PriorityQueue<PostedPlayer>inter = new PriorityQueue<PostedPlayer>(300, new Comparator<PostedPlayer>() 
		{
			@Override
			public int compare(PostedPlayer a, PostedPlayer b) 
			{
				if (a.count > b.count)
			    {
			        return -1;
			    }
			    if (a.count < b.count)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
	    while(!holder.postedPlayers.isEmpty())
	    {
	    	inter.add(holder.postedPlayers.poll());
	    }
	    while(!inter.isEmpty())
	    {
	    	PostedPlayer elem = inter.poll();
	    	trendingPlayers.add(elem.name + ": mentioned " + elem.count + " times");
	    }
	    WriteToFile.writePostsList(trendingPlayers, cont);
	    ManageInput.handleArray(trendingPlayers, listview, cont);
	}
}