package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import android.app.Activity;
import android.app.Dialog;
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
 * A static library to handle the population of the league statistics list
 * @author Jeff
 *
 */
public class LeagueList {
	/**
	 * Populates the listview that handles the league information statistically
	 * @param res
	 */
	public static void setLeagueInfoList(View res)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		//Handles the statistic part of the layout
	    PriorityQueue<TeamAnalysis> totalPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
		{
			@Override
			public int compare(TeamAnalysis a, TeamAnalysis b) 
			{
				if (a.totalProj > b.totalProj)
			    {
			        return -1;
			    }
			    if (a.totalProj < b.totalProj)
			    {
			    	return 1;
			    } 
			    return 0;
			}
		});
	    PriorityQueue<TeamAnalysis> startPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				double paaA = a.getStarterProj();
	    				double paaB = b.getStarterProj();
	    				if (paaA > paaB)
	    			    {
	    			        return -1;
	    			    }
	    			    if (paaA < paaB)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> benchPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				double paaA = (a.totalProj - a.getStarterProj());
	    				double paaB = (b.totalProj - b.getStarterProj());
	    				if (paaA > paaB)
	    			    {
	    			        return -1;
	    			    }
	    			    if (paaA < paaB)
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> qbPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.qbStarters) > b.getPosProj(b.qbStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.qbStarters) < b.getPosProj(b.qbStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> rbPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.rbStarters) > b.getPosProj(b.rbStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.rbStarters) < b.getPosProj(b.rbStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> wrPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.wrStarters) > b.getPosProj(b.wrStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.wrStarters) < b.getPosProj(b.wrStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> tePAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.teStarters) > b.getPosProj(b.teStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.teStarters) < b.getPosProj(b.teStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> dPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.dStarters) > b.getPosProj(b.dStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.dStarters) < b.getPosProj(b.dStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    PriorityQueue<TeamAnalysis> kPAA = new PriorityQueue<TeamAnalysis>(300, new Comparator<TeamAnalysis>() 
	    		{
	    			@Override
	    			public int compare(TeamAnalysis a, TeamAnalysis b) 
	    			{
	    				if (a.getPosProj(a.kStarters) > b.getPosProj(b.kStarters))
	    			    {
	    			        return -1;
	    			    }
	    			    if (a.getPosProj(a.kStarters) < b.getPosProj(b.kStarters))
	    			    {
	    			    	return 1;
	    			    } 
	    			    return 0;
	    			}
	    		});
	    StringBuilder paaTotal = new StringBuilder(1000);
	    StringBuilder paaStart = new StringBuilder(1000);
	    StringBuilder paaBench = new StringBuilder(1000);
	    StringBuilder qb = new StringBuilder(1000);
	    StringBuilder rb = new StringBuilder(1000);
	    StringBuilder wr = new StringBuilder(1000);
	    StringBuilder te = new StringBuilder(1000);
	    StringBuilder d  = new StringBuilder(1000);
	    StringBuilder k  = new StringBuilder(1000);
	    for(TeamAnalysis team : ImportLeague.newImport.teams)
	    {
	    	totalPAA.add(team);
	    	startPAA.add(team);
	    	benchPAA.add(team);
	    	qbPAA.add(team);
	    	rbPAA.add(team);
	    	wrPAA.add(team);
	    	tePAA.add(team);
	    	dPAA.add(team);
	    	kPAA.add(team);
	    }
	    int counter = 0;
	    while(!totalPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = totalPAA.poll();
	    	paaTotal.append(counter + ") " + iter.teamName + ": " + df.format(iter.totalProj) + "\n");
	    }
	    counter = 0;
	    while(!startPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = startPAA.poll();
	    	paaStart.append(counter + ") " + iter.teamName + ": " + df.format(iter.getStarterProj()) + "\n");
	    }
	    counter = 0;
	    while(!benchPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = benchPAA.poll();
	    	paaBench.append(counter + ") " + iter.teamName + ": " + df.format(iter.totalProj - iter.getStarterProj()) + "\n");
	    }
	    counter = 0;
	    while(!qbPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = qbPAA.poll();
	    	qb.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.qbStarters)) + " (" + df.format(iter.qbPAATotal) + ")\n");
	    }
	    counter = 0;
	    while(!rbPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = rbPAA.poll();
	    	rb.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.rbStarters)) + " (" + df.format(iter.rbPAATotal) + ")\n");
	    }
	    counter = 0;
	    while(!wrPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = wrPAA.poll();
	    	wr.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.wrStarters)) + " (" + df.format(iter.wrPAATotal) + ")\n");
	    }
	    counter = 0;
	    while(!tePAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = tePAA.poll();
	    	te.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.teStarters)) + " (" + df.format(iter.tePAATotal) + ")\n");
	    }
	    counter = 0;
	    while(!dPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = dPAA.poll();
	    	d.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.dStarters)) + " (" + df.format(iter.dPAATotal) + ")\n");
	    }
	    counter = 0;
	    while(!kPAA.isEmpty())
	    {
	    	counter++;
	    	TeamAnalysis iter = kPAA.poll();
	    	k.append(counter + ") " + iter.teamName + ": " + df.format(iter.getPosPAA(iter.kStarters)) + " (" + df.format(iter.kPAATotal) + ")\n");
	    }
	    List<Map<String, String>>data2 = new ArrayList<Map<String, String>>();
		SimpleAdapter adapter2 = new SimpleAdapter(ImportLeague.cont, data2, 
	    		R.layout.imported_listview_elem_stats, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
		ListView list2 = (ListView)res.findViewById(R.id.imported_league_rankings);
		Map<String, String> datum = new HashMap<String, String>();
		datum.put("main", "Total Projection");
		datum.put("sub", paaTotal.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "Projection From Starters");
		datum.put("sub", paaStart.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum = new HashMap<String, String>();
		datum.put("main", "Projection From Backups");
		datum.put("sub", paaBench.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "QB PAA Rankings");
		datum.put("sub", qb.toString());
		data2.add(datum); 
		datum = new HashMap<String, String>();
		datum.put("main", "RB PAA Rankings");
		datum.put("sub", rb.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "WR PAA Rankings");
		datum.put("sub", wr.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "TE PAA Rankings");
		datum.put("sub", te.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "D/ST PAA Rankings");
		datum.put("sub", d.toString());
		data2.add(datum);
		datum = new HashMap<String, String>();
		datum.put("main", "K PAA Rankings");
		datum.put("sub", k.toString());
		data2.add(datum);
		adapter2.notifyDataSetChanged();
	    list2.setAdapter(adapter2);	    
	    handleListOnItemClick(list2, ImportLeague.newImport);
	}
	
	
	/**
	 * Handles the onclick of the team info list
	 * @param list
	 * @param ImportLeague.newImport
	 */
	public static void handleListOnItemClick(ListView list, final ImportedTeam newImport) {
		OnItemClickListener listener = new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showGraph(ImportLeague.newImport, arg1, false);
			}
			
		};
		OnItemLongClickListener longListener = new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showGraph(ImportLeague.newImport, arg1, true);
				return true;
			}
			
		};
		list.setOnItemClickListener(listener);
		list.setOnItemLongClickListener(longListener);
	}
	
	/**
	 * Handles the rendering of the graph
	 * @param newImport
	 * @param v
	 */
	public static void showGraph(ImportedTeam newImport, View v, boolean isLong)
	{
		
		RelativeLayout base = (RelativeLayout)v;
		TextView headerText = (TextView)base.findViewById(R.id.text1);
		String header = headerText.getText().toString();
		TextView content = (TextView)base.findViewById(R.id.text2);
		String text = content.getText().toString();
		String[] teamSet = text.split("\n");
		String[] teams = new String[newImport.teams.size()];
		String[] valSet = new String[newImport.teams.size()];
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[newImport.teams.size()];
		int counter = 0;
		double maxFirst = -10000000.0;
		double minFirst = 1000000000.0;
		GraphViewStyle gvs = new GraphViewStyle();
		gvs.setTextSize(13);
		GraphView graphView = new LineGraphView(ImportLeague.cont, "");
		graphView.setGraphViewStyle(gvs);
		List<String> teamList = new ArrayList<String>();
		String test = teamSet[0];
		boolean flag = false;
		if(!test.split(" ")[test.split(" ").length - 1].contains("("))
		{
			flag = true;
		}
		if(!isLong || flag)
		{
			for(String team : teamSet)
			{
				teamList.add(team);
			}
		}
		else
		{
			PriorityQueue<String> longOrder = new PriorityQueue<String>(300, new Comparator<String>() 
			{
				@Override
				public int compare(String a, String b)  
				{
					double aVal = Double.valueOf(a.split(" \\(")[2].split("\\)")[0]);
					double bVal = Double.valueOf(b.split(" \\(")[2].split("\\)")[0]);
					if(aVal > bVal)
					{
						return -1;
					}
					if(bVal > aVal)
					{
						return 1;
					}
					return 0;
				}
			});
			for(String team : teamSet)
			{
				longOrder.add(team);
			}
			while(!longOrder.isEmpty())
			{
				teamList.add(longOrder.poll());
			}
		}
		int teamCt = 0;
		for(String teamIter : teamList)
		{
			teamCt++;
			String val = "";
			if(!isLong || (isLong && !teamIter.split(" ")[teamIter.split(" ").length-1].contains("(")))
			{
				val = teamIter.split(": ")[1].split(" \\(")[0];
			}
			else
			{
				val = teamIter.split(" \\(")[2].split("\\)")[0];
			}
			teams[counter] = teamIter.split(": ")[0];
			double value = Double.valueOf(val);
			if(value > maxFirst)
			{
				maxFirst = value;
			}
			else if(value < minFirst)
			{
				minFirst = value;
			}
			dataSet[counter] = new GraphViewData(++counter, value);
			GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();  
			seriesStyle.thickness = 5;
			GraphViewSeries exampleSeries = new GraphViewSeries(teamCt + " " + teams[counter-1].split("\\) ")[1], seriesStyle, dataSet);  
			graphView.addSeries(exampleSeries);
		}
		final double max = maxFirst;
		final double min = minFirst;
		final Dialog popUp = new Dialog(ImportLeague.cont, R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.plot_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		popUp.getWindow().setAttributes(lp);
		popUp.show(); 
		TextView headerView = (TextView)popUp.findViewById(R.id.plot_popup_header);
		headerView.setText(header);
		Button close = (Button)popUp.findViewById(R.id.plot_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
		});
		graphView.setScrollable(true); 
		double space = max - min;
		DecimalFormat df = new DecimalFormat("#.#");
		String[] valSpaced = {df.format(max), df.format(min + (space*6.0)/7.0), df.format(min + (space*5.0)/7.0), 
				df.format(min + (space*4.0)/7.0), df.format(min + (space*3.0)/7.0),
				df.format(min + (space*2.0)/7.0), df.format(min + (space*1.0)/7.0),df.format(min)};
		graphView.setManualYAxisBounds(max, min);
		int maxLoop = newImport.teams.size() +1;
		for(int i = 1; i < maxLoop; i++)
		{
			valSet[i-1] = String.valueOf(i);
		}
		graphView.setHorizontalLabels(valSet);
		graphView.setShowLegend(true); 	
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(275); 
		graphView.setVerticalLabels(valSpaced);
		((LineGraphView)graphView).setDrawBackground(true);
		//((LineGraphView) graphView).setBackgroundColor(Color.rgb(131,155,243));
		LinearLayout layout = (LinearLayout) popUp.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);
		 
	}
}
