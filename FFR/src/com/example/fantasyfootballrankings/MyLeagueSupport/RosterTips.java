package com.example.fantasyfootballrankings.MyLeagueSupport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

/**
 * A static library to handle the management of rosters, giving suggested FA targets and trade targets
 * @author Jeff
 *
 */
public class RosterTips 
{
	public static List<PlayerObject> freeAgents;
	public static ImportedTeam newImport;
	
	/**
	 * Handles the logistics of setting up roster tips
	 * @param n
	 */
	public static void init(ImportedTeam n)
	{
		newImport = n;
		setUpLists();
		

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
	
	
	public static void handleFA(TeamAnalysis team)
	{
		Map<PlayerObject, PriorityQueue<PlayerObject>> qb = faMoves(team, "QB");
		Map<PlayerObject, PriorityQueue<PlayerObject>> rb = faMoves(team, "RB");
		Map<PlayerObject, PriorityQueue<PlayerObject>> wr = faMoves(team, "WR");
		Map<PlayerObject, PriorityQueue<PlayerObject>> te = faMoves(team, "TE");
		Map<PlayerObject, PriorityQueue<PlayerObject>> def= faMoves(team, "D/ST");
		Map<PlayerObject, PriorityQueue<PlayerObject>> k  = faMoves(team, "K");

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
