package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.FetchTrends;
import FileIO.ReadFromFile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.Pages.Trending;
/**
 * A library to allow for parsing of trending players
 * @author Jeff
 *
 */
public class ParseTrending 
{
	public static List<Post> posts = new ArrayList<Post>(500);
	public static Context context;
	public static Context inter;
	public static Storage holder;
	public static HashMap<String, String> fixes = new HashMap<String, String>();

	/**
	 * Calls the controller to handle each url.
	 * This does targets, sleepers, and rookies as of now.
	 * @return 
	 * @throws IOException
	 */
	public static void trendingPlayers(final Storage holder, final Context cont) throws IOException
	{
		context = cont;
		ParsingAsyncTask stupid = new ParsingAsyncTask();
	    FetchTrends task = stupid.new FetchTrends((Activity)context, holder);
	    task.execute(holder, cont);
	}

	
	/**
	 * Sets up a list only containing the posts within the correct bounds of dates
	 * @param holder the storage to get posts from
	 * @param length the length to check relative to
	 * @param cont the context for later
	 * @throws ParseException in case my firm date format goes kablooey
	 * @throws IOException 
	 */
	public static void setUpLists(Storage holderIn, int length, Context cont) throws ParseException, IOException
	{
		holder = holderIn;
		posts.clear();
		if(length == 365)
		{
			for(Post e:holder.posts)
			{
				posts.add(e);
			}
		}
		else
		{
			for(Post e: holder.posts)
			{
				int len = handleDays(e.date);
				if(len<= length)
				{
					posts.add(e);
				}
			}
		}
		ParseTrending stupid = new ParseTrending();
	    ParseTrends task = stupid.new ParseTrends((Activity)cont);
	    task.execute(holder, cont);
	}
	
	/**
	 * A back end function to do the actual parsing
	 * for trends
	 * @author Jeff
	 *
	 */
	private class ParseTrends extends AsyncTask<Object, Void, Void> 
	{
		ProgressDialog pdia;
		Activity act;
	    public ParseTrends(Activity activity) 
	    {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        act = activity;
	    }
		@Override
		protected void onPreExecute(){ 
			super.onPreExecute();
		    pdia.setMessage("Please wait, checking the posts. This could take a few minutes...");
		    pdia.show();    
		    
		}

		@Override
		protected void onPostExecute(Void result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   posts.clear();
		   ((Trending)act).intermediateHandleTrending(holder, act);
		}
    	
