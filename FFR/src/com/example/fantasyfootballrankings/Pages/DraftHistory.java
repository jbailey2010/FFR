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
			String qbs = team.split("Quarterbacks: ")[1].split("\n")[0];
			String rbs = team.split("Running Backs: ")[1].split("\n")[0];
			String wrs = team.split("Wide Receivers: ")[1].split("\n")[0];
			String tes = team.split("Tight Ends: ")[1].split("\n")[0];
			String def = team.split("D/ST: ")[1].split("\n")[0];
			String ks = team.split("Kickers: ")[1].split("\n")[0];
			String[] qb = qbs.split(", ");
			String[] rb = rbs.split(", ");
			String[] wr = wrs.split(", ");
			String[] te = tes.split(", ");
			String[] d  = def.split(", ");
			String[] k  = ks.split(", ");
			double qbTotal = paaPos(qb);
			double rbTotal = paaPos(rb);
			double wrTotal = paaPos(wr);
			double teTotal = paaPos(te);
			double dTotal = paaPos(d);
			double kTotal = paaPos(k);
			double qbStart = paaStarters(qb, "QB");
			double rbStart = paaStarters(rb, "RB");
			double wrStart = paaStarters(wr, "WR");
			double teStart = paaStarters(te, "TE");
			double dStart = paaStarters(d, "D/ST");
			double kStart = paaStarters(k, "K");
			DecimalFormat df = new DecimalFormat("#.##");
			StringBuilder info = new StringBuilder(2000);
			info.append("Note: this is based on the currently calculated projections/PAA\n");
			info.append("Set the scoring/roster settings on the home screen to this draft's settings to see accurate versions of these numbers\n\n");
			info.append("Pos: PAA from starters (PAA total)\n\n");
			info.append("QB: " + qbStart + " (" + qbTotal + ")");
			info.append("\n\nRB: " + rbStart + " (" + rbTotal + ")");
			info.append("\n\nWR: " + wrStart + " (" + wrTotal + ")");
			info.append("\n\nTE: " + teStart + " (" + teTotal + ")");
			info.append("\n\nD/ST: "+dStart +  " (" +  dTotal + ")");
			info.append("\n\nK: " + kStart + " (" + kTotal + ")");
			double paaStart = qbStart + rbStart + wrStart + teStart + dStart + kStart;
			double paaBench = (qbTotal + wrTotal + teTotal + rbTotal + dTotal + kTotal) - paaStart;
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
	
	/**
	 * Gets the PAA of starters
	 * @param pos
	 * @param posStr
	 * @return
	 */
	public double paaStarters(String[] pos, String posStr)
	{
		double total = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		Roster r = ReadFromFile.readRoster(cont);
		int limit = 0;
		if(posStr.equals("QB"))
		{
			limit = r.qbs;
		}
		else if(posStr.equals("RB"))
		{
			limit = r.rbs;
		}
		else if(posStr.equals("WR"))
		{
			limit = r.wrs;
		}
		else if(posStr.equals("TE"))
		{
			limit = r.tes;
		}
		else if(posStr.equals("D/ST"))
		{
			limit = r.def;
		}
		else if(posStr.equals("K"))
		{
			limit = r.k;
		}
		Scoring s = ReadFromFile.readScoring(cont);
		if(s.catches > 0 && posStr.equals("WR"))
		{
			limit++;
		}
		else if(s.catches == 0 && posStr.equals("RB"))
		{
			limit++;
		}
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.paa > b.values.paa)
			    {
			        return -1;
			    }
			    if (a.values.paa < b.values.paa)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		for(int i = 0; i < pos.length; i++)
		{
			if(holder.parsedPlayers.contains(pos[i]))
			{
				for(PlayerObject player : holder.players)
				{
					if(player.info.position.equals(posStr) && player.info.name.equals(pos[i]))
					{
						inter.add(player);
						break;
					}
				}
			}
		} 
		for(int i = 0; i < limit; i++)
		{ 
			PlayerObject player = inter.poll();
			if(player != null)
			{
				total += player.values.paa;
			}
		}
		return Double.valueOf(df.format(total));
	}
	
	/**
	 * Gets the paa of all of the players at each position (given)
	 * @param pos
	 * @return
	 */
	public double paaPos(String[] pos)
	{
		double total = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		for(int i = 0; i < pos.length; i++)
		{
			if(holder.parsedPlayers.contains(pos[i]))
			{
				for(PlayerObject player : holder.players)
				{
					if(player.info.name.equals(pos[i]))
					{
						if(player.values.paa > 0 || player.values.paa < 0)
						{
							total += player.values.paa;
							break;
						}
					}
				}
			}
		}
		return Double.valueOf(df.format(total));
	}
}
