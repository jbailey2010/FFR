package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;

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
	public Double ecr;
	public double points;
	public double paa;
	public double paapd;
	public double relPrice;
	public double relPoints;
	public double secWorth;
	/**
	 * Just initializes the values stored in the object,
	 * count to one (obviously)
	 * @param firstWorth the first value set to the player
	 */
	public Values(int firstWorth)
	{
		worth = firstWorth * 1.0;
		count = 1.0;
		ecr = -1.0;
		points = 0.0;
		paa = 0.0;
		paapd = 0.0;
		relPrice = 0.0;
		relPoints = 0.0;
	}

	/**
	 * Takes in a new value and re-calculates the worth based on it
	 * @param player the Values object being worked with
	 * @param newValue the new value to work into the above parameter
	 * return is void as it just adjusts the value passed in
	 */
	public static void handleNewValue(Values player, double newValue)
	{
		double totalSum = player.worth * player.count;
		totalSum += (1.0 * newValue);
		player.count += 1.0;
		player.worth = totalSum / player.count;
	}

}
