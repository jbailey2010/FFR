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

import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfoActivity;
import com.example.fantasyfootballrankings.ClassFiles.SortHandler;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.NDSpinner;
import com.example.fantasyfootballrankings.InterfaceAugmentations.NonListSwipeDetector;
import com.example.fantasyfootballrankings.MyLeagueSupport.CompareTeams;
import com.example.fantasyfootballrankings.MyLeagueSupport.LeagueList;
import com.example.fantasyfootballrankings.MyLeagueSupport.LineupHelp;
import com.example.fantasyfootballrankings.MyLeagueSupport.MyLeagueUtils;
import com.example.fantasyfootballrankings.MyLeagueSupport.PlayerList;
import com.example.fantasyfootballrankings.MyLeagueSupport.RosterTips;
import com.example.fantasyfootballrankings.MyLeagueSupport.TeamList;
import com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources.ESPNImport;
import com.example.fantasyfootballrankings.MyLeagueSupport.ImportSources.YahooImport;
import com.ffr.fantasyfootballrankings.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import android.widget.TwoLineListItem;

import com.jjoe64.graphview.GraphView.GraphViewData;
/**
 * Handles the importing of leagues
 * @author Jeff
 *
 */
public class ImportLeague extends Activity {
        public static Context cont;
        public static Storage holder = new Storage(null);
        public static LinearLayout ll;
        public Menu menuObj;
        public MenuItem compare;
        public MenuItem refresh;
        public MenuItem scoring;
        public MenuItem roster;
        public boolean isSeenLeague;
        public boolean isSeenLineup;
        public boolean isSeenPlayer;
        public boolean isSeenRoster;
        public boolean isSeenTeam;
        public static ImportedTeam newImport;
        public TextView v;
        public static SideNavigationView sideNavigationView;
        /**
         * Sets up the layout of the activity
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_import_league);
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
        		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view_import);
        	    sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        	    sideNavigationView.setMenuClickCallback(sideNavigationCallback);
        	   // sideNavigationView.setMode(/*SideNavigationView.Mode*/);
        	    getActionBar().setDisplayHomeAsUpEnabled(true);
                ActionBar ab = getActionBar();
                //ab.setDisplayShowHomeEnabled(false);
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
                ll.setOnTouchListener(new NonListSwipeDetector(this, "Import"));
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
		                case android.R.id.home:
		    		        sideNavigationView.toggleMenu();
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
                                CompareTeams.compareTeamInit();
                                return true;
                        case R.id.refresh_league:
                                handleLongClick();
                                return true;
                        case R.id.change_roster:
                        		changeRoster();
                        		return true;
                        case R.id.change_scoring:
                        		changeScoring();
                        		return true;
                        default:
                                return super.onOptionsItemSelected(item);
                }
        }
        
        /**
         * Handles the pop up to store the new roster
         */
        public void changeRoster()
        {
            MyLeagueUtils.getRoster(cont, holder, newImport.roster, newImport.leagueHost + newImport.leagueName, newImport);
        }
        
        /**
         * Handles the pop up to store the new scoring
         */
        public void changeScoring()
        {
        	MyLeagueUtils.passSettings(cont, holder, newImport.scoring, newImport.leagueHost + newImport.leagueName);
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
            final RadioButton yahoo =(RadioButton)popUp.findViewById(R.id.yahoo_import);
            Button submit = (Button)popUp.findViewById(R.id.import_decide_submit);
            submit.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                                if(espn.isChecked())
                                {
                                        getLeagueID(0);
                                        popUp.dismiss();
                                }
                                if(yahoo.isChecked())
                                {
                                        getLeagueID(1);
                                        popUp.dismiss();
                                }
                        }
            });
        }
        
        /**
         * Puts up a pop up to get the ESPN league ID
         * @param i 
         */
        public void getLeagueID(final int i)
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
                                        if(i == 0)
                                        {
                                                callESPNParsing(test);
                                        }
                                        if(i == 1)
                                        {
                                                callYahooParsing(test);
                                        }
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
                ESPNImport espnImporter = new ESPNImport(holder, this, false);
                try {
                        espnImporter.handleESPNParsing("http://games.espn.go.com/ffl/leaguerosters?leagueId=" + id, cont);
                } catch (IOException e) {
                        Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
                }
        }
        
        /**
         * Calls the yahoo parser
         * @param id
         */
        public void callYahooParsing(String id)
        {
                YahooImport yahooImporter = new YahooImport(holder, this, (Context)this, false);
                try {
                        yahooImporter.handleYahooParsing("http://football.fantasysports.yahoo.com/f1/" + id + "/starters");
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
                        scoring = (MenuItem)menuObj.findItem(R.id.change_scoring);
                        scoring.setVisible(false);
                        scoring.setEnabled(false);
                        roster = (MenuItem)menuObj.findItem(R.id.change_roster);
                        roster.setVisible(false);
                        roster.setEnabled(false);
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
                ll.setOnTouchListener(new NonListSwipeDetector(this, "Import"));
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
                isSeenLeague = false;
                isSeenLineup = false;
                isSeenPlayer = false;
                isSeenRoster = false;
                isSeenTeam = false;
                if(menuObj != null)
                {
                        compare = (MenuItem)menuObj.findItem(R.id.compare_team);
                        compare.setVisible(false);
                        compare.setEnabled(false);
                        refresh = (MenuItem)menuObj.findItem(R.id.refresh_league);
                        refresh.setVisible(false);
                        refresh.setEnabled(false);
                        scoring = (MenuItem)menuObj.findItem(R.id.change_scoring);
                        scoring.setVisible(false);
                        scoring.setEnabled(false);
                        roster = (MenuItem)menuObj.findItem(R.id.change_roster);
                        roster.setVisible(false);
                        roster.setEnabled(false);
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
                scoring = (MenuItem)menuObj.findItem(R.id.change_scoring);
                scoring.setVisible(true);
                scoring.setEnabled(true);
                roster = (MenuItem)menuObj.findItem(R.id.change_roster);
                roster.setVisible(true);
                roster.setEnabled(true);
                SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
                String leagueDataWhole = prefs.getString(key, "SHIT").split("LEAGUEURLSPLIT")[1];
                String[] perTeam = ManageInput.tokenize(leagueDataWhole, '@', 3);
                List<TeamAnalysis> teamList = new ArrayList<TeamAnalysis>();
                Roster r = ReadFromFile.readRoster(cont, key.split("@@@")[0] + key.split("@@@")[1]);
                for(String teamSet : perTeam)
                {
                        String[] teamArr = ManageInput.tokenize(teamSet, '~', 2);
                        TeamAnalysis teamData = new TeamAnalysis(teamArr[0], teamArr[1], holder, cont, r);
                        teamList.add(teamData);
                }
                String[] keySet = key.split("@@@");
                newImport = new ImportedTeam(teamList, keySet[1], keySet[0]);
                newImport.roster = ReadFromFile.readRoster(cont, newImport.leagueHost + newImport.leagueName);
                newImport.scoring = ReadFromFile.readScoring(cont, newImport.leagueHost + newImport.leagueName);
                final View res = ((Activity)cont).getLayoutInflater().inflate(R.layout.league_stats_output, ll, false);
                final RelativeLayout league = (RelativeLayout)res.findViewById(R.id.category_league_base);
                final RelativeLayout teams  = (RelativeLayout)res.findViewById(R.id.category_team_base);
                final RelativeLayout players= (RelativeLayout)res.findViewById(R.id.category_player_base);
                final LinearLayout lineup =   (LinearLayout)  res.findViewById(R.id.category_lineup_base);
                final RelativeLayout tips =   (RelativeLayout)res.findViewById(R.id.category_tips_base);
                league.setVisibility(View.VISIBLE);
                teams.setVisibility(View.GONE);
                players.setVisibility(View.GONE);
                lineup.setVisibility(View.GONE);
                tips.setVisibility(View.GONE);
                ll.removeAllViews();
        		LeagueList.setLeagueInfoList(res);
        		isSeenLeague = true;
                final Button leagueButton = (Button)res.findViewById(R.id.category_league_stats);
                final Button teamsButton  = (Button)res.findViewById(R.id.category_team_stats);
                final Button playersButton= (Button)res.findViewById(R.id.category_player_list);
                final Button lineupButton = (Button)res.findViewById(R.id.category_lineup_help);
                final Button tipsButton = (Button)res.findViewById(R.id.imported_league_tips);
                tipsButton.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                        		if(!isSeenRoster)
                        		{
                        			RosterTips.init(newImport, res);
                        			isSeenRoster = true;
                        		}
                                league.setVisibility(View.GONE);
                                teams.setVisibility(View.GONE);
                                players.setVisibility(View.GONE);
                                lineup.setVisibility(View.GONE);
                                leagueButton.setTextSize(13);
                                teamsButton.setTextSize(13);
                                playersButton.setTextSize(13);
                                lineupButton.setTextSize(13);
                                leagueButton.setTypeface(null,Typeface.NORMAL);
                                teamsButton.setTypeface(null, Typeface.NORMAL);
                                playersButton.setTypeface(null, Typeface.NORMAL);
                                lineupButton.setTypeface(null, Typeface.NORMAL);
                                tips.setVisibility(View.VISIBLE);
                                tipsButton.setTextSize(14);
                                tipsButton.setTypeface(null, Typeface.BOLD);
                        }
                });
                leagueButton.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                        		if(!isSeenLeague)
                        		{
                        			LeagueList.setLeagueInfoList(res);
                        			isSeenLeague = true;
                        		}
                                league.setVisibility(View.VISIBLE);
                                teams.setVisibility(View.GONE);
                                players.setVisibility(View.GONE);
                                lineup.setVisibility(View.GONE);
                                leagueButton.setTextSize(14);
                                teamsButton.setTextSize(13);
                                playersButton.setTextSize(13);
                                lineupButton.setTextSize(13);
                                leagueButton.setTypeface(null,Typeface.BOLD);
                                teamsButton.setTypeface(null, Typeface.NORMAL);
                                playersButton.setTypeface(null, Typeface.NORMAL);
                                lineupButton.setTypeface(null, Typeface.NORMAL);
                                tips.setVisibility(View.GONE);
                                tipsButton.setTextSize(13);
                                tipsButton.setTypeface(null, Typeface.NORMAL);
                        }
                });
                teamsButton.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                        		if(!isSeenTeam)
                        		{
                        			TeamList.setTeamInfoList(res, cont, newImport);
                        			isSeenTeam = true;
                        		}
                                teams.setVisibility(View.VISIBLE);
                                league.setVisibility(View.GONE);
                                players.setVisibility(View.GONE);
                                lineup.setVisibility(View.GONE);
                                teamsButton.setTextSize(14);
                                leagueButton.setTextSize(13);
                                playersButton.setTextSize(13);
                                lineupButton.setTextSize(13);
                                teamsButton.setTypeface(null,Typeface.BOLD);
                                leagueButton.setTypeface(null, Typeface.NORMAL);
                                playersButton.setTypeface(null, Typeface.NORMAL);
                                lineupButton.setTypeface(null, Typeface.NORMAL);
                                tips.setVisibility(View.GONE);
                                tipsButton.setTextSize(13);
                                tipsButton.setTypeface(null, Typeface.NORMAL);
                        }
                });
                playersButton.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                        		if(!isSeenPlayer)
                        		{
                        			PlayerList.setPlayerInfoList(res, cont, newImport);
                        			isSeenPlayer = true;
                        		}
                                league.setVisibility(View.GONE);
                                teams.setVisibility(View.GONE);
                                players.setVisibility(View.VISIBLE);
                                lineup.setVisibility(View.GONE);
                                leagueButton.setTextSize(13);
                                teamsButton.setTextSize(13);
                                playersButton.setTextSize(14);
                                lineupButton.setTextSize(13);
                                leagueButton.setTypeface(null,Typeface.NORMAL);
                                teamsButton.setTypeface(null, Typeface.NORMAL);
                                playersButton.setTypeface(null, Typeface.BOLD);
                                lineupButton.setTypeface(null, Typeface.NORMAL);
                                tips.setVisibility(View.GONE);
                                tipsButton.setTextSize(13);
                                tipsButton.setTypeface(null, Typeface.NORMAL);
                        }
                });
                lineupButton.setOnClickListener(new OnClickListener(){

                        @Override
                        public void onClick(View arg0) {
                        		if(!isSeenLineup)
                        		{
                        			LineupHelp.setLineupInfo(res);
                        			isSeenLineup = !isSeenLineup;
                        		}
                                league.setVisibility(View.GONE);
                                teams.setVisibility(View.GONE);
                                players.setVisibility(View.GONE);
                                lineup.setVisibility(View.VISIBLE);
                                leagueButton.setTextSize(13);
                                teamsButton.setTextSize(13);
                                playersButton.setTextSize(13);
                                lineupButton.setTextSize(14);
                                leagueButton.setTypeface(null,Typeface.NORMAL);
                                teamsButton.setTypeface(null, Typeface.NORMAL);
                                playersButton.setTypeface(null, Typeface.NORMAL);
                                lineupButton.setTypeface(null, Typeface.BOLD);
                                tips.setVisibility(View.GONE);
                                tipsButton.setTextSize(13);
                                tipsButton.setTypeface(null, Typeface.NORMAL);
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
            v = name;
            TextView host = (TextView)res.findViewById(R.id.hostName);
            host.setText("Hosted on " + newImport.leagueHost);
            //Help work
            final TextView helpTeams = (TextView)res.findViewById(R.id.team_help_import);
            final TextView helpRanks = (TextView)res.findViewById(R.id.rankings_help_import);
            final TextView helpPlayers = (TextView)res.findViewById(R.id.player_list_help);
            final TextView helpLineup = (TextView)res.findViewById(R.id.lineup_help);
            TextView help = (TextView)res.findViewById(R.id.help_button_league_stats);
            help.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View arg0) {
                                if(!helpTeams.isShown() && !helpPlayers.isShown() && !helpRanks.isShown() && !helpLineup.isShown())
                                { 
                                        helpPlayers.setVisibility(View.VISIBLE);
                                        helpTeams.setVisibility(View.VISIBLE);
                                        helpRanks.setVisibility(View.VISIBLE);
                                        helpLineup.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                        helpPlayers.setVisibility(View.GONE);
                                        helpTeams.setVisibility(View.GONE);
                                        helpRanks.setVisibility(View.GONE);
                                        helpLineup.setVisibility(View.GONE);
                                }
                        }
            });
            ll.addView(res);
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
                                clearDataESPNInit((TextView)v, newImport, cont, true);
                        }
                        if(((TextView)((Activity)cont).findViewById(R.id.hostName)).getText().toString().contains("Yahoo"))
                        {
                                clearDataYahooInit((TextView)v, newImport, cont, true);
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
         * @param b 
         */
        public void clearDataESPNInit(TextView name, ImportedTeam newImport, Context cont, boolean b)
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
                ESPNImport espnImporter = new ESPNImport(holder, this, b);
                try {
                        espnImporter.handleESPNParsing(leagueURL, cont);
                } catch (IOException e) {
                        Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
                }
        }

        /**
         * Clears the non password yahoo stuff then calls the refreshing
         * @param name
         * @param newImport
         * @param cont
         * @param b 
         */
        public void clearDataYahooInit(TextView name, ImportedTeam newImport, Context cont, boolean b)
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
                YahooImport yahooImporter = new YahooImport(holder, this, (Context)this, b);
                try {
                        yahooImporter.handleYahooParsing(leagueURL);
                } catch (IOException e) {
                        Toast.makeText(cont, "There was an error, do you have a valid internet connection?", Toast.LENGTH_SHORT).show();
                }
        }
}