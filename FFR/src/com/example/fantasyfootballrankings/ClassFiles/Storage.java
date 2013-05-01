package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;


import java.io.Serializable;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.Pages.Home;


import FileIO.WriteToFile;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
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
	/**
	 * This fetches/parses the player names, then 
	 * calls another function to write it to file
	 * @param holder holds the array to be written to and written
	 * @param cont the context used to write to file in the called function
	 * @throws IOException 
	 */
	public static void fetchPlayerNames(final Context cont) throws IOException
	{
		//holder.playerNames.clear();
		List<String> names = new ArrayList<String>();
		String[] defenses = {"Bengals D/ST", "Steelers D/ST", "Browns D/ST", "Ravens D/ST", 
       		"Patriots D/ST", "Dolphins D/ST", "Bills D/ST", "Jets D/ST", "Texans D/ST",
       		"Colts D/ST", "Jaguars D/ST", "Titans D/ST", "Broncos D/ST", "Raiders D/ST",
       		"Chiefs D/ST", "Chargers D/ST", "Vikings D/ST", "Lions D/ST", "Packers D/ST",
       		"Bears D/ST", "Giants D/ST", "Eagles D/ST", "Cowboys D/ST", "Redskins D/ST",
       		"Falcons D/ST", "Saints D/ST", "Panthers D/ST", "Buccaneers D/ST", "Seahawks D/ST",
       		"49ers D/ST", "Rams D/ST", "Cardinals D/ST"	};
       	for(int i = 0; i < defenses.length; i++)
       	{
			names.add(defenses[i]);
       	}
		//The baseline URL and the endings to be attached for the sake of getting all data easily
		String url = "http://www.cbssports.com/nfl/playersearch?POSITION=";
		String[] concat = {"QB&print_rows=9999", "RB&print_rows=9999", "FB&print_rows=9999",
				"WR&print_rows=9999", "TE&print_rows=9999", "K&print_rows=9999"};
		for(int i = 0; i < concat.length; i++)
		{
			final String full = url + concat[i];
			try {
				Document doc = Jsoup.connect(full).get();
				fetchPlayersHelp(doc, names, cont, full, "row2");
				fetchPlayersHelp(doc, names, cont, full, "row1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Left out...why the hell aren't they in the list?
		names.add("Robert Griffin III");
		names.add("Brandon Jacobs");
		names.add("Kellen Winslow");
		names.add("Tim Hightower");
		names.add("Terrell Owens");
		
		WriteToFile.storePlayerNames(names, cont);
	}
	
	/**
	 * Abstracted as cbs is stupid, same code twice
	 * @param holder the storage to write to
	 * @param cont the context because idk
	 * @param full the url to work with
	 * @param params the id to parse from
	 * @throws IOException
	 */
	public static void fetchPlayersHelp(Document doc, List<String> names, Context cont, String full, String params) throws IOException
	{
		String playerText = HandleBasicQueries.handleTablesMulti(doc, full, params);
		String[] perRow = playerText.split("\n");
		for(int j = 0; j < perRow.length; j++)
		{
			String[][] all = new String[perRow.length][];
			all[j] = perRow[j].split(" ");
			if(all[j][0].contains(","))
			{
				String lastName = all[j][0].substring(0, all[j][0].length() - 1);
				String name = all[j][1] + " " + lastName;
				names.add(name);
			}
		}
	}
}
