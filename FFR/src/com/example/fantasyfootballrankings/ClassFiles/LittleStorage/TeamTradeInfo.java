package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;

/**
 * Handles the parsing of the teamanalysis object into a trade analysis object.
 * @author Jeff
 *
 */
public class TeamTradeInfo 
{
	public TeamAnalysis teamObj;
	public PosTradeInfo qb;
	public PosTradeInfo rb;
	public PosTradeInfo wr;
	public PosTradeInfo te;
	
	/**
	 * \Sets up the teamtradeinfo object to populate based on the teamanalysis object
	 */
	public TeamTradeInfo(TeamAnalysis team, Context cont)
	{
		teamObj = team;
		qb = new PosTradeInfo();
		rb = new PosTradeInfo();
		wr = new PosTradeInfo();
		te = new PosTradeInfo();
		Roster r = ReadFromFile.readRoster(cont);
		int tier1 = r.teams;
		int tier2 = 2*r.teams;
		int tier3 = 3*r.teams;
		for(PlayerObject player : team.players)
		{
			if(player.info.position.equals("QB"))
			{
				addToList(player, qb, tier1, tier2, tier3);
			}
			else if(player.info.position.equals("RB"))
			{
				addToList(player, rb, tier1, tier2, tier3);
			}
			else if(player.info.position.equals("WR"))
			{
				addToList(player, wr, tier1, tier2, tier3);
			}
			else if(player.info.position.equals("TE"))
			{
				addToList(player, te, tier1, tier2, tier3);
			}
		}
	}
	
	/**
	 * Adds the player to the appropriate tier of players to handle the clustering appropriately
	 */
	public void addToList(PlayerObject player, PosTradeInfo set, int tier1, int tier2, int tier3)
	{
		if(player.values.rosRank <= tier1)
		{
			set.tier1.add(player);
		}
		else if(player.values.rosRank <= tier2)
		{
			set.tier2.add(player);
		}
		else if(player.values.rosRank <= tier3)
		{
			set.tier3.add(player);
		}
		else
		{
			set.rest.add(player);
		}
		set.wholeRoster.add(player);
	}

}
