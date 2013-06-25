package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;

/**
 * Just stores some basic information on the values of a player
 * and a mini library that handles basic operations on the values
 * @author Jeff
 *
 */
public class Values 
{
	public double count;
	public double worth;
	public double high;
	public double low;
	public int ecr;
	public double points;
	public double paa;
	public double paapd;
	/**
	 * Just initializes the values stored in the object,
	 * count to one (obviously)
	 * @param firstWorth the first value set to the player
	 */
	public Values(int firstWorth)
	{
		worth = firstWorth * 1.0;
		count = 1.0;
		high = firstWorth;
		low = firstWorth;
		ecr = -1;
		points = 0.0;
		paa = 0.0;
		paapd = 0.0;
	}

	/**
	 * Takes in a new value and re-calculates the worth based on it
	 * @param player the Values object being worked with
	 * @param newValue the new value to work into the above parameter
	 * return is void as it just adjusts the value passed in
	 */
	public static void handleNewValue(Values player, double newValue)
	{
		isExtreme(player, newValue);
		double totalSum = player.worth * player.count;
		totalSum += (1.0 * newValue);
		player.count += 1.0;
		player.worth = totalSum / player.count;
	}
	
	/**
	 * Pretty basic. It just adjusts the max/min if the new value is
	 * more extreme than the previous 'best'
	 * @param player the values object to be compared to
	 * @param newValue the new value to compare with
	 */
	public static void isExtreme(Values player, double newValue)
	{
		if(player.high < newValue)
		{
			player.high = newValue;
		}
		if(player.low > newValue)
		{
			player.low = newValue;
		}
	}
}
