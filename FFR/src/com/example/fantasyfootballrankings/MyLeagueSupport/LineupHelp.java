package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseFP;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

/**
 * A static library to handle the lineup help part of importleague
 * 
 * @author Jeff
 * 
 */
public class LineupHelp {
	public static LinearLayout t;

	/**
	 * Handles the initial set up of the lineup decider front end
	 * 
	 * @param res
	 */
	public static void setLineupInfo(View res) {
		final AutoCompleteTextView p1 = (AutoCompleteTextView) res
				.findViewById(R.id.player1_input);
		final AutoCompleteTextView p2 = (AutoCompleteTextView) res
				.findViewById(R.id.player2_input);
		final LinearLayout table = (LinearLayout) res
				.findViewById(R.id.table_base);
		Button submit = (Button) res.findViewById(R.id.compare_submit);
		Button clear = (Button) res.findViewById(R.id.compare_clear);
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (PlayerObject player : ImportLeague.holder.players) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", player.info.name);
			if (!player.info.name.contains("D/ST")
					&& player.info.position.length() >= 1
					&& player.info.team.length() > 2) {
				datum.put("sub", player.info.team);
			} else {
				datum.put("sub", "");
			}
			data.add(datum);
		}
		final List<Map<String, String>> dataSorted = ManageInput.sortData(data);
		final SimpleAdapter mAdapter = new SimpleAdapter(ImportLeague.cont,
				dataSorted, android.R.layout.simple_list_item_2, new String[] {
						"main", "sub" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		p1.setAdapter(mAdapter);
		p2.setAdapter(mAdapter);
		p1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = (((TwoLineListItem) arg1)).getText1().getText()
						.toString();
				String team = (((TwoLineListItem) arg1)).getText2().getText()
						.toString();
				p1.setText(name + " - " + team);
			}
		});
		p2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = (((TwoLineListItem) arg1)).getText1().getText()
						.toString();
				String team = (((TwoLineListItem) arg1)).getText2().getText()
						.toString();
				p2.setText(name + " - " + team);
			}
		});
		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p1.setText("");
				p2.setText("");
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String p1Input = p1.getText().toString();
				String[] p1Data = p1Input.split(" - ");
				String p2Input = p2.getText().toString();
				String[] p2Data = p2Input.split(" - ");
				boolean isFound1 = false;
				PlayerObject pl1 = null;
				boolean isFound2 = false;
				PlayerObject pl2 = null;
				for (PlayerObject player : ImportLeague.holder.players) {
					if (player.info.name.equals(p1Data[0])
							&& player.info.position.equals("D/ST")) {
						isFound1 = true;
						pl1 = player;
					} else if (player.info.name.equals(p1Data[0])
							&& player.info.team.equals(p1Data[1])) {
						isFound1 = true;
						pl1 = player;
					}
					if (player.info.name.equals(p2Data[0])
							&& player.info.position.equals("D/ST")) {
						isFound2 = true;
						pl2 = player;
					} else if (player.info.name.equals(p2Data[0])
							&& player.info.team.equals(p2Data[1])) {
						isFound2 = true;
						pl2 = player;
					}
					if (isFound1 && isFound2) {
						break;
					}
				}
				// NOTE: this needs to not work in regular season
				if ((isFound1 && isFound2 && pl1.values.points > 0
						&& pl2.values.points > 0 && pl1.info.team.length() > 2 && pl2.info.team
						.length() > 0)) {
					fillTable(pl1, pl2, table, p1, p2);
				} else if (pl1.values.points == 0 || pl2.values.points == 0) {
					Toast.makeText(
							ImportLeague.cont,
							"Please enter players who are not on bye, injured, or out for any reason",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							ImportLeague.cont,
							"Input is invalid. Use the dropdown to help format input.",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Populates the table of the lineup decider with data
	 */
	public static void fillTable(PlayerObject pl1, PlayerObject pl2,
			LinearLayout table, AutoCompleteTextView p1, AutoCompleteTextView p2) {
		InputMethodManager imm = (InputMethodManager) ImportLeague.cont
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(p1.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(p2.getWindowToken(), 0);
		DecimalFormat df = new DecimalFormat("#.##");
		t = table;
		RelativeLayout ecr1 = (RelativeLayout) table
				.findViewById(R.id.player1_fp_base);
		RelativeLayout ecr2 = (RelativeLayout) table
				.findViewById(R.id.player2_fp_base);
		RelativeLayout ppw1 = (RelativeLayout) table
				.findViewById(R.id.player1_ppw_base);
		RelativeLayout ppw2 = (RelativeLayout) table
				.findViewById(R.id.player2_ppw_base);
		ecr1.setVisibility(View.GONE);
		ecr2.setVisibility(View.GONE);
		ppw1.setVisibility(View.GONE);
		ppw2.setVisibility(View.GONE);
		table.setVisibility(View.VISIBLE);
		// Names
		TextView p1name = (TextView) ((Activity) ImportLeague.cont)
				.findViewById(R.id.player1_name);
		TextView p2name = (TextView) ((Activity) ImportLeague.cont)
				.findViewById(R.id.player2_name);
		p1name.setVisibility(View.VISIBLE);
		p2name.setVisibility(View.VISIBLE);
		p1name.setText(pl1.info.name);
		p2name.setText(pl2.info.name);

		// Projections
		TextView p1proj = (TextView) table.findViewById(R.id.player1_proj);
		TextView p2proj = (TextView) table.findViewById(R.id.player2_points);
		RelativeLayout p1projb = (RelativeLayout) table
				.findViewById(R.id.player1_points_base);
		RelativeLayout p2projb = (RelativeLayout) table
				.findViewById(R.id.player2_points_base);
		if (pl1.values.points > pl2.values.points) {
			p1projb.setBackgroundColor(0XFF4DDB4D);
			p2projb.setBackgroundColor(0XFFFF4D4D);
		} else if (pl1.values.points < pl2.values.points) {
			p2projb.setBackgroundColor(0XFF4DDB4D);
			p1projb.setBackgroundColor(0XFFFF4D4D);
		}
		p1proj.setText("Projected: " + pl1.values.points);
		p2proj.setText("Projected: " + pl2.values.points);
		// Positional SOS
		TextView p1sos = (TextView) table.findViewById(R.id.player1_sos);
		TextView p2sos = (TextView) table.findViewById(R.id.player2_sos);
		RelativeLayout p1sosb = (RelativeLayout) table
				.findViewById(R.id.player1_sos_base);
		RelativeLayout p2sosb = (RelativeLayout) table
				.findViewById(R.id.player2_sos_base);
		int sos1 = 0;
		int sos2 = 0;
		boolean mightBeWeekOne = ImportLeague.holder.isRegularSeason
				&& ImportLeague.holder.sos.size() == 0;
		if (!ImportLeague.holder.isRegularSeason) {
			sos1 = ImportLeague.holder.sos.get(pl1.info.team + ","
					+ pl1.info.position);
			sos2 = ImportLeague.holder.sos.get(pl2.info.team + ","
					+ pl2.info.position);
		} else if (!mightBeWeekOne) {
			sos1 = ImportLeague.holder.sos.get(pl1.info.adp + ","
					+ pl1.info.position);
			sos2 = ImportLeague.holder.sos.get(pl2.info.adp + ","
					+ pl2.info.position);
		}
		if (mightBeWeekOne) {
			p1sos.setText(" - ");
			p2sos.setText(" - ");
		}
 else {
			if (sos1 > sos2) {
				p2sosb.setBackgroundColor(0XFF4DDB4D);
				p1sosb.setBackgroundColor(0XFFFF4D4D);
			}
			if (sos1 < sos2) {
				p1sosb.setBackgroundColor(0XFF4DDB4D);
				p2sosb.setBackgroundColor(0XFFFF4D4D);
			}
			p1sos.setText("SOS: " + sos1);
			p2sos.setText("SOS: " + sos2);
		}
		// PAA
		TextView p1paa = (TextView) table.findViewById(R.id.player1_paa);
		TextView p2paa = (TextView) table.findViewById(R.id.player2_paa);
		RelativeLayout p1paab = (RelativeLayout) table
				.findViewById(R.id.player1_paa_base);
		RelativeLayout p2paab = (RelativeLayout) table
				.findViewById(R.id.player2_paa_base);
		if (pl1.values.paa > pl2.values.paa) {
			p1paab.setBackgroundColor(0XFF4DDB4D);
			p2paab.setBackgroundColor(0XFFFF4D4D);
		} else if (pl1.values.paa < pl2.values.paa) {
			p2paab.setBackgroundColor(0XFF4DDB4D);
			p1paab.setBackgroundColor(0XFFFF4D4D);
		}
		p1paa.setText("PAA: " + df.format(pl1.values.paa));
		p2paa.setText("PAA: " + df.format(pl2.values.paa));
		// Risk
		TextView p1risk = (TextView) table.findViewById(R.id.player1_risk);
		TextView p2risk = (TextView) table.findViewById(R.id.player2_risk);
		RelativeLayout p1riskb = (RelativeLayout) table
				.findViewById(R.id.player1_risk_base);
		RelativeLayout p2riskb = (RelativeLayout) table
				.findViewById(R.id.player2_risk_base);
		if (pl1.risk > pl2.risk) {
			p2riskb.setBackgroundColor(0XFF4DDB4D);
			p1riskb.setBackgroundColor(0XFFFF4D4D);
		}
		if (pl1.risk < pl2.risk) {
			p1riskb.setBackgroundColor(0XFF4DDB4D);
			p2riskb.setBackgroundColor(0XFFFF4D4D);
		}
		p1risk.setText("Risk: " + pl1.risk);
		p2risk.setText("Risk: " + pl2.risk);
		// Weekly Rank
		TextView p1rank = (TextView) table
				.findViewById(R.id.player1_weekly_pos_rank);
		TextView p2rank = (TextView) table
				.findViewById(R.id.player2_weekly_pos_rank);
		RelativeLayout p1rankb = (RelativeLayout) table
				.findViewById(R.id.player1_weekly_pos_rank_base);
		RelativeLayout p2rankb = (RelativeLayout) table
				.findViewById(R.id.player2_weekly_pos_rank_base);
		if (pl1.values.ecr > pl2.values.ecr) {
			p2rankb.setBackgroundColor(0XFF4DDB4D);
			p1rankb.setBackgroundColor(0XFFFF4D4D);
		}
		if (pl1.values.ecr < pl2.values.ecr) {
			p1rankb.setBackgroundColor(0XFF4DDB4D);
			p2rankb.setBackgroundColor(0XFFFF4D4D);
		}
		p1rank.setText("Positional Rank: " + pl1.values.ecr);
		p2rank.setText("Positional Rank: " + pl2.values.ecr);
		if (ManageInput.confirmInternet(ImportLeague.cont)) {
			ParsingAsyncTask o = new ParsingAsyncTask();
			ParseFP task = o.new ParseFP(ImportLeague.cont, pl1.info.name,
					pl2.info.name, pl1.info.team, pl2.info.team, true);
			task.execute(ImportLeague.cont);
		}
	}

	/**
	 * Sets the percentage of experts starting player x at the top of the table
	 * 
	 * @param ecrList
	 */
	public static void setECR(List<String> ecrList) {
		RelativeLayout p1Base = (RelativeLayout) t
				.findViewById(R.id.player1_fp_base);
		RelativeLayout p2Base = (RelativeLayout) t
				.findViewById(R.id.player2_fp_base);
		RelativeLayout ppw1Base = (RelativeLayout) t
				.findViewById(R.id.player1_ppw_base);
		RelativeLayout ppw2Base = (RelativeLayout) t
				.findViewById(R.id.player2_ppw_base);
		p1Base.setVisibility(View.VISIBLE);
		p2Base.setVisibility(View.VISIBLE);
		ppw1Base.setVisibility(View.VISIBLE);
		ppw2Base.setVisibility(View.VISIBLE);
		TextView p1 = (TextView) t.findViewById(R.id.player1_fp);
		TextView p2 = (TextView) t.findViewById(R.id.player2_fp);
		TextView ppw1 = (TextView) t.findViewById(R.id.player1_ppw);
		TextView ppw2 = (TextView) t.findViewById(R.id.player2_ppw);
		p1.setText("Percentage of Experts: " + ecrList.get(0));
		p2.setText("Percentage of Experts: " + ecrList.get(1));
		ppw1.setText("Average Scoring: " + ecrList.get(2));
		ppw2.setText("Average Scoring: " + ecrList.get(3));
		if (Integer.parseInt(ecrList.get(0).substring(0,
				ecrList.get(0).length() - 1)) > Integer.parseInt(ecrList.get(1)
				.substring(0, ecrList.get(1).length() - 1))) {
			p1Base.setBackgroundColor(0XFF4DDB4D);
			p2Base.setBackgroundColor(0XFFFF4D4D);
		} else {
			p2Base.setBackgroundColor(0XFF4DDB4D);
			p1Base.setBackgroundColor(0XFFFF4D4D);
		}
		if (Double.parseDouble(ecrList.get(2)) > Double.parseDouble(ecrList
				.get(3))) {
			ppw1Base.setBackgroundColor(0XFF4DDB4D);
			ppw2Base.setBackgroundColor(0XFFFF4D4D);
		} else {
			ppw2Base.setBackgroundColor(0XFF4DDB4D);
			ppw1Base.setBackgroundColor(0XFFFF4D4D);
		}
	}
}
