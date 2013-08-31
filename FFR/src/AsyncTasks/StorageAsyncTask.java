package AsyncTasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFreeAgents;
import com.example.fantasyfootballrankings.Pages.News;
import com.example.fantasyfootballrankings.Pages.Rankings;
import com.example.fantasyfootballrankings.Pages.Trending;
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
	    	WriteToFile.writeTeamData(holder, cont);
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	//Rankings work
	    	StringBuilder players = new StringBuilder(10000);
	    	for (PlayerObject player : holder.players)
	    	{
	    		players.append( 
	    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" 
	    		+ player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
	    		player.info.adp + "&&" 
	    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + 
	    		player.info.age + "&&" + player.stats + "&&" + player.injuryStatus + 
	    		"&&"+ player.values.ecr + "&&" + 
	    		player.risk + "&&" + player.riskPos + "&&" + 
	    		player.values.points + "&&" + player.values.paa + "&&" + player.values.paapd + "~~~~");
	    	}
	    	String playerString = players.toString();
	    	editor.putString("Player Values", playerString).commit();
	    	WriteToFile.writeLeverage(cont, holder);
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
		Context cont;
	    public WriteNewPAA(Context c) 
	    {
	    	cont = c;
	    }


		@Override
		protected void onPostExecute(Void result){
			((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		   super.onPostExecute(result);
		}

	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Context cont = (Context)data[1];
	    	Storage holder = (Storage)data[0];
	    	HighLevel.getPAA(holder, cont);
	    	WriteToFile.writeTeamData(holder, cont);
	    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
	    	//Rankings work
	    	StringBuilder players = new StringBuilder(10000);
	    	for (PlayerObject player : holder.players)
	    	{
	    		System.out.println(player.info.name + " - " + player.values.worth);
	    		players.append( 
	    	    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" +
	    	    		 player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
	    	    		player.info.adp + "&&" 
	    	    		+ player.info.trend + "&&" + player.info.contractStatus +"&&" + 
	    	    		player.info.age + "&&" + player.stats + "&&" + player.injuryStatus + 
	    	    		"&&" + player.values.ecr + "&&" + 
	    	    		player.risk + "&&" + player.riskPos + "&&"  + 
	    	    		player.values.points + "&&" + player.values.paa + "&&" + player.values.paapd + "~~~~");

	    	}
	    	String playerString = players.toString();
	    	WriteToFile.writeLeverage(cont, holder);
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
	 * This handles the reading of the draft data
	 * in the background
	 * @author Jeff
	 *
	 */
	public class ReadDraft extends AsyncTask<Object, Void, Storage> 
	{
		Activity act;
		int flag;
	    public ReadDraft(Activity activity, int i) 
	    {
	        act = activity;
	        flag = i;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		}
		@Override
		protected void onPostExecute(Storage result){
			if(flag == 0)
			{
				Rankings.intermediateHandleRankings(act);
			}
			else if(flag == 2)
			{
				Trending.setNoInfo(act, result);
			}
		}
	    protected Storage doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	long start = (Long)data[2];
	    	String checkExists = (String)data[3];
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    		holder.players = new ArrayList<PlayerObject>();
    		holder.parsedPlayers = new ArrayList<String>();
    		ReadFromFile.readTeamData(holder, cont);
    		String[] st = ManageInput.tokenize(checkExists, '~', 4);
	   		for(int i = 0; i < st.length; i++)
	   		{  
	   			String[] allData = ManageInput.tokenize(st[i], '&', 2);
	   			PlayerObject newPlayer = new PlayerObject(allData[2], allData[3], allData[4], 0);
	   			newPlayer.values.paapd = Double.parseDouble(allData[16]);
	   			newPlayer.values.paa = Double.parseDouble(allData[15]);
	   			newPlayer.values.points = Double.parseDouble(allData[14]);
	   			newPlayer.riskPos = Double.parseDouble(allData[13]);
	   			newPlayer.risk = Double.parseDouble(allData[12]);
	   			newPlayer.values.ecr = Double.parseDouble(allData[11]);
	   			newPlayer.injuryStatus = allData[10];
	   			newPlayer.stats = allData[9];
	   			newPlayer.info.age = allData[8];
	   			newPlayer.info.contractStatus = allData[7];
	   			newPlayer.info.trend = allData[6];
	   			newPlayer.info.adp = allData[5];
	   			newPlayer.values.count = Double.parseDouble(allData[1]);
	   			newPlayer.values.worth = Double.parseDouble(allData[0]);
	   			holder.parsedPlayers.add(allData[2]);
	   			holder.players.add(newPlayer);
	   		}
			String[] perSet = prefs.getString("Draft Information", "Doesn't matter").split("@");
			String[][] individual = new String[perSet.length][];
			for(int j = 0; j < perSet.length; j++)
			{
				individual[j] = perSet[j].split("~");
			}
			if(!perSet[0].equals("Doesn't matter") && individual.length > 4)
			{
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
				holder.draft.ignore.clear();
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
			}
			ReadFromFile.readLeverage(cont, holder);
			System.out.println(System.nanoTime() - start + " to read from file");
			return holder;
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