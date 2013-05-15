package AsyncTasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMyFantasyLeague;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePermanentData;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePlayerNames;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.Pages.News;

import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
/**
 * A library of all the asynctasks involving parsing
 * @author Jeff
 *
 */
public class ParsingAsyncTask 
{
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	public class ParseRanks extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
		Activity act;
		Storage hold;
	    public ParseRanks(Activity activity, Storage holder) 
	    {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        act = activity;
	        hold = holder;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the rankings...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   ParseRankings.highLevel(act, hold);
		   //((Rankings)act).intermediateHandleRankings(act);
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	HttpParams httpParameters = new BasicHttpParams();
	    	HttpConnectionParams.setSoTimeout(httpParameters, 5000);
	    	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    	httpClient.setParams(httpParameters);
	    	try { 
				ParseWF.wfRankings(holder);
				ParseGE.geRankings(holder);
				ParseCBS.cbsRankings(holder);
				//ParseESPNadv.parseESPNAggregate(holder);
				ParseFFTB.parseFFTBRankingsWrapper(holder);
				//ParseESPN.parseESPN300(holder);
				ParseMyFantasyLeague.parseMFLAggregate(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
	    }
	  }

	/**
	 * Handles the parsing of the adp data
	 * @author Jeff
	 *
	 */
	public class ADPHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
	    public ADPHighLevel(Activity activity) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
	        pdia.setMessage("Please wait, fetching the rankings...");
	        pdia.show(); 
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	try { 
				HighLevel.setADP(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	}
	
	/**
	 * Handles the parsing of the stats
	 * @author Jeff
	 *
	 */
	public class StatsHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
	    public StatsHighLevel(Activity activity) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
	        pdia.setMessage("Please wait, fetching the rankings...");
	        pdia.show(); 
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	try { 
				HighLevel.setStats(holder, cont);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	}
	
	/**
	 * Handles the parsing of the strength of schedule
	 * @author Jeff
	 *
	 */
	public class SOSHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
	    public SOSHighLevel(Activity activity) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
	        pdia.setMessage("Please wait, fetching the rankings...");
	        pdia.show(); 
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	try { 
				HighLevel.getSOS(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
	    }
	}
	

	/**
	 * Handles the setting of the contract status
	 * @author Jeff
	 *
	 */
	public class ContractHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
	    public ContractHighLevel(Activity activity) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
	        pdia.setMessage("Please wait, fetching the rankings...");
	        pdia.show(); 
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	try { 
	    		HighLevel.setContractStatus(holder);
	    	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return null;
	    }
	}
	
	/**
	 * Parses the posts from the forums
	 * @author Jeff
	 *
	 */
	public class FetchTrends extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
		Storage holder;
	    public FetchTrends(Activity activity, Storage hold) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        holder = hold;
	    }
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia = new ProgressDialog(act);
		        pdia.setMessage("Please wait, parsing the forums...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   if(holder.posts.size() > 1)
		   {
			   WriteToFile.writePosts(holder, act);
		   }
		}
    	
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage hold = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
			boolean value =  prefs.getBoolean("Value Topic", true);
			boolean mustHave = prefs.getBoolean("Good Topic", true);
			boolean rookie = prefs.getBoolean("Rookie Topic", true);
			boolean dontWant = prefs.getBoolean("Bad Topic", false);
		
	    	holder = hold;
			try {
				if(mustHave)
				{
			    	//2013 'Must Haves'
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=338991&st=");
					//RB rankings
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=344555&st=");
					//QB rankings
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=329554&st=");
					//WR rankings
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=339910&st=");
					//TE rankings
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=347782&st=");
					//D/K don't exist
				}
				if(value)
				{
					//Value picks
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=332995&st=");
					//2013 sleepers
					ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=327212&st=");
				}
				if(rookie)
		 		{
			 		//Rookie rankings
		 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=331665&st=");
		 			//Draft thread
		 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=345800&st=");
		 		}
		 		if(dontWant)
		 		{
		 			//Overvalued
		 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=334675&st=");
		 			//Don't draft
		 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=345722&st=");	
		 			//Busts
		 			ParseTrending.getPosts(holder, "http://forums.rotoworld.com/index.php?showtopic=347469&st=");
		 		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	public class ParseNames extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
		Activity act;
	    public ParseNames(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	        act = activity;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, refreshing the list...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Context cont = (Context) data[0];
	    	try {
				ParsePlayerNames.fetchPlayerNames(cont);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	  }
	
	/**
	 * Handles the back-end parsing of the permanent data
	 * @author Jeff
	 *
	 */
	public class ParsePermanentDataSets extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
		Activity act;
	    public ParsePermanentDataSets(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	        act = activity;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the advanced data...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Context cont = (Context) data[0];
	    	Storage holder = (Storage) data[1];
	    	ReadFromFile.fetchNamesBackEnd(holder, cont);
	    	try {
				Map<String, String> menInBox = ParsePermanentData.parseMenInBox(holder, cont);
				Map<String, String> prRatio = ParsePermanentData.parsePassRunRatio();
				Map<String, String> oLineRanks = ParsePermanentData.parseOLineRanksWrapper();
				WriteToFile.writeMenInBox(cont, menInBox);
				WriteToFile.writePassRun(cont, prRatio);
				WriteToFile.writeOLineRanks(cont, oLineRanks);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	  }
	
	/**
	 * Handles the back-end parsing of the permanent data
	 * @author Jeff
	 *
	 */
	public class ParseRotoWorldNews extends AsyncTask<Object, Void, List<NewsObjects>> 
	{
		ProgressDialog pdia;
		Activity act;
	    public ParseRotoWorldNews(Context cont) 
	    {
	        pdia = new ProgressDialog(cont);
	        act = (Activity)cont;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the news...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(List<NewsObjects> result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   News.handleNewsListView(result, act);
		}
		
	    @Override
	    protected List<NewsObjects> doInBackground(Object... data) 
	    {
	    	Context cont = (Context) data[0];
	    	boolean rh = (Boolean)data[1];
	    	boolean rp = (Boolean)data[2];
	    	try {
	    		List<NewsObjects> news = new ArrayList<NewsObjects>(100);
	    		if(rh)
	    		{
	    			news = ParseNews.parseNewsRoto("http://www.rotoworld.com/headlines/nfl/0/football-headlines");
	    		}
	    		else if(rp)
	    		{
	    			news = ParseNews.parseNewsRoto("http://www.rotoworld.com/playernews/nfl/football-player-news");
	    		}
	    		WriteToFile.writeNewsRoto(cont, news, rh, rp);
				return news;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	  }
}

