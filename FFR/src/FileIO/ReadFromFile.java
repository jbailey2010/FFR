package FileIO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
	public static void fetchPlayers(Storage holder, Context cont) throws IOException, XPatherException, InterruptedException, ExecutionException
	{
	
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	if(!prefs.getString("Rankings List", "Not Set").equals("Not Set"))
    	{
    		ReadList listRanks = readFromFileAsyncObj.new ReadList((Activity)cont);
    		listRanks.execute(holder, cont, prefs);
    	}
	    ReadRanks rankings = readFromFileAsyncObj.new ReadRanks((Activity)cont);
		String[][] data=rankings.execute(holder, cont).get();
		
	    ReadValue values = readFromFileAsyncObj.new ReadValue();
		values.execute(holder, data);
		
		ReadNames names = readFromFileAsyncObj.new ReadNames((Activity)cont);
		names.execute(holder, cont);
		
	    ReadDraft draft = readFromFileAsyncObj.new ReadDraft((Activity)cont);
		draft.execute(holder, cont);		
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
}
