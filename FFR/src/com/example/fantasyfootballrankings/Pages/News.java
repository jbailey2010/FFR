package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.menu;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;

import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class News extends Activity {
	public static Context cont;
	public Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		cont = this;
		handleInitialLoading();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}
	
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		dialog = new Dialog(cont);
		switch (item.getItemId()) 
		{
			case R.id.refresh_news:
				refreshNewsDialog();
				return true;
			//New page opens up entirely for going home
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
		        return true;
			case R.id.view_rankings:
		        Intent intent_ranking = new Intent(cont, Rankings.class);
		        cont.startActivity(intent_ranking);		
 		        return true;
		    //New page opens up entirely for viewing the team page
			case R.id.view_team:
		        Intent intent = new Intent(cont, Team.class);
		        cont.startActivity(intent);		
 		        return true;
 		    //New page opens up entirely for viewing trending players
			case R.id.view_trending:
		        Intent team_intent = new Intent(cont, Trending.class);
		        cont.startActivity(team_intent);		
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Handles conditional loading of the news
	 */
	public static void handleInitialLoading()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String newsWhole = prefs.getString("News RotoWorld", "Not Set");
		if(newsWhole.equals("Not Set"))
		{
			ParseNews.startNewsAsync(cont);
		}
		else
		{
			ParseNews.startNewsReading(cont);
		}
	}

	/**
	 * Handles conditional refreshing of news
	 */
	public static void refreshNewsDialog()
	{
		final Dialog dialog = new Dialog(cont);
		dialog.setContentView(R.layout.refresh_news);
		Button submit = (Button)dialog.findViewById(R.id.news_refresh_submit);
		submit.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	ParseNews.startNewsAsync(cont);
            	dialog.dismiss();
            }
		});
		Button cancel = (Button)dialog.findViewById(R.id.news_refresh_cancel);
		cancel.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	dialog.dismiss();
            }
		});
		
		dialog.show();
	}
	
	/**
	 * Handles the showing of the listview of news
	 * @param result
	 * @param cont
	 */
	public static void handleNewsListView(List<NewsObjects> result, Activity cont) 
	{
		ListView listview = (ListView)cont.findViewById(R.id.listview_news);
	    List<String> news = new ArrayList<String>(10000);
	    for(NewsObjects newsObj : result)
	    {
	    	StringBuilder newsBuilder = new StringBuilder(1000);
	    	newsBuilder.append(newsObj.news + "\n\n" + newsObj.impact + "\n\n"
	    			 + newsObj.source + "\n" + "Date: " + newsObj.date + "\n");
	    	news.add(newsBuilder.toString());
	    }
	    ManageInput.handleArray(news, listview, cont);
	}

}
