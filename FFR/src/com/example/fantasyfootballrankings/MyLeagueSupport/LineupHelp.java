package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

/**
 * A static library to handle the lineup help part of importleague
 * @author Jeff
 *
 */
public class LineupHelp {
	/**
	 * Handles the initial set up of the lineup decider front end
	 * @param res
	 */
	public static void setLineupInfo(View res)
	{
		final AutoCompleteTextView p1 = (AutoCompleteTextView)res.findViewById(R.id.player1_input);
		final AutoCompleteTextView p2 = (AutoCompleteTextView)res.findViewById(R.id.player2_input);
		final LinearLayout table = (LinearLayout)res.findViewById(R.id.table_base);
		Button submit = (Button)res.findViewById(R.id.compare_submit);
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for(PlayerObject player : ImportLeague.holder.players)
		{
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("main", player.info.name);
			if(!player.info.name.contains("D/ST") && player.info.position.length() >= 1 && player.info.team.length() > 2)
			{
				datum.put("sub", player.info.team);
			}
			else
			{
				datum.put("sub", "");
			}
			data.add(datum);
		}
		 final SimpleAdapter mAdapter = new SimpleAdapter(ImportLeague.cont, data, 
		    		android.R.layout.simple_list_item_2, 
		    		new String[] {"main", "sub"}, 
		    		new int[] {android.R.id.text1, 
		    			android.R.id.text2});
		p1.setAdapter(mAdapter);
	    p2.setAdapter(mAdapter);
	    p1.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = (((TwoLineListItem)arg1)).getText1().getText().toString();
				String team = (((TwoLineListItem)arg1)).getText2().getText().toString();
				p1.setText(name + " - " + team);
			}
	    });
	    p2.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = (((TwoLineListItem)arg1)).getText1().getText().toString();
				String team = (((TwoLineListItem)arg1)).getText2().getText().toString();
				p2.setText(name + " - " + team);
			}
	    });
	    submit.setOnClickListener(new OnClickListener(){
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
				for(PlayerObject player : ImportLeague.holder.players)
				{
					if(player.info.name.equals(p1Data[0]) && player.info.team.equals(p1Data[1]))
					{
						isFound1 = true;
						pl1 = player;
					}
					if(player.info.name.equals(p2Data[0]) && player.info.team.equals(p2Data[1]))
					{
						isFound2 = true;
						pl2 = player;
					}
					if(isFound1 && isFound2)
					{
						break;
					}
				}
				if(isFound1 && isFound2 && pl1.values.points > 0 && pl2.values.points > 0 && pl1.info.team.length() > 2 && pl2.info.team.length() > 0)
				{
					fillTable(pl1, pl2, table, p1, p2);
				}
				else
				{
					Toast.makeText(ImportLeague.cont, "Input is invalid. Make sure you only select a player via the dropdown, and neither player is on bye.", Toast.LENGTH_LONG).show();
				}
			}
	    });
	}
	
	/**
	 * Populates the table of the lineup decider with data
	 */
	public static void fillTable(PlayerObject pl1, PlayerObject pl2, LinearLayout table, AutoCompleteTextView p1, AutoCompleteTextView p2) {
		InputMethodManager imm = (InputMethodManager)ImportLeague.cont.getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(p1.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(p2.getWindowToken(), 0);
			DecimalFormat df = new DecimalFormat("#.##");
		table.setVisibility(View.VISIBLE);
		//Names
		TextView p1name = (TextView)table.findViewById(R.id.player1_name);
		TextView p2name = (TextView)table.findViewById(R.id.player2_name);
		p1name.setText(pl1.info.name);
		p2name.setText(pl2.info.name);
		//Projections
		TextView p1proj = (TextView)table.findViewById(R.id.player1_proj);
		TextView p2proj = (TextView)table.findViewById(R.id.player2_points);
		RelativeLayout p1projb = (RelativeLayout)table.findViewById(R.id.player1_points_base);
		RelativeLayout p2projb = (RelativeLayout)table.findViewById(R.id.player2_points_base);
		if(pl1.values.points > pl2.values.points)
		{
			p1projb.setBackgroundColor((Color.GREEN));
			p2projb.setBackgroundColor(Color.RED);
		}
		else if(pl1.values.points < pl2.values.points)
		{
			p2projb.setBackgroundColor((Color.GREEN));
			p1projb.setBackgroundColor(Color.RED);
		}
		p1proj.setText("Projected: " + pl1.values.points);
		p2proj.setText("Projected: " + pl2.values.points);
		//Positional SOS
		TextView p1sos = (TextView)table.findViewById(R.id.player1_sos);
		TextView p2sos = (TextView)table.findViewById(R.id.player2_sos);
		RelativeLayout p1sosb = (RelativeLayout)table.findViewById(R.id.player1_sos_base);
		RelativeLayout p2sosb = (RelativeLayout)table.findViewById(R.id.player2_sos_base);
		int sos1 = ImportLeague.holder.sos.get(pl1.info.team + "," + pl1.info.position);
		int sos2 = ImportLeague.holder.sos.get(pl2.info.team + "," + pl2.info.position);
		if(sos1 > sos2)
		{
			p2sosb.setBackgroundColor(Color.GREEN);
			p1sosb.setBackgroundColor(Color.RED);
		}
		if(sos1 < sos2)
		{
			p1sosb.setBackgroundColor(Color.GREEN);
			p2sosb.setBackgroundColor(Color.RED);
		}
		p1sos.setText("SOS: " + sos1);
		p2sos.setText("SOS: " + sos2);
		//PAA
		TextView p1paa = (TextView)table.findViewById(R.id.player1_paa);
		TextView p2paa= (TextView)table.findViewById(R.id.player2_paa);
		RelativeLayout p1paab = (RelativeLayout)table.findViewById(R.id.player1_paa_base);
		RelativeLayout p2paab = (RelativeLayout)table.findViewById(R.id.player2_paa_base);
		if(pl1.values.paa > pl2.values.paa)
		{
			p1paab.setBackgroundColor((Color.GREEN));
			p2paab.setBackgroundColor(Color.RED);
		}
		else if(pl1.values.paa < pl2.values.paa)
		{
			p2paab.setBackgroundColor((Color.GREEN));
			p1paab.setBackgroundColor(Color.RED);
		}
		p1paa.setText("PAA: " + df.format(pl1.values.paa));
		p2paa.setText("PAA: " + df.format(pl2.values.paa));
		//Risk
		TextView p1risk = (TextView)table.findViewById(R.id.player1_risk);
		TextView p2risk = (TextView)table.findViewById(R.id.player2_risk);
		RelativeLayout p1riskb = (RelativeLayout)table.findViewById(R.id.player1_risk_base);
		RelativeLayout p2riskb = (RelativeLayout)table.findViewById(R.id.player2_risk_base);
		if(pl1.risk > pl2.risk)
		{
			p2riskb.setBackgroundColor(Color.GREEN);
			p1riskb.setBackgroundColor(Color.RED);
		}
		if(pl1.risk < pl2.risk)
		{
			p1riskb.setBackgroundColor(Color.GREEN);
			p2riskb.setBackgroundColor(Color.RED);
		}
		p1risk.setText("Risk: " + pl1.risk);
		p2risk.setText("Risk: " + pl2.risk);
		//Weekly Rank
		TextView p1rank = (TextView)table.findViewById(R.id.player1_weekly_pos_rank);
		TextView p2rank = (TextView)table.findViewById(R.id.player2_weekly_pos_rank);
		RelativeLayout p1rankb = (RelativeLayout)table.findViewById(R.id.player1_weekly_pos_rank_base);
		RelativeLayout p2rankb = (RelativeLayout)table.findViewById(R.id.player2_weekly_pos_rank_base);
		if(pl1.values.ecr > pl2.values.ecr)
		{
			p2rankb.setBackgroundColor(Color.GREEN);
			p1rankb.setBackgroundColor(Color.RED);
		}
		if(pl1.values.ecr < pl2.values.ecr)
		{
			p1rankb.setBackgroundColor(Color.GREEN);
			p2rankb.setBackgroundColor(Color.RED);
		}
		p1rank.setText("Positional Rank: " + pl1.values.ecr);
		p2rank.setText("Positional Rank: " + pl2.values.ecr);
	}
}
