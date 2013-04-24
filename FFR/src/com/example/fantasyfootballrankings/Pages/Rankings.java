package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.R.id;
import com.example.fantasyfootballrankings.R.layout;
import com.example.fantasyfootballrankings.R.menu;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Handles the rankings part of the java file
 * 
 * THOUGHTS: filter? With similar code, a search function of some kind? If so, through the menu?
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
		    	return true;
			//New page opens up entirely for going home
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
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
		
		//Calculator not yet implemented
	}
	
	/**
	 * Abstracted out of the menu handler as this could get ugly
	 * once the stuff is added to the dropdown
	 * @param dialog
	 * @throws IOException 
	 */
	public static void searchCalled(final Dialog dialog, final Context oCont) throws IOException
	{
		matchedPlayers = new ArrayList<String>();
		newCont = oCont;
		Storage.fetchNames(holder, newCont);
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
            	textView.setText(matchedPlayers.get(0));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    	String qbs = "";
    	for(PlayerObject qb:holder.draft.qb)
    	{
    		qbs += qb.info.name + ", ";
    	}
    	if(qbs.equals(""))
    	{
    		qbs = "None selected.";
    	}
    	else
    	{
    		qbs = qbs.substring(0, qbs.length()-2);
    	}
    	String rbs = "";
    	for(PlayerObject qb:holder.draft.rb)
    	{
    		rbs += qb.info.name + ", ";
    	}
    	if(rbs.equals(""))
    	{
    		rbs = "None selected.";
    	}
    	else
    	{
    		rbs = rbs.substring(0, rbs.length()-2);
    	}
    	String wrs = "";
    	for(PlayerObject qb:holder.draft.wr)
    	{
    		wrs += qb.info.name + ", ";
    	}
    	if(wrs.equals(""))
    	{
    		wrs = "None selected.";
    	}
    	else
    	{
    		wrs = wrs.substring(0, wrs.length()-2);
    	}
    	String tes = "";
    	for(PlayerObject qb:holder.draft.te)
    	{
    		tes += qb.info.name + ", ";
    	}
    	if(tes.equals(""))
    	{
    		tes = "None selected.";
    	}
    	else
    	{
    		tes = tes.substring(0, tes.length()-2);
    	}
    	String ds = "";
    	for(PlayerObject qb:holder.draft.def)
    	{
    		ds += qb.info.name + ", ";
    	}
    	if(ds.equals(""))
    	{
    		ds = "None selected.";
    	}
    	else
    	{
    		ds = ds.substring(0, ds.length()-2);
    	}
    	String ks = "";
    	for(PlayerObject qb:holder.draft.k)
    	{
    		ks += qb.info.name + ", ";
    	}
    	if(ks.equals(""))
    	{
    		ks = "None selected.";
    	}
    	else
    	{
    		ks = ks.substring(0, ks.length()-2);
    	} 
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
     * The function that handles what happens when
     * the rankings are all fetched
     * @param holder
     */
    public static void rankingsFetched(Activity cont)
    {
    	System.out.println("In rankings.java again");
		for(PlayerObject e: holder.players)
		{
			System.out.println("Name: " + e.info.name);
			System.out.println("Team: " + e.info.team);
			System.out.println("Position: " + e.info.position);
			System.out.println("Value: " + e.values.worth);
			System.out.println("Strength of Schedule: " + e.info.sos);
			System.out.println("------------------------------------");
		}
		//Below = how to get stuff from here. Important to know.
		//Button test = (Button)((Activity) retCont).findViewById(R.id.trending2);
    }
}
