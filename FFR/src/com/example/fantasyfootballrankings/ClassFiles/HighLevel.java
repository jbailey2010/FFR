package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseBrokenTackles;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseStats;

import FileIO.ReadFromFile;
import android.content.Context;
import android.os.StrictMode;

/**
 * Handles operations done on all of the players
 * These are to be done all at once, once all of the rankings are fetched
 * @author Jeff
 *
 */

public class HighLevel 
{
	/**
	 * Takes in the storage unit, makes four arrays of the great looking players, the good,
	 * those with a high floor but a low ceiling, and the avoids. It iterates through all of it,
	 * if a match is found, then it sets the status.
	 * NOTE: Untested, and, as of now, it just sets the status with no other effect. Also, arrays are a work in progress.
	 * @param holder the storage to work through
	 */
	public static void setStatus(Storage holder)
	{
		List<String> mustOwn = new ArrayList<String>(Arrays.asList("Jamaal Charles", 
				"David Wilson", "Randall Cobb", "Dez Bryant"));
		
		List<String> good = new ArrayList<String>(Arrays.asList("Russell Wilson", 
				"Kenny Britt", "Doug Martin", "Jimmy Graham", "C.J. Spiller", 
				"Aaron Hernandez", "LeSean McCoy", "Vincent Brown", 
				"Eddie Lacy", "Jonathan Franklin", "Giovani Bernard", "Lamar Miller", 
				"Marshawn Lynch", "Peyton Manning", "Browns D/ST", "Reggie Bush",
				"Danny Amendola", "Seahawks D/ST", "Trent Richardson", 
				"Colin Kaepernick", "Chris Johnson", "A.J. Green", "Aaron Dobson"));
		
		List<String> hflc = new ArrayList<String>(Arrays.asList("DeSean Jackson", 
				"Larry Fitzgerald", "Michael Vick", "Torrey Smith", "Maurice Jones-Drew", 
				"Ryan Mathews", "Danario Alexander", "Jay Cutler", "Michael Floyd", 
				"A.J. Jenkins", "Tandon Doss", "Sam Bradford", "Rashard Mendenhall", 
				"Matt Forte", "Antonio Brown", "Jake Locker", "Chris Ivory", "Le'Veon Bell",
				"DeAndre Hopkins", "Sidney Rice", "Mark Ingram", "Chad Spann", "Shane Vereen"));
		
		List<String> avoid = new ArrayList<String>(Arrays.asList("Andy Dalton", "Eric Decker", 
				"Wes Welker", "Demaryius Thomas", "Sidney Rice", "Shonn Greene", 
				"Mike Wallace", "Jacob Tamme", "Mikel Leshoure"));
		Iterator<PlayerObject> iter = holder.players.iterator();
		while(iter.hasNext())
		{
			PlayerObject player = (PlayerObject) iter.next();
			String name = player.info.name;
			//Go through the must own array
			if(mustOwn.contains(name))
			{
				player.info.status = "Great upside, good-looking floor. Get this player.";
				player.values.worth += 2.5;
			}
			//Go through the good array
			if(good.contains(name))
			{
				player.info.status = "Looks like he could be good considering the cost, definitely worth a shot.";
				player.values.worth += 1.25;
			}
			//Go through the high floor, low ceiling array
			if(hflc.contains(name))
			{
				player.info.status = "Could be a monster, but could be a dud. Be wary.";
				player.values.worth += 0.75;
			}
			//Go through the avoid array
			if(avoid.contains(name))
			{
				player.info.status = "Not promising. Unless you can get him cheap, don't. Limited upside.";
				player.values.worth -= 1.0;
				if(player.values.worth <= 0)
				{
					player.values.worth = 1.0;
				}
			}
		}
	}
	
	/**
	 * This parses an adp site to fetch the adp of all players given a match
	 * it also, in the interest of two birds, one stone, fetches the bye week
	 * @param holder the storage unit holding all of the players to check in
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void setADP(Storage holder) throws MalformedURLException, IOException, XPatherException
	{
		String adpURL = "http://fantasyfootballcalculator.com/adp_csv_ppr.php?teams=12";
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
    	String adpText = HandleBasicQueries.handleLists(adpURL, "pre");
    	String[] adpList = adpText.split("\n");
    	String[][] adpArray = new String[adpList.length][];
    	for(int i = 0; i < adpList.length; i++)
    	{
    		adpArray[i] = adpList[i].split(",");
    	}
    	for(int i = 0; i < adpArray.length; i++)
    	{ 
    		if(adpArray[i].length > 2)
    		{
	    		String name = adpArray[i][2];
	   			Iterator<PlayerObject> iter = holder.players.iterator();
	   			while(iter.hasNext())
	   			{
	   				PlayerObject player = (PlayerObject) iter.next();
	   				String namePlayer = player.info.name;   
	   				if(namePlayer.equals(name))
	   				{
	   					player.info.adp = adpArray[i][1];
	   					player.info.bye = adpArray[i][6];
	   				} 
	   			} 
    		}
    	}
	}
	
	/**
	 * Couldn't be simpler. It just adds all the parsed
	 * players to a list of players to be stored.
	 * Note, this shouldn't be written to file later, as who cares?
	 * It's volatile and shouldn't be treated as if it isn't.
	 * @param holder
	 */
	public static void getParsedPlayers(Storage holder)
	{
		holder.parsedPlayers.clear();
		for(PlayerObject e: holder.players)
		{
			holder.parsedPlayers.add(e.info.name);
		}
	}
	
