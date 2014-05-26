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

import org.htmlcleaner.XPatherException;

import com.ffr.fantasyfootballrankings.R;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.ComparatorHandling;
import com.example.fantasyfootballrankings.ClassFiles.HandleWatchList;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfoActivity;
import com.example.fantasyfootballrankings.ClassFiles.Simulator;
import com.example.fantasyfootballrankings.ClassFiles.SortHandler;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMath;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.*;
import com.socialize.EntityUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.speech.RecognizerIntent;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;
/**
 * Handles the rankings part of the java file
 * 
 * @author Jeff
 *
 */
public class Rankings extends Activity {
	Context cont;
	public static Context context;
	public static Storage holder = new Storage(null);
	public static AutoCompleteTextView textView;
	static Dialog dialog;
	static Button watch;
	static Button info;
	static Button compare;
	static Button sort;
	public static OutBounceListView listview;
	static boolean refreshed = false;
	static int sizeOutput = -1;
	static String teamFilter = "";
	static String posFilter = "";
	static List<String> teamList = new ArrayList<String>();
	static List<String> posList = new ArrayList<String>();
	public static List<String> watchList = new ArrayList<String>();
	public static List<Map<String, String>> data;
	public static SimpleAdapter adapter;
	static RankingsSwipeDismissListViewTouchListener touchListener;
	public static boolean isAuction;
	public static boolean isAsync = false;
	public static double aucFactor;
	public static SideNavigationView sideNavigationView;
	public static boolean isSwiping = false;
	public static boolean hasSwiped = false;
	public static String swipedText = "";
	/**
	 * Sets up the view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cont = this;
		ISideNavigationCallback sideNavigationCallback = new ISideNavigationCallback() {
		    @Override
		    public void onSideNavigationItemClick(int itemId) {
		    	switch (itemId) {
		    	case R.id.side_navigation_menu_item0:
	            	listview.smoothScrollToPosition(0);
	                break;
	            case R.id.side_navigation_menu_item1:
	            	Intent intent = new Intent(cont, Home.class);
	    	        cont.startActivity(intent);	
	                break;
	            case R.id.side_navigation_menu_item2:
	            	Intent intent2 = new Intent(cont, Rankings.class);
	    	        cont.startActivity(intent2);	
	                break;
	            case R.id.side_navigation_menu_item3:
	            	Intent intent5 = new Intent(cont, ImportLeague.class);
	    	        cont.startActivity(intent5);
	                break;
	            case R.id.side_navigation_menu_item4:
	            	Intent intent3 = new Intent(cont, Trending.class);
	    	        cont.startActivity(intent3);		
	                break;
	            case R.id.side_navigation_menu_item5:
	            	Intent intent4 = new Intent(cont, News.class);
	    	        cont.startActivity(intent4);
	                break;
	            case R.id.side_navigation_menu_item6:
	            	Intent intent6 = new Intent(cont, DraftHistory.class);
	    	        cont.startActivity(intent6);
	                break;
	            case R.id.side_navigation_menu_item7:
	            	PlayerInfoActivity.displayStats(cont);
	            	break;
	            case R.id.help:
	            	ManageInput.generalHelp(cont);
	            	break;
	            default:
	                return;
		    	}
		    }
		};
		setContentView(R.layout.activity_rankings);
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view_rankings);
	    sideNavigationView.setMenuItems(R.menu.side_navigation_view);
	    sideNavigationView.setMenuClickCallback(sideNavigationCallback);
	   // sideNavigationView.setMode(/*SideNavigationView.Mode*/);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		if(holder.draft.playersDrafted(holder.draft) == 0)
		{
			holder.draft.fixRemSalaryInit(cont);
		}
		aucFactor = ReadFromFile.readAucFactor(cont);
		watchList.clear();
		watch = (Button)findViewById(R.id.watch);
		info = (Button)findViewById(R.id.draft_info);
		compare = (Button)findViewById(R.id.player_comparator);
		sort = (Button)findViewById(R.id.sort_players);
    	listview = (OutBounceListView)findViewById(R.id.listview_rankings);
    	//widgetBase = (RelativeLayout)findViewById(R.id.rankings_widget_base);
		//hideWidget = ReadFromFile.readHideWidget(cont);
    	context = this;
    	isAuction = ReadFromFile.readIsAuction(cont);
    	setLists();
		handleRefresh();
		handleOnClickButtons();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		configSearch();
	}
	
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Call Socialize in onPause
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Call Socialize in onResume
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// Call Socialize in onDestroy before the activity is destroyed
		Socialize.onDestroy(this);
		
		super.onDestroy();
	}	

	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rankings, menu);
		if(menu != null && holder.isRegularSeason)
		{
			MenuItem a = (MenuItem)menu.findItem(R.id.save_draft);
			MenuItem b = (MenuItem)menu.findItem(R.id.refresh_draft);
			MenuItem c = (MenuItem)menu.findItem(R.id.simulator);
			a.setVisible(false);
			a.setEnabled(false);
			b.setEnabled(false);
			b.setVisible(false);
			c.setVisible(false);
			c.setEnabled(false);
		}
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
			case android.R.id.home:
		        sideNavigationView.toggleMenu();
		        return true;
			case R.id.simulator:
				if(ManageInput.confirmInternet(cont)){
					Simulator.simulatorInit(cont, holder);
				}
				else
				{
					Toast.makeText(cont, "This requires an internet connection", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.refresh_draft:
				Draft.resetDraft(holder.draft, holder, context);	
				return true;
			case R.id.refresh_ranks:
				refreshRanks();
		    	return true;
			case R.id.filter_topics_rankings:
				if(holder.players.size() > 10)
				{
					filterTopics(dialog);
				}
				else
				{
					Toast.makeText(context, "Can't filter the rankings until they're fetched", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.filter_quantity_menu:
				if(holder.players.size() > 10)
				{
					filterQuantity();
				}
				else
				{
					Toast.makeText(context, "Can't filter the quantity of rankings until they're fetched", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.help:
				helpDialog();
				return true;
			case R.id.save_draft:
				holder.draft.saveDraft(holder, cont);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	

	/**
	 * Handles the help dialog
	 */
	public void helpDialog() {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.help_rankings);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.help_rankings_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
	    });
	}
    
	/**
	 * Populate team/pos lists
	 */
	public void setLists()
	{
		Roster r = ReadFromFile.readRoster(this);
		if(posList.size() != 7)
		{
			posList.add("All Positions");
			if(r.qbs != 0)
			{
				posList.add("QB");
			}
			if(r.rbs != 0)
			{
				posList.add("RB");
			}
			if(r.wrs != 0)
			{
				posList.add("WR");
			}
			if(r.tes != 0)
			{
				posList.add("TE");
			}
			if(r.def != 0)
			{
				posList.add("D/ST");
			}
			if(r.k != 0)
			{
				posList.add("K");
			}
		}
		if(teamList.size() != 33)
		{
			teamList.add("All Teams");
			teamList.add("Arizona Cardinals");
			teamList.add("Atlanta Falcons");
			teamList.add("Baltimore Ravens");
			teamList.add("Buffalo Bills");
			teamList.add("Carolina Panthers");
			teamList.add("Chicago Bears");
			teamList.add("Cincinnati Bengals");
			teamList.add("Cleveland Browns");
			teamList.add("Dallas Cowboys");
			teamList.add("Denver Broncos");
			teamList.add("Detroit Lions");
			teamList.add("Green Bay Packers");
			teamList.add("Houston Texans");
			teamList.add("Indianapolis Colts");
			teamList.add("Jacksonville Jaguars");
			teamList.add("Kansas City Chiefs");
			teamList.add("Miami Dolphins");
			teamList.add("Minnesota Vikings");
			teamList.add("New England Patriots");
			teamList.add("New Orleans Saints");
			teamList.add("New York Giants");
			teamList.add("New York Jets");
			teamList.add("Oakland Raiders");
			teamList.add("Philadelphia Eagles");
			teamList.add("Pittsburgh Steelers");
			teamList.add("San Diego Chargers");
			teamList.add("San Francisco 49ers");
			teamList.add("Seattle Seahawks");
			teamList.add("St. Louis Rams");
			teamList.add("Tampa Bay Buccaneers");
			teamList.add("Tennessee Titans");
			teamList.add("Washington Redskins");
		}
		if(watchList.size() == 0)
		{
			watchList = ReadFromFile.readWatchList(context);
		}
	}
	
	/**
	 * handles relavent filter dialog
	 * @param dialog2
	 */
	public void filterTopics(final Dialog dialog2) 
	{
		final Dialog dialog = new Dialog(context, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.rankings_filter);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button cancel = (Button)dialog.findViewById(R.id.rankings_filter_close);
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		posList.clear();
		Roster r = ReadFromFile.readRoster(this);
		if(posList.size() != 7)
		{
			posList.add("All Positions");
			if(r.qbs != 0)
			{
				posList.add("QB");
			}
			if(r.rbs != 0)
			{
				posList.add("RB");
			}
			if(r.wrs != 0)
			{
				posList.add("WR");
			}
			if(r.tes != 0)
			{
				posList.add("TE");
			}
			if(r.def != 0)
			{
				posList.add("D/ST");
			}
			if(r.k != 0)
			{
				posList.add("K");
			}
		}
		teamList.clear();
		teamList.add("All Teams");
		teamList.add("Arizona Cardinals");
		teamList.add("Atlanta Falcons");
		teamList.add("Baltimore Ravens");
		teamList.add("Buffalo Bills");
		teamList.add("Carolina Panthers");
		teamList.add("Chicago Bears");
		teamList.add("Cincinnati Bengals");
		teamList.add("Cleveland Browns");
		teamList.add("Dallas Cowboys");
		teamList.add("Denver Broncos");
		teamList.add("Detroit Lions");
		teamList.add("Green Bay Packers");
		teamList.add("Houston Texans");
		teamList.add("Indianapolis Colts");
		teamList.add("Jacksonville Jaguars");
		teamList.add("Kansas City Chiefs");
		teamList.add("Miami Dolphins");
		teamList.add("Minnesota Vikings");
		teamList.add("New England Patriots");
		teamList.add("New Orleans Saints");
		teamList.add("New York Giants");
		teamList.add("New York Jets");
		teamList.add("Oakland Raiders");
		teamList.add("Philadelphia Eagles");
		teamList.add("Pittsburgh Steelers");
		teamList.add("San Diego Chargers");
		teamList.add("San Francisco 49ers");
		teamList.add("Seattle Seahawks");
		teamList.add("St. Louis Rams");
		teamList.add("Tampa Bay Buccaneers");
		teamList.add("Tennessee Titans");
		teamList.add("Washington Redskins");
		if(teamFilter.length() < 3)
		{
			teamFilter = "All Teams";
		}
		if(posFilter.length() < 2 && !posFilter.equals("K"))
		{
			posFilter = "All Teams";
		}
		final Spinner pos = (Spinner)dialog.findViewById(R.id.position_spinner);
		final Spinner teams = (Spinner)dialog.findViewById(R.id.team_spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, posList);
		pos.setAdapter(spinnerArrayAdapter);
		ArrayAdapter<String> spinnerArrayAdapte2r = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, teamList);
		teams.setAdapter(spinnerArrayAdapte2r);
		teams.setSelection(teamList.indexOf(teamFilter));
		pos.setSelection(posList.indexOf(posFilter));

		//Actual non-initialization work
		Button submit = (Button)dialog.findViewById(R.id.filter_rankings_submit);
		submit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				teamFilter = teams.getSelectedItem().toString();
				posFilter = pos.getSelectedItem().toString();
				if(!teamFilter.contains("All"))
				{
					teamList.clear();
					teamList.add(teamFilter);
				}
				if(!posFilter.contains("All"))
				{
					posList.clear();
					posList.add(posFilter);
				}
				intermediateHandleRankings((Rankings)context);
				dialog.dismiss();
	    	}	
		});
		dialog.show();
	}

	/**
	 * Handles the refreshing of the rankings/user input to do so
	 * @param dialog
	 */
	public void refreshRanks()
	{
		try {
			if(ManageInput.confirmInternet(cont))
			{
				refreshed = true;
				listview.setAdapter(null);
				dialog.dismiss();
				isAsync=true;
				ParseRankings.runRankings(holder, cont);
				
			}
			else
			{
				Toast.makeText(cont, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XPatherException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	}	

	
	/**
	 * Handles the possible loading of the players
	 */
	public void handleRefresh()
	{ 
		
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	Set<String> checkExists = prefs.getStringSet("Player Values", null);
    	if((checkExists != null && holder.players.size() == 0) || prefs.getBoolean("Home Update", false))
    	{
    		if(Home.holder.players != null && Home.holder.players.size() > 5)
    		{
	    		holder = Home.holder;
	    		intermediateHandleRankings((Activity) cont);
    		}
    		else
    		{
				ReadFromFile.fetchPlayers(checkExists, holder,cont, 0);
				SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
				editor.putBoolean("Home Update", false).apply();
			}
    		if(holder.playerNames == null || holder.playerNames.size() < 5)
    		{
    			ReadFromFile.fetchNamesBackEnd(holder, cont);
    		}
    	}
    	
    	else if(holder.players.size() != 0)
    	{
    		ReadFromFile.readDraft(holder, cont);
    		intermediateHandleRankings(this);
    	}
    	else
    	{
    		if(holder.playerNames == null || holder.playerNames.size() < 5)
    		{
    			ReadFromFile.fetchNamesBackEnd(holder, cont);
    		}
    		try {
    			if(ManageInput.confirmInternet(cont))
				{
    				isAsync = true;
					ParseRankings.runRankings(holder, cont);
					refreshed = true;
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection Available, The Initial Load of the Rankings Can't Be Done. Connect and Try Again.", Toast.LENGTH_LONG ).show();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	/**
	 * Sets onclick of the button bar
	 */
	public void handleOnClickButtons()
	{
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		//Handle the moreinfo click
		info.setOnClickListener(new View.OnClickListener() 
	    {
	          @Override
	          public void onClick(View v) 
	          {
	        	  if(holder.isRegularSeason)
	        	  {
	        		  Toast.makeText(context, "Note, This is a drafting tool and won't be of much use during the regular season.", Toast.LENGTH_LONG).show();
	        	  }
	        	  moreInfo(new Dialog(context, R.style.RoundCornersFull));
	          }
	    });    
		watch.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				watchList = ReadFromFile.readWatchList(context);
				if(watchList.size() > 0 && holder.parsedPlayers.contains(watchList.get(0)))
				{
					HandleWatchList.handleWatchInit(holder, cont, watchList);
				}
				else
				{
					Toast.makeText(context, "Watch list is empty", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		//Handle the search onclick
		/*search.setOnClickListener(new View.OnClickListener() 
	    {
			
	          @Override
	          public void onClick(View v) 
	          {
	        	  try {
	        		  PlayerInfo obj = new PlayerInfo();
	        		  obj.holder = holder;
	        		  obj.searchCalled(cont, false, null);
	        	  } catch (IOException e) {
						// TODO Auto-generated catch block
	        		  e.printStackTrace();
	        	  }
	          }
	    });  */
		compare.setOnClickListener(new View.OnClickListener()
		{
	          @Override
	          public void onClick(View v) 
	          {
	        	  if(holder.isRegularSeason)
	        	  {
	        		  Toast.makeText(context, "Note, This is a drafting tool and won't be of much use during the regular season.", Toast.LENGTH_LONG).show();
	        	  }
	        	  ComparatorHandling.handleComparingInit(holder, cont);
	          }
		});
		//sort pop up on click
		sort.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SortHandler.initialPopUp(cont, holder, R.id.listview_search, true, -1, null);
			}
		});
	}
	
	/**
	 * Calls the function that handles filtering 
	 * quantity size
	 */
	public void filterQuantity()
	{
		if(sizeOutput == -1)
		{
			sizeOutput = holder.players.size();
		}
		ManageInput.filterQuantity(cont,"Rankings", sizeOutput);		
	}
	
	/**
	 * Configures the built in search to do what it should
	 */
	public void configSearch(){
		ImageView submit = (ImageView)findViewById(R.id.rankings_search_submit);
		final AutoCompleteTextView input = (AutoCompleteTextView)findViewById(R.id.ranking_search);
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for(PlayerObject player : holder.players)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", player.info.name);
			if(!player.info.name.contains("D/ST") && player.info.position.length() >= 1 && player.info.team.length() > 2)
			{
				datum.put("sub", player.info.position + " - " + player.info.team);
			}
			else
			{
				datum.put("sub", "");
			}
			data.add(datum);
		}
		List<Map<String, String>> dataSorted = ManageInput.sortData(data);
		 final SimpleAdapter mAdapter = new SimpleAdapter(cont, dataSorted, 
		    		android.R.layout.simple_list_item_2, 
		    		new String[] {"main", "sub"}, 
		    		new int[] {android.R.id.text1, 
		    			android.R.id.text2});
		   input.setAdapter(mAdapter);
		input.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = ((TwoLineListItem)arg1).getText1().getText().toString();
				input.setText(text + ", " + ((TwoLineListItem)arg1).getText2().getText().toString());
			}
		});
		input.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				input.setText("");
				return true;
			}
			
		});
		input.clearFocus();
		submit.setOnClickListener(new OnClickListener(){
 
			@Override
			public void onClick(View v) {
				if(holder.parsedPlayers.contains(input.getText().toString().split(", ")[0]))
				{
					dialog.dismiss();
					new PlayerInfo().outputResults(input.getText().toString(), true, (Activity) cont, holder, false, true);
				}
				else
				{
					Toast.makeText(cont, "Not a valid player name", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
	}
	

    /**
     * Sets the dialog to handle the salary/value information
     * @param dialog
     */
    public void moreInfo(final Dialog dialog)
    {
    	DecimalFormat df = new DecimalFormat("#.##");
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.value_salary); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		String salRem = Integer.toString((int)(holder.draft.remainingSalary));
		String value = Integer.toString((int)holder.draft.value); 
		TextView remSalary = (TextView)dialog.findViewById(R.id.remSalary);
		TextView draftVal = (TextView)dialog.findViewById(R.id.draftValue);
		TextView paaView = (TextView)dialog.findViewById(R.id.draft_paa);
		double paa = 0.0;
		if(holder.draft.playersDrafted(holder.draft) != 0)
		{
			draftVal.setVisibility(View.VISIBLE);
			paa = Draft.paaTotal(holder.draft);
			paaView.setVisibility(View.VISIBLE);
			paaView.setText("PAA total: " + df.format(paa));
		}
		else
		{
			paaView.setVisibility(View.GONE);
			draftVal.setVisibility(View.GONE);
		}
		ProgressBar salBar = (ProgressBar)dialog.findViewById(R.id.progressBar1);
		salBar.setMax((int) (200 / aucFactor));
		remSalary.setText("Salary Left: $" + salRem);
		draftVal.setText("Value Thus Far: $" + value);
		salBar.setProgress(Integer.parseInt(salRem));		
		Button svDismiss = (Button)dialog.findViewById(R.id.salValDismiss);
		svDismiss.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button svInfo = (Button)dialog.findViewById(R.id.more_info);
		svInfo.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				handleInfo(new Dialog(context, R.style.RoundCornersFull));
	    	}	
		});
		Button unDraft = (Button)dialog.findViewById(R.id.undraft);
		unDraft.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
				Draft.undraft(new Dialog(context), holder, context);
	    	}	
		});
		Button valLeft = (Button)dialog.findViewById(R.id.value_left);
		valLeft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				posValLeft(new Dialog(context, R.style.RoundCornersFull), holder, context);
			}
		});
		if(!isAuction)
		{
			remSalary.setVisibility(View.GONE);
			draftVal.setVisibility(View.GONE);
			salBar.setVisibility(View.GONE);
		}
    	dialog.show();
    }
    
	/**
	 * Calculates the PAA left at a position
	 * @param pos
	 * @return
	 */
	public static String paaDiff(String pos, Storage holder)
	{
		DecimalFormat df = new DecimalFormat("#.#");
		String result = "3/5/10 back: ";
		double paaLeft = 0.0;
		int counter = 0;
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.worth > b.values.worth)
			    {
			        return -1;
			    }
			    if (a.values.worth < b.values.worth)
			    {
			    	return 1;
			    } 
			    return 0;
			}
		});
		for(PlayerObject player: holder.players)
		{
			if(!Draft.isDrafted(player.info.name, holder.draft) && player.info.position.equals(pos))
			{
				inter.add(player);
			}
		}
		while(!inter.isEmpty())
		{
			PlayerObject player = inter.poll();
			paaLeft += player.values.paa;
			counter++;
			if(counter > 10)
			{
				result += df.format(paaLeft);
				break;
			}
			if(counter == 4)
			{
				result += df.format(paaLeft) + "/";
			}
			if(counter == 6)
			{
				result += df.format(paaLeft) + "/";
			}
		}
		return result;
	}

	public void posValLeft(final Dialog dialog, Storage holder, final Context context) 
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.paa_pos_left); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    TextView qbLeft = (TextView)dialog.findViewById(R.id.qb_paa_left);
	    TextView rbLeft = (TextView)dialog.findViewById(R.id.rb_paa_left);
	    TextView wrLeft = (TextView)dialog.findViewById(R.id.wr_paa_left);
	    TextView teLeft = (TextView)dialog.findViewById(R.id.te_paa_left);
	    Roster r = ReadFromFile.readRoster(context);
	    if(r.qbs != 0)
	    {
	    	qbLeft.setText("QB: " + paaDiff("QB", holder).split(": ")[1]);
	    }
	    else
	    {
	    	qbLeft.setVisibility(View.GONE);
	    }
	    if(r.rbs != 0)
	    {
	    	rbLeft.setText("RB: " + paaDiff("RB", holder).split(": ")[1]);
	    }
	    else
	    {
	    	rbLeft.setVisibility(View.GONE);
	    }
	    if(r.wrs != 0)
	    {
	    	wrLeft.setText("WR: " + paaDiff("WR", holder).split(": ")[1]);
	    }
	    else
	    {
	    	wrLeft.setVisibility(View.GONE);
	    }
	    if(r.teams != 0)
	    {
	    	teLeft.setText("TE: " + paaDiff("TE", holder).split(": ")[1]);
	    }
	    else
	    {
	    	teLeft.setVisibility(View.GONE);
	    }
	    Button back = (Button)dialog.findViewById(R.id.val_left_back);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				moreInfo(new Dialog(context, R.style.RoundCornersFull));
			}
	    }); 
	}
	
	/**
	 * Calculates the paa left of the top x players at position y
	 */
	public static double valLeft(Storage holder, String pos, int max) 
	{
		double total = 0.0;
		int counter = 0;
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals(pos) && !Draft.isDrafted(player.info.name, holder.draft) && counter < max)
			{
				counter++;
				total += player.values.paa;
			}
		}
		return total;
	}
	
    /**
     * Sets the dialog to hold the selected players
     * then shows it.
     * @param dialog
     */
    public void handleInfo(final Dialog dialog)
    {
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.draft_team_status);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
    	String qbs = handleDraftParsing(holder.draft.qb);
    	String rbs = handleDraftParsing(holder.draft.rb);
    	String wrs = handleDraftParsing(holder.draft.wr);
    	String tes = handleDraftParsing(holder.draft.te);
    	String ds = handleDraftParsing(holder.draft.def);
    	String ks = handleDraftParsing(holder.draft.k);
    	TextView qb = (TextView)dialog.findViewById(R.id.qb_header);
    	TextView rb = (TextView)dialog.findViewById(R.id.rb_header);
    	TextView wr = (TextView)dialog.findViewById(R.id.wr_header);
    	TextView te = (TextView)dialog.findViewById(R.id.te_header);
    	TextView d = (TextView)dialog.findViewById(R.id.d_header);
    	TextView k = (TextView)dialog.findViewById(R.id.k_header);
    	qb.setText("Quarterbacks: " + qbs);
    	rb.setText("Running Backs: " + rbs);
    	wr.setText("Wide Receivers: " + wrs);
    	te.setText("Tight Ends: " + tes);
    	d.setText("D/ST: " + ds);
    	k.setText("Kickers: " + ks);
    	Roster r = ReadFromFile.readRoster(cont);
    	if(qb.getText().toString().contains("None selected") && r.qbs == 0)
    	{
    		qb.setVisibility(View.GONE);
    	}
    	if(rb.getText().toString().contains("None selected") && r.rbs == 0)
    	{
    		rb.setVisibility(View.GONE);
    	}
    	if(wr.getText().toString().contains("None selected") && r.wrs == 0)
    	{
    		wr.setVisibility(View.GONE);
    	}
    	if(te.getText().toString().contains("None selected") && r.tes == 0)
    	{
    		te.setVisibility(View.GONE);
    	}
    	if(d.getText().toString().contains("None selected") && r.def == 0)
    	{
    		d.setVisibility(View.GONE);
    	}
    	if(k.getText().toString().contains("None selected") && r.k == 0)
    	{
    		k.setVisibility(View.GONE);
    	}
    	dialog.show();
    	Button back = (Button)dialog.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				dialog.dismiss();
				moreInfo(new Dialog(context, R.style.RoundCornersFull));
			}
		});
    }
    
    /**
     * Handles parsing of the draft data
     */
    public static String handleDraftParsing(List<PlayerObject> nameList) {
    	String names = "";
    	for(PlayerObject po: nameList)
    	{
    		names += po.info.name + ", ";
    	}
    	if(names.equals(""))
    	{
    		names = "None selected.";
    	}
    	else
    	{
    		names = names.substring(0, names.length()-2);
    	}
    	return names;
	}

	/**
	 * Handles the middle ground before setting the listView
	 * @param holder
	 * @param cont
	 */
	public void intermediateHandleRankings(Activity cont)
	{ 
		isAsync = false;
		int maxSize = ReadFromFile.readFilterQuantitySize((Context)cont, "Rankings");
		PriorityQueue<PlayerObject>inter = null;
		PriorityQueue<PlayerObject> totalList = null;
		if(!holder.isRegularSeason)
		{
			if(isAuction)
			{
				inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
				totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
						{
							@Override
							public int compare(PlayerObject a, PlayerObject b) 
							{
								if (a.values.worth > b.values.worth)
							    {
							        return -1;
							    }
							    if (a.values.worth < b.values.worth)
							    {
							    	return 1;
							    }
							    return 0;
							}
						});
			}
			else
			{
				inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
							@Override
							public int compare(PlayerObject a, PlayerObject b) 
							{
								if(a.values.ecr == -1 && b.values.ecr != -1)
								{
									return 1;
								}
								if(a.values.ecr != -1 && b.values.ecr == -1)
								{
									return -1;
								}
								if(a.values.ecr == -1 && b.values.ecr == -1)
								{
									if(a.values.worth > b.values.worth)
									{
										return -1;
									}
									if(b.values.worth > a.values.worth)
									{
										return 1;
									}
									return 0;
								}
								if (a.values.ecr > b.values.ecr)
							    {
							        return 1;
							    }
							    if (a.values.ecr < b.values.ecr)
							    {
							    	return -1;
							    }
							    return 0;
							}
				});
				totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
						{
							@Override
							public int compare(PlayerObject a, PlayerObject b) 
							{
								if(a.values.ecr == -1 && b.values.ecr != -1)
								{
									return 1;
								}
								if(a.values.ecr != -1 && b.values.ecr == -1)
								{
									return -1;
								}
								if(a.values.ecr == -1 && b.values.ecr == -1)
								{
									if(a.values.worth > b.values.worth)
									{
										return -1;
									}
									if(b.values.worth > a.values.worth)
									{
										return 1;
									}
									return 0;
								}
								if (a.values.ecr > b.values.ecr)
							    {
							        return 1;
							    }
							    if (a.values.ecr < b.values.ecr)
							    {
							    	return -1;
							    }
							    return 0;
							}
						});
			}
		}
		else
		{
			inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
			{
				@Override
				public int compare(PlayerObject a, PlayerObject b) 
				{
					if(a.values.points <=0 && b.values.points > 0)
					{
						return 1;
					}
					if(a.values.points > 0 && b.values.points <= 0)
					{
						return -1;
					}
					if(a.values.points == 0 && b.values.points == 0)
					{
						if(a.values.ecr > b.values.ecr)
						{
							return 1;
						}
						if(b.values.ecr > a.values.ecr)
						{
							return -1;
						}
						return 0;
					}
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
			totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if(a.values.points <=0 && b.values.points > 0)
							{
								return 1;
							}
							if(a.values.points > 0 && b.values.points <= 0)
							{
								return -1;
							}
							if(a.values.points == 0 && b.values.points == 0)
							{
								if(a.values.ecr > b.values.ecr)
								{
									return 1;
								}
								if(b.values.ecr > a.values.ecr)
								{
									return -1;
								}
								return 0;
							}
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
		}
		if(posList.size() > 1)
		{
			posFilter = "All Positions";
		}
		else
		{
			posFilter = posList.get(0);
		}
		if(teamList.size() > 1)
		{
			teamFilter = "All Positions";
		}
		else
		{
			teamFilter = teamList.get(0);
		}
		Roster r = ReadFromFile.readRoster(cont);
		for(int i = 0; i < holder.players.size(); i++)
		{
			PlayerObject player = holder.players.get(i);
			if(!holder.draft.ignore.contains(player.info.name) && r.isRostered(player))
			{
				if((teamFilter.contains("All") && posFilter.contains("All")) || 
						(!teamFilter.contains("All") && teamList.contains(player.info.team) && posFilter.contains("All")) ||
						(teamFilter.contains("All") && !posFilter.contains("All") && posList.contains(player.info.position)) ||
						(!teamFilter.contains("All") && teamList.contains(player.info.team) && !posFilter.contains("All") && posList.contains(player.info.position)))
				{
					inter.add(player);
				}
			}
		}
		int total = inter.size();
		double fraction = (double)maxSize * 0.01;
		double newSize = total * fraction;
		if((int)newSize != total)
		{
			for(int i = 0; i < newSize; i++)
			{
				totalList.add(inter.poll());
			}
			rankingsFetched(totalList, cont);
		}
		else
		{
			rankingsFetched(inter, cont);
		}
	}

	/**
     * The function that handles what happens when
     * the rankings are all fetched
     * @param holder 
     */
    public void rankingsFetched(PriorityQueue<PlayerObject> playerList, Activity cont)
    {
	    if(refreshed)
	    {
	    	WriteToFile.storeRankingsAsync(holder, (Context)cont);
			SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
			editor.putBoolean("Rankings Update Home", true).apply();
			editor.putBoolean("Rankings Update Trending", true).apply();
			editor.putBoolean("Rankings Update Draft", true).apply();
			editor.putBoolean("Rankings Update Import", true).apply();
			if(Home.holder.players == null || Home.holder.players.size() < 5)
			{
				Home.holder = holder;
			}
	    	refreshed = false;
	    }
	    listview = (OutBounceListView) cont.findViewById(R.id.listview_rankings);
	    listview.setAdapter(null);
	    data = new ArrayList<Map<String, String>>();
	    adapter = new SimpleAdapter(cont, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub", "hidden"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2, R.id.text3});
	    listview.setAdapter(adapter);
	    handleRankingsClick(holder, cont, listview);
	    while(!playerList.isEmpty())
	    {
	    	PlayerObject elem = playerList.poll();
	        DecimalFormat df = new DecimalFormat("#.##");
	        Map<String, String> datum = new HashMap<String, String>(2);
	        if(!holder.isRegularSeason)
	        {
		        if(isAuction)
		        {
		        	if(elem.values.secWorth > 0.0)
		        	{
		        		datum.put("main", df.format(elem.values.secWorth) + ":  " + elem.info.name);
		        	}
		        	else
		        	{
		        		datum.put("main", df.format(elem.values.worth)+ ":  " + elem.info.name); 
		        	}
		        }
		        else
		        {
		        	if(elem.values.ecr != -1)
		        	{
		        		datum.put("main", df.format(elem.values.ecr)+ ":  " + elem.info.name);
		        	}
		        	else
		        	{
		        		datum.put("main", elem.info.name);
		        	}
		        }
	        }
	        else
	        {
	        	datum.put("main", df.format(elem.values.points) + ":  " + elem.info.name);
	        }
	        StringBuilder sub = new StringBuilder(1000);
	        if(elem.info.position.length() > 0)
	        {
	        	sub.append(elem.info.position);
	        }
	        if((elem.info.team.length() > 2 &&	!elem.info.team.equals("---") && !elem.info.team.equals("FA")))
	        {
	        	sub.append(" - " + elem.info.team);
	        	if((holder.bye.get(elem.info.team) != null)){
	        		sub.append(" (Bye: " + holder.bye.get(elem.info.team) + ")");
	        	}
	        }
	        if(elem.values.points > 0.0 && !holder.isRegularSeason)
	        {
	        	sub.append("\nProjection: " + df.format(elem.values.points));
	        }
	        if(holder.isRegularSeason)
	        {
	        	if(!elem.info.adp.equals("Not set") && !(elem.info.adp.equals("Bye Week") || elem.values.points == 0.0))
	        	{
	        		sub.append("\nOpponent: " + elem.info.adp);
	        		if(!elem.info.position.equals("D/ST") && !elem.info.adp.equals("Bye Week"))
	        		{
	        			sub.append(" (SOS: " + 	holder.sos.get(elem.info.adp + "," + elem.info.position) + ")");
	        		}
	        	}
	        	if(elem.values.rosRank > 0)
	        	{
	        		sub.append("\nROS Positional Ranking: " + elem.values.rosRank);
	        	}
	        }
	        
	        datum.put("sub", sub.toString());
	        if(watchList.contains(elem.info.name))
	        {
	        	datum.put("hidden", "W");
	        }
	        else
	        {
	        	datum.put("hidden", "");
	        }
	        data.add(datum);
	        adapter.notifyDataSetChanged();
	        
	    } 
	}
    
    /**
     * Handles rankings onclick (dialog)
     */
    public static void handleRankingsClick(final Storage holder, final Activity cont, final ListView listview)
    {

    	 listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listview.setSelection(arg2);
				String selected = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				String moreInfo = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text2)).getText().toString().split(" \\(Bye")[0];
				if(selected.contains(":  "))
				{
					selected = selected.split(":  ")[1];
				}
				else if(selected.contains(": "))
				{
					selected = selected.split(": ")[1];
				}
				PlayerInfo obj = new PlayerInfo();
				obj.outputResults(selected + ", " + moreInfo, true, (Rankings)context, holder, false, true);
			}
    	 });
    	 listview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				System.out.println("In long click");
				String namePlayer = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				if(namePlayer.contains(":"))
				{
					namePlayer = namePlayer.split(":  ")[1];
				}
				int i = -1;
				for(String name : watchList)
				{
					if(name.equals(namePlayer))
					{
						i = 1;
						break;
					}
				}
				if(i == -1)
				{
					for(PlayerObject iter : holder.players)
					{
						if(iter.info.name.equals(namePlayer))
						{
							Rankings.bumpEntityValue(iter, cont);
							break;
						}
					}
					((TextView)((RelativeLayout)arg1).findViewById(R.id.text3)).setText("W");
					watchList.add(namePlayer);
					WriteToFile.writeWatchList(context, watchList);
					Toast.makeText(context, namePlayer + " added to watch list", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, namePlayer + " already in watch list", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
    	 });
    	 touchListener =
                 new RankingsSwipeDismissListViewTouchListener(
                         true, "Rankings", listview,
                         new RankingsSwipeDismissListViewTouchListener.OnDismissCallback() {
                             @Override
                             public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                 listview.setOnTouchListener(null);
                                 listview.setOnScrollListener(null);
                            	 String name = "";
                                 int index = 0;
                                 Map<String, String> view = null;
                                 for (int position : reverseSortedPositions) {
                                	 if(data.get(position).containsKey(":"))
                                	 {
                                		 if(data.get(position).get("main").contains(":  "))
                                		 {
                                			 name = data.get(position).get("main").split(":  ")[1];
                                		 }
                                		 else
                                		 {
                                			 name = data.get(position).get("main").split(": ")[1];
                                		 }
                                	 }
                                	 else
                                	 {
                                		 name = data.get(position).get("main");
                                	 }
                                	 view = data.get(position);
                                	 data.remove(position);
                                	 index = position;
                                 }
                                 adapter.notifyDataSetChanged();
                                 handleDrafted(view, holder, cont, null, index);
                                 Rankings.isSwiping = false;
                                 swipedText = null;                             	
                             }
                         });
    	 if(!holder.isRegularSeason)
    	 {
	         listview.setOnTouchListener(touchListener);
	         listview.setOnScrollListener(touchListener.makeScrollListener());
    	 }
    }
    
    /**
     * Handles the drafted dialog
     */
    public static void handleDrafted(final Map<String, String> view, final Storage holder, final Activity cont, final Dialog dialog, 
    		final int index)
    { 
    	listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
    	final Dialog popup = new Dialog(cont, R.style.RoundCornersFull);
    	popup.setCancelable(false);
		popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	popup.setContentView(R.layout.draft_by_who);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popup.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    popup.getWindow().setAttributes(lp);
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	String name = view.get("main");
    	if(view.get("main").contains(":"))
    	{
    		name = view.get("main").split(":  ")[1];
    		header.setText("Who drafted " + name + "?");
    	}
    	else
    	{
    		name = view.get("main");
    		header.setText("Who drafted " + name + "?");
    	}
    	if(Draft.isDrafted(name, holder.draft))
    	{
    		Toast.makeText(cont, name + " is already drafted", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	final String d = name;
    	popup.show();
    	Button close = (Button)popup.findViewById(R.id.draft_who_close);
    	close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				data.add(index, view);
				adapter.notifyDataSetChanged();
				popup.dismiss();
				return;
			}
    	});
    	Button someone = (Button)popup.findViewById(R.id.drafted_by_someone);
    	someone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
		    	holder.draft.ignore.add(d);
				WriteToFile.writeDraft(holder.draft, cont);
				popup.dismiss();
				if(dialog != null)
				{
					dialog.dismiss();
				}
				Toast.makeText(cont, "Removing " + d + " from the list", Toast.LENGTH_SHORT).show();
			}
    	});
    	
    	Button me = (Button)popup.findViewById(R.id.drafted_by_me);
    	me.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isAuction)
				{
					draftedByMe(d, view, holder, cont, listview, popup, dialog, index);
				}
				else
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.name.equals(d))
						{
							holder.draft.draftPlayer(player, holder.draft, 1, cont);
							Toast.makeText(cont, "Drafting " + d, Toast.LENGTH_SHORT).show();
							holder.draft.ignore.add(d);
							WriteToFile.writeDraft(holder.draft, cont);
							
							popup.dismiss();
							break;
						}
					}
				}
			}
    	});
    }
    
    /**
     * Handles the 'drafted by me' dialog
     */
    public static void draftedByMe(final String name, final Map<String, String> view, final Storage holder, final Activity cont,
    		final ListView listview, final Dialog popup, final Dialog dialog, final int index)
    {
    	popup.setContentView(R.layout.draft_by_me);
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	header.setText("How much did " + name + " cost?");
    	Button back = (Button)popup.findViewById(R.id.draft_who_close);
    	back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				popup.dismiss();
				handleDrafted(view, holder, cont, dialog, index);
			}
    	});
    	List<String> possResults = new ArrayList<String>();
    	for(int i = 1; i < 201; i++)
    	{
    		possResults.add(String.valueOf(i));
    	}
    	AutoCompleteTextView price = (AutoCompleteTextView)popup.findViewById(R.id.amount_paid);
    	ArrayAdapter<String> doubleAdapter = new ArrayAdapter<String>(cont,
                android.R.layout.simple_dropdown_item_1line, possResults);
    	price.setAdapter(doubleAdapter);
    	price.setThreshold(1);
    	price.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int val = Integer.parseInt(((TextView)arg1).getText().toString());
				for(PlayerObject player : holder.players)
				{
					if(player.info.name.equals(name))
					{
						if(dialog != null)
						{
							dialog.dismiss();
						}
						if(val <= holder.draft.remainingSalary)
						{
							holder.draft.draftPlayer(player, holder.draft, val, cont);
							Toast.makeText(cont, "Drafting " + name, Toast.LENGTH_SHORT).show();
							holder.draft.ignore.add(name);
							WriteToFile.writeDraft(holder.draft, cont);
						}
						else
						{
							Toast.makeText(cont, "Not enough salary left", Toast.LENGTH_SHORT).show();
						}
						break;
					}
				}
				popup.dismiss();
			}
    	});
    }
    
    /**
     * Takes the drafted count metadata and increases it by one if it exists, otherwise it's 0
     */
    public static void bumpEntityValue(PlayerObject player, final Context cont)
    {
    	String key = "http://www.ffr.com/" + player.info.name + ", " + player.info.position + "/pi" + Home.yearKey;
		EntityUtils.getEntity((Activity) cont, key, new EntityGetListener() {
			//The entity was gotten, though an error is still possible
			@Override
			public void onGet(Entity entity) {
				int newVal = 1;
			   	if(entity.getMetaData() != null && entity.getMetaData().length() != 0)
			   	{
			   		newVal = Integer.parseInt(entity.getMetaData()) + 1;
			   	} 
			   	entity.setMetaData(String.valueOf(newVal));
		    	EntityUtils.saveEntity((Activity)cont , entity, new EntityAddListener() {
		       		@Override
		    		public void onCreate(Entity result) {
		       			//If we want to add some kind of handler, here is where to do so
		    		}
					@Override
					public void onError(SocializeException error) {
						//Some kind of error in saving, collision?
					}
		    	});
			}
			@Override
			public void onError(SocializeException error) {
				if(isNotFoundError(error)) {
					// No entity found
				}
				else {
					//Some other kind of error
				}
			}
		});
    }
}
