package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import jeff.isawesome.fantasyfootballrankings.R;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.socialize.Socialize;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

/**
 * Sets up the trending page
 * @author Jeff
 * 
 */ 
public class Trending extends Activity {
	//Some globals, the dialog and the context
	Dialog dialog;
	final Context cont = this;
	static Context context;
	Button day;
	Button week;
	Button month;
	Button all;
	static Storage holder = new Storage();
	static BounceListView listview;
	long start;
	static boolean refreshed = false;
	int lastFilter;
	public static SimpleAdapter mAdapter;
	public static List<Map<String, String>> data;
	/**
	 * Sets up the dialog to show up immediately
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		ActionBar ab = getActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		//Fetch the date of the posts, and convert it to a date
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	listview = (BounceListView)findViewById(R.id.listview_trending);
		context = this;
		initialLoad(prefs);		 
		try {
			handleDates(prefs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Call Socialize in onPause
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Call Socialize in onResume
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// Call Socialize in onDestroy before the activity is destroyed
		Socialize.onDestroy(this);
		
		super.onDestroy();
	}	
	/**
	 * Sets up the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trending, menu);
		return true;
	}
 
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.filter_quantity_menu:
				if(holder.players.size() < 10)
				{
					Toast.makeText(cont, "Can't limit quantity until the rankings are loaded", Toast.LENGTH_SHORT).show();
				}
				else
				{
					filterQuantity();
				}
				return true;
			case R.id.filter_topics_menu:
				topicalTrending(holder);
				return true;
			case R.id.news:
		        Intent intent_news = new Intent(cont, News.class);
		        cont.startActivity(intent_news);		
 		        return true;
			case R.id.help:
				helpDialog();
				return true;
			case R.id.go_home:
				Intent home_intent = new Intent(cont, Home.class);
				cont.startActivity(home_intent);		
		        return true;
			case R.id.view_rankings:
		        Intent intent = new Intent(cont, Rankings.class);
		        cont.startActivity(intent);		
 		        return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	/**
	 * Handles the help dialog
	 */
	public void helpDialog() {
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.help_trending);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.help_trending_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
	    });
	}
	
	/**
	 * Handles the initial load of trending players on creation
	 * @param prefs
	 */
	public void initialLoad(SharedPreferences prefs)
	{
		View v = findViewById(android.R.id.home);
		v.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				listview.smoothScrollToPosition(0);		
			}
		});
		if(holder.playerNames == null || holder.playerNames.size() < 5)
		{
			ReadFromFile.fetchNamesBackEnd(holder, cont);
		}
		String storedPosts = prefs.getString("Posted Players", "Not Posted");
		if(!storedPosts.equals("Not Posted"))
		{ 
			List<String>postsList = new ArrayList<String>();
			data = new ArrayList<Map<String, String>>();
			for(String post : storedPosts.split("##"))
			{
				try{
					String[] nameSet = post.split(": mentioned ");
					Map<String, String> datum = new HashMap<String, String>(2);
					String name = nameSet[0];
					String count = (nameSet[1].split(" times")[0]);
					datum.put("name", name);
					String countStr = count + " times";
					datum.put("count", countStr); 
					data.add(datum);
					postsList.add(post);
				} catch(ArrayIndexOutOfBoundsException e)
				{
					continue;
				}
			}
    		 mAdapter = new SimpleAdapter(cont, data, 
    		    		android.R.layout.simple_list_item_2, 
    		    		new String[] {"name", "count"}, 
    		    		new int[] {android.R.id.text1, 
    		    			android.R.id.text2});
   		    listview.setAdapter(mAdapter); 
    		SwipeDismissListViewTouchListener touchListener =
    				new SwipeDismissListViewTouchListener(
    						listview,
	                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
    							@Override
	                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
    								String name = "";
	                                for (int position : reverseSortedPositions) {
	                                   	Map<String, String> datum = new HashMap<String, String>(2);
	                                   	datum = data.get(position);
	                                   	name = datum.get("name");
	                                    data.remove(mAdapter.getItem(position));
	                                }
	                                mAdapter.notifyDataSetChanged();
	                                Toast.makeText(cont, "Temporarily hiding " + name, Toast.LENGTH_SHORT).show();
	                            }
	                        });
	        listview.setOnTouchListener(touchListener);
	        listview.setOnScrollListener(touchListener.makeScrollListener());
	    	setListViewOnClick();
	    }
	}
	
	/**
	 * Handles possible refreshing of trending player data
	 * @param prefs
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws XPatherException 
	 * @throws IOException 
	 */
	public void handleDates(SharedPreferences prefs) throws IOException, XPatherException, InterruptedException, ExecutionException
	{
    	//Get the posts, if they're not set, fetch them. Otherwise, get from storage.
    	String checkExists = prefs.getString("Posts", "Not Set");
    	if(checkExists.equals("Not Set"))
    	{
    		Toast.makeText(context, "Please select Filter Topics from the menu to see trending players", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		ReadFromFile.fetchPostsLocal(holder, cont);
    		if(holder.playerNames.size() < 19)
    		{
    			ReadFromFile.fetchNamesBackEnd(holder, cont);
    		}
    		if(holder.players.size() < 10)
    		{
    	    	String checkExists2 = prefs.getString("Player Values", "Not Set");
    	    	if(checkExists2 != "Not Set")
    	    	{
    				ReadFromFile.fetchPlayers(checkExists2, holder,cont, 2);
    	    	}

    		}  
    		else
    		{
    			setNoInfo(this, holder);
    		}
    	}
   		getFilterForPosts(holder); 
	}
	
	/**
	 * Handles the topical trending, once it's done it 
	 * refreshes the rankings based on the input here
	 * @param holder
	 */
	public void topicalTrending(final Storage holder)
	{
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trending_topic_filter);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		dialog.show();
		final CheckBox value = (CheckBox)dialog.findViewById(R.id.value_box);
		final CheckBox rookie = (CheckBox)dialog.findViewById(R.id.rookies);
		final CheckBox want = (CheckBox)dialog.findViewById(R.id.must_haves);
		final CheckBox dontWant = (CheckBox)dialog.findViewById(R.id.dont_want);
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		boolean valueSet =  prefs.getBoolean("Value Topic", true);
		boolean mustHaveSet = prefs.getBoolean("Good Topic", true);
		boolean rookieSet = prefs.getBoolean("Rookie Topic", true);
		boolean dontWantSet = prefs.getBoolean("Bad Topic", false);
		value.setChecked(valueSet);
		want.setChecked(mustHaveSet);
		rookie.setChecked(rookieSet);
		dontWant.setChecked(dontWantSet);
    	final SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		Button submit = (Button)dialog.findViewById(R.id.trending_filter_submit);
		submit.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	day.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	week.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	month.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
            	all.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
				Boolean valueChecked = value.isChecked();
				Boolean rookieChecked = rookie.isChecked();
				Boolean wantChecked = want.isChecked();
				Boolean dontWantChecked = dontWant.isChecked();
				editor.putBoolean("Value Topic", valueChecked);
				editor.putBoolean("Rookie Topic", rookieChecked);
				editor.putBoolean("Good Topic", wantChecked);
				editor.putBoolean("Bad Topic", dontWantChecked);
				editor.commit();
				dialog.dismiss();
				if(data == null)
				{
					data = new ArrayList<Map<String, String>>();
				}
				else
				{
					data.clear();
				}
				holder.posts.clear();
				fetchTrending(holder);
				listview.setAdapter(null);
            }
		});
		Button cancel = (Button)dialog.findViewById(R.id.trending_filter_cancel);
		cancel.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
				dialog.dismiss();
            }
		});
	} 
	
	/**
	 * Just because this has to be called twice, abstracted.
	 * Makes the loading dialog, and in a side thread calls the back
	 * function so both could happen. Yay, hackiness.
	 * @param holder
	 */
	public void fetchTrending(final Storage holder)
	{
		try {
			ParseTrending.trendingPlayers(holder, cont);
			listview.setAdapter(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * Calls the function that handles filtering 
	 * quantity size
	 */
	public void filterQuantity()
	{
		ManageInput.filterQuantity(cont, "Trending", holder.posts.size());	
	}
	
	/**
	 * Waits for a click of a button to get a filter
	 * timeframe
	 * @param holder
	 */
	private void getFilterForPosts(final Storage holder) 
	{
        day = (Button)findViewById(R.id.filter_day);
        setOnClicks(day, 1);     
        week = (Button)findViewById(R.id.filter_week);
        setOnClicks(week, 7);   
        month = (Button)findViewById(R.id.filter_month);
        setOnClicks(month, 30);
        all = (Button)findViewById(R.id.filter_all);
        setOnClicks(all, 365);
		lastFilter = ReadFromFile.readLastFilter(cont);
		if(lastFilter == 1)
		{
			day.setBackgroundColor(Color.BLACK);
		}
		if(lastFilter == 7)
		{
			week.setBackgroundColor(Color.BLACK);
		}		
		if(lastFilter == 30)
		{
			month.setBackgroundColor(Color.BLACK);
		}		
		if(lastFilter == 365)
		{
			all.setBackgroundColor(Color.BLACK);
		}
	}
	
	/**
	 * Handles setting of onclick listeners
	 * @param button
	 * @param filterSize
	 */
	public void setOnClicks(final Button button, final int filterSize)
	{
		button.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	if(holder.posts.size() > 0)
            	{
	            	day.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	week.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	month.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	all.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_btn_black));
	            	button.setBackgroundColor(Color.BLACK);
					try {
						WriteToFile.writeLastFilter(cont, filterSize);
						resetTrendingList(filterSize, cont);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					day.setClickable(false);
					month.setClickable(false);
					week.setClickable(false);
					all.setClickable(false);
            	}
            	else
            	{
            		Toast.makeText(cont, "Select filter topics to get the posts before you can do this", Toast.LENGTH_SHORT).show();
            	}
            }
		});
	}
	
	/**
	 * Refreshes the list based on the selected timeframe
	 * @param filterSize
	 * @param cont
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void resetTrendingList(int filterSize, Context cont) throws ParseException, IOException
	{
		refreshed = true;
		data.clear();
		holder.postedPlayers.clear();
		ParseTrending.setUpLists(holder, filterSize, cont);
	}
	
	
	/**
	 * Handles the middle ground before setting the listView
	 * @param holder
	 * @param cont
	 */
	public void intermediateHandleTrending(Storage holder, Activity cont)
	{
		day.setClickable(true);
		week.setClickable(true);
		month.setClickable(true);
		all.setClickable(true);
		int maxSize = ReadFromFile.readFilterQuantitySize((Context)cont, "Trending");
		PriorityQueue<PostedPlayer>finalList = new PriorityQueue<PostedPlayer>(300, new Comparator<PostedPlayer>() 
		{
			@Override
			public int compare(PostedPlayer a, PostedPlayer b) 
			{
				if (a.count > b.count)
			    {
			        return -1;
			    }
			    if (a.count < b.count)
			    {
			    	return 1;
			    }
			    return 0;
			}
		});
		int total = holder.postedPlayers.size();
		double fraction = (double)maxSize * 0.01;
		double newSize = total * fraction;
		for(int i = 0; i < newSize; i++)
		{
			finalList.add(holder.postedPlayers.poll());
		}
		holder.postedPlayers.clear();
		handleParsed(finalList, holder, cont);
	}
	
	/**
	 * Does sexy things
	 * @param holder
	 */
	public void handleParsed(PriorityQueue<PostedPlayer> playersTrending, Storage holder, final Activity cont)
	{
	    listview = (BounceListView) cont.findViewById(R.id.listview_trending);
	    listview.setAdapter(null);
	    data = new ArrayList<Map<String, String>>();
	    setListViewOnClick();
	    List<String> trendingPlayers = new ArrayList<String>(350);
	    while(!playersTrending.isEmpty())
	    {
	    	PostedPlayer elem = playersTrending.poll();
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", elem.name);
	    	datum.put("count", elem.count + " times");
	    	data.add(datum);
	    	trendingPlayers.add(elem.name + ": mentioned " + elem.count + " times");
	    }
	    if(data.size() == 0)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", "No players mentioned in this timeframe");
	    	datum.put("count", "Please try something else");
	    	trendingPlayers.add("No players mentioned in this timeframe" + ": mentioned " + "Please try something else" + " times");
	    	data.add(datum);
	    }
	    if(refreshed)
	    {
	    	WriteToFile.writePostsList(trendingPlayers, cont);
	    	refreshed = false;
	    } 
	    //final ArrayAdapter<String> mAdapter = ManageInput.handleArray(trendingPlayers, listview, cont);
	    mAdapter = new SimpleAdapter(cont, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"name", "count"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    listview.setAdapter(mAdapter);
	    SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listview,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                            	String name = "";
                                for (int position : reverseSortedPositions) {
                                	Map<String, String> datum = new HashMap<String, String>(2);
                                	datum = data.get(position);
                                	name = datum.get("name");
                                    data.remove(mAdapter.getItem(position));
                                }
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(cont, "Temporarily hiding " + name, Toast.LENGTH_SHORT).show();
                            }
                        });
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
    	setNoInfo(cont, holder);
	}
	
	/**
	 * handles what happens on click of the item in the list
	 */
	public static void setListViewOnClick()
	{
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listview.setSelection(arg2);
				String selected = ((TwoLineListItem)arg1).getText1().getText().toString();
				int index = -1;
				for(int i = 0; i < holder.players.size(); i++)
				{
					PlayerObject player = holder.players.get(i);
					if(player.info.name.equals(selected))
					{
						index = i;
					}
				}
				if(index == -1 && holder.players.size() > 10)
				{
					Toast.makeText(context, "Information not found, player not parsed. Possibly a defensive player?", Toast.LENGTH_SHORT).show();
				}
				else if(index == -1)
				{
					Toast.makeText(context, "Can't fetch player information until rankings are fetched", Toast.LENGTH_SHORT).show();
				}
				else
				{
					PlayerInfo.outputResults(selected, true, (Trending)context, holder, false, false);
				}
			}
	    });
	}


	/**
	 * Adds to the adapter if the player isn't available in the storage object
	 * @param act
	 */
	public static void setNoInfo(Activity act, Storage hold) {
		for(Map<String, String> datum : data)
		{ 
			if(!hold.parsedPlayers.contains(datum.get("name")))
			{
				datum.put("count", datum.get("count") + "\nPlayer Information Unavailable");
				mAdapter.notifyDataSetChanged();
			}
		} 
	}


	/**
	 * Sets the listview to say the posts are fetched
	 * @param act
	 */
	public static void setContent(Activity act) {
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    	Map<String, String> datum = new HashMap<String, String>(2);
    	datum.put("name", "The posts are fetched");
    	datum.put("count", "Select a timeframe from above");
    	data.add(datum);
    	mAdapter = new SimpleAdapter(act, data, 
	    		android.R.layout.simple_list_item_2, 
	    		new String[] {"name", "count"}, 
	    		new int[] {android.R.id.text1, 
	    			android.R.id.text2});
	    listview.setAdapter(mAdapter);
	}
}