package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFantasySharks;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMFL;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseRazzball;
import com.ffr.fantasyfootballrankings.R;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseDraftWizardRanks;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNFL;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleExport;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseNames;
import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.WriteNewPAA;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * The home class, sets up the three main buttons to go to trending players,
 * team view, and/or rankings themselves
 * 
 * @author Jeff
 * 
 */
public class Home extends Activity {
	// Some global variables, context and a few buttons
	public static Storage holder = new Storage(null);
	Context cont = this;
	Dialog dialog;
	Button rankings;
	Button importLeague;
	long start;
	Menu m;
	public static SideNavigationView sideNavigationView;
	// UPDATE THIS FOR YEAR CHANGES
	public static String yearKey = "2016";

	// Uniquely identifies player elems

	/**
	 * Makes the buttons and sets the listeners for them
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		cont = this;
		ISideNavigationCallback sideNavigationCallback = new ISideNavigationCallback() {
			@Override
			public void onSideNavigationItemClick(int itemId) {
				switch (itemId) {
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
				case R.id.help:
					ManageInput.generalHelp(cont);
					break;
				default:
					return;
				}
			}
		};
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(sideNavigationCallback);
		// sideNavigationView.setMode(/*SideNavigationView.Mode*/);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		rankings = (Button) findViewById(R.id.rankings);
		rankings.setOnClickListener(rankHandler);
		importLeague = (Button) findViewById(R.id.import_league_btn);
		importLeague.setOnClickListener(importHandler);
		start = System.nanoTime();
		handleInitialRefresh();
		if (ReadFromFile.readFirstOpen(cont)) {
			WriteToFile.writeFirstIsRegularSeason(cont);
			// ManageInput.generalHelp(cont);
			helpPopUp(true);

		}
		// Refresh the data on a new isregular season for the year so it doesn't
		// crash on load
		else if (ReadFromFile.firstIsRegularSeason(cont)) {
			holder.players = new ArrayList<PlayerObject>();
			SharedPreferences prefs = getSharedPreferences(Constants.SP_KEY, 0);
			prefs.edit().remove(Constants.PLAYER_RANKINGS_KEY).apply();
			Intent intent = new Intent(this, Rankings.class);
			startActivity(intent);
		}

        /*
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
            holder.isRegularSeason = true;
            ParseRankings.handleHashes();
			ParseRazzball.getRazzballRankings(holder, ReadFromFile.readRoster(this), ReadFromFile.readScoring(this));
		} catch (IOException e) {
			e.printStackTrace(System.out);
        }
        */

	}

	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		m = menu;
		return true;
	}

	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		boolean isStored = false;
		if (holder.players.size() > 0) {
			isStored = true;
		}
		switch (item.getItemId()) {
		case R.id.export_names:
			if (isStored) {
				if (!holder.isRegularSeason) {
					callExport();
				} else {
					Toast.makeText(cont, "This is a preseason only feature",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(cont,
						"Can't export rankings until they are fetched",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.refresh_names:
			nameRefresh(dialog);
			return true;
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			return true;
		case R.id.start_scoring:
			ManageInput.passSettings(cont, new Scoring(), isStored, holder);
			return true;
		case R.id.start_roster:
			ManageInput.getRoster(cont, isStored, holder);
			return true;
		case R.id.auction_or_snake:
			ManageInput.isAuctionOrSnake(cont, holder);
			return true;
		case R.id.help_home:
			helpPopUp(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Handles the help dialog popup
	 * 
	 * @param b
	 */
	public void helpPopUp(final boolean b) {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.help_home);
		dialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		Button close = (Button) dialog.findViewById(R.id.help_home_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (b) {
					if (ManageInput.confirmInternet(cont)) {
						WriteToFile.writeFirstOpen(cont);
						final ParsingAsyncTask stupid = new ParsingAsyncTask();
						ParseNames task = stupid.new ParseNames(
								(Activity) cont, true);
						task.execute(cont);
					} else {
						Toast.makeText(
								cont,
								"No Internet Connection Available. The Names List Must Be Fetched, So Please Connect and Refresh it Manually to Avoid Problems With The Rankings",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	/**
	 * Calls the handle export fn
	 */
	public void callExport() {
		HandleExport.driveInit(HandleExport.orderPlayers(holder, cont), dialog,
				cont, holder);
	}

	/**
	 * Handles the initial fetching of player names and permanent data if they
	 * haven't been fetched (initial opening of the app)
	 */
	public void handleInitialRefresh() {
		SharedPreferences prefs = cont.getSharedPreferences(Constants.SP_KEY, 0);
		Set<String> checkExists = prefs.getStringSet(Constants.PLAYER_RANKINGS_KEY, null);
		if (checkExists != null
				&& (prefs.getBoolean("Rankings Update Home", false) || holder.players
						.size() < 5)) {
			ReadFromFile.fetchPlayers(checkExists, holder, cont, 1);
			SharedPreferences.Editor editor = cont.getSharedPreferences(Constants.SP_KEY,
					0).edit();
			editor.putBoolean("Rankings Update Home", false).apply();
		}
	}

	/**
	 * Comes back here after re-loading the data to see if the menu needs
	 * altering
	 */
	public void seeIfInvalid() {
		if (m != null && holder.isRegularSeason) {
			MenuItem a = (MenuItem) m.findItem(R.id.auction_or_snake);
			a.setVisible(false);
			a.setEnabled(false);
		}
	}

	/**
	 * Handles the name refreshing given the prompt
	 * 
	 * @param dialog
	 */
	public void nameRefresh(final Dialog dialog) {
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.refresh_list);
		dialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.getWindow().setAttributes(lp);
		Button cancel = (Button) dialog.findViewById(R.id.cancel_list_refresh);
		Button submit = (Button) dialog.findViewById(R.id.confirm_list_refresh);
		final ParsingAsyncTask stupid = new ParsingAsyncTask();
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (ManageInput.confirmInternet(cont)) {
					dialog.dismiss();
					ParseNames task = stupid.new ParseNames((Activity) cont,
							false);
					task.execute(cont);
				} else {
					Toast.makeText(cont, "No Internet Connection",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * Sends the rank button to the ranking page
	 */
	View.OnClickListener rankHandler = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(cont, Rankings.class);
			cont.startActivity(intent);
		}
	};

	/**
	 * Sends the user to the import league page
	 */
	View.OnClickListener importHandler = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent5 = new Intent(cont, ImportLeague.class);
			cont.startActivity(intent5);
		}
	};
}