	    @Override
	    protected Void doInBackground(Object... data) 
	    {
	    	Storage holder = (Storage) data[0];
	    	Context cont = (Context) data[1];
	    	setUpFixes(holder);
	    	inter = cont;
			try {
				filterComments(holder, cont);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
	    }
	  }
	/**
	 * Makes sure it's within a proper bounds, then right, then if
	 * it exists within proper bounds
	 * @param holder
	 * @param length
	 * @param cont
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void filterComments(Storage holder,Context cont) throws ParseException, IOException 
	{
		holder.postedPlayers.clear();
		if(holder.playerNames.size() == 0)
		{
			ReadFromFile.fetchNames(holder, cont);
		}
		HashMap<String, PostedPlayer> playerSet = new HashMap<String, PostedPlayer>();
		HashSet<String> playerNames = new HashSet<String>();
		for(int i = 0; i < posts.size(); i++)
		{
			Post elem = posts.get(i);
			int len = handleDays(elem.date);
			String text = elem.text;
			text = text.replaceAll("[.,/?]", " ");
			String[] postWords = ManageInput.tokenize(text, ' ', 1);
			for(int j = 0; j < postWords.length-1; j++)
			{
				String firstName = postWords[j].replaceAll("\\s", "");
				String lastName = postWords[j+1].replaceAll("\\s", "");
				String capFirst = firstName;
				String capLast = lastName;
				if(firstName.length() > 0 && lastName.length() > 0 && 
						Character.isLetter(lastName.charAt(0)) && Character.isLetter(firstName.charAt(0)))
				{
					capFirst = Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1);
					capLast = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);
				} 
				String nameB = capFirst + " " + capLast;
				if(holder.playerNames.contains(nameB))
				{
					if(playerNames.contains(nameB)){
						PostedPlayer p = playerSet.get(nameB);
						p.count += 1;
						p.times.add(len);
						playerSet.put(nameB, p);
					}
					else
					{
						PostedPlayer newPlayer = new PostedPlayer(nameB, 1);
						newPlayer.times.add(len);
						playerSet.put(nameB, newPlayer);
						playerNames.add(nameB);
					}
				}
				else 
				{
					firstName = commonFixes(firstName);
					lastName = commonFixes(lastName);
					boolean first = false;
					if(!firstName.equals(""))
					{
						if(Character.isUpperCase(firstName.charAt(0)))
						{
							first = true;
						}
					}
					boolean second = false;
					if(!lastName.equals(""))
					{
						if(Character.isUpperCase(lastName.charAt(0)))
						{
							first = true;
						}
					}
					if(first || second)
					{
						boolean found = false;
						int key = -1;
						int t = 0;
						String foundKey = "";
						for(String player : holder.playerNames)
						{
							if((player.contains(firstName) && firstName.length() > 3)
									|| (player.contains(lastName) && lastName.length() > 3))
							{
								if(found)
								{
									found = false;
									break;
								}
								key = t;
								foundKey = player;
								found = true;
							}
							t++;
						}
						if(found && key != -1)
						{
							if(playerNames.contains(foundKey)){
								PostedPlayer p = playerSet.get(foundKey);
								p.count += 1;
								p.times.add(len);
								playerSet.put(foundKey, p);
							}
							else if(!foundKey.contains("D/ST"))
							{
								PostedPlayer newPlayer = new PostedPlayer(foundKey, 1);
								newPlayer.times.add(len);
								playerSet.put(foundKey, newPlayer);
								playerNames.add(foundKey);
							}
						}
					}
				}
			}
		}
		for(String name : playerNames){
			holder.postedPlayers.add(playerSet.get(name));
		}
	}
	
	/**
	 * Sets up the fixes hashmap
	 * @param holder 
	 */
	public static void setUpFixes(Storage holder)
	{
		for(String playerName : holder.playerNames){
			Character firstLetter = playerName.split(" ")[0].charAt(0);
			String lastName = "";
			if(playerName.split(" ").length == 2){
				lastName = playerName.split(" ")[1];
			}
			else{
				if(playerName.split(" ")[2].equals("III")){
					lastName = playerName.split(" ")[1];
				}
				else{
					lastName = playerName.split(" ")[2];
				}
			}
			fixes.put((firstLetter + "." + lastName).toLowerCase(), playerName);
			fixes.put((firstLetter + lastName).toLowerCase(), playerName);
		}
		fixes.put("megatron", "Calvin Johnson");
		fixes.put("calvin", "Calvin Johnson");
		fixes.put("matthews", "Ryan Mathews");
		fixes.put("djax", "DeSean Jackson");
		fixes.put("gronk", "Rob Gronkowski");
		fixes.put("sjax", "Steven Jackson");
		fixes.put("s.jax", "Steven Jackson");
		fixes.put("marshall", "Brandon Marshall");
		fixes.put("brady", "Tom Brady");
		fixes.put("kaep", "Colin Kaepernick");
		fixes.put("iii", "iii");
		fixes.put("payton", "payton");
		fixes.put("rgiii", "Robert Griffin III");
		fixes.put("rg3", "Robert Griffin III");
		fixes.put("vick", "Michael Vick");
		fixes.put("jerry", "jerry");
		fixes.put("jstew", "Jonathan Stewart");
		fixes.put("dmc", "Darren McFadden");
		fixes.put("harvin","Percy Harvin");
		fixes.put("dola", "Danny Amendola");
		
		fixes.put("still", "still");
		fixes.put("round", "round");
		fixes.put("i'm", "i'm");
		fixes.put("i'll", "i'll");
		fixes.put("i'd", "i'd");
		fixes.put("it's", "it's");
		fixes.put("both", "both");
		fixes.put("tds","tds");
		fixes.put("t",  "t");
		fixes.put("qb", "qb");
		fixes.put("qbs", "qb");
		fixes.put("well","well");
		fixes.put("still", "still");
		fixes.put("true","true");
		fixes.put("i", "i");
		fixes.put("qb's", "qb's");
		fixes.put("houston", "houston");
		fixes.put("double-check","double-check");
		fixes.put("super", "super");
		fixes.put("you", "you");
		fixes.put("they", "they");
		fixes.put("we", "we");
		fixes.put("would", "would");
		fixes.put("the", "the");
		fixes.put("they're", "they're");
		fixes.put("he", "he");
		fixes.put("bateman", "bateman");
		fixes.put("ol", "ol");
		fixes.put("o-lineman", "ol");
		fixes.put("jeffcoat", "jeffcoat");
		fixes.put("take", "take");
		fixes.put("hit", "hit");
		fixes.put("taker", "taker");
		fixes.put("lemon", "lemon");
		fixes.put("have", "have");
		fixes.put("take", "nnnn");
		fixes.put("bump", "bump");
		fixes.put("may", "may");
		fixes.put("there's", "there's");
		fixes.put("seahags", "seahags");
		fixes.put("les", "nnnn");
		fixes.put("myles", "nnnn");
		fixes.put("emery", "emery");
		fixes.put("niners", "niners");
	}

