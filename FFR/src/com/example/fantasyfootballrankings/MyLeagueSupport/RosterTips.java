package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.TeamTradeInfo;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.Pages.ImportLeague;
import com.ffr.fantasyfootballrankings.R;

/**
 * A static library to handle the management of rosters, giving suggested FA targets and trade targets
 * @author Jeff
 *
 */
public class RosterTips 
{
	public static List<PlayerObject> freeAgents;
	public static ImportedTeam newImport;
	public static View res;
	
	/**
	 * Handles the logistics of setting up roster tips
	 * @param n
	 */
	public static void init(ImportedTeam n, View r)
	{
		newImport = n;
		res = r;
		setUpLists();
		initFrontEnd();
	}
	
	/**
	 * Populates the free agency list with free agents in the league
	 */
	public static void setUpLists()
	{
		freeAgents = new ArrayList<PlayerObject>();
		for(PlayerObject player : ImportLeague.holder.players)
		{
			if(player.info.team.split(" ").length > 1)
			{
				boolean isFound = false;
				for(TeamAnalysis iter : newImport.teams)
				{
					if(iter.team.contains(player.info.name))
					{
						isFound=true;
						break;
					}
				}
				if(!isFound)
				{
					freeAgents.add(player);
				}
			}
		}
	}
	
