package AsyncTasks;

import java.io.IOException;
import java.util.ArrayList;
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
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.Pages.News;
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
	    		String fa = "";
	    		if(player.fa.size() == 0)
	    		{
	    			fa = "Signed Free Agents: &&Departing Free Agents: ";
	    		}
	    		else
	    		{
	    			fa = player.fa.get(0) + "&&" + player.fa.get(1);
	    		}
	    		String oLine = " ";
	    		if(player.info.oLineStatus != null && !player.info.oLineStatus.equals("") 
	    				&& player.info.oLineStatus.length() >= 3)
	    		{
	    			oLine = player.info.oLineStatus;
	    		}
	    		String additStat = " ";
	    		if(player.info.additionalStat != null && !player.info.additionalStat.equals("") 
	    				&& player.info.additionalStat.length() >= 3)
	    		{
	    			additStat = player.info.additionalStat;
	    		}
	    		String oLineAdv = " ";
	    		if(player.info.oLineAdv != null && player.info.oLineAdv.length() > 3)
	    		{
	    			oLineAdv = player.info.oLineAdv;
	    		}
	    		players.append( 
	    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" +
	    		Double.toString(player.values.high) + "&&" + Double.toString(player.values.low) + "&&"
	    		+ player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
	    		player.info.adp + "&&" + player.info.bye + "&&" 
	    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + player.info.sos + "&&" + 
	    		player.info.age + "&&" + player.stats + "&&" + player.draftClass + "&&" + player.injuryStatus + 
	    		"&&" + fa + "&&" + oLine + "&&" + additStat + "&&" + player.values.ecr + "&&" + 
	    		player.risk + "&&" + player.riskPos + "&&" + player.riskAll + "&&" + oLineAdv + "&&" + 
	    		player.values.points + "&&" + player.values.paa + "&&" + player.values.paapd + "&&" + player.values.oTD + 
	    		"&&" + player.values.tdDiff + "&&" + player.values.tADEZ + "&&" + player.values.roTD + "&&" + player.values.rtdDiff
	    		+ "&&" + player.values.rADEZ + "&&" + player.values.coTD + "&&" + player.values.ctdDiff + "&&" + player.values.cADEZ + 
	    		"~~~~");
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
	 * Writes new PAA to file after calculating it
	 * @author Jeff
	 *
	 */
	public class WriteNewPAA extends AsyncTask<Object, Void, Void> 
	{
	    public WriteNewPAA() 
	    {
	
	    }
	
	
		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Context cont = (Context)data[1];
	    	Storage holder = (Storage)data[0];
	    	HighLevel.getPAA(holder, cont);
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	//Rankings work
	    	StringBuilder players = new StringBuilder(10000);
	    	for (PlayerObject player : holder.players)
	    	{
	    		String fa = "";
	    		if(player.fa.size() == 0)
	    		{
	    			fa = "Signed Free Agents: &&Departing Free Agents: ";
	    		}
	    		else
	    		{
	    			fa = player.fa.get(0) + "&&" + player.fa.get(1);
	    		}
	    		String oLine = " ";
	    		if(player.info.oLineStatus != null && !player.info.oLineStatus.equals("") 
	    				&& player.info.oLineStatus.length() >= 3)
	    		{
	    			oLine = player.info.oLineStatus;
	    		}
	    		String additStat = " ";
	    		if(player.info.additionalStat != null && !player.info.additionalStat.equals("") 
	    				&& player.info.additionalStat.length() >= 3)
	    		{
	    			additStat = player.info.additionalStat;
	    		}
	    		String oLineAdv = " ";
	    		if(player.info.oLineAdv != null && player.info.oLineAdv.length() > 3)
	    		{
	    			oLineAdv = player.info.oLineAdv;
	    		}
	    		players.append( 
	    	    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" +
	    	    		Double.toString(player.values.high) + "&&" + Double.toString(player.values.low) + "&&"
	    	    		+ player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
	    	    		player.info.adp + "&&" + player.info.bye + "&&" 
	    	    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + player.info.sos + "&&" + 
	    	    		player.info.age + "&&" + player.stats + "&&" + player.draftClass + "&&" + player.injuryStatus + 
	    	    		"&&" + fa + "&&" + oLine +  "&&" + additStat + "&&" + player.values.ecr + "&&" + 
	    	    		player.risk + "&&" + player.riskPos + "&&" + player.riskAll + "&&" + oLineAdv + "&&" + 
	    	    		player.values.points + "&&" + player.values.paa + "&&" + player.values.paapd + "&&" + player.values.oTD + 
	    	    		"&&" + player.values.tdDiff + "&&" + player.values.tADEZ + "&&" + player.values.roTD + "&&" 
	    	    		+ player.values.rtdDiff
	    	    		+ "&&" + player.values.rADEZ + "&&" + player.values.coTD + "&&" + player.values.ctdDiff + "&&" + player.values.cADEZ + 
	    	    		"~~~~");

	    	}
	    	String playerString = players.toString();
	    	editor.putString("Player Values", playerString).commit();
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
	 * This handles the reading of the draft data
	 * in the background
	 * @author Jeff
	 *
	 */
	public class ReadDraft extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		boolean flag;
	    public ReadDraft(Activity activity, boolean flagCheck) 
	    {
	        act = activity;
	        flag = flagCheck;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Void result){
			if(flag)
			{
				Rankings.intermediateHandleRankings(act);
			}
		}
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	long start = (Long)data[2];
	    	String checkExists = (String)data[3];
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    		holder.players = new ArrayList<PlayerObject>();
    		holder.parsedPlayers = new ArrayList<String>();
	   		String[] perPlayer = checkExists.split("~~~~");
	   		for(int i = 0; i < perPlayer.length; i++)
	   		{  
	   			String[] allData = perPlayer[i].split("&&");
	   			PlayerObject newPlayer = new PlayerObject(allData[4], allData[5], allData[6], 0);
	   			newPlayer.values.cADEZ = Double.parseDouble(allData[36]);
	   			newPlayer.values.ctdDiff = Double.parseDouble(allData[35]);
	   			newPlayer.values.coTD = Double.parseDouble(allData[34]);
	   			newPlayer.values.rADEZ = Double.parseDouble(allData[33]);
	   			newPlayer.values.rtdDiff = Double.parseDouble(allData[32]);
	   			newPlayer.values.roTD = Double.parseDouble(allData[31]);
	   			newPlayer.values.tADEZ = Double.parseDouble(allData[30]);
	   			newPlayer.values.tdDiff = Double.parseDouble(allData[29]);
	   			newPlayer.values.oTD = Double.parseDouble(allData[28]);
	   			newPlayer.values.paapd = Double.parseDouble(allData[27]);
	   			newPlayer.values.paa = Double.parseDouble(allData[26]);
	   			newPlayer.values.points = Double.parseDouble(allData[25]);
	   			newPlayer.info.oLineAdv = allData[24];
	   			newPlayer.riskAll = Double.parseDouble(allData[23]);
	   			newPlayer.riskPos = Double.parseDouble(allData[22]);
	   			newPlayer.risk = Double.parseDouble(allData[21]);
	   			newPlayer.values.ecr = Double.parseDouble(allData[20]);
	   			newPlayer.info.additionalStat = allData[19];
	   			newPlayer.info.oLineStatus = allData[18];
	   			newPlayer.fa = new ArrayList<String>(); 
	   			newPlayer.fa.add(0, allData[16]);
	   			newPlayer.fa.add(1, allData[17]);
	   			newPlayer.injuryStatus = allData[15];
	   			newPlayer.draftClass = allData[14];
	   			newPlayer.stats = allData[13];
	   			newPlayer.info.age = allData[12];
	   			newPlayer.info.sos = Integer.parseInt(allData[11]);
	   			newPlayer.info.contractStatus = allData[10];
	   			newPlayer.info.trend = allData[9];
	   			newPlayer.info.bye = allData[8];
	   			newPlayer.info.adp = allData[7];
	   			newPlayer.values.low = Double.parseDouble(allData[3]);
	   			newPlayer.values.high = Double.parseDouble(allData[2]);
	   			newPlayer.values.count = Double.parseDouble(allData[1]);
	   			newPlayer.values.worth = Double.parseDouble(allData[0]);
	   			holder.parsedPlayers.add(allData[4]);
	   			holder.players.add(newPlayer);
	   		}
			String[] perSet = prefs.getString("Draft Information", "Doesn't matter").split("@");
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
			//Ignore fetching
			for(String name : individual[6])
			{
				if(name.length() > 3 && !holder.draft.ignore.contains(name))
				{
					holder.draft.ignore.add(name);
				}
			}
			//Values 
			holder.draft.remainingSalary = Integer.parseInt(individual[7][0]);
			holder.draft.value = Double.parseDouble(individual[8][0]);
			System.out.println(System.nanoTime() - start + " to load from file");
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
	
	/**
	 * In the back-end fetches the news
	 * @author Jeff
	 *
	 */
	public class ReadRotoNews extends AsyncTask<Object, Void, List<NewsObjects>> 
	{
		Activity act;
	    public ReadRotoNews(Context cont) 
	    {
	    	act = (Activity)cont;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		}
		@Override
		protected void onPostExecute(List<NewsObjects> result)
		{
			News.handleNewsListView(result, act);
		}
		
	    protected List<NewsObjects> doInBackground(Object... data) 
	    {
	    	Context cont = (Context) data[0];
	   		List<NewsObjects> news = ReadFromFile.readNewsRoto(cont);
			return news;
	    }
	  }
}
