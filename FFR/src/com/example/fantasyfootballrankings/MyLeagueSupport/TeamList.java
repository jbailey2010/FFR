package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FileIO.ReadFromFile;
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
			datum.put("sub", df.format(team.totalProj) + " Total Projection\n" + df.format(team.getStarterProj()) + " Projection From Starters");
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
			if(iter.qbPAATotal > team.qbPAATotal)
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
			if(iter.getPosProj(iter.qbStarters) > team.getPosProj(iter.qbStarters))
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
			if(iter.rbPAATotal > team.rbPAATotal)
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
			if(iter.getPosProj(iter.rbStarters) > team.getPosProj(iter.rbStarters))
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
			if(iter.wrPAATotal > team.wrPAATotal)
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
			if(iter.getPosProj(iter.wrStarters) > team.getPosProj(iter.wrStarters))
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
			if(iter.tePAATotal > team.tePAATotal)
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
			if(iter.getPosProj(iter.teStarters) > team.getPosProj(iter.teStarters))
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
			if(iter.dPAATotal > team.dPAATotal)
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
			if(iter.getPosProj(iter.dStarters) > team.getPosProj(iter.dStarters))
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
			if(iter.kPAATotal > team.kPAATotal)
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
			if(iter.getPosProj(iter.kStarters) > team.getPosProj(iter.kStarters))
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
		return team.getStarterProj();
	}
	
	/**
	 * Gets the total paa of all players
	 * @param team
	 * @return
	 */
	public double paaTotal(TeamAnalysis team)
	{
		return team.qbPAATotal + team.rbPAATotal + team.wrPAATotal + team.tePAATotal + team.dPAATotal + team.kPAATotal;
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
		TeamAnalysis dummy = new TeamAnalysis("", text, ImportLeague.holder, cont, ReadFromFile.readRoster(cont));
		StringBuilder output = new StringBuilder(1000);
		output.append(dummy.stringifyLineup());
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
			if(iter.getStarterProj() > team.getStarterProj())
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
			
			if((iter.totalProj - iter.getStarterProj()) > (team.totalProj - team.getStarterProj()))
			{
				rank++;
			}
		}
		return rank;
	}
	

}
