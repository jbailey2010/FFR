package com.example.fantasyfootballrankings.ClassFiles;
/**
 * Stores the roster numbers
 * @author Jeff
 *
 */
public class Roster 
{
	public int teams;
	public int qbs;
	public int rbs;
	public int wrs;
	public int tes;
	
	/**
	 * Stores the roster numbers
	 */
	public Roster(int teamCt, int qbCt, int rbCt, int wrCt, int teCt)
	{
		teams = teamCt;
		qbs = qbCt;
		rbs = rbCt;
		wrs = wrCt;
		tes = teCt;
	}
	
	/**
	 * empty constructor for the initial set up
	 */
	public Roster()
	{
		teams = 0;
		qbs = 0;
		rbs = 0;
		wrs = 0;
		tes = 0;
	}
}
