package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
	public List<Integer> times;
	
	/**
	 * Just sets it up.
	 * Couldn't be simpler.
	 */
	public PostedPlayer(String nameParsed, int countStart)
	{
		name = nameParsed;
		count = countStart;
		times = new ArrayList<Integer>();
	}
	
	/**
	 * Finds the percentage of mentions in the last x days
	 * @param threshold
	 * @return
	 */
	public int lastTime(int threshold)
	{
		Double percentage = 0.0;
		for(Integer date : times)
		{
			if(date <= threshold)
			{
				percentage += 1.0;
			}
		}
		percentage = percentage / (times.size()) * 100.0;
		return percentage.intValue();
	}
}
