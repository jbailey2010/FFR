package FileIO;

import java.text.DateFormat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeff.isawesome.fantasyfootballrankings.R;
import twitter4j.auth.AccessToken;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.Pages.Rankings;

import AsyncTasks.StorageAsyncTask;
import AsyncTasks.StorageAsyncTask.WriteDraft;
import AsyncTasks.StorageAsyncTask.WritePostsListAsync;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.widget.TextView;
/**
 * A library of all of the functions that will write to  
 * file 
 * @author Jeff
 *
 */
public class WriteToFile {
	final static StorageAsyncTask asyncObject = new StorageAsyncTask();


	/**
	 * This stores the player names to the SD card, it can 
	 * only be called by fetchPlayerNames to avoid unnecessary calls
	 * @param holder holds the array to be stored
	 * @param cont used to be allowed to write to file in android
	 */
	public static void storePlayerNames(List<String> names, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		StringBuilder history = new StringBuilder(10000);
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
	public static void storeRankingsAsync(Storage holder, Context cont)
	{
	    WriteDraft draftTask = asyncObject.new WriteDraft();
	    draftTask.execute(holder, cont);
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
	 * Stores the posts to file to avoid unnecessary calls
	 * @param holder
	 * @param cont
	 */
	public static void writePosts(Storage holder, Context cont) 
	{
		StringBuilder posts = new StringBuilder(10000);
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
	 * Writes the list of trending players to file
	 * @param trendingPlayers
	 * @param cont
	 */
	public static void writePostsList(List<String> trendingPlayers, Activity cont) 
	{
	    WritePostsListAsync draftTask = asyncObject.new WritePostsListAsync();
	    draftTask.execute(trendingPlayers, cont);
	}
	
	

	/**
	 * Just writes the filter size to file for later usage
	 * @param cont
	 * @param size
	 */
	public static void writeFilterSize(Context cont, int size, String flag)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	if(flag.equals("Rankings"))
    	{
        	editor.putInt("Filter Quantity Size Rankings", size);
    	}
    	else
    	{
    		editor.putInt("Filter Quantity Size", size);
    	}
    	editor.commit();
	}
	
	/**
	 * Writes men in box ratios to file
	 * @param cont
	 * @param players
	 */
	public static void writeMenInBox(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder menInBox = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		menInBox.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("Men In Box", menInBox.toString()).commit();
    }
	
	/**
	 * Writes pass-run ratio to file
	 * @param cont
	 * @param players
	 */
	public static void writePassRun(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder runPassRatio = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		runPassRatio.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("Run Pass Ratio", runPassRatio.toString()).commit();
	}
	
	/**
	 * Writes offensive line ranks to file
	 * @param cont
	 * @param players
	 */
	public static void writeOLineRanks(Context cont, Map<String, String> players)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder oLineRanks = new StringBuilder(10000);
    	Set<String> keys = players.keySet();
    	for(String elem : keys)
    	{
    		oLineRanks.append(elem + "~~" + players.get(elem) + "@@#");
    	}
    	editor.putString("O Line Ranks", oLineRanks.toString()).commit();
	}
	
	/**
	 * Writes the last filter to file for trending
	 * @param cont
	 * @param lastFilter
	 */
	public static void writeLastFilter(Context cont, int lastFilter)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	editor.putInt("Last Filter", lastFilter).commit();
	}
	