	public static void initFrontEnd()
	{
		final Spinner teamsSp = (Spinner)res.findViewById(R.id.team_tips_spinner);
		List<String> teamNames = new ArrayList<String>();
		for(TeamAnalysis team : newImport.teams)
		{
			teamNames.add(team.teamName);
		}
		ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(ImportLeague.cont, 
				android.R.layout.simple_spinner_dropdown_item, teamNames);
		teamsSp.setAdapter(adapterPos);
		final Button fa = (Button)res.findViewById(R.id.fa_tips);
		final Button trade = (Button)res.findViewById(R.id.trade_tips);
		fa.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fa.setBackgroundResource(R.drawable.selected_tab);
				trade.setBackgroundResource(R.drawable.not_selected_tab);
				String teamName = teamsSp.getSelectedItem().toString();
				TeamAnalysis iter = null;
				for(TeamAnalysis team : newImport.teams)
				{
					if(team.teamName.equals(teamName))
					{
						iter = team;
						break;
					}
				}
				handleFA(iter, res);
			}
		});
		trade.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				trade.setBackgroundResource(R.drawable.selected_tab);
				fa.setBackgroundResource(R.drawable.not_selected_tab);
				String teamName = teamsSp.getSelectedItem().toString();
				TeamAnalysis iter = null;
				for(TeamAnalysis team : newImport.teams)
				{
					if(team.teamName.equals(teamName))
					{
						iter = team;
						break;
					}
				}
				handleTrades(iter, res);
			} 
		});
		teamsSp.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				TextView output = (TextView)res.findViewById(R.id.fa_content);
				TextView tradeOutput = (TextView)res.findViewById(R.id.trade_content);
				String teamName = teamsSp.getSelectedItem().toString();
				TeamAnalysis iter = null;
				for(TeamAnalysis team : newImport.teams)
				{
					if(team.teamName.equals(teamName))
					{
						iter = team;
						break;
					}
				}
				if(output.isShown())
				{
					fa.setBackgroundResource(R.drawable.selected_tab);
					trade.setBackgroundResource(R.drawable.not_selected_tab);
					handleFA(iter, res);
				}
				else if(tradeOutput.isShown())
				{
					fa.setBackgroundResource(R.drawable.not_selected_tab);
					trade.setBackgroundResource(R.drawable.selected_tab);
					handleTrades(iter, res);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public static void handleTrades(TeamAnalysis team, View res)
	{
		Map<TeamAnalysis, TeamTradeInfo>leagueMaster = new HashMap<TeamAnalysis, TeamTradeInfo>();
		for(TeamAnalysis iter : newImport.teams)
		{
			leagueMaster.put(iter, new TeamTradeInfo(iter, ImportLeague.cont));
		}
		TextView output = (TextView)res.findViewById(R.id.fa_content);
		ScrollView base = (ScrollView)res.findViewById(R.id.fa_scroll);
		base.setVisibility(View.VISIBLE);
		output.setVisibility(View.GONE);
		TextView tradeOutput = (TextView)res.findViewById(R.id.trade_content);
		tradeOutput.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Gets all of the relevant maps (each position), given a team
	 * @param team
	 */
	public static void handleFA(TeamAnalysis team, View res)
	{
		Map<PlayerObject, PriorityQueue<PlayerObject>> qb = faMoves(team, "QB");
		Map<PlayerObject, PriorityQueue<PlayerObject>> rb = faMoves(team, "RB");
		Map<PlayerObject, PriorityQueue<PlayerObject>> wr = faMoves(team, "WR");
		Map<PlayerObject, PriorityQueue<PlayerObject>> te = faMoves(team, "TE");
		Map<PlayerObject, PriorityQueue<PlayerObject>> def= faMoves(team, "D/ST");
		Map<PlayerObject, PriorityQueue<PlayerObject>> k  = faMoves(team, "K");
		TextView output = (TextView)res.findViewById(R.id.fa_content);
		ScrollView base = (ScrollView)res.findViewById(R.id.fa_scroll);
		TextView tradeOutput = (TextView)res.findViewById(R.id.trade_content);
		tradeOutput.setVisibility(View.GONE);
		base.setVisibility(View.VISIBLE);
		output.setVisibility(View.VISIBLE);
		StringBuilder outputS = new StringBuilder(10000);
		parseFAData(qb, outputS, "QBs");
		parseFAData(rb, outputS, "RBs");
		parseFAData(wr, outputS, "WRs");
		parseFAData(te, outputS, "TEs");
		parseFAData(def, outputS, "D/STs");
		parseFAData(k, outputS, "Ks");
		output.setText(outputS.toString());
	}
	
	/**
	 * Handles the iterating through the map/pq and stringbuffering
	 */
	public static void parseFAData(Map<PlayerObject, PriorityQueue<PlayerObject>> qb, StringBuilder outputS, String pos)
	{
		outputS.append(pos + "\n\n");
		if(qb != null && qb.size() > 0)
		{
			for(PlayerObject old : qb.keySet())
			{
				StringBuilder outputStr = new StringBuilder(100);
				outputStr.append(old.info.name + " has a ROS ranking of " + old.values.rosRank + ", but ");
				PriorityQueue<PlayerObject> better = qb.get(old);
				int counter = 12;
				boolean flag = false;
				if(better.size() == 2)
				{
					flag = true;
				}
				while(!better.isEmpty() && counter > 0)
				{
					counter --;
					PlayerObject iter = better.poll();
					if(!flag)
					{
						outputStr.append(iter.info.name + " (" + iter.values.rosRank + "), ");
					}
					else
					{
						outputStr.append(iter.info.name + " (" + iter.values.rosRank + ") ");
					}
					if(better.size() == 1)
					{
						outputStr.append("and ");
					}
				}
				String inter = outputStr.toString();
				inter = inter.substring(0, inter.length() - 2);
				if(counter <= 9)
				{
					outputS.append(inter + " are all available\n\n");
				}
				else if(counter <= 10)
				{
					outputS.append(inter + " are available\n\n");
				}
				else if(counter <= 11)
				{
					outputS.append(inter + " is available\n\n");
				}
			}
			outputS.append("\n\n");
		}
		else
		{
			outputS.append("No improvements obviously available in free agency\n\n\n");
		}
	}
	
	
	/**
	 * Given a team and a position, it runs through all free agents and generates a list of maybe better players, in order
	 * of ROS rank
	 */
	public static Map<PlayerObject, PriorityQueue<PlayerObject>> faMoves(TeamAnalysis team, String pos)
	{
		Map<PlayerObject, PriorityQueue<PlayerObject>> improvements = new HashMap<PlayerObject, PriorityQueue<PlayerObject>>();
		for(PlayerObject player : team.players)
		{
			if(player.info.position.equals(pos))
			{
				int rosRank = player.values.rosRank;
				if(rosRank <= 0)
				{
					rosRank = 100;
				}
				PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b)
					{
						if(a.values.rosRank > b.values.rosRank)
						{
							return 1;
						}
						if(a.values.rosRank < b.values.rosRank)
						{
							return -1;
						}
						return 0;
					}
				});
				for(PlayerObject fa : freeAgents)
				{
					if(fa.info.position.equals(pos) && fa.values.rosRank > 0)
					{
						if(fa.values.rosRank < rosRank)
						{
							sorted.add(fa);
						}
					}
				}
				if(sorted.size() > 0)
				{
					improvements.put(player, sorted);
				}
			}
		}
		if(improvements.size() == 0)
		{
			return null;
		}
		return improvements;
	}
}
