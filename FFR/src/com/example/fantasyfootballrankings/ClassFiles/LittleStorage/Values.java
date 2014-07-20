package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.util.HashMap;

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
	public double secWorth;
	public int rosRank;
	public HashMap<String, Integer> startDists;
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
		rosRank = 0;
		startDists = new HashMap<String, Integer>();
		startDists.put("Bad", 0);
		startDists.put("Good", 0);
		startDists.put("Great", 0);
	}

	public Values() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Takes in a new value and re-calculates the worth based on it
	 * @param player the Values object being worked with
	 * @param newValue the new value to work into the above parameter
	 * return is void as it just adjusts the value passed in
	 */
	public static void handleNewValue(Values player, double newValue)
	{
		player.worth += newValue;
		player.count++;
	}
	
	public static void normVals(Values player){
		player.worth = player.worth / player.count;
	}

}
