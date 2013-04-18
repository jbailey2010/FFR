package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;
/**
 * Super basic class. Has a name and a count
 * to see the number of times a player was posted
 * @author Jeff
 *
 */
public class PostedPlayer 
{
	public String name;
	public int count;
	
	/**
	 * Just sets it up.
	 * Couldn't be simpler.
	 */
	public PostedPlayer(String nameParsed, int countStart)
	{
		name = nameParsed;
		count = countStart;
	}
}
