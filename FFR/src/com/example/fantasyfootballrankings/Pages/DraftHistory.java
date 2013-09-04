package com.example.fantasyfootballrankings.Pages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
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
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
/**
 * Handles the draft history data
 * @author Jeff
 *
 */
public class DraftHistory extends Activity {
	public Context cont;
	public static Storage holder = new Storage(null);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cont = this;
		setContentView(R.layout.activity_draft_history);
		setUpView();
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		if(holder.players.size() < 10 || prefs.getBoolean("Home Update Draft", false))
		{
			SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
			editor.putBoolean("Home Update Draft", false).commit();
	    	String checkExists2 = prefs.getString("Player Values", "Not Set");
	    	if(checkExists2 != "Not Set")
	    	{
				ReadFromFile.fetchPlayers(checkExists2, holder,cont, 5);
	    	}

		} 
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
	    drafts.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				handleDraftInformation(arg1);
			}
	    });
	}

	/**
	 * Gets more in depth info about each position
	 * @param v
	 */
	public void handleDraftInformation(View v)
	{
		TwoLineListItem tv = (TwoLineListItem)v;
		if(tv.getText2().getText().toString().contains("PAA"))
		{
			String team = tv.getText1().getText().toString();
			TeamAnalysis ta = new TeamAnalysis(team, holder, cont);
			DecimalFormat df = new DecimalFormat("#.##");
			StringBuilder info = new StringBuilder(2000);
			info.append("Note: this is based on the currently calculated projections/PAA\n");
			info.append("Set the scoring/roster settings on the home screen to this draft's settings to see accurate versions of these numbers\n\n");
			info.append("Pos: PAA from starters (PAA total)\n\n");
			info.append("QB: " + ta.qbStart + " (" + ta.qbTotal + ")");
			info.append("\n\nRB: " + ta.rbStart + " (" + ta.rbTotal + ")");
			info.append("\n\nWR: " + ta.wrStart + " (" + ta.wrTotal + ")");
			info.append("\n\nTE: " + ta.teStart + " (" + ta.teTotal + ")");
			info.append("\n\nD/ST: "+ta.dStart +  " (" +  ta.dTotal + ")");
			info.append("\n\nK: " + ta.kStart + " (" + ta.kTotal + ")");
			double paaStart = ta.qbStart + ta.rbStart + ta.wrStart + ta.teStart + ta.dStart + ta.kStart;
			double paaBench = (ta.qbTotal + ta.wrTotal + ta.teTotal + ta.rbTotal + ta.dTotal + ta.kTotal) - paaStart;
			info.append("\n\nPAA from starters: " + df.format(paaStart));
			info.append("\nPAA from bench: " + df.format(paaBench));
			info.append("\nPAA total: " + df.format(paaStart + paaBench) + "\n");	
			final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
		    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
			popUp.setContentView(R.layout.tweet_popup);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(popUp.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.FILL_PARENT;
		    popUp.getWindow().setAttributes(lp);
		    popUp.show(); 
		    TextView textView = (TextView)popUp.findViewById(R.id.tweet_field);
		    textView.setText(info.toString());
		    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
		    close.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					popUp.dismiss();
				}
		    });
		}
	}
	
	
}
