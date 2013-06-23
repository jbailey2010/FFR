package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.R;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.example.fantasyfootballrankings.Pages.Rankings;

import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Handles the player sorting work
 * @author Jeff
 *
 */
public class SortHandler 
{
	public static int minVal;
	public static int maxVal;
	public static String position;
	public static String subject;
	public static Storage holder;
	public static Context context;
	public static List<PlayerObject> players = new ArrayList<PlayerObject>();
	public static ArrayAdapter<String> adapter;
	
	/**
	 * Sets up the new dialog to get all the relevant info from the user
	 * @param cont
	 * @param hold
	 */
	public static void initialPopUp(Context cont, Storage hold)
	{
		players.clear();
		context = cont;
		holder = hold;
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.sort_initial); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.sort_cancel);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    final Spinner sort = (Spinner)dialog.findViewById(R.id.sort_stat_spinner);
	    final Spinner pos = (Spinner)dialog.findViewById(R.id.sort_pos_spinner);
	    final EditText max = (EditText)dialog.findViewById(R.id.maxVal);
	    final EditText min = (EditText)dialog.findViewById(R.id.minVal);
	    List<String> topics = new ArrayList<String>();
	    List<String> positions = new ArrayList<String>();
	    //Add the topics which it can sort by
	    topics.add("Risk relative to position");
	    topics.add("Risk relative to everyone");
	    topics.add("ECR");
	    topics.add("ADP");
	    topics.add("Weekly Trend");
	    topics.add("Highest Value");
	    topics.add("Lowest Value");
	    //Add the positional options
	    positions.add("All Positions");
	    positions.add("QB");
	    positions.add("RB");
	    positions.add("WR");
	    positions.add("TE");
	    positions.add("D/ST");
	    positions.add("K");
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, topics);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, 
				android.R.layout.simple_spinner_dropdown_item, positions);
		sort.setAdapter(spinnerArrayAdapter);
		pos.setAdapter(spinnerAdapter);
		Button submit = (Button) dialog.findViewById(R.id.sort_submit);
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String maxStr = max.getText().toString();
				String minStr = min.getText().toString();
				if(ManageInput.isInteger(maxStr) && ManageInput.isInteger(minStr))
				{
					minVal = Integer.parseInt(minStr);
					maxVal = Integer.parseInt(maxStr);
					if(maxVal > minVal)
					{
						position = (String)pos.getSelectedItem();
						subject = (String)sort.getSelectedItem();
						dialog.dismiss();
						handleSortingSetUp();
					}
					else
					{
						Toast.makeText(context, "Please enter a number for max that's greater than the min", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(context, "Please enter integer values for the max/min", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * Gets only the players who are the right position
	 */
	public static void handleSortingSetUp()
	{
		List<String> posList = new ArrayList<String>();
		if(position.equals("All Positions"))
		{
			posList.add("QB");
			posList.add("RB");
			posList.add("WR");
			posList.add("TE");
			posList.add("D/ST");
			posList.add("K");
		}
		else
		{
			posList.add(position);
		}
		for(PlayerObject player : holder.players)
		{
			if(posList.contains(player.info.position))
			{
				players.add(player);
			}
		}
		if(subject.equals("Risk relative to position"))
		{
			riskPos();
		}
		else if(subject.equals("Risk relative to everyone"))
		{
			riskAll();
		}
		else if(subject.equals("ECR"))
		{
			ecr();
		}
		else if(subject.equals("ADP"))
		{
			adp();
		}
		else if(subject.equals("Weekly Trend"))
		{
			weeklyTrend();
		}
		else if(subject.equals("Highest Value"))
		{
			highVal();
		}
		else if(subject.equals("Lowest Value"))
		{
			lowVal();
		}
	}
	
	/**
	 * Sets up the priority queue for risk relative to a position
	 */
	public static void riskPos()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.riskPos > b.riskPos)
				{
					return 1;
				}
				if(a.riskPos < b.riskPos)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for risk relative to all
	 */
	public static void riskAll()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.riskAll > b.riskAll)
				{
					return 1;
				}
				if(a.riskAll < b.riskAll)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for the ecr
	 */
	public static void ecr()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.ecr > b.values.ecr)
				{
					return 1;
				}
				if(a.values.ecr < b.values.ecr)
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && player.values.ecr != -1)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue for adp
	 */
	public static void adp()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(Double.parseDouble(a.info.adp) > Double.parseDouble(b.info.adp))
				{
					return 1;
				}
				if(Double.parseDouble(a.info.adp) < Double.parseDouble(b.info.adp))
				{
					return -1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && !player.info.adp.equals("Not set"))
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue based on the weekly trend in values
	 */
	public static void weeklyTrend()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(Double.parseDouble(a.info.trend) > Double.parseDouble(b.info.trend))
				{
					return -1;
				}
				if(Double.parseDouble(a.info.trend) < Double.parseDouble(b.info.trend))
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal && !player.info.trend.equals("0.0"))
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up the priority queue based on the high value
	 */
	public static void highVal()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.high > b.values.high)
				{
					return -1;
				}
				if(a.values.high < b.values.high)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Sets up a priority queue based on the low value
	 */
	public static void lowVal()
	{
		PriorityQueue<PlayerObject> sorted = new PriorityQueue<PlayerObject>(100, new Comparator<PlayerObject>()
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)
			{
				if(a.values.low > b.values.low)
				{
					return -1;
				}
				if(a.values.low < b.values.low)
				{
					return 1;
				}
				return 0;
			}
		});
		for(PlayerObject player : players)
		{
			if(player.values.worth > minVal && player.values.worth < maxVal)
			{
				sorted.add(player);
			}
		}
		wrappingUp(sorted);
	}
	
	/**
	 * Puts the priority queue into the list, and sets up the dialog
	 * @param sorted
	 */
	public static void wrappingUp(PriorityQueue<PlayerObject> sorted)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.search_output); 
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button watch = (Button)dialog.findViewById(R.id.add_watch);
	    View watchView = (View)dialog.findViewById(R.id.add_view);
	    watch.setVisibility(View.GONE);
	    watchView.setVisibility(View.GONE);
	    TextView header = (TextView)dialog.findViewById(R.id.name);
	    header.setText(subject);
	    Button back = (Button)dialog.findViewById(R.id.search_back);
	    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				initialPopUp(context, holder);
			}
	    });
	    Button close = (Button)dialog.findViewById(R.id.search_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    ListView results = (ListView)dialog.findViewById(R.id.listview_search);
	    results.setAdapter(null);
	    List<String> rankings = new ArrayList<String>(400);
	    while(!sorted.isEmpty())
	    {
	    	PlayerObject elem = sorted.poll();
			if(subject.equals("Risk relative to position"))
			{
				rankings.add(elem.riskPos + ": " + elem.info.name);
			}
			else if(subject.equals("Risk relative to everyone"))
			{
				rankings.add(elem.riskAll + ": " + elem.info.name);
			}
			else if(subject.equals("ECR"))
			{
				rankings.add(elem.values.ecr + ": " + elem.info.name);
			}
			else if(subject.equals("ADP"))
			{
				rankings.add(elem.info.adp + ": " + elem.info.name);
			}
			else if(subject.equals("Weekly Trend"))
			{
				rankings.add(elem.info.trend + ": " + elem.info.name);
			}
			else if(subject.equals("Highest Value"))
			{
				rankings.add(df.format(elem.values.high) + ": " + elem.info.name);
			}
			else if(subject.equals("Lowest Value"))
			{
				rankings.add(df.format(elem.values.low) + ": " + elem.info.name);
			}
		} 
	    adapter = ManageInput.handleArray(rankings, results, (Activity) context);
	    handleOnClicks(results);
	}
	
	/**
	 * Swipte to hide and click for more info
	 * @param results
	 */
	public static void handleOnClicks(final ListView results)
	{
		results.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				results.setSelection(arg2);
				String selected = ((TextView)arg1).getText().toString();
				selected = selected.split(": ")[1];
				PlayerInfo.outputResults(selected, true, (Rankings)context, holder, false, true);
			}
    	 });
		SwipeDismissListViewTouchListener touchListener =
                 new SwipeDismissListViewTouchListener(
                         results,
                         new SwipeDismissListViewTouchListener.OnDismissCallback() {
                             @Override
                             public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                 for (int position : reverseSortedPositions) {
                                     adapter.remove(adapter.getItem(position));
                                 }
                                 adapter.notifyDataSetChanged();
                                 Toast.makeText(context, "Temporarily hiding this player", Toast.LENGTH_SHORT).show();
                             }
                         });
         results.setOnTouchListener(touchListener);
         results.setOnScrollListener(touchListener.makeScrollListener());
	}
}
