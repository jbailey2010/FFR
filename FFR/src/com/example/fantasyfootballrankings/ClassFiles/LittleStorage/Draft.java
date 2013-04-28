package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;

/**
 * A class that will hold the structures to store a draft class,
 * in addition to value and salary remaining
 * @author Jeff
 *
 */
public class Draft
{
	public List<PlayerObject> qb = new ArrayList<PlayerObject>();
	public List<PlayerObject> rb = new ArrayList<PlayerObject>();
	public List<PlayerObject> wr = new ArrayList<PlayerObject>();
	public List<PlayerObject> te = new ArrayList<PlayerObject>();
	public List<PlayerObject> def = new ArrayList<PlayerObject>();
	public List<PlayerObject> k = new ArrayList<PlayerObject>();
	public int remainingSalary;
	public double value;
	
	/**
	 * Just initializes the integer variables to be the standard, starting
	 * numbers. The array lists don't matter at this point.
	 */
	public Draft()
	{
		remainingSalary = 200;
		value = 0.0;
	}
	
	/**
	 * Adjusts the values of a draft class given
	 * values of a pick that's made
	 * @param valuePlayer
	 * @param paid
	 */
	public void newPick(double valuePlayer, int paid)
	{
		remainingSalary -= paid;
		value += (valuePlayer - (double)paid);
	}
}
