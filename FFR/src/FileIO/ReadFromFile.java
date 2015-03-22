package FileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.ReadDraft;
import AsyncTasks.StorageAsyncTask.ReadNamesList;
import AsyncTasks.StorageAsyncTask.ReadPosts;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Flex;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.Pages.Home;
/**
 * A library of all the functions that will read
 * from file
 * @author Jeff
 *
 */
public class ReadFromFile {
	private static StorageAsyncTask readFromFileAsyncObj = new StorageAsyncTask();
	
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
     * Only reads the draft from file
     * @param holder
     * @param cont
     */
    public static void readDraft(Storage holder, Context cont)
    {
    	SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
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
		holder.playerNames = (HashSet<String>) prefs.getStringSet("Player Names", null);
	}

	/**
	 * It checks if players are written to file. If so, it fetches them and
	 * re-adds them to the priority queue, but if it isn't, it calls the runRankings
	 * function, which is the function that calls all the rankings functions and the high 
	 * level stuff
	 */
	public static void fetchPlayers(Set<String> checkExists, Storage holder, Context cont, int i) 
	{
    	long start = System.nanoTime();
		
	    ReadDraft draft = readFromFileAsyncObj.new ReadDraft((Activity)cont, i);
		draft.execute(holder, cont, start, checkExists);		
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
			if(qb.length() > 3)
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
			return prefs.getInt("Filter Quantity Size Rankings", 50);
		}
		return prefs.getInt("Filter Quantity Size", 50);
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
	 * Reads the watch list from file
	 * @param cont
	 * @return
	 */
	public static List<String> readWatchList(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		List<String> watchList = new ArrayList<String>();
		String watch = prefs.getString("Watch List", "");
		String[] watchSet = ManageInput.tokenize(watch, '-', 4);
		for(String player : watchSet)
		{
			if(player.length() > 3)
			{
				watchList.add(player);
			}
		}
		return watchList;
	}
	
	/**
	 * Reads scoring from file and returns it
	 */
	public static Scoring readScoring(Context cont)
	{
		Scoring scoring = new Scoring();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		scoring.passYards = prefs.getInt("Pass Yards", 25);
		scoring.passTD = prefs.getInt("Pass Touchdowns", 4);
		scoring.rushYards = prefs.getInt("Rush Yards", 10);
		scoring.rushTD = prefs.getInt("Rush Touchdowns", 6);
		scoring.recYards = prefs.getInt("Receiving Yards", 10);
		scoring.recTD = prefs.getInt("Receiving Touchdowns", 6);
		scoring.catches = prefs.getInt("Catches", 1);
		scoring.interception = prefs.getInt("Interceptions", 2);
		scoring.fumble = prefs.getInt("Fumbles", 2);
		return scoring;
	}
	
	/**
	 * Reads scoring from file and returns it
	 */
	public static Scoring readScoring(Context cont, String key)
	{
		Scoring scoring = new Scoring();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		scoring.passYards = prefs.getInt("Pass Yards" + key, 25);
		scoring.passTD = prefs.getInt("Pass Touchdowns" + key, 4);
		scoring.rushYards = prefs.getInt("Rush Yards" + key, 10);
		scoring.rushTD = prefs.getInt("Rush Touchdowns" + key, 6);
		scoring.recYards = prefs.getInt("Receiving Yards" + key, 10);
		scoring.recTD = prefs.getInt("Receiving Touchdowns" + key, 6);
		scoring.catches = prefs.getInt("Catches" + key, 1);
		scoring.interception = prefs.getInt("Interceptions" + key, 2);
		scoring.fumble = prefs.getInt("Fumbles" + key, 2);
		return scoring;
	}
	
	/**
	 * Returns the roster object
	 */
	public static Roster readRoster(Context cont)
	{
		Roster roster = new Roster();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		roster.teams = prefs.getInt("Number of teams", 10);
		roster.qbs = prefs.getInt("Starting QBs", 1);
		roster.rbs = prefs.getInt("Starting RBs", 2);
		roster.wrs = prefs.getInt("Starting WRs", 2);
		roster.tes = prefs.getInt("Starting TEs", 1);
		roster.flex = readFlex(cont);
		roster.def = prefs.getInt("Starting Defs", 1);
		roster.k = prefs.getInt("Starting Ks", 1);
		
		return roster;
	}
	
	/**
	 * Returns the roster object
	 */
	public static Roster readRoster(Context cont, String key)
	{
		Roster roster = new Roster();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		roster.teams = prefs.getInt("Number of teams" + key, 10);
		roster.qbs = prefs.getInt("Starting QBs" + key, 1);
		roster.rbs = prefs.getInt("Starting RBs" + key, 2);
		roster.wrs = prefs.getInt("Starting WRs" + key, 2);
		roster.tes = prefs.getInt("Starting TEs" + key, 1);
		roster.flex = readFlex(cont, key);
		roster.def = prefs.getInt("Starting Defs" + key, 1);
		roster.k = prefs.getInt("Starting Ks" + key, 1);
		return roster;
	}
	
	/**
	 * Reads the roster flex options from file
	 * @param cont
	 * @return
	 */
	private static Flex readFlex(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		int rbwr = prefs.getInt("Starting RB/WRs", 0);
		int rbwrte = prefs.getInt("Starting RB/WR/TEs", 0);
		int op = prefs.getInt("Starting OPs", 0);
		if(rbwr == 0 && rbwrte == 0 && op == 0)
		{
			return null;
		}
		return new Flex(rbwr, rbwrte, op);
	}
	
