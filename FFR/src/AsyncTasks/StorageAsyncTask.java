package AsyncTasks;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.Pages.Rankings;
/**
 * A library to hold all the asynctasks relevant to storing/reading to/from file
 * @author Jeff
 *
 */
public class StorageAsyncTask
{
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	public class WriteDraft extends AsyncTask<Object, Void, Void> 
	{
	    public WriteDraft() 
	    {
	
	    }
	
	
		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage)data[0];
	    	Context cont = (Context)data[1];
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	//Rankings work
	    	StringBuilder players = new StringBuilder(10000);
	    	for (PlayerObject player : holder.players)
	    	{
	    		players.append( 
	    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" +
	    		Double.toString(player.values.high) + "&&" + Double.toString(player.values.low) + "&&"
	    		+ player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
	    		player.info.status + "&&" + player.info.adp + "&&" + player.info.bye + "&&" 
	    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + player.info.sos + "&&" + 
	    		player.info.age + "&&" + player.info.oLineStatus + "&&" + player.info.passRunRatio + "&&" + 
	    		player.info.additionalStat + "~~~~");
	    	}
	    	String playerString = players.toString();
	    	editor.putString("Player Values", playerString).commit();
	    	//Player names work
	    	StringBuilder names = new StringBuilder(10000);
	    	for(String name: holder.parsedPlayers)
	    	{
	    		names.append(name + ",");
	    	}
	    	String namesString = names.toString();
	    	editor.putString("Parsed Player Names", namesString).commit();
	    	//Setting up draft input
	    	String draft = "";
	    	//QB
	    	draft += WriteToFile.handleDraftInput(holder.draft.qb, "") + "@";
	    	//RB
	    	draft += WriteToFile.handleDraftInput(holder.draft.rb, "") + "@";
	    	//WR
	    	draft += WriteToFile.handleDraftInput(holder.draft.wr, "") + "@";
	    	//TE
	    	draft += WriteToFile.handleDraftInput(holder.draft.te, "") + "@";
	    	//D
	    	draft += WriteToFile.handleDraftInput(holder.draft.def, "") + "@";
	    	//K
	    	draft += WriteToFile.handleDraftInput(holder.draft.k, "") + "@";
	    	//Values
	    	draft += holder.draft.remainingSalary + "@" + holder.draft.value;
	    	editor.putString("Draft Information", draft);
	    	editor.commit();
			return null;
	    }
	}
	
	/**
	 * Writes the posts to file 
	 * @author Jeff
	 *
	 */
	public class WritePostsListAsync extends AsyncTask<Object, Void, Void> 
	{
	    public WritePostsListAsync() 
	    {
	
	    }
	
	
		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	List<String> trendingPlayers = (List<String>)data[0];
	    	Context cont = (Context)data[1];
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	StringBuilder posts = new StringBuilder(10000);
	    	for(int i = 0; i < trendingPlayers.size(); i++)
	    	{
	    		posts.append(trendingPlayers.get(i) + "##");
	    	}
	    	editor.putString("Posted Players", posts.toString());
	    	editor.commit();
			return null;
	    }
	}
	
	/**
	 * Writes the rankings to file to save a bit of time later
	 * @author Jeff
	 *
	 */
	public class WriteRankListAsync extends AsyncTask<Object, Void, Void> 
	{
	    public WriteRankListAsync() 
	    {
	
	    }
	
	
		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	List<String> rankingsList = (List<String>)data[0];
	    	Context cont = (Context)data[1];
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	StringBuilder list = new StringBuilder(10000);
	    	for(int i = 0; i < rankingsList.size(); i++)
	    	{
	    		list.append(rankingsList.get(i) + "##");
	    	}
	    	editor.putString("Rankings List", list.toString());
	    	editor.commit();
			return null;
	    }
	}
	
	  /**
     * Fetches the names list from file in the background
     * @author Jeff
     *
     */
	public class ReadNamesList extends AsyncTask<Object, Void, Void> 
	{
	    public ReadNamesList() 
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
	    	try {
				ReadFromFile.fetchNames(holder, cont);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		return null;
	    }
	  }
	
	/**
	 * Reads the list in a back end way 
	 * from storage
	 * @author Jeff
	 *
	 */
	public class ReadList extends AsyncTask<Object, Void, List<String>> 
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
	    	int size = (Integer)data[3];
	    	String ranks = prefs.getString("Rankings List", "Not Set");
    		String[] posts = ranks.split("##");
    		size = posts.length;
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
	public class ReadRanks extends AsyncTask<Object, Void, String[][]> 
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
	public class ReadNames extends AsyncTask<Object, Void, Void> 
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
			String parsedNames = prefs.getString("Parsed Player Names", "Doesn't matter");
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
	public class ReadValue extends AsyncTask<Object, Void, Void> 
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
	    	Context cont = (Context)data[2];
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
   			HighLevel.setPermanentData(holder, cont);
			return null;
	    }
	  }

	
	/**
	 * This handles the reading of the draft data
	 * in the background
	 * @author Jeff
	 *
	 */
	public class ReadDraft extends AsyncTask<Object, Void, Void> 
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
			ReadFromFile.handleDraftReading(individual[0], holder.draft.qb, holder);
			//Rb fetching
			ReadFromFile.handleDraftReading(individual[1], holder.draft.rb, holder);
			//Wr fetching
			ReadFromFile.handleDraftReading(individual[2], holder.draft.wr, holder);
			//Te fetching
			ReadFromFile.handleDraftReading(individual[3], holder.draft.te, holder);
			//Def fetching
			ReadFromFile.handleDraftReading(individual[4], holder.draft.def, holder);
			//K fetching
			ReadFromFile.handleDraftReading(individual[5], holder.draft.k, holder);
			//Values
			holder.draft.remainingSalary = Integer.parseInt(individual[6][0]);
			holder.draft.value = Double.parseDouble(individual[7][0]);
			return null;
	    }
	  }
	
	/**
	 * In the back-end fetches the posts
	 * @author Jeff
	 *
	 */
	public class ReadPosts extends AsyncTask<Object, Void, Void> 
	{
	    public ReadPosts() 
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
			String checkExists = prefs.getString("Posts", "Not Set");
			String[] perPost = checkExists.split("@@@");
			String[][] split = new String[perPost.length][];
			for(int i = 0; i < perPost.length; i++)
			{
				split[i] = perPost[i].split("~~~");
				Post newPost = new Post(split[i][0], split[i][1]);
				holder.posts.add(newPost);
			}
			return null;
	    }
	  }
}
