package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;

/**
 * Stores little stuff, the most basic of the information
 * this has a miniature library of functions specific to BasicInfo stuff
 * @author Jeff
 */
public class BasicInfo 
{
	public String name;
	public String team;
	public String position;
	public String status;
	public String adp;
	public String bye;
	public String trend;
	public String contractStatus;
	public String age;
	public String oLineStatus;
	public String passRunRatio;
	public String additionalStat;
	public int sos;
	/**
	 * Simply establishes the values. Nothing special
	 * @param playerName the name of the player
	 * @param playerTeam the name of the team
	 * @param pos the player's position
	 */
	public BasicInfo(String playerName, String playerTeam, String pos)
	{
		name = playerName;
		team = playerTeam;
		position = pos;
		status = "This player's value is exactly what common intuition states.";
		adp = "Not set";
		bye = "Not set";
		trend = "0.0";
		contractStatus = "Under Contract";
		age = "0"; 
		sos = -1;
		oLineStatus = "";
		passRunRatio = "";
		additionalStat = "";
	}

	/**
	 * Pretty basic. It takes whatever position is input and the existing
	 * player and returns the longest of the two. 
	 * NOTE: this works only if the player already exists. If not, go with the first
	 * parsing's position and store that.
	 * @param position the string parsed
	 * @param player2 the player's info to be compared with
	 * @return the standardized string.
	 */
	public static String standardPos(String position, BasicInfo player2)
	{
		String returnString = player2.position;
		if(returnString.equals("PK"))
		{
			returnString = "K";
		}
		else if(position.equals("K"))
		{
			position = "K";
		}
		if(position.length() >= player2.position.length())
		{
			returnString = position;
		}
		return returnString;
	}

	
	/**
	 * A quick way to see if a team can be standardized, i.e. ATL -> Atlanta Falcons
	 * NOTE: This only will work given that the player already exists in the PQ, so if not,
	 * go with the name as parsed on first iteration
	 * @param team the team name parsed from the file originally
	 * @param player2 the already existent match's info to be compared to
	 * @return the longer of the two strings.
	 */
	public static String standardTeam(String team, BasicInfo player2)
	{
		String returnString = player2.team;
		if(team.length() > player2.team.length())
		{
			returnString = team;
		}
		return returnString;
	}
	
	/**
	 * Handles standardization of a player object
	 * @param team the parsed team name
	 * @param position the parsed position
	 * @param match the match already found in the priority queue
	 */
	public static void standardAll(String team, String position, BasicInfo match)
	{
		match.team = standardTeam(team, match);
		match.position = standardPos(position, match);
	}
}
