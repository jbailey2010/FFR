package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.PriorityQueue;

import FileIO.ReadFromFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

public class MathUtils {
	
	/**
	 * Sets the leverage of players
	 * @param holder
	 * @param cont
	 */
	public static double getLeverage(PlayerObject player, Storage holder, Context cont)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		double maxWorth = 0.0;
		double maxProj = 0.0;
		for(PlayerObject iter : holder.players)
		{
			if(iter.info.position.equals(player.info.position) && player.values.points > maxProj){
				maxWorth = iter.values.secWorth;
				maxProj = iter.values.points;
			}
		}
		return Double.parseDouble(df.format(((player.values.points / maxProj) / (player.values.secWorth / maxWorth))));
		
	}

	/**
	 * Calculates the points above average per player per position
	 * @param holder
	 * @param cont
	 */
	public static void getPAA(Storage holder, Context cont)
	{
		Roster roster = ReadFromFile.readRoster(cont);
		double qbLimit = 0.0;
		double rbLimit = 0.0;
		double wrLimit = 0.0;
		double teLimit = 0.0;
		double dLimit = 0.0;
		double kLimit = 0.0;
		int x = roster.teams;
		kLimit = 1.25 * x;
		dLimit = 1.25 * x;
		if(roster.qbs == 1)
		{
			qbLimit = (1.25 * x) + 1.33333;
		}
		else
		{
			qbLimit = (6 * x - 30);
		}
		if(roster.tes == 1)
		{
			teLimit = (1.75 * x) - 3.3333333;
		}
		else
		{
			teLimit = (7.5 * x) - 41.66667;
		}
		if(roster.rbs == 1)
		{
			rbLimit = (1.5 * x) - 2;
		}
		else if(roster.rbs == 2)
		{
			rbLimit = (3.25 * x) - 5.33333;
		}
		else
		{
			rbLimit = (6 * x) - 16.33333;
		}
		if(roster.wrs == 1)
		{
			wrLimit = (1.25 * x) + 0.33333;
		}
		else if(roster.wrs == 2)
		{
			wrLimit = (2.75 * x) - 1.66666667;
		}
		else
		{
			wrLimit = (4.5 * x) - 5;
		}
		if(roster.flex != null)
		{
			Scoring scoring = ReadFromFile.readScoring(cont);
			if(roster.flex.rbwr == 1 || roster.flex.rbwrte == 1)
			{
				if(scoring.catches == 1)
				{
					//Legit
					if(roster.rbs == 2 && roster.wrs == 2)
					{
						rbLimit = 3.75 * x - 10.666667;
						wrLimit = 4.25 * x - 2.33333;
					}
					if(roster.rbs == 1 && roster.wrs == 3)
					{
						rbLimit = 3 * x - 3.3333;
						wrLimit = 4.75 * x - 6.3333;
					}
					if(roster.rbs == 2 && roster.wrs == 3)
					{
						rbLimit = 4.5 * x - 5.33333;
						wrLimit = 5.75 * x - 14;
					}
					//Guesstimated
					if(roster.rbs == 1 && roster.wrs == 1)
					{
						rbLimit = 2 * x - 3.3333;
						wrLimit = 2 * x - 1;
					}
					if(roster.rbs == 1 && roster.wrs == 2)
					{
						rbLimit = 2.5 * x;
						wrLimit = 4.25 * x - 5;
					}
					if(roster.rbs == 2 && roster.wrs == 1)
					{
						rbLimit = 3.5 * x - 10;
						wrLimit = 2.25 * x - 1;
					}
					if(roster.rbs == 3 && roster.wrs == 1)
					{
						wrLimit = 2.5 * x + 1;
						rbLimit = 4.7 * x - 5;
					}
					if(roster.rbs == 3 && roster.wrs == 2)
					{
						rbLimit = 4.75 * x - 4.33333;
						wrLimit = 4.25 * x;
					}
					if(roster.rbs == 3 && roster.wrs == 3)
					{
						rbLimit = 4.75 * x - 1;
						wrLimit = 5.75 * x - 12;
					}
				}
				else
				{
					//Legit
					if(roster.rbs == 2 && roster.wrs == 2)
					{
						rbLimit = 2.75 * x + 6;
						wrLimit = 4.25 * x - 7.3333;
					}
					if(roster.rbs == 1 && roster.wrs == 3)
					{
						rbLimit = 2.5 * x + 3.3333;
						wrLimit = 5.25 * x - 13;
					}
					if(roster.rbs == 2 && roster.wrs == 3)
					{
						rbLimit = 4.5 * x - 5.3333;
						wrLimit = 5.75 * x - 14;
					}
					//Guesstimated
					if(roster.rbs == 1 && roster.wrs == 1)
					{
						rbLimit = 2 * x - 2;
						wrLimit = 2 * x - 1.66667;
					}
					if(roster.rbs == 1 && roster.wrs == 2)
					{
						rbLimit = 2.5 * x + 1;
						wrLimit = 4.25 * x - 6;
					}
					if(roster.rbs == 2 && roster.wrs == 1)
					{
						rbLimit = 3.5 * x - 9;
						wrLimit = 2.25 * x - 1.666667;
					}
					if(roster.rbs == 3 && roster.wrs == 1)
					{
						wrLimit = 2.5 * x + 1.5;
						rbLimit = 4.7 * x - 3.6667;
					}
					if(roster.rbs == 3 && roster.wrs == 2)
					{
						rbLimit = 4.75 * x - 3.666667;
						wrLimit = 4.25 * x - 1;
					}
					if(roster.rbs == 3 && roster.wrs == 3)
					{
						rbLimit = 4.75 * x;
						wrLimit = 5.75 * x - 13;
					}
				}
				if(roster.flex.rbwrte == 1)
				{
					teLimit += 12.0 / x;
				}
			}
			if(roster.flex.op == 1)
			{
				qbLimit = (6 * x - 31);
				teLimit += 6.0/x;
				rbLimit += x/12.0;
				if(scoring.catches == 1)
				{
					rbLimit += x/12.0;
					wrLimit += x/11.0;
				}
				else
				{
					rbLimit += x/11.0;
					wrLimit += x/12.0;
				}
			}
		}
		double qbCounter = 0.0;
		double rbCounter = 0.0;
		double wrCounter = 0.0;
		double teCounter = 0.0;
		double dCounter = 0.0;
		double kCounter = 0.0;
		double qbTotal = 0.0;
		double rbTotal = 0.0;
		double wrTotal = 0.0;
		double teTotal = 0.0;
		double dTotal = 0.0;
		double kTotal = 0.0;
		PriorityQueue<PlayerObject>qb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.worth > b.values.worth)
			    {
			        return -1;
			    }
			    if (a.values.worth < b.values.worth)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		PriorityQueue<PlayerObject>rb = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>wr = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>te = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>k = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		PriorityQueue<PlayerObject>def = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
					@Override
					public int compare(PlayerObject a, PlayerObject b) 
					{
						if (a.values.worth > b.values.worth)
					    {
					        return -1;
					    }
					    if (a.values.worth < b.values.worth)
					    {
					    	return 1;
					    }
					    return 0;
					}
				});
		for(PlayerObject player : holder.players)
		{
			if(player.info.position.equals("QB"))
			{
				qb.add(player);
			}
			else if(player.info.position.equals("RB"))
			{
				rb.add(player);
			}
			else if(player.info.position.equals("WR"))
			{
				wr.add(player);
			}
			else if(player.info.position.equals("TE"))
			{
				te.add(player);
			}
			else if(player.info.position.equals("D/ST"))
			{
				def.add(player);
			}
			else if(player.info.position.equals("K"))
			{
				k.add(player);
			}
		}
		for(qbCounter = 0; qbCounter < qbLimit; qbCounter++)
		{
			qbTotal += qb.poll().values.points;
		}
		for(rbCounter = 0; rbCounter < rbLimit; rbCounter++)
		{
			rbTotal += rb.poll().values.points;
		}
		for(wrCounter = 0; wrCounter < wrLimit; wrCounter++)
		{
			wrTotal += wr.poll().values.points;
		}
		for(teCounter = 0; teCounter < teLimit; teCounter++)
		{
			teTotal += te.poll().values.points;
		} 
		for(dCounter = 0; dCounter < dLimit; dCounter++)
		{
			dTotal += def.poll().values.points;
		}
		for(kCounter = 0; kCounter < kLimit; kCounter++)
		{
			kTotal += k.poll().values.points;
		}
		qbTotal /= qbCounter;
		rbTotal /= rbCounter;
		wrTotal /= wrCounter;
		teTotal /= teCounter;
		dTotal /= dCounter;
		kTotal /= kCounter;
		for(PlayerObject player : holder.players)
		{
			if((player.info.position.equals("QB") || player.info.position.equals("RB") || 
					player.info.position.equals("WR") || player.info.position.equals("TE") || player.info.position.equals("K")
					|| player.info.position.equals("D/ST"))&& 
					player.values.points != 0.0)
			{
				if(player.info.position.equals("QB"))
				{
					player.values.paa = player.values.points - qbTotal;
				}
				else if(player.info.position.equals("RB"))
				{ 
					player.values.paa = player.values.points - rbTotal;
				}
				else if(player.info.position.equals("WR"))
				{
					player.values.paa = player.values.points - wrTotal;
				}
				else if(player.info.position.equals("TE"))
				{
					player.values.paa = player.values.points - teTotal;
				}
				else if(player.info.position.equals("D/ST"))
				{
					player.values.paa = player.values.points - dTotal;
				}
				else if(player.info.position.equals("K"))
				{
					player.values.paa = player.values.points - kTotal;
				}
			}
		}
	}

}
