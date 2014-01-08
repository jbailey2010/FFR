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
	public TeamTradeInfo(TeamAnalysis team, Context cont, Roster r)
	{
		System.out.println(team.teamName);
		teamObj = team;
		qb = new PosTradeInfo();
		rb = new PosTradeInfo();
		wr = new PosTradeInfo();
		te = new PosTradeInfo();
		handleLists(r);
		parseClassifications(r, qb, r.qbs);
		parseClassifications(r, rb, r.rbs);
		parseClassifications(r, wr, r.wrs);
		parseClassifications(r, te, r.tes);
		System.out.println("QB: " + qb.classification);
		System.out.println("RB: " + rb.classification);
		System.out.println("WR: " + wr.classification);
		System.out.println("TE: " + te.classification);
	}
	
	/**
	 * Parses each roster of a team into a specific classification
	 */
	public void parseClassifications(Roster r, PosTradeInfo list, int numStarted)
	{
		/*
		 * Black hole check
		 * Logic:
		 * None in the top number of teams * number of players starting
		 */
		int blackHoleTotal = r.teams * numStarted;
		int reallyBadCt = 0;
		for(PlayerObject player : list.wholeRoster)
		{
			if(player.values.rosRank > blackHoleTotal || player.values.rosRank == 0)
			{
				reallyBadCt ++;
			}
		}
		if(reallyBadCt == list.wholeRoster.size())
		{
			list.classification = "Black Hole";
			return;
		}
		//Otherwise, it's tier based information, so need to do some set up for that
		double tierBreak = ((float)r.teams) / 4.0;
		boolean needFlag = false;
		boolean excessFlag = false;
		boolean quantOverQualFlag = false;
		boolean qualOverQuantFlag = false;
		boolean goodFlag = false;
		//A just in case sanity check
		if(numStarted == 0)
		{
			list.classification = "None Started";
			return;
		}
		//1 starter logic, inherently different
		if(numStarted == 1)
		{
			//Need check for 1 starter
			if(list.tier1.size() == 0 || (list.tier1.size() == 1 && list.tier1.get(0).values.rosRank > r.teams - 2*tierBreak))
			{
				needFlag = true;
			}
			//Good check for one starter
			else if(list.tier1.size() == 1 && (list.tier1.get(0).values.rosRank < r.teams/2))
			{
				goodFlag = true;
			}
			//Excess check for 1 starter
			else if(list.tier1.size() > 1)
			{
				excessFlag = true;
			}
			//Quantity over quality check for 1 starter
			else if(list.tier1.size() == 1)
			{
				int goodCt = 0;
				for(PlayerObject player : list.tier1)
				{
					if(player.values.rosRank > r.teams - tierBreak)
					{
						goodCt ++;
					}
				}
				if(goodCt == list.tier1.size() && goodCt < list.wholeRoster.size())
				{
					quantOverQualFlag = true;
				}
			}
			//Quality over quantity check for 1 starter doesn't exist, it doesn't make sense
		}
		//Multiple starter logic
		else if(numStarted > 1)
		{
			/*
			 * Need check
			 * Logic: 
			 * if more than 1 are started, none are in tier1, and
			 * none exist in the top of tier 2 or one does and 
			 * there are still less than num starting in tier 2
			 */
			if(list.tier1.size() == 0)
			{
				int topHalf = 0;
				for(PlayerObject player : list.tier2)
				{
					if(player.values.rosRank > 0 && player.values.rosRank <= r.teams + tierBreak * 2)
					{
						topHalf += 1;
					}
				}
				if(topHalf == 0)
				{
					needFlag = true;
				}
				else if(topHalf == 1 && list.tier2.size() < numStarted)
				{
					needFlag = true;
				}
			}
			/*
			 * Excess check
			 * Logic:
			 * If more than one start and the number in tier1 is > 0, then
			 * if the number in tier1 + the number in the top half of tier2 is >= numstarted and 
			 * the number in tier1 + num in tier 2 is > numStarted and tier 1 isn't empty
			 */
			if((list.tier1.size() + list.tier2.size()) > numStarted && list.tier1.size() > 0)
			{
				int goodCt = list.tier1.size();
				for(PlayerObject player : list.tier2)
				{
					if(player.values.rosRank > 0 && player.values.rosRank <= r.teams + tierBreak)
					{
						goodCt ++;
					}
				}
				if(goodCt >= numStarted)
				{
					excessFlag = true;
				}
			}
			/*
			 * Quality over quantity check
			 * Logic:
			 * If more start, numStart -1 top tier1, with the rest being back of tier 2 at best
			 */
			int goodCt = 0;
			for(PlayerObject player : list.tier1)
			{
				if(player.values.rosRank > 0 && player.values.rosRank <= r.teams/2)
				{
					goodCt++;
				}
			}
			if(goodCt == list.tier1.size() && goodCt == numStarted - 1)
			{
				double floor = r.teams * 1.5;
				goodCt = 0;
				for(PlayerObject player : list.tier2)
				{
					if(player.values.rosRank > floor)
					{
						goodCt ++;
					}
				}
				if(goodCt == list.tier2.size())
				{
					qualOverQuantFlag = true;
				}
			}
			if(list.tier1.size() == numStarted - 1 && list.tier2.size() == 0 && list.tier3.size() > 0)
			{
				qualOverQuantFlag = true;
			}
			/*
			 * Quantity over quality check
			 * Logic:
			 * If one is started and the only owned ones are bottom part of tier 1 and below or
			 * If more are started, if there are any tier 1, then they're at the very back, and there's a 
			 * decent number of tier 2, tier 3, and rest combined
			 */
			boolean tier1Okay = false;
			goodCt = 0;
			int betterCt = 0;
			for(PlayerObject player : list.tier1)
			{
				if(player.values.rosRank > r.teams - tierBreak)
				{
					goodCt ++;
				}
				if(player.values.rosRank > r.teams - 2*tierBreak)
				{
					betterCt++;
				}
			}
			if(goodCt == list.tier1.size())
			{
				tier1Okay = true;
			}
			boolean tier2Okay = (list.tier2.size() > 1);
			boolean restOkay = ((list.tier3.size() >=1 && list.rest.size() >= 1) || list.rest.size() > 1 || list.tier3.size()>1);
			if((tier1Okay && tier2Okay && restOkay) //All fits optimal situation
					|| (tier1Okay && (restOkay && numStarted <= 3)) || //Tail-ended set of players
					(tier1Okay && tier2Okay && numStarted <= 3)//Less filler
					|| (betterCt == list.tier1.size() && !tier2Okay && restOkay))
			{
				quantOverQualFlag = true;
			}
			/*
			 * Good check
			 * Logic:
			 * If there's only one player in tier 1 and 1/2 in tier 2, depending on numstarted, 
			 * and 1 in 3/rest depending on numstarted
			 */
			if((list.tier1.size() == 1 && (list.tier2.size() >= numStarted  - list.tier1.size()))//1 tier 1, rest of starters tier 2
					||(list.tier1.size() == numStarted))//full lineup of tail end tier 1 guys. Not quite excesss
			{
				goodFlag = true;
			}
		}
		if(needFlag)
		{
			list.classification = "Need";
			return;
		}
		if(excessFlag)
		{
			list.classification = "Excess";
			return;
		}
		if(quantOverQualFlag)
		{
			list.classification = "Quantity over Quality";
			return;
		}
		if(qualOverQuantFlag)
		{
			list.classification = "Quality over Quantity";
			return;
		}
		if(goodFlag)
		{
			list.classification = "Good";
			return;
		}
		/*
		 * Otherwise, it was some situation I couldn't comprehend, so debugging
		 */
		list.classification = "Something else";
		System.out.println("--------------");
		for(PlayerObject player : list.wholeRoster)
		{
			System.out.println(player.info.name + " - " + player.values.rosRank);
		}
		System.out.println("-----------");
	}
	
	/**
	 * Handles the population of the various positional information of the players
	 * @param cont
	 */
	public void handleLists(Roster r)
	{
		int tier1 = r.teams;
		int tier2 = 2*r.teams;
		int tier3 = 3*r.teams;
		for(PlayerObject player : teamObj.players)
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
		if(player.values.rosRank > 0 && player.values.rosRank <= tier1)
		{
			set.tier1.add(player);
		}
		else if(player.values.rosRank > 0 && player.values.rosRank <= tier2)
		{
			set.tier2.add(player);
		}
		else if(player.values.rosRank > 0 && player.values.rosRank <= tier3)
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
