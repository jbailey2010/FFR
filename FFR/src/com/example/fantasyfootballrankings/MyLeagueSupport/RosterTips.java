package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import android.app.Activity;
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

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.TeamTradeInfo;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.InterfaceAugmentations.NonListSwipeDetector;
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
	public static boolean isF = false;
	public static boolean isFTE = false;
	public static boolean isOP = false;
	
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
		Spinner topics = (Spinner)res.findViewById(R.id.fa_topics);
		topics.setVisibility(View.GONE);
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
		Spinner topic = (Spinner)res.findViewById(R.id.fa_topics);
		topic.setVisibility(View.GONE);
		Map<TeamAnalysis, TeamTradeInfo>leagueMaster = new HashMap<TeamAnalysis, TeamTradeInfo>();
		for(TeamAnalysis iter : newImport.teams)
		{
			leagueMaster.put(iter, new TeamTradeInfo(iter, ImportLeague.cont, ImportLeague.newImport.roster));
		}
		TextView output = (TextView)res.findViewById(R.id.fa_content);
		ScrollView base = (ScrollView)res.findViewById(R.id.fa_scroll);
		base.setOnTouchListener(new NonListSwipeDetector((Activity) ImportLeague.cont, "Import"));
		base.setVisibility(View.VISIBLE);
		output.setVisibility(View.GONE);
		TextView tradeOutput = (TextView)res.findViewById(R.id.trade_content);
		tradeOutput.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Gets all of the relevant maps (each position), given a team
	 * @param team
	 */
	public static void handleFA(final TeamAnalysis team, View res)
	{
		final TextView output = (TextView)res.findViewById(R.id.fa_content);
		final ScrollView base = (ScrollView)res.findViewById(R.id.fa_scroll);
		base.setOnTouchListener(new NonListSwipeDetector((Activity) ImportLeague.cont, "Import"));
		final TextView tradeOutput = (TextView)res.findViewById(R.id.trade_content);
		Spinner topic = (Spinner)res.findViewById(R.id.fa_topics);
		topic.setVisibility(View.VISIBLE);
		List<String> teamNames = new ArrayList<String>();
		teamNames.add("Rest of Season Free Agents");
		teamNames.add("Streaming Free Agents");
		ArrayAdapter<String> adapterPos = new ArrayAdapter<String>(ImportLeague.cont, 
				android.R.layout.simple_spinner_dropdown_item, teamNames);
		topic.setAdapter(adapterPos);
		topic.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				StringBuilder outputS = new StringBuilder(10000);
				tradeOutput.setVisibility(View.GONE);
				base.setVisibility(View.VISIBLE);
				output.setVisibility(View.VISIBLE);
				if(arg2 == 0)
				{
					parseFAData(faMoves(team, "QB", arg2), outputS, "QBs", " has a ROS ranking of ", arg2);
					parseFAData(faMoves(team, "RB", arg2), outputS, "RBs", " has a ROS ranking of ", arg2);
					parseFAData(faMoves(team, "WR", arg2), outputS, "WRs", " has a ROS ranking of ", arg2);
					parseFAData(faMoves(team, "TE", arg2), outputS, "TEs", " has a ROS ranking of ", arg2);
					parseFAData(faMoves(team, "D/ST", arg2), outputS, "D/STs", " has a ROS ranking of ", arg2);
					parseFAData(faMoves(team, "K", arg2), outputS, "Ks", " has a ROS ranking of ", arg2);
				}
				else if(arg2 == 1)
				{
					parseFAData(faMoves(team, "QB", arg2), outputS, "QBs", " has a weekly ranking of ", arg2);
					parseFAData(faMoves(team, "RB", arg2), outputS, "RBs", " has a weekly ranking of ", arg2);
					parseFAData(faMoves(team, "WR", arg2), outputS, "WRs", " has a weekly ranking of ", arg2);
					parseFAData(faMoves(team, "TE", arg2), outputS, "TEs", " has a weekly ranking of ", arg2);
					parseFAData(faMoves(team, "D/ST", arg2), outputS, "D/STs", " has a weekly ranking of ", arg2);
					parseFAData(faMoves(team, "K", arg2), outputS, "Ks", " has a weekly ranking of ", arg2);
				}
				output.setText(outputS.toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}	
		});
	}
	
	/**
	 * Handles the iterating through the map/pq and stringbuffering
	 * @param leadStr 
	 * @param map 
	 */
	public static void parseFAData(Map<PlayerObject, PriorityQueue<PlayerObject>> qb, StringBuilder outputS, String pos, String leadStr, int map)
	{
		outputS.append(pos + "\n\n");
		if(qb != null && qb.size() > 0)
		{
			for(PlayerObject old : qb.keySet())
			{
				StringBuilder outputStr = new StringBuilder(100);
				if(map == 0)
				{
					outputStr.append(old.info.name + leadStr + old.values.rosRank + ", but ");
				}
				if(map == 1)
				{
					outputStr.append(old.info.name + leadStr + old.values.ecr + ", but ");
				}
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
					if(map == 0)
					{
						if(!flag)
						{
							outputStr.append(iter.info.name + " (" + iter.values.rosRank + "), ");
						}
						else
						{
							outputStr.append(iter.info.name + " (" + iter.values.rosRank + ") ");
						}
					}
					if(map == 1)
					{
						if(!flag)
						{
							outputStr.append(iter.info.name + " (" + iter.values.ecr + "), ");
						}
						else
						{
							outputStr.append(iter.info.name + " (" + iter.values.ecr + ") ");
						}
					}
					if(better.size() == 1 || counter == 1)
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
					outputS.append(inter + ") are available\n\n");
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
			outputS.append("No obvious improvements available in free agency\n\n\n");
		}
	}
	
	
	/**
	 * Given a team and a position, it runs through all free agents and generates a list of maybe better players, in order
	 * of ROS rank
	 * @param flag 
	 */
	public static Map<PlayerObject, PriorityQueue<PlayerObject>> faMoves(TeamAnalysis team, String pos, int flag)
	{
		List<PlayerObject> iter = new ArrayList<PlayerObject>();
		if(flag == 0)
		{
			iter = team.players;
		}
		if(flag == 1)
		{
			String text = team.team;
			Map<String, String[]> rosters = new HashMap<String, String[]>();
			rosters.put("QB", text.split("Quarterbacks: ")[1].split("\n")[0].split(", "));
			rosters.put("RB", text.split("Running Backs: ")[1].split("\n")[0].split(", "));
			rosters.put("WR", text.split("Wide Receivers: ")[1].split("\n")[0].split(", "));
			rosters.put("TE", text.split("Tight Ends: ")[1].split("\n")[0].split(", "));
			rosters.put("D/ST", text.split("D/ST: ")[1].split("\n")[0].split(", "));
			rosters.put("K", text.split("Kickers: ")[1].split("\n")[0].split(", "));
			StringBuilder output = new StringBuilder(1000);
			TeamAnalysis dummy = new TeamAnalysis();
			List<String> remainingPlayers = new ArrayList<String>();
			remainingPlayers.addAll(Arrays.asList(rosters.get("QB")));
			remainingPlayers.addAll(Arrays.asList(rosters.get("RB")));
			remainingPlayers.addAll(Arrays.asList(rosters.get("WR")));
			remainingPlayers.addAll(Arrays.asList(rosters.get("TE")));
			remainingPlayers.addAll(Arrays.asList(rosters.get("D/ST")));
			remainingPlayers.addAll(Arrays.asList(rosters.get("K")));
			TeamList.isF = false;
			TeamList.isFTE = false;
			TeamList.isOP = false;
			Roster r = ImportLeague.newImport.roster;
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("RB"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "RB", ImportLeague.cont, ImportLeague.holder, r));
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("WR"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "WR", ImportLeague.cont, ImportLeague.holder, r));
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("QB"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "QB", ImportLeague.cont, ImportLeague.holder, r));
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("TE"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "TE", ImportLeague.cont, ImportLeague.holder, r));
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("D/ST"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "D/ST", ImportLeague.cont, ImportLeague.holder, r));
			output.append(dummy.optimalLineup(remainingPlayers, rosters.get("K"), rosters.get("QB"), rosters.get("RB"), rosters.get("WR"), rosters.get("TE"), "K", ImportLeague.cont, ImportLeague.holder, r));
			String[] middle = output.toString().split("\n");
			for(String posIter : middle)
			{
				String[] players = posIter.split(": ")[1].split(", ");
				for(String name : players)
				{
					if(ImportLeague.holder.parsedPlayers.contains(name))
					{
						for(PlayerObject iterPlayers : ImportLeague.holder.players)
						{
							if(iterPlayers.info.name.equals(name) && iterPlayers.info.position.equals(pos))
							{
								iter.add(iterPlayers);
								break;
							}
						}
					}
				}
			}
		}
		Map<PlayerObject, PriorityQueue<PlayerObject>> improvements = new HashMap<PlayerObject, PriorityQueue<PlayerObject>>();
		for(PlayerObject player : iter)
		{
			if(player.info.position.equals(pos))
			{
				int rosRank = 0;
				if(flag == 0)
				{
					rosRank = player.values.rosRank;
					if(rosRank <= 0)
					{
						rosRank = 100;
					}
				}
				if(flag == 1)
				{
					rosRank = player.values.ecr.intValue();
					if(rosRank <= 0)
					{
						continue;
					}
				}
				PriorityQueue<PlayerObject> sorted = null;
				if(flag == 0)
				{
					sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
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
				}
				else if(flag == 1)
				{
					sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b)
						{
							if(a.values.ecr > b.values.ecr)
							{
								return 1;
							}
							if(a.values.ecr < b.values.ecr)
							{
								return -1;
							}
							return 0;
						}
					});
				}
				for(PlayerObject fa : freeAgents)
				{
					if(flag == 0)
					{
						if(fa.info.position.equals(pos) && fa.values.rosRank > 0)
						{
							if(fa.values.rosRank < rosRank)
							{
								sorted.add(fa);
							}
						}
					}
					if(flag == 1)
					{
						if(fa.info.position.equals(pos) && fa.values.ecr > 0)
						{
							if(fa.values.ecr < rosRank)
							{
								sorted.add(fa);
							}
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
