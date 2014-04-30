package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.MyLeagueSupport.TeamList;
import com.example.fantasyfootballrankings.Pages.ImportLeague;

/**
 * Handles the analysis of a team, given the string input of it
 * @author Jeff
 *
 */
public class TeamAnalysis 
{
	public String team;
	public List<PlayerObject> players = new ArrayList<PlayerObject>();
	String[] qb;
	String[] rb;
	String[] wr;
	String[] te;
	String[] d;
	String[] k;
	public String teamName;
	Storage holder;
	Context cont;
	public double starterProj;
	public double totalProj;
	public double qbTotal;
	public double rbTotal;
	public double wrTotal;
	public double teTotal;
	public double dTotal;
	public double kTotal;
	public double qbStart;
	public double rbStart;
	public double wrStart;
	public double teStart;
	public double dStart;
	public double kStart;
	public Roster r;
	/**
	 * Does all of the string parsing
	 * @param teamStr
	 * @param hold
	 * @param c
	 */
	public TeamAnalysis(String name, String teamStr, Storage hold, Context c, Roster roster)
	{
		r = roster;
		teamName = name;
		team = teamStr;
		String qbs = teamStr.split("Quarterbacks: ")[1].split("\n")[0];
		String rbs = teamStr.split("Running Backs: ")[1].split("\n")[0];
		String wrs = teamStr.split("Wide Receivers: ")[1].split("\n")[0];
		String tes = teamStr.split("Tight Ends: ")[1].split("\n")[0];
		String def = teamStr.split("D/ST: ")[1].split("\n")[0];
		String ks = teamStr.split("Kickers: ")[1].split("\n")[0];
		qb = qbs.split(", ");
		rb = rbs.split(", ");
		wr = wrs.split(", ");
		te = tes.split(", ");
		d  = def.split(", ");
		k  = ks.split(", ");
		holder = hold;
		qbTotal = paaPos(qb);
		rbTotal = paaPos(rb);
		wrTotal = paaPos(wr);
		teTotal = paaPos(te);
		dTotal = paaPos(d);
		kTotal = paaPos(k);
		cont = c;
		List<String> remainingPlayers = new ArrayList<String>();
		remainingPlayers.addAll(Arrays.asList(qb));
		remainingPlayers.addAll(Arrays.asList(rb));
		remainingPlayers.addAll(Arrays.asList(wr));
		remainingPlayers.addAll(Arrays.asList(te));
		remainingPlayers.addAll(Arrays.asList(d));
		remainingPlayers.addAll(Arrays.asList(k));
		TeamList.isF = false;
		TeamList.isFTE = false;
		TeamList.isOP = false;
		rbStart = paaStarters(remainingPlayers, rb, qb, rb, wr, te, "RB", r);
		wrStart = paaStarters(remainingPlayers, wr, qb, rb, wr, te, "WR", r);
		qbStart = paaStarters(remainingPlayers, qb, qb, rb, wr, te, "QB", r);
		teStart = paaStarters(remainingPlayers, te, qb, rb, wr, te, "TE", r);
		dStart = paaStarters(remainingPlayers, d, qb, rb, wr, te, "D/ST", r);
		kStart = paaStarters(remainingPlayers, k, qb, rb, wr, te, "K", r);
		populateTeamsList(this);
	}
	
