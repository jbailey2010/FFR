package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.LeagueImports.ESPNImport;

import jeff.isawesome.fantasyfootballrankings.R;
import jeff.isawesome.fantasyfootballrankings.R.layout;
import jeff.isawesome.fantasyfootballrankings.R.menu;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
	SharedPreferences prefs;

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
		prefs = cont.getSharedPreferences("FFR", 0); 
		if(holder.players.size() < 10 || prefs.getBoolean("Home Update Import", false))
		{
			SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
			editor.putBoolean("Home Update Import", false).commit();
	    	String checkExists2 = prefs.getString("Player Values", "Not Set");
	    	if(checkExists2 != "Not Set")
	    	{
				ReadFromFile.fetchPlayers(checkExists2, holder,cont, 5);
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
				handleImportInit();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handles the displaying of the layouts
	 */
	public void handleLayoutInit()
	{
		if(prefs.getInt("Number of Leagues Imported", 0) == 0)
		{
			setNoContentLayout();
		}
		else
		{
			//TODO: LOAD THE CONTENT
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
		ESPNImport espnImporter = new ESPNImport();
		try {
			espnImporter.handleESPNParsing("http://games.espn.go.com/ffl/leaguerosters?leagueId=" + id, cont);
		} catch (IOException e) {
			Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
		}
	}
}
