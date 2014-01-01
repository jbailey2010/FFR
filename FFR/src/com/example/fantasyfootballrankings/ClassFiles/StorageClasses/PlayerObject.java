package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.text.DecimalFormat;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;

/**
 * Handles the player objects. The specific info and subsequently relevant
 * functions are abstracted for simplicity's sake.
 *@author - Jeff
 */ 

public class PlayerObject 
{
	public BasicInfo info;
	public Values values;
	public String stats;
	public String injuryStatus;
	public String note;
	public double risk;
	 
	/**
	 * Sets up the player object
	 * @param playerName the name of the player
	 * @param playerTeam the team of the player
	 * @param pos the position of the player
	 * @param value the worth of the player when first found. Count is set to one.
	 */
	public PlayerObject(String playerName, String playerTeam, String pos, int value)
	{
		//Holds name, team, position, status, bye, and adp
		info = new BasicInfo(playerName, playerTeam, pos);
		//Holds count, worth, high, and low values
		values = new Values(value);
		stats = " ";
		note = " "; 
		injuryStatus = "Injury Status: Healthy";
		risk = -1.0;
	}

	/**
	 * Empty dummy constructor
	 */
	public PlayerObject() {
		// TODO Auto-generated constructor stub
	}
	

	
	/**
	 * Calculates the amount of points a player has scored so far
	 */
	public double pointsSoFar(Scoring s)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		double total = 0.0;
		String stats = this.stats;
		//Just Catches
		if(stats.contains("Catch Rate: "))
		{
			int tgts = Integer.valueOf(stats.split("Targets: ")[1].split("\n")[0]);
			int catchRate = Integer.valueOf(stats.split("Catch Rate: ")[1].split("%\n")[0]);
			double catchPercent = Integer.valueOf(catchRate).doubleValue()/ 100.0;
			double catches = catchPercent * Integer.valueOf(tgts).doubleValue();
			total += catches * s.catches;
		}
		//Just Fumbles
		if(stats.contains("Fumbles: "))
		{
			total -= Integer.valueOf(stats.split("Fumbles: ")[1].split("\n")[0]) * s.fumble;
		}
		//QBs
		if(stats.contains("Interceptions: "))
		{
			total -= Integer.valueOf(stats.split("Interceptions: ")[1].split("\n")[0]) * s.interception;
			total += Integer.valueOf(stats.split("Yards: ")[1].split("\n")[0].replace(",", "")).doubleValue() / s.passYards;
			total += Integer.valueOf(stats.split("Touchdowns: ")[1].split("\n")[0]) * s.passTD;
			if(stats.contains("Rushing Yards"))
			{
				total += Integer.valueOf(stats.split("Rushing Yards: ")[1].split("\n")[0].replace(",", "")).doubleValue() / s.rushYards;
				total += Integer.valueOf(stats.split("Rushing Touchdowns: ")[1].split("\n")[0])*s.rushTD; 
			}
		}
		//RBs
		else if(stats.contains("Carries: ") && (!stats.contains("Targets") || (stats.contains("Targets") && stats.contains("Receiving Yards"))))
		{
			total += s.rushYards * Integer.valueOf(stats.split("Yards: ")[1].split("\n")[0].replace(",", ""));
			total += s.rushTD * Integer.valueOf(stats.split("Touchdowns: ")[1].split("\n")[0]);
			if(stats.contains("Targets: "))
			{
				total += Integer.valueOf(stats.split("Receiving Yards: ")[1].split("\n")[0].replace(",", "")).doubleValue() /s.recYards;
				total += s.recTD * Integer.valueOf(stats.split("Receiving Touchdowns: ")[1].split("\n")[0]);
			}
		}
		//WRs/TEs
		else if(stats.contains("Targets: "))
		{
			total += Integer.valueOf(stats.split("Yards: ")[1].split("\n")[0].replace(",", "")).doubleValue()/s.recYards;
			total += s.recTD * Integer.valueOf(stats.split("Touchdowns: ")[1].split("\n")[0]);
			if(stats.contains("Rushes"))
			{
				total += Integer.valueOf(stats.split("Rushing Yards: ")[1].split("\n")[0]).doubleValue() / s.rushYards;
				total += s.rushTD * Integer.valueOf(stats.split("Rushing Touchdowns: ")[1].split("\n")[0]);
			}
		}
		return Double.valueOf(df.format(total));
	}
}
