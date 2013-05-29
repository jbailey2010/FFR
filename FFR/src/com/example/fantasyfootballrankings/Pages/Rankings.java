package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;


import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.id;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.menu;
import com.example.fantasyfootballrankings.ClassFiles.ComparatorHandling;
import com.example.fantasyfootballrankings.ClassFiles.HandleWatchList;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.TradeHandling;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadDraft;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.view.HapticFeedbackConstants;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * Handles the rankings part of the java file
 * 
 * @author Jeff
 *
 */
public class Rankings extends Activity {
	final Context cont = this;
	static Context newCont;
	static Context context;
	static Storage holder = new Storage();
	static Button voice;
	static AutoCompleteTextView textView;
	private static final int REQUEST_CODE = 1234;
	static Dialog dialog;
	static List<String> matchedPlayers;
	static Button search;
	static Button info;
	static Button compare;
	static Button calc;
	static ListView listview;
	static boolean refreshed = false;
	static int sizeOutput = -1;
	static String teamFilter = "";
	static String posFilter = "";
	static List<String> teamList = new ArrayList<String>();
	static List<String> posList = new ArrayList<String>();
	static List<String> watchList = new ArrayList<String>();
	/**
	 * Sets up the view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		watchList.clear();
		setContentView(R.layout.activity_rankings);
		search = (Button)findViewById(R.id.search);
		info = (Button)findViewById(R.id.draft_info);
		compare = (Button)findViewById(R.id.player_comparator);
		calc = (Button)findViewById(R.id.trade_calc);
    	listview = (ListView)findViewById(R.id.listview_rankings);
    	context = this;
    	setLists();
		handleRefresh();
		handleOnClickButtons();
	}
	
	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.rankings, menu);
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
			case R.id.watch_list:
				watchList = ReadFromFile.readWatchList(context);
				if(watchList.size() > 0 && holder.parsedPlayers.contains(watchList.get(0)))
				{
					HandleWatchList.handleWatchInit(holder, cont, watchList);
				}
				else
				{
					Toast.makeText(context, "Watch list is empty", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.refresh:
				refreshRanks(dialog);
		    	return true;
			case R.id.filter_topics_rankings:
				filterTopics(dialog);
				return true;
			case R.id.filter_quantity_menu:
				filterQuantity();
				return true;
			//New page opens up entirely for going home
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
		        return true;
			case R.id.news:
		        Intent intent_news = new Intent(cont, News.class);
		        cont.startActivity(intent_news);		
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
	 * Populate team/pos lists
	 */
	public static void setLists()
	{
		if(posList.size() != 7)
		{
			posList.add("All Positions");
			posList.add("QB");
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("D/ST");
			posList.add("K");
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
	public static void filterTopics(final Dialog dialog2) 
	{
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.rankings_filter);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button cancel = (Button)dialog.findViewById(R.id.rankings_filter_close);
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		posList.clear();
		posList.add("All Positions");
		posList.add("QB");
		posList.add("RB");
		posList.add("WR");
		posList.add("TE");
		posList.add("D/ST");
		posList.add("K");
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
	public void refreshRanks(final Dialog dialog)
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.refresh); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		Button refreshDraft = (Button)dialog.findViewById(R.id.reset_draft);
		refreshDraft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Draft.resetDraft(holder.draft, holder, context);	
				dialog.dismiss();
			}
		});
		Button refreshDismiss = (Button)dialog.findViewById(R.id.refresh_cancel);
		refreshDismiss.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
		Button refreshSubmit = (Button)dialog.findViewById(R.id.refresh_confirm);
		refreshSubmit.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				refreshed = true;
				listview.setAdapter(null);
				dialog.dismiss();
				try {
					ParseRankings.runRankings(holder, cont);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (XPatherException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}	
		});
    	dialog.show();	
	}
	
	/**
	 * Handles the possible loading of the players
	 */
	public void handleRefresh()
	{
		View v = findViewById(android.R.id.home);
		v.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				listview.smoothScrollToPosition(0);		
			}
		});
		if(holder.playerNames.size() < 19)
		{
			ReadFromFile.fetchNamesBackEnd(holder, cont);
		}
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		if(holder.players.size() < 10)
		{
	    	String checkExists = prefs.getString("Player Values", "Not Set");
	    	if(checkExists != "Not Set")
	    	{
				try {
					ReadFromFile.fetchPlayers(holder,cont, true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XPatherException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
		}
		else
		{
			intermediateHandleRankings((Rankings)cont);
		}
	}
	
	/**
	 * Sets onclick of the button bar
	 */
	public void handleOnClickButtons()
	{
		dialog = new Dialog(cont);
		//Handle the moreinfo click
		info.setOnClickListener(new View.OnClickListener() 
	    {
	          @Override
	          public void onClick(View v) 
	          {
	        	  moreInfo(dialog);
	          }
	    });    
		//Handle the search onclick
		search.setOnClickListener(new View.OnClickListener() 
	    {
	          @Override
	          public void onClick(View v) 
	          {
	        	  try {
	        		  searchCalled(new Dialog(cont), cont);
	        	  } catch (IOException e) {
						// TODO Auto-generated catch block
	        		  e.printStackTrace();
	        	  }
	          }
	    });  
		compare.setOnClickListener(new View.OnClickListener()
		{
	          @Override
	          public void onClick(View v) 
	          {
	        	  ComparatorHandling.handleComparingInit(holder, cont);
	          }
		});
		
		//Calculator pop up on click
		calc.setOnClickListener(new View.OnClickListener() 
	    {
	          @Override
	          public void onClick(View v) 
	          {
	        	  TradeHandling.handleTradeInit(holder, cont);
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
	 * Abstracted out of the menu handler as this could get ugly
	 * once the stuff is added to the dropdown
	 * @param dialog
	 * @throws IOException 
	 */
	public static void searchCalled(final Dialog dialog, final Context oCont) throws IOException
	{
		matchedPlayers = new ArrayList<String>(15);
		newCont = oCont;
		ReadFromFile.fetchNames(holder, newCont);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       

		dialog.setContentView(R.layout.search_players);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    
		voice = (Button) dialog.findViewById(R.id.speakButton);
        textView = (AutoCompleteTextView)(dialog).findViewById(R.id.player_input);
        voice.setOnClickListener(new OnClickListener() {

    	    @Override
    	    public void onClick(final View v) {
    	            ((Rankings)newCont).speakButtonClicked(v);
    	            
    	    }
    	});
        if(matchedPlayers.size() == 0)
        {
        	ManageInput.setupAutoCompleteSearch(holder, holder.parsedPlayers, textView, newCont);
        }
        Button searchDismiss = (Button)dialog.findViewById(R.id.search_cancel);
		searchDismiss.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button searchSubmit = (Button)dialog.findViewById(R.id.search_submit);
		searchSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				if(holder.parsedPlayers.contains(textView.getText().toString()))
				{
					dialog.dismiss();
					outputResults(dialog, textView.getText().toString(), false, (Rankings)newCont, holder, false);
				}
			}
		});
		dialog.show();		
	}

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Enter the player you'd like to search for");
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            textView = (AutoCompleteTextView)dialog.findViewById(R.id.player_input);

            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            matchedPlayers = ManageInput.voiceInput(matches, newCont, holder, textView);
            if(matchedPlayers.size() != 0)
            {
            	double maxVal = 0.0;
            	String maxPlayer = "";
            	for(String player : matchedPlayers)
            	{
            		for(int i = 0; i < holder.players.size(); i++)
            		{
            			PlayerObject playerIter = holder.players.get(i);
            			if(playerIter.info.name.equals(player))
            			{
            				if(playerIter.values.worth > maxVal)
            				{
            					maxPlayer = playerIter.info.name;
            					maxVal = playerIter.values.worth;
            				}
                			break;
            			}
            		}
            	}
            	textView.setText(maxPlayer);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * outputs the results to the search dialog
     * @param dialog
     * @param namePlayer
     */
    public static void outputResults(final Dialog dialog2, final String namePlayer, boolean flag, 
    		final Activity act, final Storage holder, final boolean watchFlag)
    {
    	final Dialog dialog = new Dialog(act);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);       
    	dialog.setContentView(R.layout.search_output);
    	Button addWatch = (Button)dialog.findViewById(R.id.add_watch);
    	//If the add to list boolean exists
    	if(!watchFlag)
    	{
	    	addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//Check if the player is in the watchList
					int i = -1;
					for(String name : watchList)
					{
						if(name.equals(namePlayer))
						{
							i++;
							break;
						}
					}
					//if not, add him on the click of the button
					if(i == -1)
					{
						watchList.add(namePlayer);
						WriteToFile.writeWatchList(context, watchList);
						Toast.makeText(context, namePlayer + " added to watch list", Toast.LENGTH_SHORT).show();
					}
					else//if so, ignore the click
					{
						Toast.makeText(context, namePlayer + " already in watch list", Toast.LENGTH_SHORT).show();
					}
				}
	    	});
    	}
    	//Otherwise, the call is from the watch list, so it gives the option to remove it
    	else
    	{
    		addWatch.setText("Remove From Watch List");
    		addWatch.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast.makeText(context, namePlayer + " removed from watch list", Toast.LENGTH_SHORT).show();
					watchList.remove(namePlayer);
					WriteToFile.writeWatchList((Context)act, watchList);
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, watchList);
				}
    		});
    	}
    	//Create the output, make sure it's valid
    	List<String>output = new ArrayList<String>(12);
    	final TextView name = (TextView)dialog.findViewById(R.id.name);
    	if(namePlayer.equals(""))
    	{
    		return;
    	}
    	//Set up the header, and make a mock object with the set name
    	name.setText(namePlayer);
    	if(!watchFlag)
    	{
    		name.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					handleDrafted(name, holder, (Activity)context, dialog);
					return true;
				}
    		});
    	}
    	PlayerObject searchedPlayer = new PlayerObject("","","",0);
    	for(PlayerObject player : holder.players)
    	{
    		if(player.info.name.equals(namePlayer))
    		{
    			searchedPlayer = player;
    			break;
    		}
    	}
    	//If it's called from trending or watch list, ignore back
    	if(flag)
    	{
    		Button backButton = (Button)dialog.findViewById(R.id.search_back);
    		backButton.setVisibility(Button.GONE);
    		View backView = (View)dialog.findViewById(R.id.back_view);
    		backView.setVisibility(View.GONE);
    	}
    	//Set the data in the list
    	setSearchContent(searchedPlayer, output);
    	//Show the dialog, then set the list
    	dialog.show();
    	ListView results = (ListView)dialog.findViewById(R.id.listview_search);
    	ManageInput.handleArray(output, results, act);
    	Button back = (Button)dialog.findViewById(R.id.search_back);
    	//If it isn't gone, set that it goes back
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					dialog.dismiss();
					searchCalled(dialog2, newCont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}); 
		//Setting up close
    	Button close = (Button)dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener()
		{ 
			public void onClick(View v) {
				//If it is called from trending or rankings, dismiss it
				if(!watchFlag)
				{
					dialog.dismiss();
				}
				else //otherwise it was from watch list, so call back from there
				{
					dialog.dismiss();
					HandleWatchList.handleWatchInit(holder, (Context)act, watchList);
				}
			}
		}); 
    }
    
    /**
     * Sets the output of the search
     * @param searchedPlayer
     * @param output
     */
    public static void setSearchContent(PlayerObject searchedPlayer, List<String> output)
    {
       	DecimalFormat df = new DecimalFormat("#.##");
    	String low = String.valueOf(searchedPlayer.values.low);
    	if(searchedPlayer.values.low == 100)
    	{
    		low = String.valueOf(searchedPlayer.values.high);
    	}
    	output.add("Worth: " + df.format(searchedPlayer.values.worth));
    	if(searchedPlayer.info.position.length() > 1)
    	{
    		output.add("Position: " + searchedPlayer.info.position);
    	}
    	if(searchedPlayer.info.position.length() > 1)
    	{
    		output.add("Team: " + searchedPlayer.info.team);
    	}
    	if(!searchedPlayer.info.age.equals("0") && !searchedPlayer.info.position.equals("D/ST") && 
    			!searchedPlayer.info.age.equals("") && searchedPlayer.info.age.length() >= 2)
    	{
    		output.add("Age: " + searchedPlayer.info.age);
    	}
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST")
    			&& !searchedPlayer.stats.equals(" ") && searchedPlayer.stats.length() > 5)
    	{
    		output.add(searchedPlayer.stats);
    		if(!searchedPlayer.injuryStatus.contains("Healthy"))
    		{
    			output.add(searchedPlayer.injuryStatus);
    		}
    	}
    	if(!searchedPlayer.info.status.contains("intuition"))
    	{
    		output.add("Status: " + searchedPlayer.info.status);
    	}
    	if(searchedPlayer.info.sos > 0)
    	{
    		output.add("Positional SOS: " + searchedPlayer.info.sos);
    	}
    	if(searchedPlayer.info.bye != null && !searchedPlayer.info.bye.contains("null") &&  
    			!searchedPlayer.info.bye.equals("Not set"))
    	{
    		output.add("Bye: " + searchedPlayer.info.bye);
    	}
    	if(searchedPlayer.draftClass != null && !searchedPlayer.draftClass.contains("null") &&
    			!searchedPlayer.info.position.equals("K") && searchedPlayer.draftClass.length() > 4)
    	{
    		output.add(searchedPlayer.draftClass);
    	}
    	if(searchedPlayer.fa.size() > 1)
    	{
	    	if(searchedPlayer.fa.get(0).contains("\n"))
	    	{
	    		output.add(searchedPlayer.fa.get(0));
	    	}
	    	if(searchedPlayer.fa.get(1).contains("\n"))
	    	{
	    		output.add(searchedPlayer.fa.get(1));
	    	}
    	}
    	if(!searchedPlayer.info.adp.equals("Not set"))
    	{
    		output.add("ADP: " + searchedPlayer.info.adp);
    	}
    	if(!searchedPlayer.info.trend.equals("0.0"))
    	{
    		output.add("Weekly Value Trend: " + searchedPlayer.info.trend);
    	}
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST") && 
    			!searchedPlayer.info.contractStatus.contains("Under Contract"))
    	{
    		output.add("Contract Status: " + searchedPlayer.info.contractStatus);
    	} 
    	output.add("Showed up in " + searchedPlayer.values.count + " rankings.");
    	output.add("Highest value: " + searchedPlayer.values.high);
    	output.add("Lowest value: " + low);
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
    	{ 
    		if(searchedPlayer.info.passRunRatio != null && searchedPlayer.info.passRunRatio.length() > 2)
    		{
    			output.add(searchedPlayer.info.passRunRatio);
    		}
    		if(searchedPlayer.info.oLineStatus != null && searchedPlayer.info.oLineStatus.length() > 3)
    		{
    			output.add(searchedPlayer.info.oLineStatus);
    		}
    		if(searchedPlayer.info.additionalStat != null && !searchedPlayer.info.additionalStat.equals("")
    				&& searchedPlayer.info.additionalStat.length() > 2)
	    	{
	    		output.add(searchedPlayer.info.additionalStat);
	    	}
    	}
    }
    
    /**
     * Sets the dialog to handle the salary/value information
     * @param dialog
     */
    public static void moreInfo(final Dialog dialog)
    {
		dialog.setContentView(R.layout.value_salary); 
		String salRem = Integer.toString(holder.draft.remainingSalary);
		String value = Integer.toString((int)holder.draft.value); 
		TextView remSalary = (TextView)dialog.findViewById(R.id.remSalary);
		TextView draftVal = (TextView)dialog.findViewById(R.id.draftValue);
		ProgressBar salBar = (ProgressBar)dialog.findViewById(R.id.progressBar1);
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
				handleInfo(dialog);
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
    	dialog.show();
    }

    
    /**
     * Sets the dialog to hold the selected players
     * then shows it.
     * @param dialog
     */
    public static void handleInfo(final Dialog dialog)
    {
		dialog.setContentView(R.layout.draft_team_status);
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
    	dialog.show();
    	Button back = (Button)dialog.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				moreInfo(dialog);
			}
		});
    }
    
    /**
     * Handles parsing of the draft data
     */
    private static String handleDraftParsing(List<PlayerObject> nameList) {
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
	public static void intermediateHandleRankings(Activity cont)
	{
		int maxSize = ReadFromFile.readFilterQuantitySize((Context)cont, "Rankings");
		PriorityQueue<PlayerObject>inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
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
		PriorityQueue<PlayerObject>totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
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
		for(int i = 0; i < holder.players.size(); i++)
		{
			PlayerObject player = holder.players.get(i);
			if(posList.contains(player.info.position) && teamList.contains(player.info.team)
					&& !holder.draft.ignore.contains(player.info.name))
			{
				inter.add(player);
			}
		}
		int total = inter.size();
		double fraction = (double)maxSize * 0.01;
		double newSize = total * fraction;
		for(int i = 0; i < newSize; i++)
		{
			totalList.add(inter.poll());
		}
		rankingsFetched(totalList, cont);
	}
	
	/**
	 * A function that takes only a list of rankings and sets it to the 
	 * adapter, to be called only by the thing reading rankings from file
	 * @param rankings
	 * @param cont
	 */
	public static void listSetUp(List<String> rankings, Activity cont)
	{
	    listview = (ListView) cont.findViewById(R.id.listview_rankings);
	    listview.setAdapter(null);
	    handleRankingsClick(holder, cont, listview);
	    ManageInput.handleArray(rankings, listview, cont);
	}
	
	/**
     * The function that handles what happens when
     * the rankings are all fetched
     * @param holder
     */
    public static void rankingsFetched(PriorityQueue<PlayerObject> playerList, Activity cont)
    {
	    listview = (ListView) cont.findViewById(R.id.listview_rankings);
	    listview.setAdapter(null);
	    handleRankingsClick(holder, cont, listview);
	    List<String> rankings = new ArrayList<String>(400);
	    while(!playerList.isEmpty())
	    {
	    	PlayerObject elem = playerList.poll();
	        DecimalFormat df = new DecimalFormat("#.##");
	    	rankings.add(df.format(elem.values.worth) + ":  " + elem.info.name);
	    } 
	    if(refreshed)
	    {
	    	WriteToFile.storeRankingsAsync(holder, (Context)cont);
	    	refreshed = false;
	    }
    	WriteToFile.storeListRankings(rankings, cont);
	    ManageInput.handleArray(rankings, listview, cont);
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
				String selected = ((TextView)arg1).getText().toString();
				selected = selected.split(":  ")[1];
				Rankings.outputResults(dialog, selected, true, (Rankings)context, holder, false);
			}
    	 });
    	 listview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				handleDrafted(arg1, holder, cont, null);
				return true;
			}
    	 });
    }
    
    /**
     * Handles the drafted dialog
     */
    public static void handleDrafted(final View view, final Storage holder, final Activity cont, final Dialog dialog)
    { 
    	final Dialog popup = new Dialog(cont);
		popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	popup.setContentView(R.layout.draft_by_who);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popup.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    popup.getWindow().setAttributes(lp);
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	String name = "";
    	if(((TextView)view).getText().toString().contains(":"))
    	{
    		name = ((TextView)view).getText().toString().split(":  ")[1];
    		header.setText("Who drafted " + name + "?");
    	}
    	else
    	{
    		name = ((TextView)view).getText().toString();
    		header.setText("Who drafted " + name + "?");
    	}
    	popup.show();
    	Button close = (Button)popup.findViewById(R.id.draft_who_close);
    	close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popup.dismiss();
				return;
			}
    	});
    	Button someone = (Button)popup.findViewById(R.id.drafted_by_someone);
    	someone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
		    	String name =  ((TextView)view).getText().toString();
		    	if(((TextView)view).getText().toString().contains(":"))
		    	{
		    		name = ((TextView)view).getText().toString().split(":  ")[1];
		    	} 	
				holder.draft.ignore.add(name);
				intermediateHandleRankings(cont);
				WriteToFile.writeDraft(holder.draft, cont);
				popup.dismiss();
				if(dialog != null)
				{
					dialog.dismiss();
				}
				Toast.makeText(cont, "Removing " + name + " from the list", Toast.LENGTH_SHORT).show();
			}
    	});
    	
    	Button me = (Button)popup.findViewById(R.id.drafted_by_me);
    	me.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
		    	String name =  ((TextView)view).getText().toString();
		    	if(((TextView)view).getText().toString().contains(":"))
		    	{
		    		name = ((TextView)view).getText().toString().split(":  ")[1];
		    	} 	
				draftedByMe(name, view, holder, cont, listview, popup, dialog);
			}
    	});
    }
    
    /**
     * Handles the 'drafted by me' dialog
     */
    public static void draftedByMe(final String name, final View view, final Storage holder, final Activity cont,
    		final ListView listview, final Dialog popup, final Dialog dialog)
    {
    	popup.setContentView(R.layout.draft_by_me);
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	header.setText("How much did " + name + " cost?");
    	Button back = (Button)popup.findViewById(R.id.draft_who_close);
    	back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				popup.dismiss();
				handleDrafted(view, holder, cont, dialog);
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
							intermediateHandleRankings(cont);
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
}
