package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.LeagueImports.ESPNImport;

import com.ffr.fantasyfootballrankings.R;
import com.ffr.fantasyfootballrankings.R.layout;
import com.ffr.fantasyfootballrankings.R.menu;
import FileIO.ReadFromFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
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
			if(Home.holder.players != null && Home.holder.players.size() > 5)
			{
				holder = Home.holder; 
			}
			else
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
		System.out.println(prefs.getString("ESPN Username", "default"));
		editor.remove("ESPN Username");
		System.out.println(prefs.getString("ESPN Password", "default"));
		editor.remove("ESPN Password");
		System.out.println(prefs.getBoolean("ESPN Credentials Stored", false));

		editor.remove("ESPN Credentials Stored");
		System.out.println(prefs.getInt("Number of Leagues Imported", -1));
		editor.remove("Number of Leagues Imported");
		System.out.println(prefs.getString("Imported League Keys", "nada"));
		String oldKeys = prefs.getString("Imported League Keys", "");
		String[]oldKeysSplit = oldKeys.split("~~~");
		for(String key : oldKeysSplit)
		{
			System.out.println(key);
			System.out.println(prefs.getString(key, "zilch"));
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
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
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
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
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
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    list.setAdapter(adapter);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String usedKeys = prefs.getString("Imported League Keys", "");
		System.out.println(usedKeys);
		String[] keySet = ManageInput.tokenize(usedKeys, '~', 3);
		for(String key : keySet)
		{
			Map<String, String> datum = new HashMap<String, String>();
			String[] keySplit = ManageInput.tokenize(key, '@', 3);
			datum.put("main", keySplit[1]);
			datum.put("sub", keySplit[0]);
			data.add(datum);
			adapter.notifyDataSetChanged();
		}
		ll.addView(list);
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String keyPart1 = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				String keyPart2 = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text2)).getText().toString();
				System.out.println(keyPart1);
				System.out.println(keyPart2);
				String key = keyPart2 + "@@@" + keyPart1;
				System.out.println(key);
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
		String leagueDataWhole = prefs.getString(key, "SHIT");
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
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		ListView list = (ListView)res.findViewById(R.id.imported_teams_info);
		DecimalFormat df = new DecimalFormat("#.##");
		for(TeamAnalysis team : newImport.teams)
		{
			Map<String, String> datum = new HashMap<String, String>();
			datum.put("main", team.teamName + "\n\n" + team.team);
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
	    TextView back = (TextView)res.findViewById(R.id.back_button_league_stats);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				handleLayoutInit();
			}
	    });
	    //Handles the basic league information
	    TextView name = (TextView)res.findViewById(R.id.league_name);
	    name.setText(newImport.leagueName);
	    TextView host = (TextView)res.findViewById(R.id.hostName);
	    host.setText(newImport.leagueHost);
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
	    paaTotal.append("Total PAA:\n\n");
	    StringBuilder paaStart = new StringBuilder(1000);
	    paaStart.append("PAA From Starters:\n\n");
	    StringBuilder paaBench = new StringBuilder(1000);
	    paaBench.append("PAA From the Bench:\n\n");
	    StringBuilder qb = new StringBuilder(1000);
	    qb.append("QB PAA Rankings:\n\n");
	    StringBuilder rb = new StringBuilder(1000);
	    rb.append("RB PAA Rankings:\n\n");
	    StringBuilder wr = new StringBuilder(1000);
	    wr.append("WR PAA Rankings:\n\n");
	    StringBuilder te = new StringBuilder(1000);
	    te.append("TE PAA Rankings:\n\n");
	    StringBuilder d  = new StringBuilder(1000);
	    d.append("D/ST PAA Rankings:\n\n");
	    StringBuilder k  = new StringBuilder(1000);
	    k.append("K PAA Rankings:\n\n");
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
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		ListView list2 = (ListView)res.findViewById(R.id.imported_league_rankings);
		Map<String, String> datum = new HashMap<String, String>();
		datum.put("main", paaTotal.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", paaStart.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum = new HashMap<String, String>();
		datum.put("main", paaBench.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", qb.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", rb.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", wr.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", te.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", d.toString());
		datum.put("sub", "");
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", k.toString());
		datum.put("sub", "");
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
	    ll.addView(res);
	    
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
		String team = ((TextView)((RelativeLayout)v).findViewById(R.id.text1)).getText().toString();
		String[] allData = team.split("\n\n");
		TeamAnalysis ta = new TeamAnalysis("", team, holder, cont);
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder info = new StringBuilder(2000);
		info.append(allData[0] + "\n\n");
		info.append("Note: this is based on the currently calculated projections/PAA\n");
		info.append("Set the scoring/roster settings on the home screen to this draft's settings to see accurate versions of these numbers\n\n");
		info.append("Pos: PAA from starters (PAA total) - League Rank\n\n");
		info.append("QB: " + ta.qbStart + " (" + ta.qbTotal + ") - Ranked " + rankQBs(newImport, ta));
		info.append("\n\nRB: " + ta.rbStart + " (" + ta.rbTotal + ") - Ranked " + rankRBs(newImport, ta));
		info.append("\n\nWR: " + ta.wrStart + " (" + ta.wrTotal + ") - Ranked " + rankWRs(newImport, ta));
		info.append("\n\nTE: " + ta.teStart + " (" + ta.teTotal + ") - Ranked " + rankTEs(newImport, ta));
		info.append("\n\nD/ST: "+ta.dStart +  " (" +  ta.dTotal + ") - Ranked " + rankDs(newImport, ta));
		info.append("\n\nK: " + ta.kStart + " (" + ta.kTotal + ") - Ranked " + rankKs(newImport, ta));
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