	/**
	 * A dummy constructor to handle generic queries without needing storage of data
	 */
	public TeamAnalysis() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Populates each team's player object list
	 */
	public void populateTeamsList(TeamAnalysis team)
	{
		Map<String, String> posFix = new HashMap<String, String>();
		posFix.put("Quarterbacks", "QB");
		posFix.put("Running Backs", "RB");
		posFix.put("Wide Receivers", "WR");
		posFix.put("Tight Ends", "TE");
		posFix.put("Kickers", "K");
		String[] posSet = team.team.split("\n");
		for(String pos : posSet)
		{
			if(!pos.contains("None "))
			{
				String position = posFix.get(pos.split(": ")[0]);
				String[] playerList = pos.split(": ")[1].split(", ");
				for(String name: playerList)
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.name.equals(name) && player.info.position.equals(position))
						{
							team.players.add(player);
							break;
						}
					}
				}
			}
		}
	}

	

	/**
	 * Gets the paa of all of the players at each position (given)
	 * @param pos
	 * @return
	 */
	public double paaPos(String[] pos)
	{
		double total = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		for(int i = 0; i < pos.length; i++)
		{
			if(this.holder.parsedPlayers.contains(pos[i]))
			{
				for(PlayerObject player : this.holder.players)
				{
					if(player.info.name.equals(pos[i]))
					{
						if(player.values.paa > 0 || player.values.paa < 0)
						{
							total += player.values.paa;
							totalProj += player.values.points;
							break;
						}
					}
				}
			}
		}
		return Double.valueOf(df.format(total));
	}
	
	public void manageStarters(){
		HashSet<PlayerObject> ignore = new HashSet<PlayerObject>();
		List<PlayerObject> qbBasic = startersList(r.qbs, qb, "QB");
		List<PlayerObject> rbBasic = startersList(r.rbs, rb, "RB");
		List<PlayerObject> wrBasic = startersList(r.wrs, wr, "WR");
		List<PlayerObject> teBasic = startersList(r.tes, te, "TE");
		List<PlayerObject> dfBasic = startersList(r.def, d, "D/ST");
		List<PlayerObject> kBasic  = startersList(r.k,   k, "K");
		ignore.addAll(qbBasic);
		ignore.addAll(rbBasic);
		ignore.addAll(wrBasic);
		ignore.addAll(teBasic);
		ignore.addAll(dfBasic);
		ignore.addAll(kBasic);
		if(r.flex != null && (r.flex.rbwr > 0 || r.flex.rbwrte > 0 || r.flex.op > 0)){
			int qbConsider = 0;
			int rbConsider = 0;
			int wrConsider = 0;
			int teConsider = 0;
			if(r.flex.rbwr > 0){
				rbConsider++;
				wrConsider++;
			}
			if(r.flex.rbwrte > 0){
				rbConsider++;
				wrConsider++;
				teConsider++;
			}
			if(r.flex.op > 0){
				qbConsider++;
				rbConsider++;
				wrConsider++;
				teConsider++;
			}
			List<PlayerObject> qbNext = new ArrayList<PlayerObject>();
			List<PlayerObject> rbNext = new ArrayList<PlayerObject>();
			List<PlayerObject> wrNext = new ArrayList<PlayerObject>();
			List<PlayerObject> teNext = new ArrayList<PlayerObject>();
			if(qbConsider > 0){
				qbNext = getNextBest(qbConsider, ignore, qb, "QB");
			}
			if(rbConsider > 0){
				rbNext = getNextBest(rbConsider, ignore, rb, "RB");
			}
			if(wrConsider > 0){
				wrNext = getNextBest(wrConsider, ignore, wr, "WR");
			}
			if(teConsider > 0){
				teNext = getNextBest(teConsider, ignore, te, "TE");
			}
		}
		/*
		 * For each non-null flex, find top scorer that applies, add to ignore, find next highest at other position, add...etc.
		 * 
		 * Make custom class with a list of players, a getTotal method, a hashmap from player to type of flex, and pq that shit
		 * 
		 * Add to lists appropriately, save lists to object, clean the fucking below code and optimal lineup to look at starters list...
		 */
	}
	
	public List<PlayerObject> getNextBest(int limit, HashSet<PlayerObject> ignore, String[] pos, String posStr){
		PriorityQueue<PlayerObject> posSort = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.points > b.values.points)
			    {
			        return -1;
			    }
			    if (a.values.points < b.values.points)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		int counter = 0;
		for(String playerName : pos){
			for(PlayerObject player : holder.players){
				if(player.info.name.equals(playerName) && player.info.position.equals(posStr)){
					posSort.add(player);
					break;
				}
			}
		}
		List<PlayerObject> posList = new ArrayList<PlayerObject>();
		while(!posSort.isEmpty()){
			if((counter) >= limit){
				break;
			}
			PlayerObject nextBest = posSort.poll();
			if(!ignore.contains(nextBest)){
				posList.add(nextBest);
				counter ++;
				System.out.println("Next best-ing " + nextBest.info.name + " - " + nextBest.values.points);
			}
		}
		return posList;
	}
	
	/**
	 * Finds the basic starters for a given roster/team, NOT flexes
	 */
	public List<PlayerObject> startersList(int limit, String[] pos, String posStr){
		PriorityQueue<PlayerObject> posSort = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.points > b.values.points)
			    {
			        return -1;
			    }
			    if (a.values.points < b.values.points)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		int counter = 0;
		for(String playerName : pos){
			for(PlayerObject player : holder.players){
				if(player.info.name.equals(playerName) && player.info.position.equals(posStr)){
					posSort.add(player);
					break;
				}
			}
		}
		List<PlayerObject> posList = new ArrayList<PlayerObject>();
		while(!posSort.isEmpty()){
			if((counter) >= limit){
				break;
			}
			PlayerObject nextBest = posSort.poll();
			posList.add(nextBest);
			counter ++;
			System.out.println("Adding " + nextBest.info.name + " - " + nextBest.values.points);
		}
		return posList;
	}
	
	/**
	 * Gets the PAA of starters
	 * @param pos
	 * @param posStr
	 * @return
	 */
	public double paaStarters(List<String> remainingPlayers, String[] pos, String[] qbs, String[] rbs, String[] wrs, String[] tes, 
			String posStr, Roster r)
	{
		double total = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		int limit = 0;
		String tempName = "";
		if(posStr.equals("QB"))
		{
			limit = r.qbs;
		}
		else if(posStr.equals("RB"))
		{
			limit = r.rbs;
		}
		else if(posStr.equals("WR"))
		{
			limit = r.wrs;
		}
		else if(posStr.equals("TE"))
		{
			limit = r.tes;
		}
		else if(posStr.equals("D/ST"))
		{
			limit = r.def;
		}
		else if(posStr.equals("K"))
		{
			limit = r.k;
		}
		if(r.flex != null && r.flex.rbwr > 0 && (posStr.equals("RB") || posStr.equals("WR")) && !TeamList.isF)
		{
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("WR") && rb.size() <= r.rbs)
			{
				TeamList.isF = true;
				limit++;
			}
			else if(posStr.equals("RB") && wr.size() <= r.wrs)
			{
				TeamList.isF = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points)
				{
					remainingPlayers.remove(wrNextBest.info.name);
					TeamList.isF = true;
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points)
				{
					TeamList.isF = true;
					remainingPlayers.remove(rbNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("RB") && wrNextBest.values.points > rbNextBest.values.points)
				{
					tempName = wrNextBest.info.name;
					remainingPlayers.remove(tempName);
				}
			}
		}
		if(r.flex != null && r.flex.rbwrte > 0 && (posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")) && !TeamList.isFTE)
		{
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < tes.length; i++)
			{
				if(holder.parsedPlayers.contains(tes[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("TE") && player.info.name.equals(tes[i]))
						{
							te.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("TE") && te.size() < limit)
			{
				limit = te.size();
			}
			else if(posStr.equals("WR") && (rb.size() <= r.rbs && te.size() <= r.tes))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else if(posStr.equals("RB") && (wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else if(posStr.equals("TE") && (rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				for(int i = 0; i < r.tes; i++)
				{
					te.poll();
				}
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				PlayerObject teNextBest = te.poll();
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(teNextBest.info.name))
				{
					teNextBest = rb.poll();
					if(teNextBest == null)
					{
						teNextBest = new PlayerObject();
						teNextBest.values = new Values();
						teNextBest.values.points = 0;
					}
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(wrNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(rbNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(teNextBest.info.name);
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.op > 0 && (posStr.equals("QB") || posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")) && !TeamList.isOP)
		{
			PriorityQueue<PlayerObject> qb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < qbs.length; i++)
			{
				if(holder.parsedPlayers.contains(qbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("QB") && player.info.name.equals(qbs[i]))
						{
							qb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < tes.length; i++)
			{
				if(holder.parsedPlayers.contains(tes[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("TE") && player.info.name.equals(tes[i]))
						{
							te.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("QB") && qb.size() < limit)
			{
				limit = qb.size();
			}
			else if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("TE") && te.size() < limit)
			{
				limit = te.size();
			}
			else if(posStr.equals("QB") && (rb.size() <= r.rbs && wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("WR") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("RB") && (qb.size() <= r.qbs &&wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("TE") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
				TeamList.isOP = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.qbs; i++)
				{
					qb.poll();
				}
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				for(int i = 0; i < r.tes; i++)
				{
					te.poll();
				}
				PlayerObject qbNextBest = qb.poll();
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				PlayerObject teNextBest = te.poll();
				if(qbNextBest == null)
				{
					qbNextBest = new PlayerObject();
					qbNextBest.values = new Values();
					qbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(qbNextBest.info.name))
				{
					qbNextBest = qb.poll();
					if(qbNextBest == null)
					{
						qbNextBest = new PlayerObject();
						qbNextBest.values = new Values();
						qbNextBest.values.points = 0;
					}
				}
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(teNextBest.info.name))
				{
					teNextBest = te.poll();
					if(teNextBest == null)
					{
						teNextBest = new PlayerObject();
						teNextBest.values = new Values();
						teNextBest.values.points = 0;
					}
				}
				if(posStr.equals("QB") && qbNextBest.values.points > wrNextBest.values.points && qbNextBest.values.points > rbNextBest.values.points && qbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(qbNextBest.info.name);
				}
				else if(posStr.equals("WR") && wrNextBest.values.points > qbNextBest.values.points && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(wrNextBest.info.name);
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > qbNextBest.values.points && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(rbNextBest.info.name);
				}
				else if(posStr.equals("TE") && teNextBest.values.points > qbNextBest.values.points && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(teNextBest.info.name);
				}
			}
		}
		if(tempName.length() > 1)
		{
			remainingPlayers.add(tempName);
		}
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.paa > b.values.paa)
			    {
			        return -1;
			    }
			    if (a.values.paa < b.values.paa)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		for(int i = 0; i < pos.length; i++)
		{
			if(holder.parsedPlayers.contains(pos[i]))
			{
				for(PlayerObject player : holder.players)
				{
					if(player.info.position.equals(posStr) && player.info.name.equals(pos[i]))
					{
						inter.add(player);
						break;
					}
				}
			}
		} 
		if(limit > inter.size())
		{
			limit = inter.size();
		}
		for(int i = 0; i < limit; i++)
		{ 
			PlayerObject player = inter.poll();
			if(player != null)
			{
				total += player.values.paa;
				starterProj += player.values.points;
			}
		}
		return Double.valueOf(df.format(total));
	}
	
	/**
	 * Returns the list of optimal starters for a team
	 * @param remainingPlayers 
	 */
	public String optimalLineup(List<String> remainingPlayers, String[] pos, String[] qbs, String[] rbs, String[] wrs, String[] tes, 
			String posStr, Context cont, Storage holder, Roster r)
	{
		StringBuilder result = new StringBuilder(100);
		DecimalFormat df = new DecimalFormat("#.##");
		String tempName = "";
		int limit = 0;
		if(posStr.equals("QB"))
		{
			limit = r.qbs;
		}
		else if(posStr.equals("RB"))
		{
			limit = r.rbs;
		}
		else if(posStr.equals("WR"))
		{
			limit = r.wrs;
		}
		else if(posStr.equals("TE"))
		{
			limit = r.tes;
		}
		else if(posStr.equals("D/ST"))
		{
			limit = r.def;
		}
		else if(posStr.equals("K"))
		{
			limit = r.k;
		}
		if(r.flex != null && r.flex.rbwr > 0 && (posStr.equals("RB") || posStr.equals("WR")) && !TeamList.isF)
		{
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("WR") && rb.size() <= r.rbs)
			{
				TeamList.isF = true;
				limit++;
			}
			else if(posStr.equals("RB") && wr.size() <= r.wrs)
			{
				TeamList.isF = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points)
				{
					remainingPlayers.remove(wrNextBest.info.name);
					TeamList.isF = true;
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points)
				{
					TeamList.isF = true;
					remainingPlayers.remove(rbNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("RB") && wrNextBest.values.points > rbNextBest.values.points)
				{
					tempName = wrNextBest.info.name;
					remainingPlayers.remove(tempName);
				}
			}
		}
		if(r.flex != null && r.flex.rbwrte > 0 && (posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")) && !TeamList.isFTE)
		{
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < tes.length; i++)
			{
				if(holder.parsedPlayers.contains(tes[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("TE") && player.info.name.equals(tes[i]))
						{
							te.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("TE") && te.size() < limit)
			{
				limit = te.size();
			}
			else if(posStr.equals("WR") && (rb.size() <= r.rbs && te.size() <= r.tes))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else if(posStr.equals("RB") && (wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else if(posStr.equals("TE") && (rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
				TeamList.isFTE = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				for(int i = 0; i < r.tes; i++)
				{
					te.poll();
				}
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				PlayerObject teNextBest = te.poll();
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(teNextBest.info.name))
				{
					teNextBest = rb.poll();
					if(teNextBest == null)
					{
						teNextBest = new PlayerObject();
						teNextBest.values = new Values();
						teNextBest.values.points = 0;
					}
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(wrNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(rbNextBest.info.name);
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					TeamList.isFTE = true;
					remainingPlayers.remove(teNextBest.info.name);
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.op > 0 && (posStr.equals("QB") || posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")) && !TeamList.isOP)
		{
			PriorityQueue<PlayerObject> qb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			PriorityQueue<PlayerObject> te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.paa > b.values.paa)
						    {
						        return -1;
						    }
						    if (a.values.paa < b.values.paa)
						    {
						    	return 1;
						    }
						    return 0;
						}
					});
			for(int i = 0; i < qbs.length; i++)
			{
				if(holder.parsedPlayers.contains(qbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("QB") && player.info.name.equals(qbs[i]))
						{
							qb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < rbs.length; i++)
			{
				if(holder.parsedPlayers.contains(rbs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("RB") && player.info.name.equals(rbs[i]))
						{
							rb.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < wrs.length; i++)
			{
				if(holder.parsedPlayers.contains(wrs[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("WR") && player.info.name.equals(wrs[i]))
						{
							wr.add(player);
							break;
						}
					}
				}
			} 
			for(int i = 0; i < tes.length; i++)
			{
				if(holder.parsedPlayers.contains(tes[i]))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.position.equals("TE") && player.info.name.equals(tes[i]))
						{
							te.add(player);
							break;
						}
					}
				}
			} 
			if(posStr.equals("QB") && qb.size() < limit)
			{
				limit = qb.size();
			}
			else if(posStr.equals("RB") && rb.size() < limit)
			{
				limit = rb.size();
			}
			else if(posStr.equals("WR") && wr.size() < limit)
			{
				limit = wr.size();
			}
			else if(posStr.equals("TE") && te.size() < limit)
			{
				limit = te.size();
			}
			else if(posStr.equals("QB") && (rb.size() <= r.rbs && wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("WR") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("RB") && (qb.size() <= r.qbs &&wr.size() <= r.wrs && te.size() <= r.tes))
			{
				TeamList.isOP = true;
				limit++;
			}
			else if(posStr.equals("TE") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
				TeamList.isOP = true;
				limit++;
			}
			else
			{
				for(int i = 0; i < r.qbs; i++)
				{
					qb.poll();
				}
				for(int i = 0; i < r.rbs; i++)
				{
					rb.poll();
				}
				for(int i = 0; i < r.wrs; i++)
				{
					wr.poll();
				}
				for(int i = 0; i < r.tes; i++)
				{
					te.poll();
				}
				PlayerObject qbNextBest = qb.poll();
				PlayerObject rbNextBest = rb.poll();
				PlayerObject wrNextBest = wr.poll();
				PlayerObject teNextBest = te.poll();
				if(qbNextBest == null)
				{
					qbNextBest = new PlayerObject();
					qbNextBest.values = new Values();
					qbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(qbNextBest.info.name))
				{
					qbNextBest = qb.poll();
					if(qbNextBest == null)
					{
						qbNextBest = new PlayerObject();
						qbNextBest.values = new Values();
						qbNextBest.values.points = 0;
					}
				}
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(rbNextBest.info.name))
				{
					rbNextBest = rb.poll();
					if(rbNextBest == null)
					{
						rbNextBest = new PlayerObject();
						rbNextBest.values = new Values();
						rbNextBest.values.points = 0;
					}
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(wrNextBest.info.name))
				{
					wrNextBest = wr.poll();
					if(wrNextBest == null)
					{
						wrNextBest = new PlayerObject();
						wrNextBest.values = new Values();
						wrNextBest.values.points = 0;
					}
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				else if(!remainingPlayers.contains(teNextBest.info.name))
				{
					teNextBest = te.poll();
					if(teNextBest == null)
					{
						teNextBest = new PlayerObject();
						teNextBest.values = new Values();
						teNextBest.values.points = 0;
					}
				}
				if(posStr.equals("QB") && qbNextBest.values.points > wrNextBest.values.points && qbNextBest.values.points > rbNextBest.values.points && qbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(qbNextBest.info.name);
				}
				else if(posStr.equals("WR") && wrNextBest.values.points > qbNextBest.values.points && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(wrNextBest.info.name);
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > qbNextBest.values.points && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(rbNextBest.info.name);
				}
				else if(posStr.equals("TE") && teNextBest.values.points > qbNextBest.values.points && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					TeamList.isOP = true;
					limit++;
					remainingPlayers.remove(teNextBest.info.name);
				}
			}
		}
		if(tempName.length() > 1)
		{
			remainingPlayers.add(tempName);
		}
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.points > b.values.points)
			    {
			        return -1;
			    }
			    if (a.values.points < b.values.points)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		for(int i = 0; i < pos.length; i++)
		{
			if(holder.parsedPlayers.contains(pos[i]))
			{
				for(PlayerObject player : holder.players)
				{
					if(player.info.position.equals(posStr) && player.info.name.equals(pos[i]))
					{
						inter.add(player);
						break;
					}
				}
			}
		} 
		if(limit > inter.size())
		{
			limit = inter.size();
		}
		for(int i = 0; i < limit; i++)
		{ 
			if(i == 0)
			{
				result.append(posStr + "s: ");
			}
			PlayerObject player = inter.poll();
			if(player != null)
			{
				result.append(player.info.name + ", ");
				remainingPlayers.remove(player.info.name);
			}
		}
		String res = result.toString();
		if(res.length() != 0)
		{
			res = res.substring(0, res.length() - 2) + "\n";
		}
		else
		{
			res = posStr + "s: N/A\n";
		}
		return res;
	}


}
