package FileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadDraft;
import AsyncTasks.StorageAsyncTask.ReadList;
import AsyncTasks.StorageAsyncTask.ReadNames;
import AsyncTasks.StorageAsyncTask.ReadNamesList;
import AsyncTasks.StorageAsyncTask.ReadPosts;
import AsyncTasks.StorageAsyncTask.ReadRanks;
import AsyncTasks.StorageAsyncTask.ReadValue;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.Pages.Rankings;
/**
 * A library of all the functions that will read
 * from file
 * @author Jeff
 *
 */
public class ReadFromFile {
	static StorageAsyncTask readFromFileAsyncObj = new StorageAsyncTask();
	
	/**
	 * Fetches the names list from file in the back end
	 * @param holder
	 * @param cont
	 */
    public static void fetchNamesBackEnd(Storage holder, Context cont)
    {
	    ReadNamesList values = readFromFileAsyncObj.new ReadNamesList();
		values.execute(holder, cont);
    }
	
  
	/** 
	 * Designed to be the one call that's made to handle any fetching...etc of 
	 * the player names. If they're already written, it fetches them and stores.
	 * If not, it calls a function to fetch them and write to file. It's set up this way
	 * to minimize other calls such that only one function needs to be called, yet 
	 * running time is minimized this way.
	 * @param holder holds the array to be written to
	 * @param cont holds the context to be used to write to file.
	 * @throws IOException 
	 */
	public static void fetchNames(Storage holder, Context cont) throws IOException
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String checkExists = prefs.getString("Player Names", "Not Set");
		holder.playerNames.clear();
		String[] j = checkExists.split(",");
		for(int i = 0; i < j.length; i++)
		{
			holder.playerNames.add(j[i]);
		}
	}

	/**
	 * It checks if players are written to file. If so, it fetches them and
	 * re-adds them to the priority queue, but if it isn't, it calls the runRankings
	 * function, which is the function that calls all the rankings functions and the high 
	 * level stuff
	 * @param holder the storage to be added to
	 * @param cont the context used to read/write to/from file
	 * @throws IOException
	 * @throws XPatherException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void fetchPlayers(Storage holder, Context cont, boolean flag) throws IOException, XPatherException, InterruptedException, ExecutionException
	{
	
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	if(!prefs.getString("Rankings List", "Not Set").equals("Not Set"))
    	{
    		ReadList listRanks = readFromFileAsyncObj.new ReadList((Activity)cont, flag);
    		listRanks.execute(holder, cont, prefs);
    	}
    	long start = System.nanoTime();
	    ReadRanks rankings = readFromFileAsyncObj.new ReadRanks((Activity)cont);
		String[][] data=rankings.execute(holder, cont).get();
		
	    ReadValue values = readFromFileAsyncObj.new ReadValue();
		values.execute(holder, data, cont);
		
		ReadNames names = readFromFileAsyncObj.new ReadNames((Activity)cont);
		names.execute(holder, cont);
		
	    ReadDraft draft = readFromFileAsyncObj.new ReadDraft((Activity)cont, flag);
		draft.execute(holder, cont, start);		
	}
	
	
	

	
	/**
	 * Gets the names into the respective draft
	 * @param individual
	 * @param target
	 * @param holder
	 */
	public static void handleDraftReading(String[] individual, List<PlayerObject> target, Storage holder)
	{
		target.clear();
		for(String qb : individual)
		{
			for(PlayerObject player : holder.players)
			{
				if(player.info.name.equals(qb))
				{
					target.add(player);
				}
			}
		}
	}

	/**
	 * Fetches the players from local to a local object
	 * @param holder
	 * @param cont
	 */
	public static void fetchPostsLocal(Storage holder, Context cont) 
	{
	    ReadPosts values = readFromFileAsyncObj.new ReadPosts();
		values.execute(holder, cont);
	}
	
	
	/**
	 * Reads the filter quantity size from file
	 * @param cont the context from which it reads
	 * @return the size
	 */
	public static int readFilterQuantitySize(Context cont, String flag) 
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		if(flag.equals("Rankings"))
		{
			return prefs.getInt("Filter Quantity Size Rankings", 100);
		}
		return prefs.getInt("Filter Quantity Size", 100);
	}
	
	/**
	 * Reads a hashmap of men in box from file
	 * @param cont
	 * @return
	 */
	public static Map<String, String> readMenInBox(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String checkExists = prefs.getString("Men In Box", "Not Set");
		Map<String,String> players = new HashMap<String, String>();
		String[] perPlayer = checkExists.split("@@#");
		for(int i = 0; i < perPlayer.length; i++)
		{
			String[] individualData = perPlayer[i].split("~~");
			players.put(individualData[0], individualData[1]);
		}
		return players;
	}
	
	/**
	 * handles the reading of the pass run ratios per team
	 * from file
	 * @param cont
	 * @return
	 */
	public static Map<String, String> readPassRun(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String checkExists = prefs.getString("Run Pass Ratio", "Not Set");
		Map<String,String> teams = new HashMap<String, String>();
		String[] perPlayer = checkExists.split("@@#");
		for(int i = 0; i < perPlayer.length; i++)
		{
			String[] individualData = perPlayer[i].split("~~");
			teams.put(individualData[0], individualData[1]);
		}
		return teams;
	}
	
	/**
	 * Handles reading of o line rankings from file
	 * @param cont
	 * @return
	 */
	public static Map<String, String> readOLineRanks(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String checkExists = prefs.getString("O Line Ranks", "Not Set");
		Map<String,String> teams = new HashMap<String, String>();
		String[] perPlayer = checkExists.split("@@#");
		for(int i = 0; i < perPlayer.length; i++)
		{
			String[] individualData = perPlayer[i].split("~~");
			if(individualData.length > 1)
			{
				teams.put(individualData[0], individualData[1]);
			}
		}
		return teams;
	}
	
	/**
	 * Handles reading the last filter used from
	 * file (for trending)
	 * @param cont
	 * @return
	 */
	public static int readLastFilter(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getInt("Last Filter", 365);
	}
	
	/**
	 * Reads the rotoworld news from file
	 * @param cont
	 * @return
	 */
	public static List<NewsObjects> readNewsRoto(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String newsWhole = prefs.getString("News RotoWorld", "Not Set");
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
		String[] perHeadline = newsWhole.split("@@@");
		for(int i = 0; i < perHeadline.length; i++)
		{
			String[] newsData = perHeadline[i].split("~~");
			NewsObjects newsObj = new NewsObjects(newsData[0], newsData[1], 
					newsData[2]);
			if(newsData.length == 4)
			{
				newsObj = new NewsObjects(newsData[0], newsData[1], 
						newsData[2]);
			}
			newsSet.add(newsObj);
		}
		return newsSet;
	}
	
	/**
	 * Reads the news title from file
	 * @param cont
	 * @return
	 */
	public static String readNewsTitle(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getString("News Title", "NFL News");
	}
}
