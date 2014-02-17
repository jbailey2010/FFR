package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

/**
 * A static class to handle the team listing in importleague
 * @author Jeff
 *
 */
public class TeamList {
	private static Context cont;
	private static ImportedTeam newImport;
	public static boolean isF = false;
	public static boolean isFTE = false;
	public static boolean isOP = false;
	/**
	 * Handles the population of the team information listview
	 * both click, onclick, and initial output
	 * @param res
	 */
	public static void setTeamInfoList(View res, Context c, ImportedTeam n){
		cont = c;
		newImport = n;
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
			datum.put("sub", df.format(team.totalProj) + " Total Projection\n" + df.format(team.starterProj) + " Projection From Starters");
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
	}
	
	/**
	 * Gives the team specific information pop up
	 * @param v
	 * @param newImport 
	 */
	public static void teamSpecPopUp(View v, ImportedTeam newImport)
	{
		String team = ((TextView)((RelativeLayout)v).findViewById(R.id.text2)).getText().toString();
		String header = ((TextView)((RelativeLayout)v).findViewById(R.id.text1)).getText().toString();
		TeamAnalysis ta = new TeamAnalysis("", team, ImportLeague.holder, cont, newImport.roster);
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
		GraphView graphView = new LineGraphView(cont, "");
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
		GraphViewSeries es = new GraphViewSeries("Whole Roster Rank", seriesStyle, dataSet);
		graphView.addSeries(es);
		
		counter = 0;
		dataSet2[counter++] = new GraphViewData(counter, max - rankQBStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankRBStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankWRStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankTEStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankDStart(newImport, ta));
		dataSet2[counter++] = new GraphViewData(counter, max - rankKStart(newImport, ta));
		es = new GraphViewSeries("Starters Rank", seriesStyle2, dataSet2);
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
	 * Ranks the qb data
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public static int rankQBs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankQBStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	public static int rankRBs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankRBStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	public static int rankWRs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankWRStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	public static int rankTEs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankTEStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	public static int rankDs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankDStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	public static int rankKs(ImportedTeam leagueSet, TeamAnalysis team)
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
	
	public static int rankKStart(ImportedTeam leagueSet, TeamAnalysis team)
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
	 * Comes up with a pop up that shows a specific team's 
	 * perfect lineup
	 * @param newImport
	 * @param v
	 */
	public static void optimalLineup(ImportedTeam newImport, View v)
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
		List<String>remainingPlayers = new ArrayList<String>();
		remainingPlayers.addAll(Arrays.asList(rosters.get("QB")));
		remainingPlayers.addAll(Arrays.asList(rosters.get("RB")));
		remainingPlayers.addAll(Arrays.asList(rosters.get("WR")));
		remainingPlayers.addAll(Arrays.asList(rosters.get("TE")));
		remainingPlayers.addAll(Arrays.asList(rosters.get("D/ST")));
		remainingPlayers.addAll(Arrays.asList(rosters.get("K")));
		StringBuilder output = new StringBuilder(1000);
		TeamAnalysis dummy = new TeamAnalysis();
		isF = false;
		isFTE = false;
		isOP = false;
		String rb = (dummy.optimalLineup(remainingPlayers, rosters.get("RB"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "RB", cont, ImportLeague.holder, newImport.roster));
		String wr = (dummy.optimalLineup(remainingPlayers, rosters.get("WR"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "WR", cont, ImportLeague.holder, newImport.roster));
		String qb = (dummy.optimalLineup(remainingPlayers, rosters.get("QB"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "QB", cont, ImportLeague.holder, newImport.roster));
		String te = (dummy.optimalLineup(remainingPlayers, rosters.get("TE"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "TE", cont, ImportLeague.holder, newImport.roster));
		String d = (dummy.optimalLineup(remainingPlayers, rosters.get("D/ST"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "D/ST", cont, ImportLeague.holder, newImport.roster));
		String k = (dummy.optimalLineup(remainingPlayers, rosters.get("K"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "K", cont, ImportLeague.holder, newImport.roster));
		output.append(qb).append(rb).append(wr).append(te).append(d).append(k);
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
	 * Ranks each teams total PAA relative to the rest of the league
	 * @param leagueSet
	 * @param team
	 * @return
	 */
	public static int rankPAATot(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.totalProj > team.totalProj)
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
	public static int rankPAAStart(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			if(iter.starterProj > team.starterProj)
			{
				rank++;
			}
		}
		return rank;
	}
	
	public static int rankPAABench(ImportedTeam leagueSet, TeamAnalysis team)
	{
		int rank = 1;
		for(TeamAnalysis iter : leagueSet.teams)
		{
			
			if((iter.totalProj - iter.starterProj) > (team.totalProj - team.starterProj))
			{
				rank++;
			}
		}
		return rank;
	}
	

}
