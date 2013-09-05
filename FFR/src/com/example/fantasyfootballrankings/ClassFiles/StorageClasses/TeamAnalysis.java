package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.PriorityQueue;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;

/**
 * Handles the analysis of a team, given the string input of it
 * @author Jeff
 *
 */
public class TeamAnalysis 
{
	String team;
	String[] qb;
	String[] rb;
	String[] wr;
	String[] te;
	String[] d;
	String[] k;
	Storage holder;
	Context cont;
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
	public TeamAnalysis(String teamStr, Storage hold, Context c)
	{
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
		qbStart = paaStarters(qb, "QB");
		rbStart = paaStarters(rb, "RB");
		wrStart = paaStarters(wr, "WR");
		teStart = paaStarters(te, "TE");
		dStart = paaStarters(d, "D/ST");
		kStart = paaStarters(k, "K");
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
	public double paaStarters(String[] pos, String posStr)
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
		Scoring s = ReadFromFile.readScoring(cont);
		if(s.catches > 0 && posStr.equals("WR"))
		{
			limit++;
		}
		else if(s.catches == 0 && posStr.equals("RB"))
		{
			limit++;
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
			}
		}
		return Double.valueOf(df.format(total));
	}


}
