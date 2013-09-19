package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.LeagueImports.ESPNImport;
import com.ffr.fantasyfootballrankings.R;
import com.ffr.fantasyfootballrankings.R.layout;
import com.ffr.fantasyfootballrankings.R.menu;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.ValueDependentColor;

import FileIO.ReadFromFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView.GraphViewData;
/**
 * Handles the importing of leagues
 * @author Jeff
 *
 */
public class ImportLeague extends Activity {
	public Context cont;
	public static Storage holder = new Storage(null);
	public static LinearLayout ll;

	/**
	 * Sets up the layout of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_league);
		cont = this;
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		if(holder.players.size() < 10 || prefs.getBoolean("Home Update Import", false) || prefs.getBoolean("Rankings Update Import", false))
		{
			if(Home.holder.players != null && Home.holder.players.size() > 5 && !prefs.getBoolean("Home Update Import",  false) 
					&& !prefs.getBoolean("Rankings Update Import",false))
			{
				holder = Home.holder; 
			}
			else if(Home.holder.players == null || Home.holder.players.size() < 5 || 
					prefs.getBoolean("Home Update Import", false) || prefs.getBoolean("Rankings Update Import", false))
			{
				SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
				editor.putBoolean("Home Update Import", false).commit();
				editor.putBoolean("Rankings Update Import", false).commit();
		    	String checkExists2 = prefs.getString("Player Values", "Not Set");
		    	if(!checkExists2.equals("Not Set"))
		    	{
					ReadFromFile.fetchPlayers(checkExists2, holder,cont, 5);
		    	}
			}
		} 
		ll = (LinearLayout)findViewById(R.id.import_base);
		handleLayoutInit();
	}
 
	/**
	 * Makes the menu appear
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.import_league, menu);
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
			case R.id.draft_history:
				Intent hist_intent = new Intent(cont, DraftHistory.class);
				cont.startActivity(hist_intent);
				return true;
			case R.id.news:
		        Intent news_intent = new Intent(cont, News.class);
		        cont.startActivity(news_intent);		
				return true;
			case R.id.importa_league:
				if(ManageInput.confirmInternet(cont))
				{
					if(holder.players != null && holder.players.size() > 4)
					{
						handleImportInit();
					}
					else
					{
						Toast.makeText(cont, "Can't import a league before fetching the rankings", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(cont, "No Internet Available", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.clear_imports:
				clearImports();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Clears all of the stored data
	 */
	public void clearImports()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.remove("ESPN Username");
		editor.remove("ESPN Password");
		editor.remove("ESPN Credentials Stored");
		editor.remove("Number of Leagues Imported");
		String oldKeys = prefs.getString("Imported League Keys", "");
		String[]oldKeysSplit = oldKeys.split("~~~");
		for(String key : oldKeysSplit)
		{
			editor.remove(key);
		}
		editor.remove("Imported League Keys");
		Toast.makeText(cont, "Data cleared", Toast.LENGTH_SHORT).show();
		editor.commit();
		handleLayoutInit();
	}
	
	
	/**
	 * Handles the initial importing of a league
	 */
	public void handleImportInit()
	{
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.import_service_decider);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    Button close = (Button)popUp.findViewById(R.id.import_decider_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				return;
			}
	    });
	    final RadioButton espn = (RadioButton)popUp.findViewById(R.id.espn_import);
	    Button submit = (Button)popUp.findViewById(R.id.import_decide_submit);
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(espn.isChecked())
				{
					espnGetLeagueID();
					popUp.dismiss();
				}
			}
	    });
	}
	
	/**
	 * Puts up a pop up to get the ESPN league ID
	 */
	public void espnGetLeagueID()
	{
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.import_espn_league_id);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    Button close = (Button)popUp.findViewById(R.id.import_espnid_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				return;
			}
	    });
	    Button back = (Button)popUp.findViewById(R.id.import_espnid_back);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				handleImportInit();
				return;
			}
	    });
	    final EditText id = (EditText)popUp.findViewById(R.id.league_id_espn_input);
	    Button submit = (Button)popUp.findViewById(R.id.espn_id_input_submit);
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String test = id.getText().toString();
				if(test.length() > 1 && ManageInput.isInteger(test))
				{
					popUp.dismiss();
					callESPNParsing(test);
				}
				else
				{
					Toast.makeText(cont, "Please enter a number", Toast.LENGTH_SHORT).show();
				}
			}
	    });
	}
	
	/**
	 * Calls the espn parser
	 * @param id
	 */
	public void callESPNParsing(String id)
	{
		ESPNImport espnImporter = new ESPNImport(holder, this);
		try {
			espnImporter.handleESPNParsing("http://games.espn.go.com/ffl/leaguerosters?leagueId=" + id, cont);
		} catch (IOException e) {
			Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Handles the displaying of the layouts
	 */
	public void handleLayoutInit()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		ll.removeAllViews();
		if(prefs.getInt("Number of Leagues Imported", 0) == 0)
		{
			setNoContentLayout();
		}
		else
		{
			setLeaguesImportedList();
		}
	}

	/**
	 * If there is no content to be loaded, this says that
	 */ 
	public void setNoContentLayout() 
	{
		View res = ((Activity) cont).getLayoutInflater().inflate(R.layout.import_none_imported, ll, false);
		ll.addView(res);
	}
	
	/** 
	 * Gives the list of imported leagues to choose from
	 */
	public void setLeaguesImportedList()
	{
		View res = ((Activity)cont).getLayoutInflater().inflate(R.layout.leagues_imported_list, ll, false);
		ListView list = (ListView)res.findViewById(R.id.leagues_imported_list);
	    List<Map<String, String>>data = new ArrayList<Map<String, String>>();
		SimpleAdapter adapter = new SimpleAdapter(cont, data, 
				R.layout.imported_listview_elem_stats,
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    list.setAdapter(adapter);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String usedKeys = prefs.getString("Imported League Keys", "");
		String[] keySet = ManageInput.tokenize(usedKeys, '~', 3);
		for(String key : keySet)
		{
			Map<String, String> datum = new HashMap<String, String>();
			String[] keySplit = ManageInput.tokenize(key, '@', 3);
			String teamData = prefs.getString(key, "SHIT");
			String[] perTeam = ManageInput.tokenize(teamData, '@', 3);

			datum.put("main", keySplit[1]);
			datum.put("sub", "Hosted on " + keySplit[0] + "\n" + perTeam.length + " team league");
			data.add(datum);
			adapter.notifyDataSetChanged();
		}
		ll.addView(res);
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String keyPart1 = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				String keyPart2 = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text2)).getText().toString().split("Hosted on ")[1].split("\n")[0];
				String key = keyPart2 + "@@@" + keyPart1;
				handleLeaguePopulation(key);
			}
		});
	}

	/**
	 * Handles populating the league information area
	 * @param key
	 */
	public void handleLeaguePopulation(String key)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String leagueDataWhole = prefs.getString(key, "SHIT").split("LEAGUEURLSPLIT")[1];
		String[] perTeam = ManageInput.tokenize(leagueDataWhole, '@', 3);
		List<TeamAnalysis> teamList = new ArrayList<TeamAnalysis>();
		for(String teamSet : perTeam)
		{
			String[] teamArr = ManageInput.tokenize(teamSet, '~', 2);
			TeamAnalysis teamData = new TeamAnalysis(teamArr[0], teamArr[1], holder, cont);
			teamList.add(teamData);
		}
		String[] keySet = key.split("@@@");
		final ImportedTeam newImport = new ImportedTeam(teamList, keySet[1], keySet[0]);
		View res = ((Activity)cont).getLayoutInflater().inflate(R.layout.league_stats_output, ll, false);
		//Below sets the team information
	    List<Map<String, String>>data = new ArrayList<Map<String, String>>();
		SimpleAdapter adapter = new SimpleAdapter(cont, data, 
	    		R.layout.imported_listview_elem_team, 
	    		new String[] {"head", "main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2, R.id.text3});
		ListView list = (ListView)res.findViewById(R.id.imported_teams_info);
		DecimalFormat df = new DecimalFormat("#.##");
		for(TeamAnalysis team : newImport.teams)
		{
			Map<String, String> datum = new HashMap<String, String>();
			datum.put("head", team.teamName);
			datum.put("main", team.team);
			datum.put("sub", df.format(paaTotal(team)) + " PAA total\n" + df.format(paaStart(team)) + " PAA from starters");
			data.add(datum);
		}
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				teamSpecPopUp(arg1, newImport);
			}
	    });
	    //Handles the back button
	    ImageView back = (ImageView)res.findViewById(R.id.back_button_league_stats);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				handleLayoutInit();
			}
	    });
	    //Handles the basic league information
	    TextView name = (TextView)res.findViewById(R.id.league_name);
	    name.setText(newImport.leagueName);
	    handleLongClick(name, newImport, cont);
	    TextView host = (TextView)res.findViewById(R.id.hostName);
	    host.setText("Hosted on " + newImport.leagueHost);
	    //Handles the statistic part of the layout
	    PriorityQueue<TeamAnalysis> totalPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
		{
			@Override
			public int compare(TeamAnalysis a, TeamAnalysis b) 
			{
				double paaA = paaTotal(a);
				double paaB = paaTotal(b);
				if (paaA > paaB)
			    {
			        return -1;
			    }
			    if (paaA < paaB)
			    {
			    	return 1;
			    } 
			    return 0;
			}
		});
	    PriorityQueue<TeamAnalysis> startPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				double paaA = paaStart(a);
	    				double paaB = paaStart(b);
	    				if (paaA > paaB)
	    			    {
	    			        return -1;
	    			    }
	    			    if (paaA < paaB)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> benchPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				double paaA = paaBench(a);
	    				double paaB = paaBench(b);
	    				if (paaA > paaB)
	    			    {
	    			        return -1;
	    			    }
	    			    if (paaA < paaB)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> qbPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.qbTotal > b.qbTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.qbTotal < b.qbTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> rbPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.rbTotal > b.rbTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.rbTotal < b.rbTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> wrPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.wrTotal > b.wrTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.wrTotal < b.wrTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> tePAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.teTotal > b.teTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.teTotal < b.teTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> dPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.dTotal > b.dTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.dTotal < b.dTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> kPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.kTotal > b.kTotal)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.kTotal < b.kTotal)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    StringBuilder paaTotal = new StringBuilder(1000);
	    StringBuilder paaStart = new StringBuilder(1000);
	    StringBuilder paaBench = new StringBuilder(1000);
	    StringBuilder qb = new StringBuilder(1000);
	    StringBuilder rb = new StringBuilder(1000);
	    StringBuilder wr = new StringBuilder(1000);
	    StringBuilder te = new StringBuilder(1000);
	    StringBuilder d  = new StringBuilder(1000);
	    StringBuilder k  = new StringBuilder(1000);
	    for(TeamAnalysis team : newImport.teams)
	    {
	    	totalPAA.add(team);
	    	startPAA.add(team);
	    	benchPAA.add(team);
	    	qbPAA.add(team);
	    	rbPAA.add(team);
	    	wrPAA.add(team);
	    	tePAA.add(team);
	    	dPAA.add(team);
	    	kPAA.add(team);
	    }
	    int counter = 0;
	    while(!totalPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = totalPAA.poll();
	    	paaTotal.append(counter + ") " + iter.teamName + ": " + df.format(paaTotal(iter)) + "\n");
	    }
	    counter = 0;
	    while(!startPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = startPAA.poll();
	    	paaStart.append(counter + ") " + iter.teamName + ": " + df.format(paaStart(iter)) + "\n");
	    }
	    counter = 0;
	    while(!benchPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = benchPAA.poll();
	    	paaBench.append(counter + ") " + iter.teamName + ": " + df.format(paaBench(iter)) + "\n");
	    }
	    counter = 0;
	    while(!qbPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = qbPAA.poll();
	    	qb.append(counter + ") " + iter.teamName + ": " + df.format(iter.qbTotal) + "\n");
	    }
	    counter = 0;
	    while(!rbPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = rbPAA.poll();
	    	rb.append(counter + ") " + iter.teamName + ": " + df.format(iter.rbTotal) + "\n");
	    }
	    counter = 0;
	    while(!wrPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = wrPAA.poll();
	    	wr.append(counter + ") " + iter.teamName + ": " + df.format(iter.wrTotal) + "\n");
	    }
	    counter = 0;
	    while(!tePAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = tePAA.poll();
	    	te.append(counter + ") " + iter.teamName + ": " + df.format(iter.teTotal) + "\n");
	    }
	    counter = 0;
	    while(!dPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = dPAA.poll();
	    	d.append(counter + ") " + iter.teamName + ": " + df.format(iter.dTotal) + "\n");
	    }
	    counter = 0;
	    while(!kPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = kPAA.poll();
	    	k.append(counter + ") " + iter.teamName + ": " + df.format(iter.kTotal) + "\n");
	    }
	    List<Map<String, String>>data2 = new ArrayList<Map<String, String>>();
		SimpleAdapter adapter2 = new SimpleAdapter(cont, data2, 
	    		R.layout.imported_listview_elem_stats, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		ListView list2 = (ListView)res.findViewById(R.id.imported_league_rankings);
		Map<String, String> datum = new HashMap<String, String>();
		datum.put("main", "Total PAA");
		datum.put("sub", paaTotal.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "PAA From Starters");
		datum.put("sub", paaStart.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum = new HashMap<String, String>();
		datum.put("main", "PAA From Backups");
		datum.put("sub", paaBench.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "QB PAA Rankings");
		datum.put("sub", qb.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "RB PAA Rankings");
		datum.put("sub", rb.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "WR PAA Rankings");
		datum.put("sub", wr.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "TE PAA Rankings");
		datum.put("sub", te.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "D/ST PAA Rankings");
		datum.put("sub", d.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "K PAA Rankings");
		datum.put("sub", k.toString());
		data2.add(datum);
		adapter2.notifyDataSetChanged();
	    list2.setAdapter(adapter2);
	    ll.removeAllViews();
	    list.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
	    list2.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
	    handleListOnItemClick(list2, newImport);
	    //Help work
	    final TextView helpTeams = (TextView)res.findViewById(R.id.team_help_import);
	    final TextView helpRanks = (TextView)res.findViewById(R.id.rankings_help_import);
	    TextView help = (TextView)res.findViewById(R.id.help_button_league_stats);
	    help.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!helpTeams.isShown())
				{ 
					helpTeams.setVisibility(View.VISIBLE);
					helpRanks.setVisibility(View.VISIBLE);
				}
				else
				{
					helpTeams.setVisibility(View.GONE);
					helpRanks.setVisibility(View.GONE);
				}
			}
	    });
	    ll.addView(res);
	}
	
	/**
	 * Handles the onclick of the team info list
	 * @param list
	 * @param newImport
	 */
	public void handleListOnItemClick(ListView list, final ImportedTeam newImport) {
		OnItemClickListener listener = new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showGraph(newImport, arg1);
			}
			
		};
		list.setOnItemClickListener(listener);
	}
	
	/**
	 * Handles the logic of deciding what to do when the name has been clicked
	 */
	public void handleLongClick(TextView name, final ImportedTeam newImport, final Context cont)
	{
		name.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				if(ManageInput.confirmInternet(cont))
				{
					if(((TextView)((Activity)cont).findViewById(R.id.hostName)).getText().toString().contains("ESPN"))
					{
						clearDataESPNInit((TextView)v, newImport, cont);
					}
				}
				else
				{
					Toast.makeText(cont, "No internet connection available, can't refresh the league", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			
		});		
	}
	
	public void clearDataESPNInit(TextView name, ImportedTeam newImport, Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		int numImported = prefs.getInt("Number of Leagues Imported", 1);
		editor.putInt("Number of Leagues Imported", numImported-1);
		String keyPart2 = name.getText().toString();
		String keyPart1 = ((TextView)findViewById(R.id.hostName)).getText().toString().split("Hosted on ")[1];
		String key = keyPart1 + "@@@" + keyPart2;
		String remKey = key + "~~~";
		System.out.println(key);
		String oldKeys = prefs.getString("Imported League Keys", "");
		System.out.println(oldKeys);
		System.out.println(oldKeys.contains(remKey));
		System.out.println(oldKeys.contains(key));
		oldKeys = oldKeys.replaceAll(remKey, "");
		String leagueURL = prefs.getString(key, "").split("LEAGUEURLSPLIT")[0];
		System.out.println("Using " + leagueURL);
		editor.remove(key);
		editor.putString("Imported League Keys", oldKeys);
		editor.commit();
		ESPNImport espnImporter = new ESPNImport(holder, this);
		try {
			espnImporter.handleESPNParsing(leagueURL, cont);
		} catch (IOException e) {
			Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Handles the rendering of the graph
	 * @param newImport
	 * @param v
	 */
	public void showGraph(ImportedTeam newImport, View v)
	{
		
		RelativeLayout base = (RelativeLayout)v;
		TextView headerText = (TextView)base.findViewById(R.id.text1);
		String header = headerText.getText().toString();
		TextView content = (TextView)base.findViewById(R.id.text2);
		String text = content.getText().toString();
		String[] teamSet = text.split("\n");
		String[] teams = new String[newImport.teams.size()];
		String[] valSet = new String[newImport.teams.size()];
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[newImport.teams.size()];
		int counter = 0;
		double maxFirst = -10000000.0;
		double minFirst = 1000000000.0;
		GraphViewStyle gvs = new GraphViewStyle();
		gvs.setTextSize(16);
		GraphView graphView = new LineGraphView(this, "");
		graphView.setGraphViewStyle(gvs);
		for(String teamIter : teamSet)
		{
			String val = teamIter.split(": ")[1];
			teams[counter] = teamIter.split(": ")[0];
			double value = Double.valueOf(val);
			if(value > maxFirst)
			{
				maxFirst = value;
			}
			else if(value < minFirst)
			{
				minFirst = value;
			}
			dataSet[counter] = new GraphViewData(++counter, value);
			GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
			seriesStyle.thickness = 5;
			GraphViewSeries exampleSeries = new GraphViewSeries(teams[counter-1].replace(')', ' '), seriesStyle, dataSet);  
			graphView.addSeries(exampleSeries);
		}
		final double max = maxFirst;
		final double min = minFirst;
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.plot_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		popUp.getWindow().setAttributes(lp);
		popUp.show(); 
		TextView headerView = (TextView)popUp.findViewById(R.id.plot_popup_header);
		headerView.setText(header);
		Button close = (Button)popUp.findViewById(R.id.plot_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
		});
		graphView.setScrollable(true); 
		double space = max - min;
		DecimalFormat df = new DecimalFormat("#.#");
		String[] valSpaced = {df.format(max), df.format(min + (space*6.0)/7.0), df.format(min + (space*5.0)/7.0), 
				df.format(min + (space*4.0)/7.0), df.format(min + (space*3.0)/7.0),
				df.format(min + (space*2.0)/7.0), df.format(min + (space*1.0)/7.0),df.format(min)};
		graphView.setManualYAxisBounds(max, min);
		int maxLoop = newImport.teams.size() +1;
		for(int i = 1; i < maxLoop; i++)
		{
			valSet[i-1] = String.valueOf(i);
		}
		graphView.setHorizontalLabels(valSet);
		graphView.setShowLegend(true); 	
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(275); 
		graphView.setVerticalLabels(valSpaced);
		((LineGraphView)graphView).setDrawBackground(true);
		//((LineGraphView) graphView).setBackgroundColor(Color.rgb(131,155,243));
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);
		 
	}

	/**
	 * Calculates the total paa of the starters
	 * @param team
	 * @return
	 */
	public double paaStart(TeamAnalysis team)
	{
		return team.qbStart + team.rbStart + team.wrStart + team.teStart + team.dStart + team.kStart;
	}
	
	/**
	 * Gets the total paa of all players
	 * @param team
	 * @return
	 */
	public double paaTotal(TeamAnalysis team)
	{
		return team.qbTotal + team.rbTotal + team.wrTotal + team.teTotal + team.dTotal + team.kTotal;
	}
	
	/**
	 * Gets the paa of the bench of a team
	 * @param team
	 * @return
	 */
	public double paaBench(TeamAnalysis team)
	{
		return paaTotal(team) - paaStart(team);
	}
	
	/**
	 * Gives the team specific information pop up
	 * @param v
	 * @param newImport 
	 */
	public void teamSpecPopUp(View v, ImportedTeam newImport)
	{
		String team = ((TextView)((RelativeLayout)v).findViewById(R.id.text2)).getText().toString();
		String header = ((TextView)((RelativeLayout)v).findViewById(R.id.text1)).getText().toString();
		TeamAnalysis ta = new TeamAnalysis("", team, holder, cont);
		DecimalFormat df = new DecimalFormat("#.##");
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.team_info_popup);
		TextView head = (TextView)popUp.findViewById(R.id.team_info_popup_header);
		head.setText(header);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    Button close = (Button)popUp.findViewById(R.id.team_info_close);
	    close.setOnClickListener(new OnClickListener(){
		@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
	    });
		popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    GraphViewStyle gvs = new GraphViewStyle();
		gvs.setTextSize(15);
		GraphView graphView = new LineGraphView(this, "");
		graphView.setGraphViewStyle(gvs);
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[9];
		int max = newImport.teams.size() + 1;
		String[] horizLabels = {"Total", "Start", "Bench", "QBs", "RBs", "WRs", "TEs", "D/ST", "Ks"};
		String[] vertLabels = new String[max-1];
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
		for(int i = 1; i < max; i++)
		{
			vertLabels[i-1] = String.valueOf(i);
		}
		
		int counter = 0;
		dataSet[counter++] = new GraphViewData(counter, max - rankPAATot(newImport, ta));
		GraphViewSeries es = new GraphViewSeries("PAA Total: " + df.format(paaTotal(ta)), seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter,  max - rankPAAStart(newImport, ta));
		es = new GraphViewSeries("PAA Starters: " + df.format(paaStart(ta)), seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter,  max - rankPAABench(newImport, ta));
		es = new GraphViewSeries("PAA Bench: " + df.format(paaBench(ta)), seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankQBs(newImport, ta));
		es = new GraphViewSeries("PAA QBs: " + ta.qbStart + " (" + ta.qbTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankRBs(newImport, ta));
		es = new GraphViewSeries("PAA RBs: " + ta.rbStart + " (" + ta.rbTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankWRs(newImport, ta));
		es = new GraphViewSeries("PAA WRs: " + ta.wrStart + " (" + ta.wrTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankTEs(newImport, ta));
		es = new GraphViewSeries("PAA TEs: " + ta.teStart + " (" + ta.teTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankDs(newImport, ta));
		es = new GraphViewSeries("PAA D/ST: " + ta.dStart + " (" + ta.dTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		dataSet[counter++] = new GraphViewData(counter, max - rankKs(newImport, ta));
		es = new GraphViewSeries("PAA Ks: " + ta.kStart + " (" + ta.kTotal + ")", seriesStyle, dataSet);
		graphView.addSeries(es);
		graphView.setHorizontalLabels(horizLabels);
		graphView.setVerticalLabels(vertLabels);
		graphView.setScrollable(true); 
		graphView.setShowLegend(true); 	
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(250); 
		((LineGraphView)graphView).setDrawBackground(true);
		graphView.setManualYAxisBounds(max-1, 1);
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);

	}
	
	public int rankPAATot(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(paaTotal(iter) > paaTotal(team))
			{
				rank++;
			}
		}
		return rank;
	}
	
	public int rankPAAStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(paaStart(iter) > paaStart(team))
			{
				rank++;
			}
		}
		return rank;
	}
	
	public int rankPAABench(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(paaBench(iter) > paaBench(team))
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the qb data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankQBs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.qbTotal > team.qbTotal)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the rb data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankRBs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.rbTotal > team.rbTotal)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the WR data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankWRs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.wrTotal > team.wrTotal)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the te data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankTEs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.teTotal > team.teTotal)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the defense data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankDs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.dTotal > team.dTotal)
			{
				rank++;
			}
		}
		return rank;
	}
	
	/**
	 * Ranks the kicker data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public int rankKs(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.kTotal > team.kTotal)
			{
				rank++;
			}
		}
		return rank;
	}
}
