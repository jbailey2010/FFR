package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;

/**
 * Handles the specific position data for a specific team, breaking into tiers, whole sets,
 * and classifications based on tier data
 * @author Jeff
 *
 */
public class PosTradeInfo 
{
	public List<PlayerObject> wholeRoster;
	public List<PlayerObject> tier1;
	public List<PlayerObject> tier2;
	public List<PlayerObject> tier3;
	public List<PlayerObject> rest;
	public String classification;
	
	/**
	 * Dummy initialization 
	 */
	public PosTradeInfo()
	{
		wholeRoster = new ArrayList<PlayerObject>();
		tier1 = new ArrayList<PlayerObject>();
		tier2 = new ArrayList<PlayerObject>();
		tier3 = new ArrayList<PlayerObject>();
		rest = new ArrayList<PlayerObject>();
		classification = "";
	}
}
