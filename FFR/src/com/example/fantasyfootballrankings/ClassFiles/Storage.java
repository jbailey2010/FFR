package com.example.fantasyfootballrankings.ClassFiles;



import java.io.Serializable;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import java.util.Comparator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.Pages.Home;


import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.widget.CheckBox;
import android.widget.EditText;

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
	public Draft draft;
	public List<PlayerObject> players;
	public List<String> playerNames;
	public List<Post> posts;
	public List<String> parsedPlayers;
	public PriorityQueue<PostedPlayer> postedPlayers;
	/**
	 * This sets up the priority queue and it's subsequent comparator.
	 * No parameters are necessary, and the playerNames array doesn't need initialization.
	 */
	public Storage()
	{
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
		parsedPlayers = new ArrayList<String>(350);
		draft = new Draft();
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
		name = name.toLowerCase();
		for(int i = 0; i < holder.playerNames.size(); i++)
		{
			
			String nameIter = holder.playerNames.get(i);
			nameIter = nameIter.toLowerCase();
			if(name.equals(nameIter))
			{
				return holder.playerNames.get(i);
			}
			//Handle an extra space just in case that's a problem
			String[] poss = name.split(" ");
			String[] exis = nameIter.split(" ");
			if(name.contains("d/st") && nameIter.contains("d/st"))
			{
				if(poss[1] == exis[1])
				{
					return holder.playerNames.get(i);
				}
			}
			if(poss[0].equals(exis[0]) && poss[1].equals(exis[1]))
			{
				return holder.playerNames.get(i);
			}
			//Finally, if one is one character and the other a full name
			if(poss[0] == exis[0].substring(0,1) && poss[0].length() == 1 && poss[1] == exis[1]
					&& !exis[1].contains("d/st"))
			{ 
				return holder.playerNames.get(i);
			}
			//One last case, in case of a shorter version of a name (ced/cedric)...etc,
			//while avoiding overlap from the previous if statement
			
			if(poss[1].equals(exis[1]) && exis[0].contains(poss[0]) 
					&& poss[0].length() > 1)
			{
				return holder.playerNames.get(i);
			}
		}
		return name + " NO MATCH FOUND";
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
		 Iterator<PlayerObject> iter = holder.players.iterator();
		 while(iter.hasNext())
		 {
			 PlayerObject player = (PlayerObject) iter.next();
			 if(player.info.name.equals(name))
			 {
				 return player;
			 }
		 }
		 return null;
	}
}