	/**
	 * A very very very subjective function that will
	 * make some common fixes to player names
	 * @param inName
	 * @return
	 */
	public static String commonFixes(String inName)
	{
		String name = inName.toLowerCase();
		if(fixes.containsKey(name))
		{
			return fixes.get(name);
		}
		return inName;
	}
	/**
	 * Finds the difference in date between the current time and a passed
	 * in date. Good for filters on trending and trending only.
	 * @param holder the storage that isn't used whatsoever here
	 * @param date the date of the post, passed in
	 * @return
	 * @throws ParseException 
	 */
	public static int handleDays(String date) throws ParseException
	{
		if(date.contains("Yesterday"))
		{
			return 1;
		}
		else if(date.contains("Today"))
		{
			return 0;
		}
		else if(date.equals(""))
		{
			return 365;
		}
		String[] dateStuff = date.split("-"); 
		String useThis = dateStuff[0];
		useThis = useThis.replace(" ", "-");
		useThis = useThis.substring(0, useThis.length() - 1);
		dateStuff = useThis.split("-");
		String finalDate = dateStuff[1] + " " + dateStuff[0] + ", " + dateStuff[2];
		Date postDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(finalDate);
		double mathStuff = ((new java.util.Date()).getTime() - postDate.getTime());
    	return (int)Math.abs(mathStuff / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * All the code is the same per url, so this is the de facto controller that 
	 * handles it for each
	 * @param holder the storage to write to later
	 * @param url  the url to parse
	 * @throws IOException
	 */
	public static void getPosts(Storage holder, String url) throws IOException
	{
		int length = trendingPlayersLength(url);
		holder.posts.addAll(parseForum(length, url));
	}
	
	/**
	 * Actually parses the forum, returning the parsed text from
	 * across all of the pages
	 * @param length the number ofpages to be parsed
	 * @param url the url to parse
	 * @return the text from all the responses
	 * @throws IOException
	 */
	public static List<Post> parseForum(int length, String url) throws IOException
	{
		List<Post> posts = new ArrayList<Post>();
		for(int i = 0; i < length; i++) 
		{
			String newUrl = url + Integer.toString(i*20);
			Document doc = Jsoup.connect(newUrl).timeout(0).get();
			List<String> pagePost = HandleBasicQueries.handleListsMulti(doc, newUrl, "div.post.entry-content");
			List<String> datesPost = HandleBasicQueries.handleListsMulti(doc, newUrl, "abbr.published");
			for(int j = 0; j < pagePost.size(); j++){
				posts.add(new Post(pagePost.get(j), datesPost.get(j)));
			}
		}
		return posts;
	}
	
	/**
	 * Parses the number of pages in a rotoworld forum
	 * Note: the parameters in the query are hard-coded, as they are constant
	 * throughout the site.
	 * @param url the url to be parsed
	 * @return the number of pages.
	 * @throws IOException
	 */
	public static int trendingPlayersLength(String url) throws IOException
	{
		List<String> pageTotal = HandleBasicQueries.handleLists(url, "td div div div ul.ipsList_inline.left.pages li a");
		String total = "";
		for(int i = 0; i < pageTotal.size(); i++)
		{
			if(pageTotal.get(i).contains("Page"))
			{
				total = pageTotal.get(i);
				break;
			}
		}
		String[] broken = total.split(" ");
		String length = broken[broken.length - 1];
		if(length.equals(""))
		{
			length = "1";
		}
		return Integer.parseInt(length);
	}
}