	/**
	 * Writes the rotoworld news set to file
	 * @param cont
	 * @param news
	 */
	public static void writeNewsRoto(Context cont, List<NewsObjects> news, boolean rh, boolean rp, 
			boolean th, boolean cbs, boolean si)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(NewsObjects newsObj : news)
    	{
    		newsSet.append(newsObj.news + "~~" + newsObj.impact + "~~" + 
    				newsObj.date + "@@@");
    	}
    	editor.putBoolean("Use Headlines", rh);
    	editor.putBoolean("Use Player News", rp);
    	editor.putBoolean("Use The Huddle", th);
    	editor.putBoolean("Use CBS News", cbs);
    	editor.putBoolean("Use SI News", si);
    	editor.putString("News RotoWorld", newsSet.toString()).commit();
	}
	
	/**
	 * Writes the twitter feed data to file
	 * @param cont
	 * @param news
	 * @param selection
	 */
	public static void writeNewsTwitter(Context cont, List<NewsObjects> news, String selection)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(NewsObjects newsObj : news)
    	{
    		newsSet.append(newsObj.news + "~~" + newsObj.impact + "~~" + 
    				newsObj.date + "@@@");
    	}
    	editor.putString("News RotoWorld", newsSet.toString()).commit();
    	editor.putString("Selected Twitter Feed", selection).commit();
	}
	
	/**
	 * Writes the news selection to file
	 * @param cont
	 * @param selection
	 */
	public static void writeNewsSelection(Context cont, String selection)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	editor.putString("News Title", selection).commit();
	}
	
	/**
	 * Writes the watch list to file
	 */
	public static void writeWatchList(Context cont, List<String> watch)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder newsSet = new StringBuilder(10000);
    	for(String name : watch)
    	{
    		if(name.length() > 3 && name.charAt(0)!=' ')
    		{
    			newsSet.append(name + "----");
    		}
    	}
    	editor.putString("Watch List", newsSet.toString()).commit();
	}
	
	/**
	 * Writes the draft to file (supplementary to writing rankings to file)
	 */
	public static void writeDraft(Draft draft, Context cont)
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	String draftList = "";
    	draftList += writeDraftHelper(draft.qb) + "@";
    	draftList += writeDraftHelper(draft.rb) + "@";
    	draftList += writeDraftHelper(draft.wr) + "@";
    	draftList += writeDraftHelper(draft.te) + "@";
    	draftList += writeDraftHelper(draft.def)+ "@";
    	draftList += writeDraftHelper(draft.k)  + "@";
    	StringBuilder inter = new StringBuilder(1000);
    	for(String name : draft.ignore)
    	{
    		inter.append(name + "~");
    	}
    	if(inter.length() < 3)
    	{
    		inter.append(" ");
    	}
    	draftList += inter.toString() + "@"; 
    	draftList += draft.remainingSalary + "@" + draft.value;
    	editor.putString("Draft Information", draftList).commit();
	}
	
	/**
	 * Writes scoring to file
	 */
	public static void writeScoring(Context cont, Scoring scoring)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putInt("Pass Yards", scoring.passYards);
		editor.putInt("Pass Touchdowns", scoring.passTD);
		editor.putInt("Rush Yards", scoring.rushYards);
		editor.putInt("Rush Touchdowns", scoring.rushTD);
		editor.putInt("Receiving Yards", scoring.recYards);
		editor.putInt("Receiving Touchdowns", scoring.recTD);
		editor.putInt("Catches", scoring.catches);
		editor.putInt("Interceptions", scoring.interception);
		editor.putInt("Fumbles", scoring.fumble);
		editor.putBoolean("Is Scoring Set?", true);
		editor.commit();
	}
	
	/**
	 * Writes roster to file
	 */
	public static void writeRoster(Context cont, Roster roster)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putInt("Number of teams", roster.teams);
		editor.putInt("Starting QBs", roster.qbs);
		editor.putInt("Starting RBs", roster.rbs);
		editor.putInt("Starting WRs", roster.wrs);
		editor.putInt("Starting TEs", roster.tes);
		editor.putInt("Starting flexes", roster.flex);
		editor.putBoolean("Is roster set?", true);
		editor.commit();
	}
	
	/**
	 * Writes that the app had been opened
	 * @param cont
	 */
	public static void writeFirstOpen(Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putBoolean("First Open", false).commit();
	}
	
	/**
	 * Writes to file if it's an auciton
	 * @param aucFactor, Storage holder 
	 */
	public static void writeIsAuction(Boolean isAuction, Context cont, double aucFactor, Storage holder)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putBoolean("Is Auction", isAuction).commit();
		editor.putFloat("Auction Factor", (float) aucFactor).commit();
	}
	
	/**
	 * A helper for writing rankings to file
	 */
	public static String writeDraftHelper(List<PlayerObject> list)
	{
		StringBuilder retSet = new StringBuilder(5000);
		int counter = -1;
		for(PlayerObject player : list)
		{
			retSet.append(player.info.name + "~");
			counter ++;
		}
		if(counter == -1)
		{
			retSet.append(" ");
		}
		return retSet.toString();
	}
	
	/**
	 * Writes the use ID to file
	 */
	public static void storeID(long l, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putLong("Use ID", l);
		editor.commit();
	}
	
	/**
	 * Writes the token data to file to be read later
	 */
	public static void storeToken(AccessToken token, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putString("Token", token.getToken());
		editor.putString("Token Secret", token.getTokenSecret());
		editor.commit();
	}
	
	/**
	 * Writes the o line rankings to file
	 * @param holder
	 * @param cont
	 */
	public static void writeTeamData(Storage holder, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		StringBuilder oLineRanks = new StringBuilder(10000);
		
		//Oline ranks
		Set<String> keys = holder.oLineRanks.keySet();
		for(String key : keys)
		{
			oLineRanks.append(key + "##" + holder.oLineRanks.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");
		
		//Oline advanced
		Set<String> keysAdv = holder.oLineAdv.keySet();
		for(String key : keysAdv)
		{
			oLineRanks.append(key + "##" + holder.oLineAdv.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");
		
		//Draft classes
		Set<String> keysDraft = holder.draftClasses.keySet();
		for(String key : keysDraft)
		{
			oLineRanks.append(key + "##" + holder.draftClasses.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");
		
		//Sos
		Set<String> keysSos = holder.sos.keySet();
		for(String key : keysSos)
		{
			oLineRanks.append(key + "##" + holder.sos.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");
		
		//Bye
		Set<String> keysBye = holder.bye.keySet();
		for(String key : keysBye)
		{
			oLineRanks.append(key + "##" + holder.bye.get(key) + "%%%");
		}
		oLineRanks.append("@#@#");
		
		//FA
		Set<String> keysFA = holder.fa.keySet();
		for(String key : keysFA)
		{
			List<String> fa = holder.fa.get(key);
			if(fa.size() == 0)
			{
				oLineRanks.append("Signed Free Agents: &&Departing Free Agents: ");
			}
			else
			{
				oLineRanks.append(key + "##" + fa.get(0) + "&&" + fa.get(1) + "%%%");
			}
		}
		
		editor.putString("Team By Team Data", oLineRanks.toString());
		editor.commit();
	}
	
	/**
	 * Writes hiding the widget to file
	 * @param hide
	 * @param cont
	 */
	public static void writeHideWidget(boolean hide, Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putBoolean("Hide Widget", hide).commit();
	}
	
	/**
	 * Stores a draft's data
	 * @param holder
	 * @param cont
	 * @param teamName
	 * @param teamCount
	 * @param noteStr 
	 */
	public static void writeDraftData(Storage holder, Context cont, String teamName, int teamCount, String noteStr)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		int nextDraft = ReadFromFile.readCurrDraft(cont) + 1;
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder secondaryData = new StringBuilder(1000);
		secondaryData.append("PAA: " + df.format(Draft.paaTotal(holder.draft)));
		if(ReadFromFile.readIsAuction(cont))
		{
			secondaryData.append("\nValue: " + df.format(holder.draft.value));
			secondaryData.append("\nAverage leverage: " + Draft.averageLev(holder.draft));
			secondaryData.append("\nPAA per dollar: " + df.format(Draft.paaTotal(holder.draft)/((200.0/ReadFromFile.readAucFactor(cont))
					- holder.draft.remainingSalary)));
		}
		String qbs = Rankings.handleDraftParsing(holder.draft.qb);
    	String rbs = Rankings.handleDraftParsing(holder.draft.rb);
    	String wrs = Rankings.handleDraftParsing(holder.draft.wr);
    	String tes = Rankings.handleDraftParsing(holder.draft.te);
    	String ds = Rankings.handleDraftParsing(holder.draft.def);
    	String ks = Rankings.handleDraftParsing(holder.draft.k);
    	StringBuilder team = new StringBuilder(10000);
    	if(noteStr.length() > 1)
    	{
    		noteStr= "Comment: " + noteStr + "\n";
    	}
    	team.append(teamName + ": " + teamCount + " team league\n" + noteStr + "\n");
    	team.append("Quarterbacks: " + qbs + "\n");
    	team.append("Running Backs: " + rbs + "\n");
    	team.append("Wide Receivers: " + wrs + "\n");
    	team.append("Tight Ends: " + tes + "\n");
    	team.append("D/ST: " + ds + "\n");
    	team.append("Kickers: " + ks + "\n");
    	editor.putString("Primary " + ReadFromFile.readCurrDraft(cont), team.toString());
    	editor.putString("Secondary " + ReadFromFile.readCurrDraft(cont), secondaryData.toString());
		editor.putInt("Current Draft", nextDraft).commit();
	}
	
	/**
	 * Clears the draft data
	 * @param cont
	 */
	public static void clearDraftData(Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		int max = ReadFromFile.readCurrDraft(cont);
		for(int i = 0; i < max; i++)
		{
			editor.remove("Primary " + i);
			editor.remove("Secondary " + i);
		}
		editor.remove("Current Draft");
		editor.commit();
	}
	
	/**
	 * Writes the leverage data to file
	 * @param cont
	 * @param holder
	 */
	public static void writeLeverage(Context cont, Storage holder)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		for(PlayerObject player : holder.players)
		{
			editor.putFloat(player.info.name + player.info.team + player.info.position + "Cost", (float) player.values.relPrice);
			editor.putFloat(player.info.name + player.info.team + player.info.position + "Points", (float) player.values.relPoints);
		}
		editor.commit();
	}
}
