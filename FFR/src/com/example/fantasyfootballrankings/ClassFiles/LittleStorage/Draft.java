package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
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
	 * Returns number of players drafted
	 * @param draft
	 * @return
	 */
	public static int playersDrafted(Draft draft)
	{
		return draft.qb.size() + draft.rb.size() + draft.wr.size() + draft.te.size() + draft.def.size() + draft.k.size();
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
	 * Checks if a player is drafted
	 */
	public static boolean isDrafted(String name, Draft draft)
	{
		for(PlayerObject player : draft.qb)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.rb)
		{
			System.out.println(player.info.name);
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.wr)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.te)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.def)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.k)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(String playerName : draft.ignore)
		{
			if(playerName.equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if a player is drafted by you
	 */
	public static boolean draftedMe(String name, Draft draft)
	{
		for(PlayerObject player : draft.qb)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.rb)
		{
			System.out.println(player.info.name);
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.wr)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.te)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.def)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		for(PlayerObject player : draft.k)
		{
			if(player.info.name.equals(name))
			{
				return true;
			}
		}
		return false;
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
	
	/**
	 * Starts the undrafting process
	 */
	public static void undraft(final Dialog dialog, final Storage holder, final Context cont)
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.search_output);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		if(holder.draft.ignore.size() == 0)
		{
			Toast.makeText(cont, "No one drafted", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			return;
		}
		Button close = (Button)dialog.findViewById(R.id.search_close);
		TextView header = (TextView)dialog.findViewById(R.id.name);
		header.setText("Select the player to undraft");
		Button add = (Button)dialog.findViewById(R.id.add_watch);
		View addView = (View)dialog.findViewById(R.id.add_view);
		add.setVisibility(Button.GONE);
		addView.setVisibility(View.GONE);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button back = (Button)dialog.findViewById(R.id.search_back);
		back.setText("Reset draft");
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				resetDraft(holder.draft, holder, cont);
			}
		});
		ListView listWatch = (ListView)dialog.findViewById(R.id.listview_search);
	    ArrayAdapter<String> mAdapter = display(dialog, holder.draft.ignore, holder, listWatch, cont);
	    handleListSelect(holder, cont, listWatch, dialog, mAdapter);
	}
	
	/**
	 * Sets the display of the watch list
	 */
	public static ArrayAdapter<String> display(Dialog dialog, List<String> ignore, Storage holder, ListView listWatch,
			Context cont)
	{
		listWatch.setAdapter(null);
		List<String> totalList = new ArrayList<String>();
		for(String player : ignore)
		{
			totalList.add(player);
		}
	    return ManageInput.handleArray(totalList, listWatch, (Activity) cont);
	}
	
	/**
	 * Sets the element onclick to show data
	 */
	public static void handleListSelect(final Storage holder, final Context cont, ListView listview, 
			final Dialog dialog, final ArrayAdapter<String> mAdapter)
	{
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String selected = ((TextView)arg1).getText().toString();
				Dialog dialog2 = new Dialog(cont);
				dialog.dismiss();
				undraftPlayer(selected, dialog2, holder, (Activity)cont);
			}
	    });	
	}
	
	/**
	 * Starts the undrafting of the player
	 */
	public static void undraftPlayer(final String name, final Dialog dialog, final Storage holder, 
			final Activity cont)
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.draft_by_who);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		Button close = (Button)dialog.findViewById(R.id.draft_who_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				undraft(new Dialog(cont), holder, cont);
			}
		});
		TextView header = (TextView)dialog.findViewById(R.id.name_header);
		header.setText("Who drafted " + name + "?");
		Button someone = (Button)dialog.findViewById(R.id.drafted_by_someone);
		someone.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				holder.draft.ignore.remove(name);
				WriteToFile.writeDraft(holder.draft, cont);
				Toast.makeText(cont, "Undrafting " + name, Toast.LENGTH_SHORT).show();
				Rankings.intermediateHandleRankings(cont);
			}
		});
		Button me = (Button)dialog.findViewById(R.id.drafted_by_me);
		me.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				System.out.println("Clicked");
				draftedByMe(name, holder,cont, new Dialog(cont));
				dialog.dismiss();
			}
		});
	}
	
	   /**
     * Handles the 'drafted by me' dialog
     */
    public static void draftedByMe(final String name, final Storage holder, final Activity cont, final Dialog popup)
    {
    	popup.setContentView(R.layout.draft_by_me);
    	popup.show();
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	header.setText("How much did " + name + " cost?");
    	Button back = (Button)popup.findViewById(R.id.draft_who_close);
    	back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popup.dismiss();
				undraftPlayer(name, new Dialog(cont), holder, cont);
			}
    	});
    	List<String> possResults = new ArrayList<String>();
    	for(int i = 1; i < 201; i++)
    	{
    		possResults.add(String.valueOf(i));
    	}
    	AutoCompleteTextView price = (AutoCompleteTextView)popup.findViewById(R.id.amount_paid);
    	ArrayAdapter<String> doubleAdapter = new ArrayAdapter<String>(cont,
                android.R.layout.simple_dropdown_item_1line, possResults);
    	price.setAdapter(doubleAdapter);
    	price.setThreshold(1);
    	System.out.println("After threshold");
    	price.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
		    	int val = Integer.parseInt(((TextView)arg1).getText().toString());
				popup.dismiss();
				handleUnDraftingMe(val, holder, name, cont, popup);
			}
    	});
    }
    
    /**
     * Handles undrafting a player 
     */
    public static void handleUnDraftingMe(int val, Storage holder, String name, Context cont, Dialog popup)
    {
		for(PlayerObject player : holder.players)
		{
			if(player.info.name.equals(name))
			{
				System.out.println("in if");
				if(player.info.position.equals("QB"))
				{
					holder.draft.qb.remove(player);
				}
				else if(player.info.position.equals("RB"))
				{
					System.out.println("in rb");
					holder.draft.rb.remove(player);
				}
				else if(player.info.position.equals("WR"))
				{
					holder.draft.wr.remove(player);
				}
				else if(player.info.position.equals("TE"))
				{
					holder.draft.te.remove(player);
				}
				else if(player.info.position.equals("D/ST"))
				{
					holder.draft.def.remove(player);
				}
				else
				{
					holder.draft.k.remove(player);
				}
				holder.draft.ignore.remove(name);
				holder.draft.remainingSalary += val;
				if(holder.draft.remainingSalary > 200)
				{
					holder.draft.remainingSalary = 200;
				}
				holder.draft.value -= (player.values.worth - val);
				if(playersDrafted(holder.draft) == 0)
				{
					holder.draft.remainingSalary = 200;
					holder.draft.value = 0;
				}
				Toast.makeText(cont, "Undrafting " + name, Toast.LENGTH_SHORT).show();
				WriteToFile.writeDraft(holder.draft, cont);
				Rankings.intermediateHandleRankings((Activity)cont);
			}
		}
    }
}