	/**
	 * Reads the roster flex options from file
	 * @param cont
	 * @return
	 */
	private static Flex readFlex(Context cont, String key)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		int rbwr = prefs.getInt("Starting RB/WRs" + key, 0);
		int rbwrte = prefs.getInt("Starting RB/WR/TEs" + key, 0);
		int op = prefs.getInt("Starting OPs" + key, 0);
		if(rbwr == 0 && rbwrte == 0 && op == 0)
		{
			return null;
		}
		return new Flex(rbwr, rbwrte, op);
	}
	
	/**
	 * Reads if it's the first usage of the app, defaulting to true
	 */
	public static boolean readFirstOpen(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		return prefs.getBoolean("First Open", true);
	}
	
	/**
	 * Reads from file to see if it is an auction draft
	 */
	public static boolean readIsAuction(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		return prefs.getBoolean("Is Auction", true);
	}
	
	/**
	 * Reads the use id from file
	 */
	public static long readUseID(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getLong("Use ID", -1);
	}
	
	/**
	 * Reads the token from file
	 */
	public static String readToken(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getString("Token", "Not set");
	}
	
	/**
	 * Reads the token secret from file
	 */
	public static String readTokenSecret(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getString("Token Secret", "Not set");
	}
	
	/**
	 * Reads the o line ranks from file, setting them to the hash map
	 * @param holder
	 * @param cont
	 */
	public static void readTeamData(Storage holder, Context cont)
	{
		holder.oLineAdv.clear();
		holder.draftClasses.clear();
		holder.sos.clear();
		holder.bye.clear();
		holder.fa.clear();
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		String oLineStr = prefs.getString("Team By Team Data", "Not##Set");
		String[] totalSet = oLineStr.split("@#@#");
		try{
			//OLine Advanced
			String[] perLevel = totalSet[0].split("%%%");
			for(String team : perLevel)
			{
				holder.oLineAdv.put(team.split("##")[0], team.split("##")[1]);
			}
			//Drafts
			perLevel = totalSet[1].split("%%%");
			for(String team : perLevel)
			{
				holder.draftClasses.put(team.split("##")[0], team.split("##")[1]);
			}
			//SOS
			perLevel = totalSet[2].split("%%%");
			for(String team : perLevel)
			{
				if(team.split("##")[0].equals(" ")){
					break;
				}
				else
				{
					holder.sos.put(team.split("##")[0], Integer.parseInt(team.split("##")[1]));
				}
			}
			//Bye
			perLevel = totalSet[3].split("%%%");
			for(String team : perLevel)
			{
				holder.bye.put(team.split("##")[0], team.split("##")[1]);
			}
			//FA
			perLevel = totalSet[4].split("%%%");
			for(String team : perLevel)
			{
				String[] fa = team.split("##")[1].split("&&");
				List<String> faList = new ArrayList<String>();
				faList.add(fa[0]);
				faList.add(fa[1]);
				holder.fa.put(team.split("##")[0], faList);
			}
			 perLevel = totalSet[5].split("%%%");
			 for(String note : perLevel)
			 {
			 	holder.notes.put(note.split("##")[0], note.split("##")[1]);
			 }
			 
		}catch(ArrayIndexOutOfBoundsException e)
		{
			//OLine Advanced
			String[] perLevel = totalSet[1].split("%%%");
			for(String team : perLevel)
			{
				holder.draftClasses.put(team.split("##")[0], team.split("##")[1]);
			}
			//SOS
			perLevel = totalSet[2].split("%%%");
			for(String team : perLevel)
			{
				if(team.split("##")[0].equals(" ")){
					break;
				}
				else
				{
					holder.sos.put(team.split("##")[0], Integer.parseInt(team.split("##")[1]));
				}
			}
			//Bye
			perLevel = totalSet[3].split("%%%");
			for(String team : perLevel)
			{
				holder.bye.put(team.split("##")[0], team.split("##")[1]);
			}
			//FA
			perLevel = totalSet[4].split("%%%");
			for(String team : perLevel)
			{
				String[] fa = team.split("##")[1].split("&&");
				List<String> faList = new ArrayList<String>();
				faList.add(fa[0]);
				faList.add(fa[1]);
				holder.fa.put(team.split("##")[0], faList);
			}			
		}
		return;
	}


	/**
	 * Reads the current draft
	 * @param cont
	 * @return
	 */
	 static int readCurrDraft(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getInt("Current Draft", 0);
	}
	
	/**
	 * Reads the secondary data from file
	 * @param cont
	 * @return
	 */
	public static List<String> readSecData(Context cont)
	{
		List<String>secData = new ArrayList<String>();
		int max = readCurrDraft(cont);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		for(int i = 0; i < max; i++)
		{
			secData.add(prefs.getString("Secondary " + i, "Doesn't matter"));
		}
		if(secData.size() == 0)
		{
			secData.add("Save a draft to see it here.");
		}
		return secData;
	}
	
	/**
	 * Reads the primary data from file
	 * @param cont
	 * @return
	 */
	public static List<String> readPrimData(Context cont)
	{
		List<String>secData = new ArrayList<String>();
		int max = readCurrDraft(cont);
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		for(int i = 0; i < max; i++)
		{
			secData.add(prefs.getString("Primary " + i, "Doesn't matter"));
		}
		if(secData.size() == 0)
		{
			secData.add("No drafts saved.");
		}
		return secData;
	}

	
	/**
	 * Returns the auction factor as input by the user, defaulting to 1
	 * @param cont
	 * @return
	 */
	public static double readAucFactor(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return (double)prefs.getFloat("Auction Factor", (float)1.0);
	}

	public static boolean firstIsRegularSeason(Context cont) {
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0);
		boolean needsRefresh = prefs.getBoolean("Is regular season new " + Home.yearKey, true);
		if(needsRefresh){
			WriteToFile.writeFirstIsRegularSeason(cont);
		}
		return needsRefresh;
	}
}
