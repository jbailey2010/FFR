package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import FileIO.ReadFromFile;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.ffr.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
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
	public Draft(Context cont)
	{
		if(cont != null)
		{
			remainingSalary = (int)(200 / ReadFromFile.readAucFactor(cont));
		}
		else
		{
			remainingSalary = 200;
		}
		value = 0.0;
	}
	
	/**
	 * Fixes the remaining salary given a context
	 * @param cont
	 */
	public void fixRemSalaryInit(Context cont)
	{
		remainingSalary = (int)(200 / ReadFromFile.readAucFactor(cont));
	}
	
	/**
	 * Calculates the total PAA of the team
	 */
	public static double paaTotal(Draft draft)
	{
		double total = 0.0;
		for(PlayerObject player : draft.qb)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		for(PlayerObject player : draft.rb)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		for(PlayerObject player : draft.wr)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		for(PlayerObject player : draft.te)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		for(PlayerObject player : draft.def)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		for(PlayerObject player : draft.k)
		{
			if(player.values.paa != 0.0 || player.values.points != 0.0)
			{
				total += player.values.paa;
			}
		}
		return total;
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
			newPick(player.values.secWorth, paid);
		}
		else if(player.info.position.equals("RB"))
		{
			draft.rb.add(player);
			newPick(player.values.secWorth, paid);
		}
		else if(player.info.position.equals("WR"))
		{
			draft.wr.add(player);
			newPick(player.values.secWorth, paid);
		}
		else if(player.info.position.equals("TE"))
		{
			draft.te.add(player);
			newPick(player.values.secWorth, paid);
		}
		else if(player.info.position.equals("D/ST"))
		{
			draft.def.add(player);
			newPick(player.values.secWorth, paid);
		}
		else
		{
			draft.k.add(player);
			newPick(player.values.secWorth, paid);
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
		draft.remainingSalary = (int) (200 / ReadFromFile.readAucFactor(cont));
		draft.value = 0.0;
		WriteToFile.writeDraft(draft, cont);
		((Rankings)cont).intermediateHandleRankings((Activity)cont);
	}
	
	/**
	 * Kills the draft remotely (FROM HOME)
	 * @param draft
	 * @param cont
	 */
	public static void resetDraftRemote(Draft draft, Context cont)
	{
		draft.qb.clear();
		draft.rb.clear();
		draft.wr.clear();
		draft.te.clear();
		draft.def.clear();
		draft.ignore.clear();
		draft.k.clear();
		draft.remainingSalary = (int) (200/ReadFromFile.readAucFactor(cont));
		draft.value = 0.0;
		WriteToFile.writeDraft(draft, cont);
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
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		RelativeLayout base = (RelativeLayout)dialog.findViewById(R.id.info_sub_header_dark);
	    Button l = (Button)dialog.findViewById(R.id.dummy_btn_left);
	    Button r = (Button)dialog.findViewById(R.id.dummy_btn_right);
	    Button c = (Button)dialog.findViewById(R.id.dummy_btn_center);
		base.setVisibility(View.GONE);
		l.setVisibility(View.GONE);
		r.setVisibility(View.GONE);
		c.setVisibility(View.GONE);
		LinearLayout base2 = (LinearLayout)dialog.findViewById(R.id.category_base);
		base2.setVisibility(View.GONE);
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
		add.setVisibility(Button.GONE);
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
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		DecimalFormat df = new DecimalFormat("#.##");
		boolean isAuction = ReadFromFile.readIsAuction(cont);
		for(String name : holder.draft.ignore)
		{
			PlayerObject p = null;
			for(PlayerObject player : holder.players)
			{
				if(player.info.name.equals(name))
				{
					p = player;
					break;
				}
			}
			if(p == null)
			{
				//Bad shit happened
			}
			Map<String, String> datum = new HashMap<String, String>(2);
			if(isAuction)
			{
				datum.put("main", df.format(p.values.secWorth) + ":  " + p.info.name);
			}
			else
			{
				datum.put("main", df.format(p.values.ecr)+ ":  " + p.info.name );
			}
			datum.put("sub", p.info.position + " - " + p.info.team);
			data.add(datum);
		}
		final SimpleAdapter adapter = new SimpleAdapter(cont, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"main", "sub"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listWatch.setAdapter(adapter);
	    //ArrayAdapter<String> mAdapter = display(dialog, holder.draft.ignore, holder, listWatch, cont);
	    handleListSelect(holder, cont, listWatch, dialog, adapter, data);
	}
	
	/**
	 * Sets the element onclick to show data
	 * @param data 
	 */
	public static void handleListSelect(final Storage holder, final Context cont, ListView listview, 
			final Dialog dialog, final SimpleAdapter adapter, final List<Map<String, String>> data)
	{
	    SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        false, "Irrelevant", listview,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            	String name = "";
                                for (int position : reverseSortedPositions) {
                                	
                                	name = data.get(position).get("main").split(":  ")[1];//adapter.getItem(position);
                                	dialog.dismiss();
                                	undraftPlayer(name, new Dialog(cont, R.style.RoundCornersFull), holder, (Activity)cont, adapter, position, data);
                                	data.remove(position);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
	}
	
	/**
	 * Starts the undrafting of the player
	 * @param data 
	 */
	public static void undraftPlayer(final String name, final Dialog dialog, final Storage holder, 
			final Activity cont, final SimpleAdapter adapter, final int position, final List<Map<String, String>> data)
	{
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.draft_by_who);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		final boolean isAuction = ReadFromFile.readIsAuction(cont);
		Button close = (Button)dialog.findViewById(R.id.draft_who_close);
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Map<String, String> datum = new HashMap<String, String>();
				DecimalFormat df = new DecimalFormat("#.##");
				for(PlayerObject player:holder.players)
				{
					if(player.info.name.equals(name))
					{
						if(isAuction)
						{
							datum.put("main", df.format(player.values.secWorth) + ":  " + player.info.name);
						}
						else
						{
							datum.put("main", df.format(player.values.ecr)+ ":  " + player.info.name );
						}
						datum.put("sub", player.info.position + " - " + player.info.team);
						break;
					}
				}
				data.add(position, datum);
				adapter.notifyDataSetChanged();
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
				((Rankings)cont).intermediateHandleRankings(cont);
			}
		});
		Button me = (Button)dialog.findViewById(R.id.drafted_by_me);
		me.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isAuction)
				{
					draftedByMe(name, holder,cont, new Dialog(cont, R.style.RoundCornersFull), adapter, position, false, data);
				}
				else
				{
					handleUnDraftingMe(1, holder, name, cont, null);
				}
				dialog.dismiss();
			}
		});
	}
	
	   /**
     * Handles the 'drafted by me' dialog
	 * @param data 
     */
    public static void draftedByMe(final String name, final Storage holder, final Activity cont, final Dialog popup,
    		final SimpleAdapter adapter, final int position, final boolean flag, final List<Map<String, String>> data)
    {
		popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	popup.setContentView(R.layout.draft_by_me);
    	WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popup.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    popup.getWindow().setAttributes(lp);
    	popup.show();
    	TextView header = (TextView)popup.findViewById(R.id.name_header);
    	header.setText("How much did " + name + " cost?");
    	Button back = (Button)popup.findViewById(R.id.draft_who_close);
    	back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popup.dismiss();
				undraftPlayer(name, new Dialog(cont), holder, cont, adapter, position, data);
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
    	price.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
		    	int val = Integer.parseInt(((TextView)arg1).getText().toString());
				popup.dismiss();
				if(flag)
				{
					data.remove(position);
					adapter.notifyDataSetChanged();
				}
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
				if(player.info.position.equals("QB"))
				{
					holder.draft.qb.remove(player);
				}
				else if(player.info.position.equals("RB"))
				{
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
				holder.draft.value -= (player.values.secWorth - val);
				if(playersDrafted(holder.draft) == 0)
				{
					holder.draft.remainingSalary = 200;
					holder.draft.value = 0;
				}
				Toast.makeText(cont, "Undrafting " + name, Toast.LENGTH_SHORT).show();
				WriteToFile.writeDraft(holder.draft, cont);
				((Rankings)cont).intermediateHandleRankings((Activity)cont);
			}
		}
    }
}
