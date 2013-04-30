package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;

import java.io.Serializable;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseCBS;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseGE;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseWF;
import com.example.fantasyfootballrankings.Pages.Home;
import com.example.fantasyfootballrankings.Pages.Rankings;


import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Handles the storage of players and player names.
 * @author - Jeff
 */

/**
 *Stores various info as it's needed
 * @author Jeff
 *
 */
public class Storage 
{
	public Draft draft;
	public List<PlayerObject> players;
	public List<String> playerNames;
	public List<Post> posts;
	public List<String> parsedPlayers;
	public PriorityQueue<PostedPlayer> postedPlayers;
	/**
	 * This sets up the priority queue and it's subsequent comparator.
	 * No parameters are necessary, and the playerNames array doesn't need initialization.
	 */
	public Storage()
	{
		players = new ArrayList<PlayerObject>(350);
		postedPlayers = new PriorityQueue<PostedPlayer>(100, new Comparator<PostedPlayer>()
		{
			@Override
			public int compare(PostedPlayer a, PostedPlayer b)
			{
				if(a.count > b.count)
				{
					return -1;
				}
				if(a.count < b.count)
				{
					return 1;
				}
				return 0;
			}
		});
		playerNames = new ArrayList<String>(400);
		posts = new ArrayList<Post>(500);
		parsedPlayers = new ArrayList<String>(350);
		draft = new Draft();
	}

	/**
	 * This checks if a name exists in the parsed list. If so, it 
	 * returns that for the sake of name continuity throughout all.
	 * This list is all of the nfl, so if they're not in here, it's
	 * due to a formatting error. It should also make comparisons through the priority queue
	 * easy peasy.
	 * @param holder the storage unit to be checked through
	 * @param name the name to check through storage
	 * @return the standardized name
	 */
	public static String nameExists(Storage holder, String name)
	{
		name = name.toLowerCase();
		for(int i = 0; i < holder.playerNames.size(); i++)
		{
			String nameIter = holder.playerNames.get(i);
			nameIter = nameIter.toLowerCase();
			if(name.equals(nameIter))
			{
				return holder.playerNames.get(i);
			}
			//Handle an extra space just in case that's a problem
			String[] poss = name.split(" ");
			String[] exis = nameIter.split(" ");
			if(name.contains("d/st") && nameIter.contains("d/st"))
			{
				if(poss[1] == exis[1])
				{
					return holder.playerNames.get(i);
				}
			}
			if(poss[0].equals(exis[0]) && poss[1].equals(exis[1]))
			{
				return holder.playerNames.get(i);
			}
			//Finally, if one is one character and the other a full name
			if(poss[0] == exis[0].substring(0,1) && poss[0].length() == 1 && poss[1] == exis[1]
					&& !exis[1].contains("d/st"))
			{ 
				return holder.playerNames.get(i);
			}
			//One last case, in case of a shorter version of a name (ced/cedric)...etc,
			//while avoiding overlap from the previous if statement
			if(poss[1].equals(exis[1]) && exis[0].contains(poss[0]) 
					&& poss[0].length() > 1)
			{
				return holder.playerNames.get(i);
			}
		}
		return name + " NO MATCH FOUND";
	}
	
