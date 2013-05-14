package com.example.fantasyfootballrankings.ClassFiles;

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
	}
	

	

}
