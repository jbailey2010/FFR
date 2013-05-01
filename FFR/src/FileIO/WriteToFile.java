package FileIO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
/**
 * A library of all of the functions that will write to 
 * file 
 * @author Jeff
 *
 */
public class WriteToFile {

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
		final WriteToFile stupid = new WriteToFile();
	    
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
	 * Writes the list of trending players to file
	 * @param trendingPlayers
	 * @param cont
	 */
	public static void writePostsList(List<String> trendingPlayers, Activity cont) 
	{
    	SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
    	StringBuilder posts = new StringBuilder(5000);
    	for(int i = 0; i < trendingPlayers.size(); i++)
    	{
    		posts.append(trendingPlayers.get(i) + "##");
    	}
    	editor.putString("Posted Players", posts.toString());
    	editor.commit();
	}

	
}
