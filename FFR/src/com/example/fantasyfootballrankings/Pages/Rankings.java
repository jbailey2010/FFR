package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;

import java.net.MalformedURLException;
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
	static Context retCont;
	static Storage holder = new Storage();
	static Button voice;
	static AutoCompleteTextView textView;
	private static final int REQUEST_CODE = 1234;
	Dialog dialog;
	static List<String> matchedPlayers;
	/**
	 * Sets up the view
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		retCont = this;
		setContentView(R.layout.activity_rankings);
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
	/**	//Makes a dialog for fetching the rankings, if confirmed, it will call
	//A fn that will fetch the rankings and write to file. If it exists on file,
	//delete it and re-write it. Thus, saving is unnecessary
	//USE THE BELOW CODE IF RANKINGS AREN'T WRITTEN TO FILE. If they are, fetch. If not,
	//this, then above comments.
	case R.id.get_rankings:
		dialog.setContentView(R.layout.refresh);      	    	
		Button resetButton = (Button)dialog.findViewById(R.id.refresh_cancel);
		resetButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				dialog.dismiss();
	    	}	
		});
    	dialog.show();
	        return true;
	        
	        //BELOW DELETES SHARED PREFS, SO DO THAT IF THEY EXIST!
	         
	      SharedPreferences preferences = getSharedPreferences("Mypref", 0);
preferences.edit().remove("text").commit();*/
	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			//pops up to handle the value/salary dialog
			case R.id.value_sal:
				dialog.setContentView(R.layout.value_salary); 
				/**
				 * TO BE DONE HERE (in separate fn): SOMEHOW GET THE VALUE NUMBER AND SALARY SPENT AND ADJUST
				 * THE PROGRESS BAR BASED ON SALARY LEFT AND SETTEXT ON THOSE TEXTVIEWS TO 
				 * THE APPROPRIATE VALUES, varname.setProgress = fn to help with that. It has a val of 200 to start.
				 * remSalary = textView, draftValue = textView, SalaryBar = progressBar
				 * remSalary = Salary left: ____, draftValue = Value Thus Far: ____
				 */
				Button svDismiss = (Button)dialog.findViewById(R.id.salValDismiss);
				svDismiss.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) {
						dialog.dismiss();
			    	}	
				});
		    	dialog.show();		       
		    	return true;
			case R.id.search_player:
			try {
				searchCalled(dialog, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
     * The function that handles what happens when
     * the rankings are all fetched
     * @param holder
     */
    public static void rankingsFetched()
    {
    	System.out.println("In rankings.java again");
		for(PlayerObject e: holder.players)
		{
			System.out.println(e.info.name + ": " + e.values.worth);
		}
		//Below = how to get stuff from here. Important to know.
		//Button test = (Button)((Activity) retCont).findViewById(R.id.trending2);
    }
}
