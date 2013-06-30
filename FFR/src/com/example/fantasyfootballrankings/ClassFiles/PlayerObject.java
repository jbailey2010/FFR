package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
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
	public String draftClass;
	public String injuryStatus;
	public List<String> fa;
	public double risk;
	public double riskPos;
	public double riskAll;
	public List<Double> vals;
	 
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
		draftClass = "";
		injuryStatus = "Injury Status: Healthy";
		fa = new ArrayList<String>();
		vals = new ArrayList<Double>();
		risk = -1.0;
		riskPos = -1.0;
		riskAll = -1.0;
	}
	

	

}
