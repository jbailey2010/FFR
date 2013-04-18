package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.htmlcleaner.XPatherException;

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
				"Colin Kaepernick"));
		
		List<String> hflc = new ArrayList<String>(Arrays.asList("DeSean Jackson", 
				"Larry Fitzgerald", "Michael Vick", "Torrey Smith", "Maurice Jones-Drew", 
				"Ryan Mathews", "Danario Alexander", "Jay Cutler", "Michael Floyd", 
				"A.J. Jenkins", "Tandon Doss", "Sam Bradford", "Rashard Mendenhall", 
				"Matt Forte", "Antonio Brown"));
		
		List<String> avoid = new ArrayList<String>(Arrays.asList("Andy Dalton", "Eric Decker", 
				"Wes Welker", "Demaryius Thomas", "Sidney Rice", "Shonn Greene", 
				"Mike Wallace"));
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
				player.info.status = "Looks like he could be good, definitely worth a shot. Could be a bounce-back candidate.";
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
		for(PlayerObject e: holder.players)
		{
			holder.parsedPlayers.add(e.info.name);
		}
	}
}
