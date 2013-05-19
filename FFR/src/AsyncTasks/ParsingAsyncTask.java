package AsyncTasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;


import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlcleaner.XPatherException;
import org.xml.sax.SAXException;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPN;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPNadv;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseFFTB;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseMyFantasyLeague;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePermanentData;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParsePlayerNames;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseTrending;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.Pages.News;
import com.example.fantasyfootballrankings.Pages.Rankings;

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
	long start;
	long all;
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
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	try { 
	    		start = System.nanoTime();
	    		all = System.nanoTime();
				ParseWF.wfRankings(holder);
				
				System.out.print((System.nanoTime() - start));
				System.out.println("    after WF");
				start = System.nanoTime();
				
				ParseGE.geRankings(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after GE");
				start = System.nanoTime();
				
				ParseCBS.cbsRankings(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after CBS");
				start = System.nanoTime();
				
				ParseESPNadv.parseESPNAggregate(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after espn aggr");
				start = System.nanoTime();
				
				ParseFFTB.parseFFTBRankingsWrapper(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after fftb");
				start = System.nanoTime();
				
				ParseESPN.parseESPN300(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after espn");
				start = System.nanoTime();
				
				ParseMyFantasyLeague.parseMFLAggregate(holder);
				
				System.out.print(System.nanoTime() - start);
				System.out.println("    after mfl aggr");
				System.out.println();
				System.out.println(System.nanoTime() - all);
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
	public class TeamInfoHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
	    public TeamInfoHighLevel(Activity activity) 
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
	    	start = System.nanoTime();
	    	try { 
				HighLevel.setStats(holder, cont);
				HighLevel.setTeamInfo(holder, cont);
				//HighLevel.getSOS(holder);
				HighLevel.setADP(holder);
	    		//HighLevel.setContractStatus(holder);
				HighLevel.setStatus(holder);
				
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
	 * Handles the setting of the contract status
	 * @author Jeff
	 *
	 */
	public class NonStatHighLevel extends AsyncTask<Object, Void, Void> 
	{
		Activity act;
		ProgressDialog pdia;
		Storage hold;
	    public NonStatHighLevel(Activity activity, Storage holder) 
	    {
	        act = activity;
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
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
		   if(hold.players.size() > 1)
		   {
			   ((Rankings) act).intermediateHandleRankings(act);
		   }
		}
		
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	start = System.nanoTime();
	    	try { 
				HighLevel.setStats(holder, cont);
				HighLevel.setTeamInfo(holder, cont);
				//HighLevel.getSOS(holder);
				HighLevel.setADP(holder);
	    		//HighLevel.setContractStatus(holder);
				HighLevel.setStatus(holder);
			    HighLevel.getParsedPlayers(holder);
			    HighLevel.setPermanentData(holder, cont);
	    		HighLevel.parseSpecificData(holder, cont);
	    		System.out.println(System.nanoTime() - start); 
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
	 * Handles the back-end parsing of the news
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
	    	boolean th = (Boolean)data[3];
	    	boolean cbs = (Boolean)data[4];
	    	boolean si = (Boolean)data[5];
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
	    		else if(th)
	    		{
	    			news = ParseNews.parseNewsHuddle();
	    		}
	    		else if(cbs)
	    		{
	    			news = ParseNews.parseCBS();
	    		}
	    		else if(si)
	    		{
	    			news = ParseNews.parseSI();
	    		}
	    		WriteToFile.writeNewsRoto(cont, news, rh, rp, th, cbs, si);
				return news;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	  }
	
	/**
	 * Handles the back-end parsing of the twitter feeds
	 * @author Jeff
	 *
	 */
	public class ParseTwitterFeeds extends AsyncTask<Object, Void, List<NewsObjects>> 
	{
		ProgressDialog pdia;
		Activity act;
	    public ParseTwitterFeeds(Context cont) 
	    {
	        pdia = new ProgressDialog(cont);
	        act = (Activity)cont;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the feeds...");
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
	    	String selection = (String)data[1];
	    	try {
	    		List<NewsObjects> news = new ArrayList<NewsObjects>(100);
	    		String url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=adamschefter&count=15";
	    		if(selection.contains("Mortenson"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=mortreport&count=15";
	    		}
	    		else if(selection.contains("LaCanfora"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=jasonlacanfora&count=15";
	    		}
	    		else if(selection.contains("Brad Evans"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=yahoonoise&count=15";
	    		}
	    		else if(selection.contains("Glazer"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=jayglazer&count=15";
	    		}
	    		else if(selection.contains("Clay"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=mikeclaynfl&count=15";
	    		}
	    		else if(selection.contains("Douche"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=fantasydouche&count=15";
	    		}
	    		else if(selection.contains("Eric Mack"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=ericmackfantasy&count=15";
	    		}
	    		else if(selection.contains("Late Round"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=lateroundqb&count=15";
	    		}
	    		else if(selection.contains("Wesseling"))
	    		{
	    			url = "https://api.twitter.com/1/statuses/user_timeline.xml?include_entities=true&include_rts=true&screen_name=chriswesseling&count=15";
	    		}
	    		else if(selection.contains("Aggregate"))
	    		{
	    			url = "https://api.twitter.com/1/lists/statuses.xml?slug=fantasy-football-writers&owner_screen_name=ChrisWesseling&count=25";
	    		}
	    		news = ParseNews.parseTwitter(url);
	    		WriteToFile.writeNewsTwitter(cont, news, selection);
				return news;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }
	  }
}

