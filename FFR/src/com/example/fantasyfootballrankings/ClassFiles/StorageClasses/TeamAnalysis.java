package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
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
	/**
	 * Does all of the string parsing
	 * @param teamStr
	 * @param hold
	 * @param c
	 */
	public TeamAnalysis(String name, String teamStr, Storage hold, Context c)
	{
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
		qbStart = paaStarters(qb, qb, rb, wr, te, "QB");
		rbStart = paaStarters(rb, qb, rb, wr, te, "RB");
		wrStart = paaStarters(wr, qb, rb, wr, te, "WR");
		teStart = paaStarters(te, qb, rb, wr, te, "TE");
		dStart = paaStarters(d, qb, rb, wr, te, "D/ST");
		kStart = paaStarters(k, qb, rb, wr, te, "K");
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
	
	/**
	 * Gets the PAA of starters
	 * @param pos
	 * @param posStr
	 * @return
	 */
	public double paaStarters(String[] pos, String[] qbs, String[] rbs, String[] wrs, String[] tes, String posStr)
	{
		double total = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");
		Roster r = ReadFromFile.readRoster(cont);
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
		if(r.flex != null && r.flex.rbwr > 0 && (posStr.equals("RB") || posStr.equals("WR")))
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
				limit++;
			}
			else if(posStr.equals("RB") && wr.size() <= r.wrs)
			{
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
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points)
				{
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.rbwrte > 0 && (posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")))
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
				limit++;
			}
			else if(posStr.equals("RB") && (wr.size() <= r.wrs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("TE") && (rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
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
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.op > 0 && (posStr.equals("QB") || posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")))
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
				limit++;
			}
			else if(posStr.equals("WR") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("RB") && (qb.size() <= r.qbs &&wr.size() <= r.wrs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("TE") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
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
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				if(posStr.equals("QB") && qbNextBest.values.points > wrNextBest.values.points && qbNextBest.values.points > rbNextBest.values.points && qbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("WR") && wrNextBest.values.points > qbNextBest.values.points && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > qbNextBest.values.points && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > qbNextBest.values.points && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
			}
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
	 */
	public String optimalLineup(String[] pos, String[] qbs, String[] rbs, String[] wrs, String[] tes, String posStr, Context cont, Storage holder)
	{
		StringBuilder result = new StringBuilder(100);
		DecimalFormat df = new DecimalFormat("#.##");
		Roster r = ReadFromFile.readRoster(cont);
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
		if(r.flex != null && r.flex.rbwr > 0 && (posStr.equals("RB") || posStr.equals("WR")))
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
				limit++;
			}
			else if(posStr.equals("RB") && wr.size() <= r.wrs)
			{
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
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points)
				{
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.rbwrte > 0 && (posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")))
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
				limit++;
			}
			else if(posStr.equals("RB") && (wr.size() <= r.wrs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("TE") && (rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
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
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				if(posStr.equals("WR") && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
			}
		}
		if(r.flex != null && r.flex.op > 0 && (posStr.equals("QB") || posStr.equals("RB") || posStr.equals("WR") || posStr.equals("TE")))
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
				limit++;
			}
			else if(posStr.equals("WR") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("RB") && (qb.size() <= r.qbs &&wr.size() <= r.wrs && te.size() <= r.tes))
			{
				limit++;
			}
			else if(posStr.equals("TE") && (qb.size() <= r.qbs &&rb.size() <= r.rbs && wr.size() <= r.wrs))
			{
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
				if(rbNextBest == null)
				{
					rbNextBest = new PlayerObject();
					rbNextBest.values = new Values();
					rbNextBest.values.points = 0;
				}
				if(wrNextBest == null)
				{
					wrNextBest = new PlayerObject();
					wrNextBest.values = new Values();
					wrNextBest.values.points = 0;
				}
				if(teNextBest == null)
				{
					teNextBest = new PlayerObject();
					teNextBest.values = new Values();
					teNextBest.values.points = 0;
				}
				if(posStr.equals("QB") && qbNextBest.values.points > wrNextBest.values.points && qbNextBest.values.points > rbNextBest.values.points && qbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("WR") && wrNextBest.values.points > qbNextBest.values.points && wrNextBest.values.points > rbNextBest.values.points && wrNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("RB") && rbNextBest.values.points > qbNextBest.values.points && rbNextBest.values.points > wrNextBest.values.points && rbNextBest.values.points > teNextBest.values.points)
				{
					limit++;
				}
				else if(posStr.equals("TE") && teNextBest.values.points > qbNextBest.values.points && teNextBest.values.points > wrNextBest.values.points && teNextBest.values.points > rbNextBest.values.points)
				{
					limit++;
				}
			}
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