	/**
	 * Sets a contract status for players on the fftoolbox list
	 * @param holder
	 * @throws IOException
	 */
	public static void setContractStatus(Storage holder) throws IOException
	{
		String cyStr = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/contract_year_players.cfm?player_pos=QB", "td:not([.c])");
		cyStr += HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/contract_year_players.cfm?player_pos=RB", "td:not([.c])");
		cyStr += HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/contract_year_players.cfm?player_pos=WR", "td:not([.c])");
		cyStr += HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/2013/contract_year_players.cfm?player_pos=TE", "td:not([.c])");
		String[] players = cyStr.split("\n");
		for(String name:players)
		{
			if(name.contains(" "))
			{
				if(!name.split(" ")[0].contains("North") &&
						!name.split(" ")[0].contains("South") &&
						!name.split(" ")[0].contains("East") &&
						!name.split(" ")[0].contains("West"))
				{
					for(PlayerObject player : holder.players)
					{
						if(player.info.name.equals(name))
						{
							player.info.contractStatus = "In a contract year";
						}
					}
				}
			}
		}
	}
	
	/**
	 * A function that gets the strength of schedule for each team
	 * and specific positions per.
	 * @param holder
	 * @throws IOException
	 */ 
	public static void getSOS(Storage holder) throws IOException
	{
		String data = HandleBasicQueries.handleLists("http://www.fftoolbox.com/football/strength_of_schedule.cfm", "tr.c");
		data = data.replaceAll("st", "").replaceAll("nd", "").replaceAll("rd", "").replaceAll("th", "");
		String[] allArr = data.split("\n");
		String[][] team = new String[allArr.length][];
        HashMap<String, Integer>sos = new HashMap<String, Integer>();
		for(int i = 0; i  < allArr.length; i++)
		{  
			team[i] = allArr[i].split(" ");
			String keyBase = ParseRankings.fixTeams(team[i][0]) + ",";
			sos.put(keyBase + "QB", Integer.parseInt(team[i][1]));
			sos.put(keyBase + "RB", Integer.parseInt(team[i][2]));
			sos.put(keyBase + "WR", Integer.parseInt(team[i][3]));
			sos.put(keyBase + "TE", Integer.parseInt(team[i][4]));
			sos.put(keyBase + "K", Integer.parseInt(team[i][5]));
			sos.put(keyBase + "D/ST", Integer.parseInt(team[i][6]));
		}
		Set<String> keys = sos.keySet();
		for(PlayerObject player : holder.players)
		{
			if(keys.contains(player.info.team + "," + player.info.position))
			{
				player.info.sos = sos.get(player.info.team + "," + player.info.position);
			}
		}
	}
	
	/**
	 * A high level function that sets the permanent data in the
	 * player objects
	 * @param holder
	 * @param cont
	 */
	public static void setPermanentData(Storage holder, Context cont)
	{
		Map<String, String> ranks = ReadFromFile.readOLineRanks(cont);
		Map<String, String> ratios = ReadFromFile.readPassRun(cont);
		Map<String, String> menInBox = ReadFromFile.readMenInBox(cont);
		Set<String> names = menInBox.keySet();
		for(PlayerObject elem : holder.players)
		{
			elem.info.oLineStatus = ranks.get(elem.info.team);
			elem.info.passRunRatio = ratios.get(elem.info.team);
			if(elem.info.position.equals("RB") && names.contains(elem.info.name))
			{
				elem.info.additionalStat = menInBox.get(elem.info.name);
			}
		}
	}
	
	/**
	 * Sets the stats of a player
	 * @param holder
	 * @param cont
	 * @throws IOException
	 */
	public static void setStats(Storage holder, Context cont) throws IOException
	{
		Map<String, String> qbs = ParseStats.parseQBStats();
		Map<String, String> rbs = ParseStats.parseRBStats();
		Map<String, String> wrs = ParseStats.parseWRStats();
		Map<String, String> tes = ParseStats.parseTEStats();
		Map<String, String> bt = ParseBrokenTackles.parseBrokenTackles();
		Set<String> keys = bt.keySet();
		for(PlayerObject player : holder.players)
		{
			if(!player.info.position.equals("K") && !player.info.position.equals("D/ST"))
			{
				String[] name = player.info.name.split(" ");
				String testName = name[0].charAt(0) + " " + name[1];
				if(player.info.position.equals("QB"))
				{
					player.stats = qbs.get(testName);
				}
				else if(player.info.position.equals("RB"))
				{
					player.stats = rbs.get(testName);
				}
				else if(player.info.position.equals("WR"))
				{
					player.stats = wrs.get(testName);
				}
				else if(player.info.position.equals("TE"))
				{
					player.stats = tes.get(testName);
				}
				if(keys.contains(player.info.name))
				{
					player.stats += "Broken Tackles: " + bt.get(player.info.name) + "\n";
				}
			}
		}
	}
}
