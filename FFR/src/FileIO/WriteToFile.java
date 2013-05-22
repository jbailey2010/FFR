package FileIO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.WriteDraft;
import AsyncTasks.StorageAsyncTask.WritePostsListAsync;
import AsyncTasks.StorageAsyncTask.WriteRankListAsync;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
/**
 * A library of all of the functions that will write to  
 * file 
 * @author Jeff
 *
 */
public class WriteToFile {
	final static StorageAsyncTask asyncObject = new StorageAsyncTask();


	/**
	 * This stores the player names to the SD card, it can 
	 * only be called by fetchPlayerNames to avoid unnecessary calls
	 * @param holder holds the array to be stored
	 * @param cont used to be allowed to write to file in android
	 */
	public static void storePlayerNames(List<String> names, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		StringBuilder history = new StringBuilder(10000);
		for(int i = 0; i < names.size(); i++)
		{
			history.append(names.get(i) + ",");
		}
		editor.putString("Player Names", history.toString());
		editor.commit();
	}

	/**
	 * Handles writing rankings to file asynchronously
	 * @param holder
	 * @param cont
	 */
	public static void storeRankingsAsync(Storage holder, Context cont)
	{
	    WriteDraft draftTask = asyncObject.new WriteDraft();
	    draftTask.execute(holder, cont);
	}

	/**
	 * A tiny helper function that helps add to the returned string
	 */
	public static String handleDraftInput(List<PlayerObject> qb, String draft)
	{
		for(PlayerObject name : qb)
		{
			draft += name.info.name + "~";
		}
		if(draft.length() > 2)
		{
			draft = draft.substring(0, draft.length() - 1);
		}
		else
		{
			draft = "None Selected";
		}
		return draft;
	}

	/**
	 * Stores the posts to file to avoid unnecessary calls
	 * @param holder
	 * @param cont
	 */
	public static void writePosts(Storage holder, Context cont) 
	{
		StringBuilder posts = new StringBuilder(10000);
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		for(int i = 0; i < holder.posts.size(); i++)
		{
			Post post = holder.posts.get(i);
			posts.append(post.text + "~~~" + post.date + "@@@");
		}
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date today = Calendar.getInstance().getTime();        
		String reportDate = df.format(today);
		editor.putString("Date of Posts", reportDate);
		editor.putString("Posts", posts.toString());
		editor.commit();
	}

	
	/**
	 * Writes the list of trending players to file
	 * @param trendingPlayers
	 * @param cont
	 */
	public static void writePostsList(List<String> trendingPlayers, Activity cont) 
	{
	    WritePostsListAsync draftTask = asyncObject.new WritePostsListAsync();
	    draftTask.execute(trendingPlayers, cont);
	}
	


	/**
	 * Calls the async loader to write the list of rankings tofile
	 * @param rankings
	 * @param cont
	 */
	public static void storeListRankings(List<String> rankings, Context cont)
	{
	    WriteRankListAsync draftTask = asyncObject.new WriteRankListAsync();
	    draftTask.execute(rankings, cont);
	}
	
	

	/**
	 * Just writes the filter size to file for later usage
	 * @param cont
	 * @param size
	 */
	public static void writeFilterSize(Context cont, int size, String flag)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	if(flag.equals("Rankings"))
    	{
        	editor.putInt("Filter Quantity Size Rankings", size);
    	}
    	else
    	{
    		editor.putInt("Filter Quantity Size", size);
    	}
    	editor.commit();
	}
	
	/**
	 * Writes men in box ratios to file
	 * @param cont
	 * @param players
	 */
	public static void writeMenInBox(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder menInBox = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		menInBox.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("Men In Box", menInBox.toString()).commit();
    }
	
	/**
	 * Writes pass-run ratio to file
	 * @param cont
	 * @param players
	 */
	public static void writePassRun(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder runPassRatio = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		runPassRatio.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("Run Pass Ratio", runPassRatio.toString()).commit();
	}
	
	/**
	 * Writes offensive line ranks to file
	 * @param cont
	 * @param players
	 */
	public static void writeOLineRanks(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder oLineRanks = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		oLineRanks.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("O Line Ranks", oLineRanks.toString()).commit();
	}
	
	/**
	 * Writes the last filter to file for trending
	 * @param cont
	 * @param lastFilter
	 */
	public static void writeLastFilter(Context cont, int lastFilter)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	editor.putInt("Last Filter", lastFilter).commit();
	}
	
	/**
	 * Writes the rotoworld news set to file
	 * @param cont
	 * @param news
	 */
	public static void writeNewsRoto(Context cont, List<NewsObjects> news, boolean rh, boolean rp, 
			boolean th, boolean cbs, boolean si)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(NewsObjects newsObj : news)
    	{
    		newsSet.append(newsObj.news + "~~" + newsObj.impact + "~~" + 
    				newsObj.date + "@@@");
    	}
    	editor.putBoolean("Use Headlines", rh);
    	editor.putBoolean("Use Player News", rp);
    	editor.putBoolean("Use The Huddle", th);
    	editor.putBoolean("Use CBS News", cbs);
    	editor.putBoolean("Use SI News", si);
    	editor.putString("News RotoWorld", newsSet.toString()).commit();
	}
	
	/**
	 * Writes the twitter feed data to file
	 * @param cont
	 * @param news
	 * @param selection
	 */
	public static void writeNewsTwitter(Context cont, List<NewsObjects> news, String selection)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(NewsObjects newsObj : news)
    	{
    		newsSet.append(newsObj.news + "~~" + newsObj.impact + "~~" + 
    				newsObj.date + "@@@");
    	}
    	editor.putString("News RotoWorld", newsSet.toString()).commit();
    	editor.putString("Selected Twitter Feed", selection).commit();
	}
	
	/**
	 * Writes the news selection to file
	 * @param cont
	 * @param selection
	 */
	public static void writeNewsSelection(Context cont, String selection)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	editor.putString("News Title", selection).commit();
	}
	
	/**
	 * Writes the watch list to file
	 */
	public static void writeWatchList(Context cont, List<String> watch)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(String name : watch)
    	{
    		newsSet.append(name + "----");
    	}
    	editor.putString("Watch List", newsSet.toString()).commit();
	}
}
