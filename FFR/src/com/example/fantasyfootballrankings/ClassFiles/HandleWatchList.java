package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Handles watch list stufcf
 * @author Jeff
 *
 */
public class HandleWatchList 
{
	/**
	 * Sets up the display
	 * @param holder
	 * @param cont
	 * @param watchList
	 */
	public static void handleWatchInit(final Storage holder, final Context cont, final List<String> watchList)
	{
		final Dialog dialog = new Dialog(cont);
		dialog.setContentView(R.layout.search_output);
		dialog.show();
		if(watchList.size() == 0 || (watchList.size() > 0 && !holder.parsedPlayers.contains(watchList.get(0))))
		{
			Toast.makeText(cont, "Watch list empty", Toast.LENGTH_SHORT);
			dialog.dismiss();
			return;
		}
		Button close = (Button)dialog.findViewById(R.id.search_close);
		TextView header = (TextView)dialog.findViewById(R.id.name);
		header.setText("Watch List");
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
		ListView listWatch = (ListView)dialog.findViewById(R.id.listview_search);
	    listWatch.setAdapter(null);
	    List<String> listAdapter = new ArrayList<String>();
	    for(String name : watchList)
	    {
	    	for(PlayerObject iter : holder.players)
	    	{
	    		if(iter.info.name.equals(name))
	    		{
	    			DecimalFormat df = new DecimalFormat("#.##");
	    	    	String val = df.format(iter.values.worth);
	    	    	listAdapter.add(val + ": " + iter.info.name + ", " + iter.info.position + " - " + 
	    	    			iter.info.team);
	    	    	break;
	    		}
	    	}
	    }
	    ManageInput.handleArray(listAdapter, listWatch, (Activity) cont);
	    handleListSelect(holder, cont, watchList, listWatch, dialog);
	}

	/**
	 * Sets the element onclick to show data
	 */
	public static void handleListSelect(final Storage holder, final Context cont,final List<String> watchList, 
			ListView listview, final Dialog dialog)
	{
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String selected = ((TextView)arg1).getText().toString();
				selected = selected.split(": ")[1];
				selected = selected.split(", ")[0];
				Dialog dialog2 = new Dialog(cont);
				dialog.dismiss();
				Rankings.outputResults(dialog2, selected, true,(Activity)cont, holder, true);
			}
	    });	
		listview.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				TextView elem = (TextView)arg1;
				elem.setVisibility(TextView.GONE);
				watchList.remove(elem.getText().toString().split(": ")[1].split(", ")[0]);
				WriteToFile.writeWatchList(cont, watchList);
				if(watchList.size() == 0)
				{
					dialog.dismiss();
					Toast.makeText(cont, "No players left in the watch list", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}
}
