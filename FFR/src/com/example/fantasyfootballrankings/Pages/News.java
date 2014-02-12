package com.example.fantasyfootballrankings.Pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ffr.fantasyfootballrankings.R;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfoActivity;
import com.example.fantasyfootballrankings.ClassFiles.TwitterWork;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.InterfaceAugmentations.BounceListView;
import com.example.fantasyfootballrankings.InterfaceAugmentations.SwipeDismissListViewTouchListener;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Handles news parsing/twitter parsing
 * @author Jeff
 *
 */
public class News extends Activity {
	public static Context cont;
	public Dialog dialog;
	public static String selection = "NFL News";
	static TwitterWork obj = new TwitterWork();
	public static SideNavigationView sideNavigationView;
	BounceListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		ActionBar ab = getActionBar();
		cont = this;
		//ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
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
	            	Intent intent5 = new Intent(cont, ImportLeague.class);
	    	        cont.startActivity(intent5);
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
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view_news);
	    sideNavigationView.setMenuItems(R.menu.side_navigation_view);
	    sideNavigationView.setMenuClickCallback(sideNavigationCallback);
	   // sideNavigationView.setMode(/*SideNavigationView.Mode*/);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
		//SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		handleInitialLoading();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
		return true;
	}
	
	/**
	 * Runs the on selection part of the menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		dialog = new Dialog(cont, R.style.RoundCornersFull);
		switch (item.getItemId()) 
		{
			case android.R.id.home:
				System.out.println("Pressed");
		        sideNavigationView.toggleMenu();
		        return true;
			case R.id.refresh_news:
				if(ManageInput.confirmInternet(cont))
				{
					refreshNewsDialog();
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.twitter_feeds:
				if(ManageInput.confirmInternet(cont))
				{
					obj.twitterInitial(cont, 1, "", true);
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
				}
				return true; 
			case R.id.twitter_search:
				if(ManageInput.confirmInternet(cont))
				{
					obj.twitterInitial(cont, 2, "", true);
				}
				else
				{
					Toast.makeText(cont, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
				}
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
		dialog.setContentView(R.layout.help_news);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.help_news_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			} 
	    });
	}

	/**
	 * Handles conditional loading of the news
	 */
	public static void handleInitialLoading()
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String newsWhole = prefs.getString("News RotoWorld", "Not Set");
		if(newsWhole.equals("Not Set"))
		{
			//ParseNews.startNewsAsync(cont, true, false, false, false, false);
			Toast.makeText(cont, "Select an option above to get your news", Toast.LENGTH_SHORT).show();
		}
		else
		{
			ParseNews.startNewsReading(cont);
			selection = ReadFromFile.readNewsTitle(cont);
			setHeader(selection);

		}
	}

	/**
	 * Handles conditional refreshing of news
	 */
	public static void refreshNewsDialog()
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.news_topics_dialog);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		boolean rhCheck = prefs.getBoolean("Use Headlines", false);
		boolean rpCheck = prefs.getBoolean("Use Player News", false);
		boolean thCheck = prefs.getBoolean("Use The Huddle", false);
		boolean cCheck = prefs.getBoolean("Use CBS News", false);
		boolean siCheck = prefs.getBoolean("Use SI News", false);
		if(!rpCheck && !rhCheck && !thCheck)
		{
			rhCheck = true;
		}
    	final RadioButton rh = (RadioButton)dialog.findViewById(R.id.rotoworld_headlines);
    	final RadioButton rp = (RadioButton)dialog.findViewById(R.id.rotoworld_player_news);
    	final RadioButton th = (RadioButton)dialog.findViewById(R.id.the_huddle_news);
    	final RadioButton cbs = (RadioButton)dialog.findViewById(R.id.cbs_news);
    	final RadioButton si = (RadioButton)dialog.findViewById(R.id.si_news);
    	rh.setChecked(rhCheck);
    	rp.setChecked(rpCheck);
    	th.setChecked(thCheck);
    	cbs.setChecked(cCheck);
    	si.setChecked(siCheck);
		Button submit = (Button)dialog.findViewById(R.id.button_news_confirm);
		submit.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            	ParseNews.startNewsAsync(cont, rh.isChecked(), rp.isChecked(), th.isChecked(),
            			cbs.isChecked(), si.isChecked());
            	if(rh.isChecked())
            	{
            		selection = "Rotoworld Headline News";
            	}
            	else if(rp.isChecked())
            	{
            		selection = "Rotoworld Player News";
            	}
            	else if(th.isChecked())
            	{
            		selection = "The Huddle News";
            	}
            	else if(cbs.isChecked())
            	{
            		selection = "CBS News";
            	}
            	else if(si.isChecked())
            	{
            		selection = "Sports Illustrated News";
            	}
            	setHeader(selection);
            	WriteToFile.writeNewsSelection(cont, selection);
            	dialog.dismiss();
            }
		});
		Button cancel = (Button)dialog.findViewById(R.id.button_news_cancel);
		cancel.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	dialog.dismiss();
            }
		});
		dialog.show();
	}
	
	/**
	 * Handles conditional refreshing of news
	 */
	public static void twitterFeedsDialog()
	{
		//Set up dialog
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.twitter_feeds);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
		//Set up adapter
		List<String> spinnerList = new ArrayList<String>();
		spinnerList.add("Adam Schefter (NFL News)");
		spinnerList.add("Chris Mortenson (NFL News)");
		spinnerList.add("Jason LaCanfora (NFL News)");
		spinnerList.add("Jay Glazer (NFL News)");
		spinnerList.add("Aggregate Beat Writers (NFL News)");
		spinnerList.add("Aggregate Feed (Fantasy)");
		spinnerList.add("Brad Evans (Fantasy)");
		spinnerList.add("Chet Gresham (Fantasy)");
		spinnerList.add("Chris Wesseling (Fantasy)");
		spinnerList.add("Eric Mack (Fantasy)");
		spinnerList.add("Fantasy Douche (Fantasy)");
		spinnerList.add("Kay Adams (Fantasy)");
		spinnerList.add("Late Round QB (Fantasy)");
		spinnerList.add("Mike Clay (Fantasy)");
		spinnerList.add("Sigmund Bloom (Fantasy)");
		final Spinner feeds = (Spinner)dialog.findViewById(R.id.spinner_feeds);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(cont, 
				android.R.layout.simple_spinner_dropdown_item, spinnerList);
		feeds.setAdapter(spinnerArrayAdapter);
		//Load pre-selected item 
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		String selectionFeed = prefs.getString("Selected Twitter Feed", "Adam Schefter (NFL News)");
		feeds.setSelection(spinnerList.indexOf(selectionFeed));
		
		//Submit onclick
		Button submit = (Button)dialog.findViewById(R.id.twitter_submit);
		submit.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            	String selectionFeed = feeds.getSelectedItem().toString();
            	ParseNews.startTwitterAsync(cont, selectionFeed, obj);
            	String[] brokenUp = selectionFeed.split(" \\(");
            	selection = brokenUp[0] + "'s Twitter Feed";
            	if(selection.contains("Aggregate"))
            	{
            		selection = "Aggregate Twitter Feed";
            	}
            	WriteToFile.writeNewsSelection(cont, selection);
            	setHeader(selection);
            	dialog.dismiss();
            }
		});
		//cancel onclick
		Button cancel = (Button)dialog.findViewById(R.id.twitter_cancel);
		cancel.setOnClickListener(new View.OnClickListener()
		{	
            @Override
            public void onClick(View v) 
            {
            	dialog.dismiss();
            }
		});
		dialog.show();
	}
	
	/**
	 * Handles the pop up to get the user input
	 */
	public static void twitterSearchDialog()
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.twitter_search);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button cancel = (Button)dialog.findViewById(R.id.twitter_search_cancel);
	    Button submit = (Button)dialog.findViewById(R.id.twitter_search_submit);
	    final EditText input = (EditText)dialog.findViewById(R.id.twitter_search_input);
	    cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				return;
			}
	    });
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String queryTerms = input.getText().toString().replace(",", " ");
				if(queryTerms.length() < 3)
				{
					Toast.makeText(cont, "Please input a query", Toast.LENGTH_SHORT).show();
				}
				else
				{
					((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					ParseNews.startTwitterSearchAsync(cont, queryTerms, "Twitter Search: " + queryTerms, true, " ", obj);
					WriteToFile.writeNewsSelection(cont, "Twitter Search: " + queryTerms);
					setHeader("Twitter Search: " + queryTerms);
					dialog.dismiss();
				}
			}
	    });
	}
	
	/**
	 * Sets the header of the page to the context
	 * @param selection
	 */
	public static void setHeader(String selection)
	{
		if(selection.contains("Aggregate"))
		{
			selection = "Aggregate Twitter Feed";
		}
		TextView header = (TextView)((Activity) cont).findViewById(R.id.news_header);
		header.setText(selection);
	}
	
	/**
	 * Handles the showing of the listview of news
	 * @param result
	 * @param cont
	 */
	public void handleNewsListView(List<NewsObjects> result, final Activity cont) 
	{
		listview = (BounceListView)cont.findViewById(R.id.listview_news);
		cont.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	    final List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	    for(NewsObjects newsObj : result)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("news", newsObj.news);
	    	datum.put("sub", newsObj.impact);
	    	datum.put("date", newsObj.date);
	    	data.add(datum);
	    }
	    if(data.size() == 0)
	    {
	    	Map<String, String> datum = new HashMap<String, String>(2);
	    	datum.put("news", "No results were found");
	    	datum.put("sub", "");
	    	datum.put("date", "Try again, or try a different search");
	    	data.add(datum);
	    }
	    final SimpleAdapter adapter = new SimpleAdapter(cont, data, 
	    		R.layout.bold_header_elem,
	    		new String[] {"news", "sub", "date"}, 
	    		new int[] {R.id.text1, 
	    			R.id.text2, R.id.text3});

	    listview.setAdapter(adapter);
	    final SwipeDismissListViewTouchListener touchListener = 
                new SwipeDismissListViewTouchListener(
                        true, "News", listview,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {

							@Override
							public void onDismiss(ListView listView,
									int[] reverseSortedPositions) {
								for (int position : reverseSortedPositions) {
                                	data.remove(position);
                                    //adapter.remove(adapter.getItem(position));
                                }
                                adapter.notifyDataSetChanged();
                                Toast.makeText(cont, "Temporarily hiding this news piece", Toast.LENGTH_SHORT).show();
								
							}
						});
                        
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());
        listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String[] headline = ((TextView)((RelativeLayout)arg1).findViewById(R.id.text1)).getText().toString().replaceAll("\\(", "").replaceAll("\\)", "").replace("'s ", " ").split(" ");
				boolean isName = false;
				PlayerObject match = new PlayerObject();
				for(int i = 0; i < headline.length - 1; i++)
				{
					String twoLong = headline[i] + " " + headline[i+1];
					if(Home.holder.parsedPlayers.contains(twoLong))
					{
						isName = true;
						for(PlayerObject player : Home.holder.players)
						{
							if(player.info.name.equals(twoLong))
							{
								match = player;
								break;
							}
						}
					}
					if(i + 2 < headline.length)
					{
						String threeLong = twoLong + " " + headline[i+2];
						if(Home.holder.parsedPlayers.contains(threeLong))
						{
							isName = true;
							for(PlayerObject player : Home.holder.players)
							{
								if(player.info.name.equals(threeLong))
								{
									match = player;
									break;
								}
							}
						}
					}
				}
				if(isName)
				{
					PlayerInfo obj = new PlayerInfo();
					obj.outputResults(match.info.name + ", " + match.info.position + " - " + match.info.team, true, (News)cont, Home.holder, false, false);
				}
			}
        	
        });
	}

}
