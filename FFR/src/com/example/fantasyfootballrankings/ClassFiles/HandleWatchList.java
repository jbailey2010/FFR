package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.ffr.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.Pages.Trending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Handles watch list
 * @author Jeff
 *
 */
public class HandleWatchList 
{
	static boolean selected = false;
	public static List<Map<String, String>> dataSet;
	/**
	 * Sets up the display
	 * @param holder
	 * @param cont
	 * @param watchList
	 */
	public static void handleWatchInit(final Storage holder, final Context cont, final List<String> watchList)
	{
		final Dialog dialog = new Dialog(cont);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.search_output);
		final ListView listWatch = (ListView)dialog.findViewById(R.id.listview_search);
		dialog.show();
		RelativeLayout base = (RelativeLayout)dialog.findViewById(R.id.info_sub_header);
		base.setVisibility(View.GONE);
		LinearLayout base2 = (LinearLayout)dialog.findViewById(R.id.category_base);
		base2.setVisibility(View.GONE);
		if(watchList.size() == 0 || (watchList.size() > 0 && !holder.parsedPlayers.contains(watchList.get(0))))
		{
			Toast.makeText(cont, "Watch list empty", Toast.LENGTH_SHORT).show();
			dialog.dismiss();
			return;
		} 
		Button close = (Button)dialog.findViewById(R.id.search_close);
		TextView header = (TextView)dialog.findViewById(R.id.name);
		header.setText("Watch List");
		Button add = (Button)dialog.findViewById(R.id.add_watch);
		add.setText("Hide Drafted");
		add.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			    selected = !selected;
			    if(!selected)
			    {
			    	Toast.makeText(cont, "Showing drafted players", Toast.LENGTH_SHORT).show();
			    }
			    else
			    {
			    	Toast.makeText(cont, "Hiding drafted players", Toast.LENGTH_SHORT).show();
			    }
			    display(dialog, watchList, holder, listWatch, cont);
			}
		});
		close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button back = (Button)dialog.findViewById(R.id.search_back);
		back.setText("Clear Watch List");
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(watchList.size() > 0 && holder.parsedPlayers.contains(watchList.get(0)))
				{
					Toast.makeText(cont, "Watch List Cleared", Toast.LENGTH_SHORT).show();
					watchList.clear();
					WriteToFile.writeWatchList(cont, watchList);
					dialog.dismiss();
					return;
				}
				else
				{
					Toast.makeText(cont, "No players in the list to clear", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    SimpleAdapter mAdapter = display(dialog, watchList, holder, listWatch, cont);
	    handleListSelect(holder, cont, watchList, listWatch, dialog, mAdapter);
	}
	
	/**
	 * Sets the display of the watch list
	 */
	public static SimpleAdapter display(Dialog dialog, List<String> watchList, Storage holder, ListView listWatch,
			Context cont)
	{
		listWatch.setAdapter(null);
		PriorityQueue<PlayerObject>totalList = null;
		boolean isAuction = ReadFromFile.readIsAuction(cont);
		if(isAuction)
		{
			totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
			{
				@Override
				public int compare(PlayerObject a, PlayerObject b) 
				{
					if (a.values.worth > b.values.worth)
				    {
				        return -1;
				    }
				    if (a.values.worth < b.values.worth)
				    {
				    	return 1;
				    }
				    return 0;
				}
			});
		}
		else
		{
			totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
					{
						@Override
						public int compare(PlayerObject a, PlayerObject b) 
						{
							if (a.values.ecr > b.values.ecr)
						    {
						        return 1;
						    }
						    if (a.values.ecr < b.values.ecr)
						    {
						    	return -1;
						    }
						    return 0;
						}
					});
		}
		
		dataSet = new ArrayList<Map<String, String>>();
	    List<String> listAdapter = new ArrayList<String>();
	    for(String name : watchList)
	    {
	    	for(PlayerObject iter : holder.players)
	    	{
	    		if(iter.info.name.equals(name))
	    		{
	    			totalList.add(iter);
	    	    	break;
	    		}
	    	}
	    }
	    while(!totalList.isEmpty())
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	PlayerObject iter = totalList.poll();
			DecimalFormat df = new DecimalFormat("#.##");
			String val = "";
			if(!holder.isRegularSeason)
			{
				if(isAuction)
				{
					val = df.format(iter.values.secWorth);
				}
				else
				{
					val = df.format(iter.values.ecr);
				}
			}
			else
			{
				val = df.format(iter.values.points);
			}
			if(selected)
	    	{
	    		if(Draft.isDrafted(iter.info.name, holder.draft))
	    		{
	    			continue;
	    		}
		    	else
		    	{
			    	datum.put("name", val + ": " + iter.info.name);
			    	datum.put("info", iter.info.position + " - " + iter.info.team);
		    	}
	    	}
	    	else
	    	{
		    	if(Draft.draftedMe(iter.info.name, holder.draft))
		    	{
			    	datum.put("name", "DRAFTED (YOU) - " + val + ": " + iter.info.name);
			    	datum.put("info", iter.info.position + " - " + iter.info.team);

		    	}
		    	else if(Draft.isDrafted(iter.info.name, holder.draft))
		    	{
		    		datum.put("name", "DRAFTED - " + val + ": " + iter.info.name);
			    	datum.put("info", iter.info.position + " - " + iter.info.team);
		    	}
		    	else
		    	{
		    		datum.put("name", val + ": " + iter.info.name);
			    	datum.put("info", iter.info.position + " - " + iter.info.team);
		    	}
	    	}
	    	dataSet.add(datum);
	    }
	    final SimpleAdapter adapter = new SimpleAdapter(cont, dataSet, 
	    		R.layout.web_listview_item, 
	    		new String[] {"name", "info"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listWatch.setAdapter(adapter);
	    return adapter;
	}

	/**
	 * Sets the element onclick to show data
	 */
	public static void handleListSelect(final Storage holder, final Context cont,final List<String> watchList, 
			ListView listview, final Dialog dialog, final SimpleAdapter mAdapter)
	{
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String nameText = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				String selected = nameText.split(": ")[1];
				dialog.dismiss();
				PlayerInfo obj = new PlayerInfo();
				obj.outputResults(selected, true,(Activity)cont, holder, true, true);
			}
	    });	
	    SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listview,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            	String name = "";
                                for (int position : reverseSortedPositions) {
                                	Map<String, String> in = dataSet.remove(position);
                                	name = in.get("name").split(": ")[1];
                                }
                                mAdapter.notifyDataSetChanged();
                                watchList.remove(name);
                                WriteToFile.writeWatchList(cont, watchList);
                                if(watchList.size() == 0)
                				{
                					dialog.dismiss();
                					Toast.makeText(cont, "No players left in the watch list", Toast.LENGTH_SHORT).show();
                				}
                                Toast.makeText(cont, "Removing " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
	}
}
