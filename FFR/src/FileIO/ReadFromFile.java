package FileIO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

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
		ReadFromFile stupid = new ReadFromFile();
	
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    	if(!prefs.getString("Rankings List", "Not Set").equals("Not Set"))
    	{
    		ReadList listRanks = stupid.new ReadList((Activity)cont);
    		listRanks.execute(holder, cont, prefs);
    	}
	    ReadRanks rankings = stupid.new ReadRanks((Activity)cont);
		String[][] data=rankings.execute(holder, cont).get();
		
	    ReadValue values = stupid.new ReadValue();
		values.execute(holder, data);
		
		ReadNames names = stupid.new ReadNames((Activity)cont);
		names.execute(holder, cont);
		
	    ReadDraft draft = stupid.new ReadDraft((Activity)cont);
		draft.execute(holder, cont);		
	}
	
	private class ReadList extends AsyncTask<Object, Void, List<String>> 
	{
		Activity act;
	    public ReadList(Activity activity) 
	    {
	    	act = activity;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
  
		}
	
		@Override
		protected void onPostExecute(List<String> result){
			Rankings.listSetUp(result, act);
		}
		
	    protected List<String> doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	SharedPreferences prefs = (SharedPreferences)data[2];
	    	String ranks = prefs.getString("Rankings List", "Not Set");
    		String[] posts = ranks.split("##");
    		List<String>postsList = Arrays.asList(posts);
    		return postsList;
	    }
	  }
	
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	private class ReadRanks extends AsyncTask<Object, Void, String[][]> 
	{
	    public ReadRanks(Activity activity) 
	    {
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
  
		}
	
		@Override
		protected void onPostExecute(String[][] result){
		}
		
	    protected String[][] doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	   		//Get the aggregate rankings
	    	if(!holder.players.isEmpty())
	    	{
	    		holder.players.clear();
	    	}
	    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
	    	String checkExists = prefs.getString("Player Values", "Not Set");
	   		String[] perPlayer = checkExists.split("~~~~");
	   		String[][] allData = new String[perPlayer.length][];
	   		for(int i = 0; i < perPlayer.length; i++)
	   		{ 
	   			allData[i] = perPlayer[i].split("&&");
	   			PlayerObject newPlayer = new PlayerObject(allData[i][4], allData[i][5], allData[i][6], 0);
	   			holder.players.add(newPlayer);
	   		}
			return allData;
	    }
	  }
	
	/**
	 * This handles the running of the name readings
	 * in the background of the main thread
	 * @author Jeff
	 *
	 */
	private class ReadNames extends AsyncTask<Object, Void, Void> 
	{
	    
	    public ReadNames(Activity activity)
	    {
	    }
	    
		@Override
		protected void onPreExecute(){ 
	        super.onPreExecute(); 
		}
	
		@Override
		protected void onPostExecute(Void result){
		}
		
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	   		//Get the aggregate rankings
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			String parsedNames = prefs.getString("Player Names", "Doesn't matter");
			String[] namesSplit = parsedNames.split(",");
			if(!holder.parsedPlayers.isEmpty())
			{
				holder.parsedPlayers.clear();
			}
			for(String names: namesSplit)
			{
				holder.parsedPlayers.add(names);
			}
			return null;
	    }
	  }
	
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	private class ReadValue extends AsyncTask<Object, Void, Void> 
	{
	    public ReadValue() 
	    {
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
	
		}
	
		@Override
		protected void onPostExecute(Void result){
		}
		
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	String[][]allData = (String[][])data[1];
	   		for(int i = 0; i < holder.players.size(); i++)
	   		{ 
	   			PlayerObject player = holder.players.get(i);
	   			player.info.age = allData[i][13];
	   			player.info.sos = Integer.parseInt(allData[i][12]);
	   			player.info.contractStatus = allData[i][11];
	   			player.info.trend = allData[i][10];
	   			player.info.bye = allData[i][9];
	   			player.info.adp = allData[i][8];
	   			player.info.status = allData[i][7];
	   			player.values.low = Double.parseDouble(allData[i][3]);
	   			player.values.high = Double.parseDouble(allData[i][2]);
	   			player.values.count = Double.parseDouble(allData[i][1]);
	   			player.values.worth = Double.parseDouble(allData[i][0]);
	   		}
			return null;
	    }
	  }
	
	/**
	 * This handles the reading of the draft data
	 * in the background
	 * @author Jeff
	 *
	 */
	private class ReadDraft extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
	    public ReadDraft(Activity activity) 
	    {
	        act = activity;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result){
			if(act.getSharedPreferences("FFR", 0).getString("Rankings List", "Not Set").equals("Not Set"))
			{
				Rankings.intermediateHandleRankings(act);
			}
		}
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	   		//Get the aggregate rankings
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			String draftSet = prefs.getString("Draft Information", "Doesn't matter");
			String[] perSet = draftSet.split("@");
			String[][] individual = new String[perSet.length][];
			for(int j = 0; j < perSet.length; j++)
			{
				individual[j] = perSet[j].split("~");
			}
			//Qb fetching
			handleDraftReading(individual[0], holder.draft.qb, holder);
			//Rb fetching
			handleDraftReading(individual[1], holder.draft.rb, holder);
			//Wr fetching
			handleDraftReading(individual[2], holder.draft.wr, holder);
			//Te fetching
			handleDraftReading(individual[3], holder.draft.te, holder);
			//Def fetching
			handleDraftReading(individual[4], holder.draft.def, holder);
			//K fetching
			handleDraftReading(individual[5], holder.draft.k, holder);
			//Values
			holder.draft.remainingSalary = Integer.parseInt(individual[6][0]);
			holder.draft.value = Double.parseDouble(individual[7][0]);
			return null;
	    }
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
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String checkExists = prefs.getString("Posts", "Not Set");
		String[] perPost = checkExists.split("@@@");
		String[][] split = new String[perPost.length][];
		for(int i = 0; i < perPost.length; i++)
		{
			split[i] = perPost[i].split("~~~");
			Post newPost = new Post(split[i][0], split[i][1]);
			holder.posts.add(newPost);
		}
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
