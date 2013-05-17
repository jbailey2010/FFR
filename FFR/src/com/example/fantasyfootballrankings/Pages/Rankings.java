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
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.TradeHandling;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Handles the rankings part of the java file
 * 
 * @author Jeff
 *
 */
public class Rankings extends Activity {
	final Context cont = this;
	static Context newCont;
	static Storage holder = new Storage();
	static Button voice;
	static AutoCompleteTextView textView;
	private static final int REQUEST_CODE = 1234;
	Dialog dialog;
	static List<String> matchedPlayers;
	static Button search;
	static Button info;
	static Button compare;
	static Button calc;
	static ListView listview;
	static boolean refreshed = false;
	static int sizeOutput = -1;
	/**
	 * Sets up the view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rankings);
		search = (Button)findViewById(R.id.search);
		info = (Button)findViewById(R.id.draft_info);
		compare = (Button)findViewById(R.id.player_comparator);
		calc = (Button)findViewById(R.id.trade_calc);
    	listview = (ListView)findViewById(R.id.listview_rankings);
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
			case R.id.refresh:
				refreshRanks(dialog);
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
	 * Handles the refreshing of the rankings/user input to do so
	 * @param dialog
	 */
	public void refreshRanks(final Dialog dialog)
	{
		dialog.setContentView(R.layout.refresh); 
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
		ReadFromFile.fetchNamesBackEnd(holder, cont);
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	String checkExists = prefs.getString("Player Values", "Not Set");
    	if(checkExists != "Not Set")
    	{
			try {
				ReadFromFile.fetchPlayers(holder,cont);
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
	        		  searchCalled(dialog, cont);
	        	  } catch (IOException e) {
						// TODO Auto-generated catch block
	        		  e.printStackTrace();
	        	  }
	          }
	    });  
		//Comparator not yet implemented
		
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
		dialog.setContentView(R.layout.search_players);
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
					outputResults(dialog, textView.getText().toString());
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
    public static void outputResults(final Dialog dialog, String namePlayer)
    {
    	dialog.setContentView(R.layout.search_output);
    	List<String>output = new ArrayList<String>(12);
    	TextView name = (TextView)dialog.findViewById(R.id.name);
    	if(namePlayer.equals(""))
    	{
    		return;
    	}
    	name.setText(namePlayer);
    	PlayerObject searchedPlayer = new PlayerObject("","","",0);
    	for(PlayerObject player : holder.players)
    	{
    		if(player.info.name.equals(namePlayer))
    		{
    			searchedPlayer = player;
    			break;
    		}
    	}
    	DecimalFormat df = new DecimalFormat("#.##");
    	String low = String.valueOf(searchedPlayer.values.low);
    	if(searchedPlayer.values.low == 100)
    	{
    		low = String.valueOf(searchedPlayer.values.high);
    	}
    	output.add("Worth: " + df.format(searchedPlayer.values.worth));
    	if(searchedPlayer.info.position.equals("K") || searchedPlayer.info.position.length() > 1)
    	{
    		output.add("Position: " + searchedPlayer.info.position);
    	}
    	output.add("Team: " + searchedPlayer.info.team);
    	if(!searchedPlayer.info.age.equals("0") && !searchedPlayer.info.position.equals("D/ST"))
    	{
    		output.add("Age: " + searchedPlayer.info.age);
    	}
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST")
    			&& !searchedPlayer.stats.equals(" ") && searchedPlayer.stats.length() > 5)
    	{
    		output.add(searchedPlayer.stats);
    		output.add(searchedPlayer.injuryStatus);
    	}
    	output.add("Status: " + searchedPlayer.info.status);
    	if(searchedPlayer.info.sos > 0)
    	{
    		output.add("Positional SOS: " + searchedPlayer.info.sos);
    	}
    	if(!searchedPlayer.info.bye.equals("Not set"))
    	{
    		output.add("Bye: " + searchedPlayer.info.bye);
    	}
    	if(!searchedPlayer.info.position.equals("K") && searchedPlayer.draftClass.length() > 4)
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
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
    	{
    		output.add("Contract Status: " + searchedPlayer.info.contractStatus);
    	} 
    	output.add("Showed up in " + searchedPlayer.values.count + " rankings.");
    	output.add("Highest value: " + searchedPlayer.values.high);
    	output.add("Lowest value: " + low);
    	if(!searchedPlayer.info.position.equals("K") && !searchedPlayer.info.position.equals("D/ST"))
    	{ 
    		if(searchedPlayer.info.passRunRatio.length() > 2)
    		{
    			output.add(searchedPlayer.info.passRunRatio);
    		}
    		if(searchedPlayer.info.oLineStatus.length() > 3)
    		{
    			output.add(searchedPlayer.info.oLineStatus);
    		}
    		if(!searchedPlayer.info.additionalStat.equals(""))
	    	{
	    		output.add(searchedPlayer.info.additionalStat);
	    	}
    	}
    	dialog.show();
    	ListView results = (ListView)dialog.findViewById(R.id.listview_search);
    	ManageInput.handleArray(output, results, (Rankings)newCont);
    	Button back = (Button)dialog.findViewById(R.id.search_back);
		back.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				try {
					searchCalled(dialog, newCont);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	Button close = (Button)dialog.findViewById(R.id.search_close);
		close.setOnClickListener(new OnClickListener()
		{ 
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
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
    	double totalWorth = 0.0;
    	for(PlayerObject player : holder.players)
    	{
    		totalWorth += player.values.worth;
    	}
    	System.out.println(holder.players.size() + " " + totalWorth);
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
		int total = holder.players.size();
		double fraction = (double)maxSize * 0.01;
		double newSize = total * fraction;
		for(int i = 0; i < holder.players.size(); i++)
		{
			inter.add(holder.players.get(i));
		}
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
}
