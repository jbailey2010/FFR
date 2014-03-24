package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;

import android.content.Context;

/**
 * Handles the storage of players and player names.
 * @author - Jeff
 */

/**
 *Stores various info as it's needed
 * @author Jeff
 *
 */
public class Storage 
{
	public boolean isRegularSeason;
	public Draft draft;
	public List<PlayerObject> players;
	public List<String> playerNames;
	public List<Post> posts;
	public HashSet<String> parsedPlayers;
	public PriorityQueue<PostedPlayer> postedPlayers;
	public Map<String, String> oLineAdv = new HashMap<String, String>();
	public Map<String, String> draftClasses = new HashMap<String, String>();
	public Map<String, Integer> sos = new HashMap<String, Integer>();
	public Map<String, String> bye = new HashMap<String, String>();
	public Map<String, List<String>> fa = new HashMap<String, List<String>>();
	public Map<String, String> notes = new HashMap<String, String>();
	/**
	 * This sets up the priority queue and it's subsequent comparator.
	 * No parameters are necessary, and the playerNames array doesn't need initialization.
	 */
	public Storage(Context cont)
	{
		isRegularSeason = false;
		players = new ArrayList<PlayerObject>(350);
		postedPlayers = new PriorityQueue<PostedPlayer>(100, new Comparator<PostedPlayer>()
		{
			@Override
			public int compare(PostedPlayer a, PostedPlayer b)
			{
				if(a.count > b.count)
				{
					return -1;
				}
				if(a.count < b.count)
				{
					return 1;
				}
				return 0;
			}
		});
		playerNames = new ArrayList<String>(400);
		posts = new ArrayList<Post>(500);
		parsedPlayers = new HashSet<String>(350);
		draft = new Draft(cont);
	}
 
	/**
	 * This checks if a name exists in the parsed list. If so, it 
	 * returns that for the sake of name continuity throughout all.
	 * This list is all of the nfl, so if they're not in here, it's
	 * due to a formatting error. It should also make comparisons through the priority queue
	 * easy peasy.
	 * @param holder the storage unit to be checked through
	 * @param name the name to check through storage
	 * @return the standardized name
	 */
	public static String nameExists(Storage holder, String name)
	{
		if(holder.playerNames.contains(name))
		{
			return name;
		}
		System.out.println(name + " not found...");
		return name;
	}
	
	/**
	 * This iterates through the priority queue and sees
	 * if the name exists within the priority queue already
	 * @param holder the storage to be parsed through
	 * @param name the PASSED IN STANDARDIZED name
	 * @return true if it exists, false if it does not
	 */
	public static PlayerObject pqExists(Storage holder, String name)
	{
		if(holder.parsedPlayers.contains(name))
		{
			 for(PlayerObject player : holder.players)
			 {
				 if(player.info.name.equals(name))
				 {
					 return player;
				 }
			 }
		}
		 return null;
	}
	
	/**
	 * Gets the maximum projection of the players stored
	 * @return
	 */
	public double maxProj()
	{
		double max = 0.0;
		for(PlayerObject player : this.players)
		{
			if(player.values.points > max)
			{
				max = player.values.points;
			}
		}
		return max;
	}
}