	/**
	 * This iterates through the priority queue and sees
	 * if the name exists within the priority queue already
	 * @param holder the storage to be parsed through
	 * @param name the PASSED IN STANDARDIZED name
	 * @return true if it exists, false if it does not
	 */
	public static PlayerObject pqExists(Storage holder, String name)
	{
		 Iterator<PlayerObject> iter = holder.players.iterator();
		 while(iter.hasNext())
		 {
			 PlayerObject player = (PlayerObject) iter.next();
			 if(player.info.name.equals(name))
			 {
				 return player;
			 }
		 }
		 return null;
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
	 * This fetches/parses the player names, then 
	 * calls another function to write it to file
	 * @param holder holds the array to be written to and written
	 * @param cont the context used to write to file in the called function
	 * @throws IOException 
	 */
	public static void fetchPlayerNames(final Context cont) throws IOException
	{
		//holder.playerNames.clear();
		List<String> names = new ArrayList<String>();
		String[] defenses = {"Bengals D/ST", "Steelers D/ST", "Browns D/ST", "Ravens D/ST", 
       		"Patriots D/ST", "Dolphins D/ST", "Bills D/ST", "Jets D/ST", "Texans D/ST",
       		"Colts D/ST", "Jaguars D/ST", "Titans D/ST", "Broncos D/ST", "Raiders D/ST",
       		"Chiefs D/ST", "Chargers D/ST", "Vikings D/ST", "Lions D/ST", "Packers D/ST",
       		"Bears D/ST", "Giants D/ST", "Eagles D/ST", "Cowboys D/ST", "Redskins D/ST",
       		"Falcons D/ST", "Saints D/ST", "Panthers D/ST", "Buccaneers D/ST", "Seahawks D/ST",
       		"49ers D/ST", "Rams D/ST", "Cardinals D/ST"	};
       	for(int i = 0; i < defenses.length; i++)
       	{
			names.add(defenses[i]);
       	}
		//The baseline URL and the endings to be attached for the sake of getting all data easily
		String url = "http://www.cbssports.com/nfl/playersearch?POSITION=";
		String[] concat = {"QB&print_rows=9999", "RB&print_rows=9999", "FB&print_rows=9999",
				"WR&print_rows=9999", "TE&print_rows=9999", "K&print_rows=9999"};
		for(int i = 0; i < concat.length; i++)
		{
			final String full = url + concat[i];
			try {
				Document doc = Jsoup.connect(full).get();
				fetchPlayersHelp(doc, names, cont, full, "row2");
				fetchPlayersHelp(doc, names, cont, full, "row1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Left out...why the hell aren't they in the list?
		names.add("Robert Griffin III");
		names.add("Brandon Jacobs");
		names.add("Kellen Winslow");
		names.add("Tim Hightower");
		names.add("Terrell Owens");
		
		storePlayerNames(names, cont);
	}
	
	/**
	 * Abstracted as cbs is stupid, same code twice
	 * @param holder the storage to write to
	 * @param cont the context because idk
	 * @param full the url to work with
	 * @param params the id to parse from
	 * @throws IOException
	 */
	public static void fetchPlayersHelp(Document doc, List<String> names, Context cont, String full, String params) throws IOException
	{
		String playerText = HandleBasicQueries.handleTablesMulti(doc, full, params);
		String[] perRow = playerText.split("\n");
		for(int j = 0; j < perRow.length; j++)
		{
			String[][] all = new String[perRow.length][];
			all[j] = perRow[j].split(" ");
			if(all[j][0].contains(","))
			{
				String lastName = all[j][0].substring(0, all[j][0].length() - 1);
				String name = all[j][1] + " " + lastName;
				names.add(name);
			}
		}
	}
	
	/**
	 * This stores the player names to the SD card, it can 
	 * only be called by fetchPlayerNames to avoid unnecessary calls
	 * @param holder holds the array to be stored
	 * @param cont used to be allowed to write to file in android
	 */
	public static void storePlayerNames(List<String> names, Context cont)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder history = new StringBuilder(1000);
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
	public static void storeAsync(Storage holder, Context cont)
	{
		final Storage stupid = new Storage();
	    
	    WriteDraft draftTask = stupid.new WriteDraft();
	    draftTask.execute(holder, cont);
	}
	
	
	/**
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	private class WriteDraft extends AsyncTask<Object, Void, Void> 
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
	    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + player.info.sos + "~~~~");
	    	}
	    	String playerString = players.toString();
	    	editor.putString("Player Values", playerString).commit();
	    	//Player names work
	    	StringBuilder names = new StringBuilder(2000);
	    	for(String name: holder.parsedPlayers)
	    	{
	    		names.append(name + ",");
	    	}
	    	String namesString = names.toString();
	    	editor.putString("Player Names", namesString).commit();
	    	//Setting up draft input
	    	String draft = "";
	    	//QB
	    	draft += handleDraftInput(holder.draft.qb, "") + "@";
	    	//RB
	    	draft += handleDraftInput(holder.draft.rb, "") + "@";
	    	//WR
	    	draft += handleDraftInput(holder.draft.wr, "") + "@";
	    	//TE
	    	draft += handleDraftInput(holder.draft.te, "") + "@";
	    	//D
	    	draft += handleDraftInput(holder.draft.def, "") + "@";
	    	//K
	    	draft += handleDraftInput(holder.draft.k, "") + "@";
	    	//Values
	    	draft += holder.draft.remainingSalary + "@" + holder.draft.value;
	    	editor.putString("Draft Information", draft);
	    	editor.commit();
			return null;
	    }
	  }
	/**
	 * Where the players themselves are written to file.
	 * Note, this is not remotely automated, so any new objects need to 
	 * be added here!
	 * @param holder the storage to iterate through
	 * @param cont the context to allow storage
	 */
	public static void storePlayers(Storage holder, Context cont)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		//Setting up player rankings input
    	String players = "";
    	for (PlayerObject player : holder.players)
    	{
    		players += 
    		Double.toString(player.values.worth) + "&&" + Double.toString(player.values.count) + "&&" +
    		Double.toString(player.values.high) + "&&" + Double.toString(player.values.low) + "&&"
    		+ player.info.name + "&&" + player.info.team + "&&" + player.info.position + "&&" + 
    		player.info.status + "&&" + player.info.adp + "&&" + player.info.bye + "&&" 
    		+ player.info.trend + "&&" + player.info.contractStatus + "&&" + player.info.sos + "~~~~";
    	}
    	players = players.substring(0, players.length() - 4);
    	editor.putString("Player Values", players);
    	//Setting up parsedPlayers input
    	String names = "";
    	for(String name: holder.parsedPlayers)
    	{
    		names += name + ",";
    	}
    	names = names.substring(0, names.length() - 1);
    	editor.putString("Player Names", names);
    	//Setting up draft input
    	String draft = "";
    	//QB
    	draft += handleDraftInput(holder.draft.qb, "");
    	draft += "@";
    	//RB
    	draft += handleDraftInput(holder.draft.rb, "");
    	draft += "@";
    	//WR
    	draft += handleDraftInput(holder.draft.wr, "");
    	draft += "@";
    	//TE
    	draft += handleDraftInput(holder.draft.te, "");
    	draft += "@";
    	//D
    	draft += handleDraftInput(holder.draft.def, "");
    	draft += "@";
    	//K
    	draft += handleDraftInput(holder.draft.k, "");
    	//Values
    	draft += "@" + holder.draft.remainingSalary + "@" + holder.draft.value;
    	editor.putString("Draft Information", draft);
    	editor.commit();
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
		Storage stupid = new Storage();

		ReadNames names = stupid.new ReadNames((Activity)cont);
		names.execute(holder, cont);
		
	    ReadDraft draft = stupid.new ReadDraft();
		draft.execute(holder, cont);
		
	    ReadRanks rankings = stupid.new ReadRanks((Activity)cont);
		String[][] data=rankings.execute(holder, cont).get();
		
	    ReadInfo info = stupid.new ReadInfo((Activity)cont);
		info.execute(holder, data);
		
	    ReadValue values = stupid.new ReadValue((Activity)cont);
		values.execute(holder, data);
		
	}
	
	/**
	 * This handles the running of the name readings
	 * in the background of the main thread
	 * @author Jeff
	 *
	 */
	private class ReadNames extends AsyncTask<Object, Void, Void> 
	{
	    ProgressDialog pdia;
	    
	    public ReadNames(Activity activity)
	    {
	    	pdia = new ProgressDialog(activity);
	    }
	    
		@Override
		protected void onPreExecute(){ 
	        super.onPreExecute(); 
	        pdia.setMessage("Please wait, reading the names...");
	        pdia.show();
		}
    
		@Override
		protected void onPostExecute(Void result){
		   pdia.dismiss();
		}
		
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	   		//Get the aggregate rankings
	   		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
    		String parsedNames = prefs.getString("Player Names", "Doesn't matter");
    		String[] namesSplit = parsedNames.split(",");
    		holder.parsedPlayers.clear();
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
	private class ReadRanks extends AsyncTask<Object, Void, String[][]> 
	{
		ProgressDialog pdia;
	    public ReadRanks(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, reading the ranks...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(String[][] result){
		   pdia.dismiss();
		}
		
	    protected String[][] doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	   		//Get the aggregate rankings
	   		holder.players.clear();
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
	 * This handles the running of the rankings in the background
	 * such that the user can't do anything until they're fetched
	 * @author Jeff
	 *
	 */
	private class ReadInfo extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
	    public ReadInfo(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, reading the info...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   pdia.dismiss();
		   //Rankings.rankingsFetched(act);
		}
		
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	String[][]allData = (String[][])data[1];
	   		//Get the aggregate rankings
	   		for(int i = 0; i < holder.players.size(); i++)
	   		{ 
	   			PlayerObject player =holder.players.get(i);
	   			player.info.sos = Integer.parseInt(allData[i][12]);
	   			player.info.contractStatus = allData[i][11];
	   			player.info.trend = allData[i][10];
	   			player.info.bye = allData[i][9];
	   			player.info.adp = allData[i][8];
	   			player.info.status = allData[i][7];
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
		ProgressDialog pdia;
		Activity act;
	    public ReadValue(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	        act = activity;
	    }
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, reading the values...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Void result){
		   pdia.dismiss();
		   Rankings.rankingsFetched(act);
		}
		
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	String[][]allData = (String[][])data[1];
	   		for(int i = 0; i < holder.players.size(); i++)
	   		{ 
	   			PlayerObject player = holder.players.get(i);
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
	    
		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
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
	 * Stores the posts to file to avoid unnecessary calls
	 * @param holder
	 * @param cont
	 */
	public static void writePosts(Storage holder, Context cont) 
	{
    	StringBuilder posts = new StringBuilder(3000);
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
	 * Writes the list of trending players to file
	 * @param trendingPlayers
	 * @param cont
	 */
	public void writePostsList(List<String> trendingPlayers, Activity cont) 
	{
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	String posts = "";
    	for(int i = 0; i < trendingPlayers.size(); i++)
    	{
    		String post = trendingPlayers.get(i);
    		posts += post + "##";
    	}
    	editor.putString("Posted Players", posts);
    	editor.commit();
	}
}
