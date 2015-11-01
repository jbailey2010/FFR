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
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;
import com.example.fantasyfootballrankings.ClassFiles.Utils.GraphingUtils;
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
 * 
 * @author Jeff
 * 
 */
public class LeagueList {
	/**
	 * Populates the listview that handles the league information statistically
	 * 
	 * @param res
	 */
	public static void setLeagueInfoList(View res) {
		DecimalFormat df = new DecimalFormat(Constants.NUMBER_FORMAT);
		// Handles the statistic part of the layout
		PriorityQueue<TeamAnalysis> totalPAA = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.totalProj > b.totalProj) {
							return -1;
						}
						if (a.totalProj < b.totalProj) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> startPAA = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						double paaA = a.getStarterProj();
						double paaB = b.getStarterProj();
						if (paaA > paaB) {
							return -1;
						}
						if (paaA < paaB) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> benchPAA = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						double paaA = (a.totalProj - a.getStarterProj());
						double paaB = (b.totalProj - b.getStarterProj());
						if (paaA > paaB) {
							return -1;
						}
						if (paaA < paaB) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> qbProj = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.qbStarters) > b
								.getProjSum(b.qbStarters)) {
							return -1;
						}
						if (a.getProjSum(a.qbStarters) < b
								.getProjSum(b.qbStarters)) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> rbProj = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.rbStarters) > b
								.getProjSum(b.rbStarters)) {
							return -1;
						}
						if (a.getProjSum(a.rbStarters) < b
								.getProjSum(b.rbStarters)) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> wrProj = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.wrStarters) > b
								.getProjSum(b.wrStarters)) {
							return -1;
						}
						if (a.getProjSum(a.wrStarters) < b
								.getProjSum(b.wrStarters)) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> teProj = new PriorityQueue<TeamAnalysis>(
				300, new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.teStarters) > b
								.getProjSum(b.teStarters)) {
							return -1;
						}
						if (a.getProjSum(a.teStarters) < b
								.getProjSum(b.teStarters)) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> dProj = new PriorityQueue<TeamAnalysis>(300,
				new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.dStarters) > b
								.getProjSum(b.dStarters)) {
							return -1;
						}
						if (a.getProjSum(a.dStarters) < b
								.getProjSum(b.dStarters)) {
							return 1;
						}
						return 0;
					}
				});
		PriorityQueue<TeamAnalysis> kProj = new PriorityQueue<TeamAnalysis>(300,
				new Comparator<TeamAnalysis>() {
					@Override
					public int compare(TeamAnalysis a, TeamAnalysis b) {
						if (a.getProjSum(a.kStarters) > b
								.getProjSum(b.kStarters)) {
							return -1;
						}
						if (a.getProjSum(a.kStarters) < b
								.getProjSum(b.kStarters)) {
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
		StringBuilder d = new StringBuilder(1000);
		StringBuilder k = new StringBuilder(1000);
		for (TeamAnalysis team : ImportLeague.newImport.teams) {
			totalPAA.add(team);
			startPAA.add(team);
			benchPAA.add(team);
			qbProj.add(team);
			rbProj.add(team);
			wrProj.add(team);
			teProj.add(team);
			dProj.add(team);
			kProj.add(team);
		}
		int counter = 0;
		while (!totalPAA.isEmpty()) {
			counter++;
			TeamAnalysis iter = totalPAA.poll();
			paaTotal.append(counter + ") " + iter.teamName + ": "
					+ df.format(iter.totalProj) + Constants.LINE_BREAK);
		}
		counter = 0;
		while (!startPAA.isEmpty()) {
			counter++;
			TeamAnalysis iter = startPAA.poll();
			paaStart.append(counter + ") " + iter.teamName + ": "
					+ df.format(iter.getStarterProj()) + Constants.LINE_BREAK);
		}
		counter = 0;
		while (!benchPAA.isEmpty()) {
			counter++;
			TeamAnalysis iter = benchPAA.poll();
			paaBench.append(counter + ") " + iter.teamName + ": "
					+ df.format(iter.totalProj - iter.getStarterProj()) + Constants.LINE_BREAK);
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.QB)) {
			while (!qbProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = qbProj.poll();
				qb.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.qbStarters)) + " ("
						+ df.format(iter.qbProjTotal) + ")\n");
			}
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.RB)) {
			while (!rbProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = rbProj.poll();
				rb.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.rbStarters)) + " ("
						+ df.format(iter.rbProjTotal) + ")\n");
			}
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.WR)) {
			while (!wrProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = wrProj.poll();
				wr.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.wrStarters)) + " ("
						+ df.format(iter.wrProjTotal) + ")\n");
			}
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.TE)) {
			while (!teProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = teProj.poll();
				te.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.teStarters)) + " ("
						+ df.format(iter.teProjTotal) + ")\n");
			}
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.DST)) {
			while (!dProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = dProj.poll();
				d.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.dStarters)) + " ("
						+ df.format(iter.dProjTotal) + ")\n");
			}
		}
		counter = 0;
		if (ImportLeague.newImport.doesLeagueAllowPosition(Constants.K)) {
			while (!kProj.isEmpty()) {
				counter++;
				TeamAnalysis iter = kProj.poll();
				k.append(counter + ") " + iter.teamName + ": "
						+ df.format(iter.getProjSum(iter.kStarters)) + " ("
						+ df.format(iter.kProjTotal) + ")\n");
			}
		}
		List<Map<String, String>> data2 = new ArrayList<Map<String, String>>();
		SimpleAdapter adapter2 = new SimpleAdapter(ImportLeague.cont, data2,
				R.layout.imported_listview_elem_stats, new String[] { "main",
						"sub" }, new int[] { R.id.text1, R.id.text2 });
		ListView list2 = (ListView) res
				.findViewById(R.id.imported_league_rankings);
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
		if (qb.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "QB Projections");
			datum.put("sub", qb.toString());
			data2.add(datum);
		}
		if (rb.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "RB Projections");
			datum.put("sub", rb.toString());
			data2.add(datum);
		}
		if (wr.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "WR Projections");
			datum.put("sub", wr.toString());
			data2.add(datum);
		}
		if (te.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "TE Projections");
			datum.put("sub", te.toString());
			data2.add(datum);
		}
		if (d.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "D/ST Projections");
			datum.put("sub", d.toString());
			data2.add(datum);
		}
		if (k.toString().length() > 5) {
			datum = new HashMap<String, String>();
			datum.put("main", "K Projections");
			datum.put("sub", k.toString());
			data2.add(datum);
		}
		adapter2.notifyDataSetChanged();
		list2.setAdapter(adapter2);
		handleListOnItemClick(list2, ImportLeague.newImport);
	}

	/**
	 * Handles the onclick of the team info list
	 * 
	 * @param list
	 * @param ImportLeague
	 *            .newImport
	 */
	public static void handleListOnItemClick(ListView list,
			final ImportedTeam newImport) {
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showGraph(ImportLeague.newImport, arg1, false);
			}

		};
		OnItemLongClickListener longListener = new OnItemLongClickListener() {

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
	 * 
	 * @param newImport
	 * @param v
	 */
	public static void showGraph(ImportedTeam newImport, View v, boolean isLong) {

		RelativeLayout base = (RelativeLayout) v;
		TextView headerText = (TextView) base.findViewById(R.id.text1);
		String header = headerText.getText().toString();
		TextView content = (TextView) base.findViewById(R.id.text2);
		String text = content.getText().toString();
		String[] teamSet = text.split(Constants.LINE_BREAK);
		String[] teams = new String[newImport.teams.size()];
		String[] valSet = new String[newImport.teams.size()];
		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[newImport.teams
				.size()];
		int counter = 0;
		double maxFirst = -10000000.0;
		double minFirst = 1000000000.0;

		GraphView graphView = GraphingUtils
				.generateGraphView(ImportLeague.cont);

		List<String> teamList = new ArrayList<String>();
		String test = teamSet[0];
		boolean flag = false;
		if (!test.split(" ")[test.split(" ").length - 1].contains("(")) {
			flag = true;
		}
		if (!isLong || flag) {
			for (String team : teamSet) {
				teamList.add(team);
			}
		} else {
			PriorityQueue<String> longOrder = new PriorityQueue<String>(300,
					new Comparator<String>() {
						@Override
						public int compare(String a, String b) {
							double aVal = Double.valueOf(a.split(" \\(")[2]
									.split("\\)")[0]);
							double bVal = Double.valueOf(b.split(" \\(")[2]
									.split("\\)")[0]);
							if (aVal > bVal) {
								return -1;
							}
							if (bVal > aVal) {
								return 1;
							}
							return 0;
						}
					});
			for (String team : teamSet) {
				longOrder.add(team);
			}
			while (!longOrder.isEmpty()) {
				teamList.add(longOrder.poll());
			}
		}
		int teamCt = 0;
		for (String teamIter : teamList) {
			teamCt++;
			String val = "";
			if (!isLong
					|| (isLong && !teamIter.split(" ")[teamIter.split(" ").length - 1]
							.contains("("))) {
				val = teamIter.split(": ")[1].split(" \\(")[0];
			} else {
				val = teamIter.split(" \\(")[2].split("\\)")[0];
			}
			teams[counter] = teamIter.split(": ")[0];
			double value = Double.valueOf(val);
			if (value > maxFirst) {
				maxFirst = value;
			} else if (value < minFirst) {
				minFirst = value;
			}
			dataSet[counter] = new GraphViewData(++counter, value);

			GraphingUtils
					.addSeries(graphView,
							teamCt + " " + teams[counter - 1].split("\\) ")[1],
					GraphingUtils.getGraphSeriesStyle(null, 5), dataSet);
		}
		final double max = maxFirst;
		final double min = minFirst;
		final Dialog popUp = new Dialog(ImportLeague.cont,
				R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popUp.setContentView(R.layout.plot_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		popUp.getWindow().setAttributes(lp);
		popUp.show();
		TextView headerView = (TextView) popUp
				.findViewById(R.id.plot_popup_header);
		headerView.setText(header);
		Button close = (Button) popUp.findViewById(R.id.plot_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
		});

		double space = max - min;
		DecimalFormat df = new DecimalFormat("#.#");
		String[] valSpaced = { df.format(max),
				df.format(min + (space * 6.0) / 7.0),
				df.format(min + (space * 5.0) / 7.0),
				df.format(min + (space * 4.0) / 7.0),
				df.format(min + (space * 3.0) / 7.0),
				df.format(min + (space * 2.0) / 7.0),
				df.format(min + (space * 1.0) / 7.0), df.format(min) };

		int maxLoop = newImport.teams.size() + 1;
		for (int i = 1; i < maxLoop; i++) {
			valSet[i - 1] = String.valueOf(i);
		}

		GraphingUtils.configureLegend(graphView);
		GraphingUtils.configureAxes(graphView, valSet, valSpaced, min, max);

		((LineGraphView) graphView).setDrawBackground(true);
		LinearLayout layout = (LinearLayout) popUp
				.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);

	}
}
