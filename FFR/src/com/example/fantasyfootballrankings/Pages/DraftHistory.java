package com.example.fantasyfootballrankings.Pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;

import jeff.isawesome.fantasyfootballrankings.R;
import jeff.isawesome.fantasyfootballrankings.R.layout;
import jeff.isawesome.fantasyfootballrankings.R.menu;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SimpleAdapter;
/**
 * Handles the draft history data
 * @author Jeff
 *
 */
public class DraftHistory extends Activity {
	public Context cont = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draft_history);
		setUpView();
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.draft_history, menu);
		return true;
	}
	
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.clear_drafts:
				clearDraft();
				return true;
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
		        return true;
			case R.id.view_rankings:
		        Intent intent_ranking = new Intent(cont, Rankings.class);
		        cont.startActivity(intent_ranking);		
 		        return true;
			case R.id.view_trending:
		        Intent team_intent = new Intent(cont, Trending.class);
		        cont.startActivity(team_intent);		
				return true;
			case R.id.news:
		        Intent news_intent = new Intent(cont, News.class);
		        cont.startActivity(news_intent);		
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Clears the draft data
	 */
	public void clearDraft() {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.clear_draft_history_confirm);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.draft_history_clear_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
	    });
	    Button submit = (Button)dialog.findViewById(R.id.draft_history_clear_submit);
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				WriteToFile.clearDraftData(cont);
				setUpView();
				dialog.dismiss();
			}
	    });
	}
	
	/**
	 * Sets the listview content
	 */
	public void setUpView(){
		BounceListView drafts = (BounceListView)findViewById(R.id.draft_history_listview);
		List<String> primary = ReadFromFile.readPrimData(cont);
		List<String> sec = ReadFromFile.readSecData(cont);
		List<Map<String, String>>data = new ArrayList<Map<String, String>>();
	    for(int i = 0; i < primary.size(); i++)
	    {
	    	Map<String, String> datum = new HashMap<String, String>();
	    	datum.put("main", primary.get(i));
	    	datum.put("sub", sec.get(i));
	    	data.add(datum);
	    }
	    SimpleAdapter adapter = new SimpleAdapter(cont, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    drafts.setAdapter(adapter);
	}

}
