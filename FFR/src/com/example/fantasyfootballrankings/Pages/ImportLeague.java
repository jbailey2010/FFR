package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.SortHandler;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.NDSpinner;
import com.example.fantasyfootballrankings.LeagueImports.ESPNImport;
import com.ffr.fantasyfootballrankings.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import FileIO.ReadFromFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
	public Menu menuObj;
	public MenuItem compare;
	public MenuItem refresh;
	public ImportedTeam newImport;
	public TextView v;
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
				Set<String> checkExists2 = prefs.getStringSet("Player Values", null);
		    	if(checkExists2 != null)
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
		menuObj = menu;
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
			case R.id.compare_team:
				compareTeamInit();
				return true;
			case R.id.refresh_league:
				handleLongClick();
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
		if(menuObj != null)
		{
			compare = (MenuItem)menuObj.findItem(R.id.compare_team);
			compare.setVisible(false);
			compare.setEnabled(false);
			refresh = (MenuItem)menuObj.findItem(R.id.refresh_league);
			refresh.setVisible(false);
			refresh.setEnabled(false);
		}
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
		if(menuObj != null)
		{
			compare = (MenuItem)menuObj.findItem(R.id.compare_team);
			compare.setVisible(false);
			compare.setEnabled(false);
			refresh = (MenuItem)menuObj.findItem(R.id.refresh_league);
			refresh.setVisible(false);
			refresh.setEnabled(false);
		}
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
		compare = (MenuItem)menuObj.findItem(R.id.compare_team);
		compare.setVisible(true);
		compare.setEnabled(true);
		refresh = (MenuItem)menuObj.findItem(R.id.refresh_league);
		refresh.setVisible(true);
		refresh.setEnabled(true);
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
		newImport = new ImportedTeam(teamList, keySet[1], keySet[0]);
		View res = ((Activity)cont).getLayoutInflater().inflate(R.layout.league_stats_output, ll, false);
		final RelativeLayout league = (RelativeLayout)res.findViewById(R.id.category_league_base);
		final RelativeLayout teams  = (RelativeLayout)res.findViewById(R.id.category_team_base);
		final RelativeLayout players= (RelativeLayout)res.findViewById(R.id.category_player_base);
		league.setVisibility(View.VISIBLE);
		teams.setVisibility(View.GONE);
		players.setVisibility(View.GONE);
		final Button leagueButton = (Button)res.findViewById(R.id.category_league_stats);
		final Button teamsButton  = (Button)res.findViewById(R.id.category_team_stats);
		final Button playersButton= (Button)res.findViewById(R.id.category_player_list);
		leagueButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				league.setVisibility(View.VISIBLE);
				teams.setVisibility(View.GONE);
				players.setVisibility(View.GONE);
				leagueButton.setTextSize(14);
				teamsButton.setTextSize(13);
				playersButton.setTextSize(13);
				leagueButton.setTypeface(null,Typeface.BOLD);
				teamsButton.setTypeface(null, Typeface.NORMAL);
				playersButton.setTypeface(null, Typeface.NORMAL);
			}
		});
		teamsButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				teams.setVisibility(View.VISIBLE);
				league.setVisibility(View.GONE);
				players.setVisibility(View.GONE);
				teamsButton.setTextSize(14);
				leagueButton.setTextSize(13);
				playersButton.setTextSize(13);
				teamsButton.setTypeface(null,Typeface.BOLD);
				leagueButton.setTypeface(null, Typeface.NORMAL);
				playersButton.setTypeface(null, Typeface.NORMAL);
			}
		});
		playersButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				league.setVisibility(View.GONE);
				teams.setVisibility(View.GONE);
				players.setVisibility(View.VISIBLE);
				leagueButton.setTextSize(13);
				teamsButton.setTextSize(13);
				playersButton.setTextSize(14);
				leagueButton.setTypeface(null,Typeface.NORMAL);
				teamsButton.setTypeface(null, Typeface.NORMAL);
				playersButton.setTypeface(null, Typeface.BOLD);
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
	    //Handles the three listview populations
	    setTeamInfoList(res);
	    setLeagueInfoList(res);
	    setPlayerInfoList(res);
	    //Handles the basic league information
	    TextView name = (TextView)res.findViewById(R.id.league_name);
	    name.setText(newImport.leagueName);
	    v = name;
	    TextView host = (TextView)res.findViewById(R.id.hostName);
	    host.setText("Hosted on " + newImport.leagueHost);
	    //Help work
	    final TextView helpTeams = (TextView)res.findViewById(R.id.team_help_import);
	    final TextView helpRanks = (TextView)res.findViewById(R.id.rankings_help_import);
	    final TextView helpPlayers = (TextView)res.findViewById(R.id.player_list_help);
	    TextView help = (TextView)res.findViewById(R.id.help_button_league_stats);
	    help.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!helpTeams.isShown() && !helpPlayers.isShown() && !helpRanks.isShown())
				{ 
					helpPlayers.setVisibility(View.VISIBLE);
					helpTeams.setVisibility(View.VISIBLE);
					helpRanks.setVisibility(View.VISIBLE);
				}
				else
				{
					helpPlayers.setVisibility(View.GONE);
					helpTeams.setVisibility(View.GONE);
					helpRanks.setVisibility(View.GONE);
				}
			}
	    });
	    ll.addView(res);
	}
	
	/**
	 * Populates the listview that handles the league information statistically
	 * @param res
	 */
	public void setLeagueInfoList(View res)
	{
		DecimalFormat df = new DecimalFormat("#.##");
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
	    				if (a.qbStart > b.qbStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.qbStart < b.qbStart)
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
	    				if (a.rbStart > b.rbStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.rbStart < b.rbStart)
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
	    				if (a.wrStart > b.wrStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.wrStart < b.wrStart)
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
	    				if (a.teStart > b.teStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.teStart < b.teStart)
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
	    				if (a.dStart > b.dStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.dStart < b.dStart)
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
	    				if (a.kStart > b.kStart)
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.kStart < b.kStart)
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
	    	qb.append(counter + ") " + iter.teamName + ": " + df.format(iter.qbStart) + " (" + df.format(iter.qbTotal) + ")\n");
	    }
	    counter = 0;
	    while(!rbPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = rbPAA.poll();
	    	rb.append(counter + ") " + iter.teamName + ": " + df.format(iter.rbStart) + " (" + df.format(iter.rbTotal) + ")\n");
	    }
	    counter = 0;
	    while(!wrPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = wrPAA.poll();
	    	wr.append(counter + ") " + iter.teamName + ": " + df.format(iter.wrStart) + " (" + df.format(iter.wrTotal) + ")\n");
	    }
	    counter = 0;
	    while(!tePAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = tePAA.poll();
	    	te.append(counter + ") " + iter.teamName + ": " + df.format(iter.teStart) + " (" + df.format(iter.teTotal) + ")\n");
	    }
	    counter = 0;
	    while(!dPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = dPAA.poll();
	    	d.append(counter + ") " + iter.teamName + ": " + df.format(iter.dStart) + " (" + df.format(iter.dTotal) + ")\n");
	    }
	    counter = 0;
	    while(!kPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = kPAA.poll();
	    	k.append(counter + ") " + iter.teamName + ": " + df.format(iter.kStart) + " (" + df.format(iter.kTotal) + ")\n");
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
	}
	
	
	public void setPlayerInfoList(View res)
	{
		 List<Map<String, String>>data = new ArrayList<Map<String, String>>();
		 SimpleAdapter adapter = new SimpleAdapter(cont, data, 
		    		R.layout.web_listview_item, 
		    		new String[] {"main", "sub"}, 
		    		new int[] {R.id.text1, 
		    			R.id.text2});
		 final ListView list = (ListView)res.findViewById(R.id.imported_teams_players);
		 list.setAdapter(adapter);
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
		 final Spinner pos = (Spinner)res.findViewById(R.id.player_pos_spinner);
		 final Spinner status = (Spinner)res.findViewById(R.id.player_status_spinner);
		 final NDSpinner sortSp = (NDSpinner)res.findViewById(R.id.player_sort_spinner);
		 pos.clearFocus();
		 status.clearFocus();
		 list.clearFocus();
		 sortSp.clearFocus();
		 List<String> positions = new ArrayList<String>();
		 positions.add("All Positions");
		 positions.add("QB");
		 positions.add("RB");
		 positions.add("WR");
		 positions.add("TE");
		 positions.add("D/ST");
		 positions.add("K");
		 List<String> playerStatus = new ArrayList<String>();
		 playerStatus.add("All Players");
		 playerStatus.add("Free Agents");
		 playerStatus.add("On Team");
		 List<String> sortFactors = new ArrayList<String>();
		 sortFactors.add("PAA");
		 sortFactors.add("Projection");
		 sortFactors.add("Weekly Ranking");
		 sortFactors.add("ROS Ranking");
		 sortFactors.add("Custom");
		 ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, positions);
		 pos.setAdapter(adapterPos);
		 ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, playerStatus);
		 status.setAdapter(statusAdapter);
		 ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(cont, 
					android.R.layout.simple_spinner_dropdown_item, sortFactors);
		 sortSp.setAdapter(sAdapter);
		 final OnItemSelectedListener l = new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String pos = ((TextView)arg1).getText().toString();
					populatePlayerList(list, pos, status.getSelectedItem().toString(), sortSp.getSelectedItem().toString());
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			 };
		 pos.post(new Runnable() {
			    public void run() {
			    	pos.setOnItemSelectedListener(l);
			    }
			});
		 final OnItemSelectedListener l2 = new OnItemSelectedListener(){
 
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String status = ((TextView)arg1).getText().toString();
					populatePlayerList(list, pos.getSelectedItem().toString(), status, sortSp.getSelectedItem().toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			 };
		 status.post(new Runnable() {
			    public void run() {
			    	status.setOnItemSelectedListener(l2);
			}});
		 final OnItemSelectedListener l3 = new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String sort = ((TextView)arg1).getText().toString();
				populatePlayerList(list, pos.getSelectedItem().toString(), status.getSelectedItem().toString(), sort);
				if(sort.equals("Custom"))
				{
					pos.setClickable(false);
					Toast.makeText(cont, "Note position now can only be set through the pop up, not through the drop down here until you choose"
							+ " a different sorting factor", Toast.LENGTH_LONG).show();
				}
				else
				{
					pos.setClickable(true);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		 };
		 sortSp.post(new Runnable() {
			    public void run() {
			    	sortSp.setOnItemSelectedListener(l3);
			}});
		 populatePlayerList(list, "All Positions", "All Players", "PAA");
	}
	
	public void setPlayerAdapter(List<Map<String, String>> data, ListView list)
	{
		SimpleAdapter adapter = new SimpleAdapter(cont, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString().split(":  ")[1];
				PlayerInfo obj = new PlayerInfo();
				obj.outputResults(name, true, (Activity) cont, holder, false, false);
			}
		});
	}
	
	/**
	 * Handles the logic of populating the player list 
	 * to fill the selected team/status...etc.
	 */
	public void populatePlayerList(ListView list, String pos, String status, String sortFactor)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		PriorityQueue<PlayerObject> players = null;
		if(sortFactor.equals("Custom"))
		{
			int flag = 1;
			if(status.equals("All Players"))
			{
				flag = 1;
			}
			if(status.equals("Free Agents"))
			{
				flag = 3;
			}
			if(status.equals("On Team"))
			{
				flag = 2;
			}
			SortHandler.initialPopUp(cont, holder, R.id.imported_teams_players, false, flag, newImport);
		}
		else
		{
			if(sortFactor.equals("PAA"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
			    		{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.points == 0 && b.values.points > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.points > 0 && b.values.points == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.points == 0 && b.values.points == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.paa > b.values.paa || (b.values.points == 0 && a.values.points > 0))
			    			    {
			    			        return -1;
			    			    }
			    			    if (a.values.paa < b.values.paa || (a.values.points == 0 && b.values.points > 0))
			    			    {
			    			    	return 1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("Projection"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.points <= 0 && b.values.points > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.points > 0 && b.values.points == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.points == 0 && b.values.points == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.points > b.values.points || (b.values.points == 0 && a.values.points > 0))
			    			    {
			    			        return -1;
			    			    }
			    			    if (a.values.points < b.values.points || (a.values.points == 0 && b.values.points > 0))
			    			    {
			    			    	return 1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("Weekly Ranking"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.ecr <= 0 && b.values.ecr > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.ecr > 0 && b.values.ecr == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.ecr == 0 && b.values.ecr == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.ecr > b.values.ecr || (b.values.ecr == 0 && a.values.ecr > 0))
			    			    {
			    			        return 1;
			    			    }
			    			    if (a.values.ecr < b.values.ecr || (a.values.ecr == 0 && b.values.ecr > 0))
			    			    {
			    			    	return -1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			else if(sortFactor.equals("ROS Ranking"))
			{
				players = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>()
						{
			    			@Override
			    			public int compare(PlayerObject a, PlayerObject b) 
			    			{
			    				if(a.values.rosRank <= 0 && b.values.rosRank > 0)
			    				{
			    					return 1;
			    				}
			    				if(a.values.rosRank > 0 && b.values.rosRank == 0)
			    				{
			    					return -1;
			    				}
			    				if(a.values.rosRank == 0 && b.values.rosRank == 0)
			    				{
			    					return 0;
			    				}
			    				if (a.values.rosRank > b.values.rosRank || (b.values.rosRank == 0 && a.values.rosRank > 0))
			    			    {
			    			        return 1;
			    			    }
			    			    if (a.values.rosRank < b.values.rosRank || (a.values.rosRank == 0 && b.values.rosRank > 0))
			    			    {
			    			    	return -1;
			    			    } 
			    			    return 0;
			    			}
			    		});
			}
			 for(PlayerObject player : holder.players)
			 {
				 if(player.values.ecr > 0 && (pos.equals("All Positions") || player.info.position.equals(pos)) && 
						 !(player.info.team.length() == 0 || player.info.team.length() == 1 || player.info.position.length() == 0) )
				 {
					 players.add(player);
				 }
			 }
			 while(!players.isEmpty())
			 {
				 Map<String, String> datum = new HashMap<String, String>();
				 PlayerObject iter = players.poll();
				 datum.put("main", df.format(iter.values.points) + ":  " + iter.info.name);
				 StringBuilder subInfo = new StringBuilder(100);
				 for(TeamAnalysis team : newImport.teams)
				 {
					 if(team.team.contains(iter.info.name))
					 {
						 subInfo.append(team.teamName + "\n");
						 break;
					 }
				 }
				 if(!subInfo.toString().contains("\n"))
				 {
					 subInfo.append("Free Agent \n");
				 }
				 if(status.equals("Free Agents") && !subInfo.toString().contains("Free Agent"))
				 {
					 continue;
				 }
				 if(status.equals("On Team") && subInfo.toString().contains("Free Agent"))
				 {
					 continue;
				 }
				 subInfo.append(iter.info.position + " - " + iter.info.team + "\n");
				 if(iter.values.rosRank > 0)
				 {
					 subInfo.append("ROS Positional Rank: " + iter.values.rosRank + "\n");
				 }
				 subInfo.append("Weekly Positional Rank: " + iter.values.ecr.intValue()+ "\n");
				 if(!iter.info.adp.contains("Not set"))
				 {
					 subInfo.append("Opponent: " + iter.info.adp);
					 if(holder.sos.keySet().contains(iter.info.team + "," + iter.info.position))
					 {
						 subInfo.append(" (SOS: " + holder.sos.get(iter.info.team + "," + iter.info.position) + ")");
					 }
				 }
				 else
				 {
					 subInfo.append("Bye Week");
				 }
				 datum.put("sub", subInfo.toString());
				 data.add(datum);
			 }
			 setPlayerAdapter(data, list);
		}
	}
	
	/**
	 * Handles the population of the team information listview
	 * both click, onclick, and initial output
	 * @param res
	 */
	public void setTeamInfoList(View res){
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
			datum.put("sub", df.format(paaTotal(team)) + " PAA Total\n" + df.format(paaStart(team)) + " PAA From Starters");
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
		OnItemLongClickListener longListener = new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				optimalLineup(newImport, arg1);
				return true;
			}
		};
		list.setOnItemLongClickListener(longListener);
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
				showGraph(newImport, arg1, false);
			}
			
		};
		OnItemLongClickListener longListener = new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showGraph(newImport, arg1, true);
				return true;
			}
			
		};
		list.setOnItemClickListener(listener);
		list.setOnItemLongClickListener(longListener);
	}
	
	/**
	 * Handles the logic of deciding what to do when the name has been clicked
	 */
	public void handleLongClick()
	{
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
	}
	
	/**
	 * Clears the non password espn stuff then calls the refreshing
	 * @param name
	 * @param newImport
	 * @param cont
	 */
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
		String oldKeys = prefs.getString("Imported League Keys", "");
		oldKeys = oldKeys.replaceAll(remKey, "");
		String leagueURL = prefs.getString(key, "").split("LEAGUEURLSPLIT")[0];
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
	 * Comes up with a pop up that shows a specific team's 
	 * perfect lineup
	 * @param newImport
	 * @param v
	 */
	public void optimalLineup(ImportedTeam newImport, View v)
	{
		RelativeLayout base = (RelativeLayout)v;
		TextView headerText = (TextView)base.findViewById(R.id.text1);
		String header = headerText.getText().toString();
		TextView content = (TextView)base.findViewById(R.id.text2);
		String text = content.getText().toString();
		Map<String, String[]> rosters = new HashMap<String, String[]>();
		rosters.put("QB", text.split("Quarterbacks: ")[1].split("\n")[0].split(", "));
		rosters.put("RB", text.split("Running Backs: ")[1].split("\n")[0].split(", "));
		rosters.put("WR", text.split("Wide Receivers: ")[1].split("\n")[0].split(", "));
		rosters.put("TE", text.split("Tight Ends: ")[1].split("\n")[0].split(", "));
		rosters.put("D/ST", text.split("D/ST: ")[1].split("\n")[0].split(", "));
		rosters.put("K", text.split("Kickers: ")[1].split("\n")[0].split(", "));
		StringBuilder output = new StringBuilder(1000);
		TeamAnalysis dummy = new TeamAnalysis();
		output.append(dummy.optimalLineup(rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), "QB", cont, holder));
		output.append(dummy.optimalLineup(rosters.get("RB"), rosters.get("RB"), rosters.get("WR"), "RB", cont, holder));
		output.append(dummy.optimalLineup(rosters.get("WR"), rosters.get("RB"), rosters.get("WR"), "WR", cont, holder));
		output.append(dummy.optimalLineup(rosters.get("TE"), rosters.get("RB"), rosters.get("WR"), "TE", cont, holder));
		output.append(dummy.optimalLineup(rosters.get("D/ST"), rosters.get("RB"), rosters.get("WR"), "D/ST", cont, holder));
		output.append(dummy.optimalLineup(rosters.get("K"), rosters.get("RB"), rosters.get("WR"), "K", cont, holder));
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.team_optimal_lineup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    Button close = (Button)popUp.findViewById(R.id.optimal_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				return;
			}
	    });
	    TextView teamName = (TextView)popUp.findViewById(R.id.team_name_optimal);
	    teamName.setText(header);
	    TextView teamRoster = (TextView)popUp.findViewById(R.id.team_roster_optimal);
	    teamRoster.setText(output.toString());
	}
	
	/**
	 * Handles the rendering of the graph
	 * @param newImport
	 * @param v
	 */
	public void showGraph(ImportedTeam newImport, View v, boolean isLong)
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
		gvs.setTextSize(13);
		GraphView graphView = new LineGraphView(this, "");
		graphView.setGraphViewStyle(gvs);
		List<String> teamList = new ArrayList<String>();
		String test = teamSet[0];
		boolean flag = false;
		if(!test.split(" ")[test.split(" ").length - 1].contains("("))
		{
			flag = true;
		}
		if(!isLong || flag)
		{
			for(String team : teamSet)
			{
				teamList.add(team);
			}
		}
		else
		{
			PriorityQueue<String> longOrder = new PriorityQueue<String>(300, new Comparator<String>() 
			{
				@Override
				public int compare(String a, String b)  
				{
					double aVal = Double.valueOf(a.split(" \\(")[1].split("\\)")[0]);
					double bVal = Double.valueOf(b.split(" \\(")[1].split("\\)")[0]);
					if(aVal > bVal)
					{
						return -1;
					}
					if(bVal > aVal)
					{
						return 1;
					}
					return 0;
				}
			});
			for(String team : teamSet)
			{
				longOrder.add(team);
			}
			while(!longOrder.isEmpty())
			{
				teamList.add(longOrder.poll());
			}
		}
		int teamCt = 0;
		for(String teamIter : teamList)
		{
			teamCt++;
			String val = "";
			if(!isLong || (isLong && !teamIter.split(" ")[teamIter.split(" ").length-1].contains("(")))
			{
				val = teamIter.split(": ")[1].split(" \\(")[0];
			}
			else
			{
				val = teamIter.split(" \\(")[1].split("\\)")[0];
			}
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
			GraphViewSeries exampleSeries = new GraphViewSeries(teamCt + " " + teams[counter-1].split("\\)")[1], seriesStyle, dataSet);  
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
		gvs.setTextSize(12);
		GraphView graphView = new LineGraphView(this, "");
		graphView.setGraphViewStyle(gvs);
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[9];
		int max = newImport.teams.size() + 1;
		String[] horizLabels = {"QB", "RB", "WR", "TE", "D", "K", "All", "Start", "Rest"};
		String[] vertLabels = new String[max-1];
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
		for(int i = 1; i < max; i++)
		{
			vertLabels[i-1] = String.valueOf(i);
		}
		
		int counter = 0;
		GraphViewSeriesStyle seriesStyle2 = new GraphViewSeriesStyle();
		seriesStyle2.color = Color.CYAN;
		GraphViewDataInterface[] dataSet2 = new GraphViewDataInterface[6];
		dataSet[counter++] = new GraphViewData(counter, max - rankQBs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankRBs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankWRs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankTEs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankDs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankKs(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter, max - rankPAATot(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter,  max - rankPAAStart(newImport, ta));
		dataSet[counter++] = new GraphViewData(counter,  max - rankPAABench(newImport, ta));
		GraphViewSeries es = new GraphViewSeries("PAA (Whole Roster)", seriesStyle, dataSet);
		graphView.addSeries(es);
		
		counter = 0;
		dataSet2[counter++] = new GraphViewData(counter, max - rankQBStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankRBStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankWRStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankTEStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankDStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankKStart(newImport, ta));
		es = new GraphViewSeries("PAA (Starters)", seriesStyle2, dataSet2);
		graphView.addSeries(es);

		
		graphView.setHorizontalLabels(horizLabels);
		graphView.setVerticalLabels(vertLabels);
		graphView.setScrollable(true); 
		graphView.setShowLegend(true); 	
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(250); 
		graphView.setManualYAxisBounds(max-1, 1);
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);

	}
	
	/**
	 * Handles the comaprison of teams, specifically the initial aspects of it
	 */
	public void compareTeamInit()
	{
		List<String> teamNames = new ArrayList<String>();
		for(TeamAnalysis team : newImport.teams)
		{
			teamNames.add(team.teamName);
		}
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.compare_teams_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    Button close = (Button)popUp.findViewById(R.id.compare_close);
	    close.setOnClickListener(new OnClickListener(){
		@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
	    });
	    popUp.show();
	    final Spinner team1 = (Spinner)popUp.findViewById(R.id.team1_spinner);
	    final Spinner team2 = (Spinner)popUp.findViewById(R.id.team2_spinner);
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(cont, 
				android.R.layout.simple_spinner_dropdown_item, teamNames);
	    team1.setAdapter(spinnerArrayAdapter);
	    team2.setAdapter(spinnerArrayAdapter);
	    team2.setSelection(1);
	    Button submit = (Button)popUp.findViewById(R.id.compare_submit);
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String team1Str = team1.getSelectedItem().toString();
				String team2Str = team2.getSelectedItem().toString();
				if(!team1Str.equals(team2Str))
				{
					popUp.dismiss();
					compareTeamOutput(team1Str, team2Str);
				}
				else
				{
					Toast.makeText(cont, "Please select different teams", Toast.LENGTH_SHORT).show();
				}
			}
	    });
	}
	
	/**
	 * Shows the graph of the team comparison
	 * @param team1
	 * @param team2
	 */
	public void compareTeamOutput(String team1, String team2)
	{
		TeamAnalysis t1 = null;
		TeamAnalysis t2 = null;
		for(TeamAnalysis team : newImport.teams)
		{
			if(team.teamName.equals(team1))
			{
				t1 = team;
			}
			if(team.teamName.equals(team2))
			{
				t2 = team;
			}
		}
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.team_info_popup);
		TextView head = (TextView)popUp.findViewById(R.id.team_info_popup_header);
		int r1 = rankPAAStart(newImport, t1);
		int r2 = rankPAAStart(newImport, t2);
		String winner = "";
		if(r1 > r2)
		{
			winner = t2.teamName;
		}
		else
		{
			winner = t1.teamName;
		}
		if(holder.isRegularSeason)
		{
			head.setText("Projected Winner: " + winner);
		}
		else
		{
			head.setText("Better Team: " + winner);
		}
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
		gvs.setTextSize(12);
		GraphView graphView = new LineGraphView(this, "");
		graphView.setGraphViewStyle(gvs);
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[7];
		int max = newImport.teams.size() + 1;
		String[] horizLabels = {"Starters", "QB", "RB", "WR", "TE", "D", "K"};
		String[] vertLabels = new String[max-1];
		GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
		for(int i = 1; i < max; i++)
		{
			vertLabels[i-1] = String.valueOf(i);
		}
		int counter = 0;
		dataSet[counter++] = new GraphViewData(counter, max - rankPAAStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankQBStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankRBStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankWRStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankTEStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankDStart(newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max - rankKStart(newImport, t1));
		GraphViewSeries es = new GraphViewSeries(t1.teamName + " Starters", seriesStyle, dataSet);
		graphView.addSeries(es);
		GraphViewSeriesStyle seriesStyle2 = new GraphViewSeriesStyle();
		seriesStyle2.color = Color.RED;
		GraphViewDataInterface[] dataSet2 = new GraphViewDataInterface[7];
		counter = 0;
		dataSet2[counter++] = new GraphViewData(counter, max - rankPAAStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankQBStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankRBStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankWRStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankTEStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankDStart(newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max - rankKStart(newImport, t2));
		es = new GraphViewSeries(t2.teamName + " Starters", seriesStyle2, dataSet2);
		graphView.addSeries(es);
		graphView.setHorizontalLabels(horizLabels);
		graphView.setVerticalLabels(vertLabels);
		graphView.setScrollable(true); 
		graphView.setShowLegend(true); 	
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(250); 
		graphView.setManualYAxisBounds(max-1, 1);
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);
	}
	
	/**
	 * Ranks each teams total PAA relative to the rest of the league
	 * @param leagueSet
	 * @param team
	 * @return
	 */
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
	
	/**
	 * Ranks an individual teams paa in starters relative to the rest of the league
	 * @param leagueSet
	 * @param team
	 * @return
	 */
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
	
	public int rankQBStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.qbStart > team.qbStart)
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
	
	public int rankRBStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.rbStart > team.rbStart)
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
	
	public int rankWRStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.wrStart > team.wrStart)
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
	
	public int rankTEStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.teStart > team.teStart)
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
	
	public int rankDStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.dStart > team.dStart)
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
	
	public int rankKStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.kStart > team.kStart)
			{
				rank++;
			}
		}
		return rank;
	}
}
