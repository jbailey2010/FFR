package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import FileIO.WriteToFile;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.Pages.Rankings;

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
	public List<String> ignore = new ArrayList<String>();
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
	 * Sets up when a player is drafted
	 */
	public void draftPlayer(PlayerObject player, Draft draft, int paid, Context cont)
	{
		if(player.info.position.equals("QB"))
		{
			draft.qb.add(player);
			newPick(player.values.worth, paid);
		}
		else if(player.info.position.equals("RB"))
		{
			draft.rb.add(player);
			newPick(player.values.worth, paid);
		}
		else if(player.info.position.equals("WR"))
		{
			draft.wr.add(player);
			newPick(player.values.worth, paid);
		}
		else if(player.info.position.equals("TE"))
		{
			draft.te.add(player);
			newPick(player.values.worth, paid);
		}
		else if(player.info.position.equals("D/ST"))
		{
			draft.def.add(player);
			newPick(player.values.worth, paid);
		}
		else
		{
			draft.k.add(player);
			newPick(player.values.worth, paid);
		}
		WriteToFile.writeDraft(draft, cont);
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
	
	/**
	 * Resets the draft data
	 */
	public static void resetDraft(Draft draft, Storage holder, Context cont)
	{
		Toast.makeText(cont, "Draft information reset", Toast.LENGTH_SHORT).show();
		draft.qb.clear();
		draft.rb.clear();
		draft.wr.clear();
		draft.te.clear();
		draft.def.clear();
		draft.ignore.clear();
		draft.k.clear();
		draft.remainingSalary = 200;
		draft.value = 0.0;
		WriteToFile.writeDraft(draft, cont);
		Rankings.intermediateHandleRankings((Activity)cont);
	}
}
