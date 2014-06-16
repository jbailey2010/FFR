package com.example.fantasyfootballrankings.Pages;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import com.ffr.fantasyfootballrankings.R;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.PlayerInfoActivity;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.OutBounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;
import com.socialize.EntityUtils;
import com.socialize.Socialize;
import com.socialize.entity.EntityStats;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Sets up the trending page
 * @author Jeff
 * 
 */ 
public class Trending extends Activity {
	//Some globals, the dialog and the context
	Dialog dialog;
	Context cont;
	static Context context;
	Button day;
	Button week;
	Button month;
	Button all;
	static Storage holder = new Storage(null);
	static OutBounceListView listview;
	long start;
	static boolean refreshed = false;
	int lastFilter;
	public static SimpleAdapter mAdapter;
	public static List<Map<String, String>> data;
	public static SideNavigationView sideNavigationView;
	/**
	 * Sets up the dialog to show up immediately
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trending);
		cont = this;
		ISideNavigationCallback sideNavigationCallback = new ISideNavigationCallback() {
		    @Override
		    public void onSideNavigationItemClick(int itemId) {
		    	switch (itemId) {
		    	case R.id.side_navigation_menu_item0:
	            	listview.smoothScrollToPosition(0);
	                break;
	            case R.id.side_navigation_menu_item1:
	            	Intent intent = new Intent(cont, Home.class);
	    	        cont.startActivity(intent);	
	                break;
	            case R.id.side_navigation_menu_item2:
	            	Intent intent2 = new Intent(cont, Rankings.class);
	    	        cont.startActivity(intent2);	
	                break;
	            case R.id.side_navigation_menu_item3:
	            	/*
	            	Intent intent5 = new Intent(cont, ImportLeague.class);
	    	        cont.startActivity(intent5);
	    	        */
	            	Toast.makeText(cont, "Still in development, should be available soon!", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.side_navigation_menu_item4:
	            	Intent intent3 = new Intent(cont, Trending.class);
	    	        cont.startActivity(intent3);		
	                break;
	            case R.id.side_navigation_menu_item5:
	            	Intent intent4 = new Intent(cont, News.class);
	    	        cont.startActivity(intent4);
	                break;
	            case R.id.side_navigation_menu_item6:
	            	Intent intent6 = new Intent(cont, DraftHistory.class);
	    	        cont.startActivity(intent6);
	                break;
	            case R.id.side_navigation_menu_item7:
	            	PlayerInfoActivity.displayStats(cont);
	            	break;
	            case R.id.help:
	            	ManageInput.generalHelp(cont);
	            	break;
	            default:
	                return;
		    	}
		    }
		};
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view_trending);
	    sideNavigationView.setMenuItems(R.menu.side_navigation_view);
	    sideNavigationView.setMenuClickCallback(sideNavigationCallback);
	   // sideNavigationView.setMode(/*SideNavigationView.Mode*/);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		//ab.setDisplayShowHomeEnabled(false);
		//Fetch the date of the posts, and convert it to a date
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	listview = (OutBounceListView)findViewById(R.id.listview_trending);
		context = this;
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
		initialLoad(prefs);
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
			case android.R.id.home:
		        sideNavigationView.toggleMenu();
		        return true;
			case R.id.filter_quantity_menu:
				if(holder.players.size() < 10)
				{
					Toast.makeText(cont, "Can't limit quantity until the rankings are loaded", Toast.LENGTH_SHORT).show();
				}
				else if(holder.posts == null || holder.posts.size() == 0){
					Toast.makeText(cont, "You have to fetch the posts (press filter) to do this", Toast.LENGTH_SHORT).show(); 
				}
				else
				{
					filterQuantity();
				}
				return true;
			case R.id.filter_topics_menu:
				topicalTrending(holder);
				return true;
			case R.id.help:
				helpDialog();
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
	
	public static void setHolder(Storage result)
	{
		holder.posts = result.posts;
	}
	
	/**
	 * Handles the initial load of trending players on creation
	 * @param result 
	 * @param prefs
	 */
	public void initialLoad(SharedPreferences prefs)
	{
		if(holder.playerNames == null || holder.playerNames.size() < 5)
		{
			ReadFromFile.fetchNamesBackEnd(holder, cont);
		}
		String storedPosts = prefs.getString("Posted Players", "Not Posted");
		Boolean lastEmpty = prefs.getBoolean("Last Empty", false);

		if(!storedPosts.equals("Not Posted"))
		{ 
			List<String>postsList = new ArrayList<String>();
			data = new ArrayList<Map<String, String>>();
			if(lastEmpty)
			{
				Map<String, String> datum = new HashMap<String, String>(2);
				datum.put("name", "No posts were found with the selection you made.");
				datum.put("count", "It's possible that thread is not yet available.");
				data.add(datum);
			}
			else
			{
				String[] posts = ManageInput.tokenize(storedPosts, '#', 2);
				mAdapter = new SimpleAdapter(cont, data, 
			    		R.layout.web_listview_item, 
			    		new String[] {"name", "count"}, 
			    		new int[] {R.id.text1, 
			    			R.id.text2});
				for(String post : posts)
				{
					try{
						String[] nameSet = post.split(": mentioned ");
						Map<String, String> datum = new HashMap<String, String>(2);
						final String name = nameSet[0];
						final StringBuilder sec = new StringBuilder();
						sec.append(nameSet[1].split("//")[1]);
						datum.put("name", name + "");
						datum.put("count", sec.toString());
						PlayerObject match = new PlayerObject();
						for(PlayerObject player : holder.players)
						{
							if(player.info.name.equals(name))
							{
								match = player;
								break;
							}
						}
						if(match.info != null && match.info.name != null)
						{
							EntityUtils.getEntity(this, "http://www.ffr.com/" + match.info.name + ", " + match.info.position + "/pi" + Home.yearKey, new EntityGetListener() {
					    		@Override
					    		public void onError(SocializeException error) {
					    			if(isNotFoundError(error)) {
					    				// No entity found
					    			}
					    			else {
					    				// lol at handle error
					    			}
					    		}
	
								@Override
								public void onGet(com.socialize.entity.Entity result) {
									EntityStats es = result.getEntityStats();
									int views = es.getViews();
									for(Map<String, String> elem : data)
									{
										if(elem.get("name").equals(name))
										{
											elem.put("count", elem.get("count") + "\n" + views + " total player views");
											mAdapter.notifyDataSetChanged();
											break;
										}
									}
								}
					    	});
						}
						data.add(datum);
						postsList.add(post);
					} catch(ArrayIndexOutOfBoundsException e)
					{
						continue;
					}
				}
			}
   		    listview.setAdapter(mAdapter); 
    		SwipeDismissListViewTouchListener touchListener =
    				new SwipeDismissListViewTouchListener(
    						true, "Trending", listview,
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
		else{
			if(holder.posts.size() != 0){
				Toast.makeText(cont, "The posts are saved, but not the players. Select a time frame above", Toast.LENGTH_SHORT).show();
			}
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

    	}
		if(holder.playerNames.size() < 19)
		{
			ReadFromFile.fetchNamesBackEnd(holder, cont);
		}
		if(holder.players.size() < 10 || prefs.getBoolean("Home Update Trending", false) || prefs.getBoolean("Rankings Update Trending", false))
		{
			if(Home.holder.players != null && Home.holder.players.size() > 5)
			{
				holder = Home.holder;
			}
			else
			{
				SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
				editor.putBoolean("Home Update Trending", false).apply();
				editor.putBoolean("Rankings Update Trending", false).apply();
				Set<String> checkExists2 = prefs.getStringSet("Player Values", null);
		    	if(checkExists2 != null)
		    	{
					ReadFromFile.fetchPlayers(checkExists2, holder,cont, 2);
		    	}
			}
			if(holder.playerNames == null || holder.playerNames.size() < 5)
			{
				ReadFromFile.fetchNamesBackEnd(holder, cont);
			}

		}  
		else
		{
			setNoInfo(this, holder);
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
		if(holder.isRegularSeason)
		{
			value.setText("Buy Low/Sell High");
			rookie.setText("Dynasty/Keepers");
			want.setText("Contract Year Guys");
			dontWant.setText("Mid-Season Trade Targets");
		}
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
				editor.apply();
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
				if(ManageInput.confirmInternet(cont))
				{
					fetchTrending(holder);
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection", Toast.LENGTH_SHORT).show();
				}
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
	 * function so both could happen. 
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
            		lastFilter = filterSize;
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
	public void resetTrendingList(int filterSize, Context cont) throws ParseException, IOException
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
	    listview = (OutBounceListView) cont.findViewById(R.id.listview_trending);
	    listview.setAdapter(null);
	    data = new ArrayList<Map<String, String>>();
	    setListViewOnClick();
	    List<String> trendingPlayers = new ArrayList<String>(350);
	    while(!playersTrending.isEmpty())
	    {
	    	final PostedPlayer elem = playersTrending.poll();
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", elem.name + "");
	    	StringBuilder count = new StringBuilder(1000);
	    	count.append(elem.count + " times");
	    	final StringBuilder sub = new StringBuilder(1000);
	    	boolean lastTrue = false;
	    	if(lastFilter >= 7 && elem.lastTime(1) > 0)
	    	{
	    		int lastDay = elem.lastTime(1);
	    		sub.append(lastDay + "% in the last day");
	    		lastTrue = true;
	    	}
	    	boolean lastWeekTrue = false;
	    	if(lastFilter >= 28 && elem.lastTime(8) > 0)
	    	{
	    		if(lastTrue)
	    		{
	    			sub.append("\n");
	    		}
	    		int lastWeek = elem.lastTime(8);
	    		sub.append(lastWeek + "% in the last week");
	    		lastWeekTrue = true;
	    	}
	    	if(lastFilter >= 45 && elem.lastTime(32) > 0)
	    	{
	    		if(lastWeekTrue)
	    		{
	    			sub.append("\n");
	    		}
	    		int lastMonth = elem.lastTime(32);
	    		sub.append(lastMonth + "% in the last month");
	    	}
	    	StringBuilder sb = new StringBuilder(1000);
	    	sb.append(count.toString());
	    	if(sub.toString().length() > 4)
	    	{
	    		sb.append("\n" + sub.toString());
	    	}
	    	datum.put("count", sb.toString());
	    	
	    	PlayerObject match = new PlayerObject();
	    	for(PlayerObject player : holder.players)
	    	{
	    		if(player.info.name.equals(elem.name))
	    		{
	    			match = player;
	    			break;
	    		}
	    	}
	    	if(match.info != null && match.info.name != null)
			{
				EntityUtils.getEntity(this, "http://www.ffr.com/" + match.info.name + ", " + match.info.position + "/pi" + Home.yearKey, new EntityGetListener() {
		    		@Override
		    		public void onError(SocializeException error) {
		    			if(isNotFoundError(error)) {
		    				// No entity found
		    			}
		    			else {
		    				// lol at handle error
		    			}
		    		}

					@Override
					public void onGet(com.socialize.entity.Entity result) {
						EntityStats es = result.getEntityStats();
						int views = es.getViews();
						for(Map<String, String> iter : data)
						{
							if(iter.get("name").equals(elem.name))
							{
								iter.put("count", iter.get("count") + "\n" + views + " total player views");
								mAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
		    	});
			}
	    	data.add(datum);
	    	trendingPlayers.add(elem.name + ": mentioned " + elem.count + " times//" + sb.toString());
	    }
	    if(data.size() == 0)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("name", "No players mentioned in this timeframe\n");
	    	datum.put("count", "Please try something else");
	    	trendingPlayers.add("No players mentioned in this timeframe" + ": mentioned " + "Please try something else" + " times//");
	    	data.add(datum);
	    }
	    if(refreshed)
	    {
	    	WriteToFile.writePostsList(trendingPlayers, cont);
	    	refreshed = false;
	    } 
	    //final ArrayAdapter<String> mAdapter = ManageInput.handleArray(trendingPlayers, listview, cont);
	    mAdapter = new SimpleAdapter(cont, data, 
	    		R.layout.bold_header_elem_underlined, 
	    		new String[] {"name", "count"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listview.setAdapter(mAdapter);
	    SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        true, "Trending", listview,
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
	public void setListViewOnClick()
	{
	    listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listview.setSelection(arg2);
				String selected = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString();
				int index = -1;
				PlayerObject match = new PlayerObject();
				for(int i = 0; i < holder.players.size(); i++)
				{
					PlayerObject player = holder.players.get(i);
					if(player.info.name.equals(selected))
					{
						match = player;
						index = i;
						break;
					}
				}
				if(index == -1 && holder.players.size() > 10)
				{
					if(!selected.contains("No players mentioned") && !selected.contains("The posts are fetched")){
						Toast.makeText(context, "Information not found, player not parsed. Possibly a defensive player?", Toast.LENGTH_SHORT).show();
					}
				}
				else if(index == -1)
				{
					Toast.makeText(context, "Can't fetch player information until rankings are fetched", Toast.LENGTH_SHORT).show();
				}
				else
				{
					PlayerInfo obj = new PlayerInfo();
					obj.outputResults(selected + ", " + match.info.position + " - " + match.info.team, true, (Trending)context, holder, false, false, false);
				}
			}
	    });
	}


	/**
	 * Adds to the adapter if the player isn't available in the storage object
	 * @param act
	 */
	public static void setNoInfo(Activity act, Storage hold) {
		if(hold.parsedPlayers.size() > 10)
		{
			for(Map<String, String> datum : data)
			{ 
				if(!hold.parsedPlayers.contains(datum.get("name")) && datum.containsKey("count") && !datum.get("count").contains("try something else"))
				{
					datum.put("count", datum.get("count") + "\nPlayer Information Unavailable");
					mAdapter.notifyDataSetChanged();
				}
			} 
		}
	}


	/**
	 * Sets the listview to say the posts are fetched
	 * @param act
	 */
	public static void setContent(Activity act, boolean flag) {
		final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    	Map<String, String> datum = new HashMap<String, String>(2);
    	if(flag)
    	{
    		datum.put("name", "No posts were found with the selection you made.");
			datum.put("count", "It's possible that thread is not yet available.");
			datum.put("freq", ""); 
    	}
    	else
    	{
	    	datum.put("name", "The posts are fetched\n");
	    	datum.put("count", "Select a timeframe from above");
    	}
    	data.add(datum);
    	mAdapter = new SimpleAdapter(act, data, 
	    		R.layout.web_listview_item, 
	    		new String[] {"name", "count"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2});
	    listview.setAdapter(mAdapter);
	}
}