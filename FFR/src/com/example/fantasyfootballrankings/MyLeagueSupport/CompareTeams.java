package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
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
 * A static library to handle the comparing of teams
 * 
 * @author Jeff
 * 
 */
public class CompareTeams {
	/**
	 * Handles the comaprison of teams, specifically the initial aspects of it
	 */
	public static void compareTeamInit() {
		List<String> teamNames = new ArrayList<String>();
		for (TeamAnalysis team : ImportLeague.newImport.teams) {
			teamNames.add(team.teamName);
		}
		final Dialog popUp = new Dialog(ImportLeague.cont,
				R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popUp.setContentView(R.layout.compare_teams_popup);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		Button close = (Button) popUp.findViewById(R.id.compare_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
		});
		popUp.show();
		final Spinner team1 = (Spinner) popUp.findViewById(R.id.team1_spinner);
		final Spinner team2 = (Spinner) popUp.findViewById(R.id.team2_spinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				ImportLeague.cont,
				android.R.layout.simple_spinner_dropdown_item, teamNames);
		team1.setAdapter(spinnerArrayAdapter);
		team2.setAdapter(spinnerArrayAdapter);
		team2.setSelection(1);
		Button submit = (Button) popUp.findViewById(R.id.compare_submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String team1Str = team1.getSelectedItem().toString();
				String team2Str = team2.getSelectedItem().toString();
				if (!team1Str.equals(team2Str)) {
					popUp.dismiss();
					compareTeamOutput(team1Str, team2Str);
				} else {
					Toast.makeText(ImportLeague.cont,
							"Please select different teams", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * Shows the graph of the team comparison
	 * 
	 * @param team1
	 * @param team2
	 */
	public static void compareTeamOutput(String team1, String team2) {
		TeamAnalysis t1 = null;
		TeamAnalysis t2 = null;
		for (TeamAnalysis team : ImportLeague.newImport.teams) {
			if (team.teamName.equals(team1)) {
				t1 = team;
			}
			if (team.teamName.equals(team2)) {
				t2 = team;
			}
		}
		final Dialog popUp = new Dialog(ImportLeague.cont,
				R.style.RoundCornersFull);
		popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popUp.setContentView(R.layout.team_info_popup);
		TextView head = (TextView) popUp
				.findViewById(R.id.team_info_popup_header);
		int r1 = TeamList.rankPAAStart(ImportLeague.newImport, t1);
		int r2 = TeamList.rankPAAStart(ImportLeague.newImport, t2);
		String winner = "";
		if (r1 > r2) {
			winner = t2.teamName;
		} else {
			winner = t1.teamName;
		}
		if (ImportLeague.holder.isRegularSeason) {
			head.setText("Projected Winner: " + winner);
		} else {
			head.setText("Better Team: " + winner);
		}
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(popUp.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		Button close = (Button) popUp.findViewById(R.id.team_info_close);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popUp.dismiss();
			}
		});
		popUp.getWindow().setAttributes(lp);
		popUp.show();

		GraphView graphView = GraphingUtils
				.generateGraphView(ImportLeague.cont);

		GraphViewDataInterface[] dataSet = new GraphViewDataInterface[7];
		int max = ImportLeague.newImport.teams.size() + 1;
		String[] horizLabels = { "Starters", "QB", "RB", "WR", "TE", "D", "K" };
		String[] vertLabels = new String[max - 1];
		for (int i = 1; i < max; i++) {
			vertLabels[i - 1] = String.valueOf(i);
		}
		int counter = 0;
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankPAAStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankQBStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankRBStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankWRStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankTEStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankDStart(ImportLeague.newImport, t1));
		dataSet[counter++] = new GraphViewData(counter, max
				- TeamList.rankKStart(ImportLeague.newImport, t1));

		GraphingUtils.addSeries(graphView, t1.teamName + " Starters",
				GraphingUtils.getGraphSeriesStyle(null, null), dataSet);

		GraphViewDataInterface[] dataSet2 = new GraphViewDataInterface[7];
		counter = 0;
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankPAAStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankQBStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankRBStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankWRStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankTEStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankDStart(ImportLeague.newImport, t2));
		dataSet2[counter++] = new GraphViewData(counter, max
				- TeamList.rankKStart(ImportLeague.newImport, t2));

		GraphingUtils.addSeries(graphView, t2.teamName + " Starters",
				GraphingUtils.getGraphSeriesStyle(Color.RED, null), dataSet2);

		GraphingUtils.configureLegend(graphView);
		GraphingUtils.configureAxes(graphView, horizLabels, vertLabels, 1,
				max - 1);

		LinearLayout layout = (LinearLayout) popUp
				.findViewById(R.id.plot_base_layout);
		layout.addView(graphView);
	}

}
