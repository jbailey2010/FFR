package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.FetchTrends;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.ParseRankings;
import com.example.fantasyfootballrankings.ClassFiles.Storage;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.Pages.Rankings;
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
		    pdia.setMessage("Please wait, checking the posts...");
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
		for(int i = 0; i < posts.size(); i++)
		{
			String text = posts.get(i).text;
			text = text.replaceAll("\\.", " ");
			text = text.replaceAll(",", " ");
			text = text.replaceAll(";", " ");
			text = text.replaceAll("!", " ");
			text = text.replaceAll("\"", "");
			text = text.replaceAll("\\\\'", "");
			text = text.replaceAll("/", " ");
			text = text.replaceAll("WRs", "");
			text = text.replaceAll("RBs", "");
			text = text.replaceAll("TEs", "");
			text = text.replaceAll("QBs", "");
			String[] postWords = text.split(" ");
			for(int j = 0; j < postWords.length-1; j++)
			{
				String firstName = postWords[j].replaceAll("\\s", "");
				String lastName = postWords[j+1].replaceAll("\\s", "");
				if(holder.playerNames.contains(firstName + " " + lastName))
				{
					boolean inserted = false;
                    for (PostedPlayer e : holder.postedPlayers)
                    {
                    	if(e.name.equals(firstName + " " + lastName))
                        {
                    		holder.postedPlayers.remove(e);
                            e.count++;
                            holder.postedPlayers.add(e);
                            inserted = true;
                        }
                    }
					if(inserted == false)
					{
						PostedPlayer newPlayer = new PostedPlayer(firstName + " " + lastName, 1);
						holder.postedPlayers.add(newPlayer);
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
						for(int t = 0; t < holder.playerNames.size(); t++)
						{
							String player = holder.playerNames.get(t);
							if((player.contains(firstName) && firstName.length() > 3)
									|| (player.contains(lastName) && lastName.length() > 3))
							{
								if(found)
								{
									found = false;
									break;
								}
								key = t;
								found = true;
							}
						}
						if(found && key != -1)
						{
							String player = holder.playerNames.get(key);
							boolean inserted = false;
		                    for (PostedPlayer e : holder.postedPlayers)
		                    {
		                    	if(e.name.equals(player))
		                        {
		                    		holder.postedPlayers.remove(e);
		                            e.count++;
		                            holder.postedPlayers.add(e);
		                            inserted = true;
		                        }
		                    }
							if(inserted == false && !player.contains("D/ST"))
							{
								PostedPlayer newPlayer = new PostedPlayer(player, 1);
								holder.postedPlayers.add(newPlayer);
							}
						}
					}
				}
			}
		}
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
		//Name shorthand/fixes below
		if(name.equals("megatron") || name.equals("calvin"))
		{
			return "Calvin Johnson";
		}
		else if(name.contains("matthews"))
		{
			return "Ryan Mathews";
		}
		else if(name.contains("djax"))
		{
			return "DeSean Jackson";
		}
		else if(name.contains("gronk"))
		{
			return "Rob Gronkowski";
		}
		else if(name.contains("sjax") || name.contains("s.jax"))
		{
			return "Steven Jackson";
		}
		else if(name.equals("marshall"))
		{
			return "Brandon Marshall";
		}
		else if(name.equals("brady"))
		{
			return "Tom Brady";
		}
		else if(name.equals("Payton"))
		{
			return "payton";
		}
		else if(name.contains("kaep"))
		{
			return "Colin Kaepernick";
		}
		else if(name.equals("iii"))
		{
			return "iii";
		}
		else if(name.contains("rgiii") || name.contains("rg3"))
		{
			return "Robert Griffin III";
		}
		else if(name.equals("vick"))
		{
			return "Michael Vick";
		}
		//Grammatical fixes below
		else if(name.equals("still"))
		{
			return "still";
		}
		else if(name.equals("round"))
		{
			return "round";
		}
		else if(name.equals("i'm"))
		{
			return "i'm";
		}
		else if(name.contains("i'll"))
		{
			return "i'll";
		}
		else if(name.equals("i'd"))
		{
			return "i'd";
		}
		else if(name.contains("it's"))
		{
			return "it's";
		}
		else if(name.equals("both"))
		{
			return "both";
		}
		else if(name.equals("tds"))
		{
			return "tds";
		}
		else if(name.equals("t"))
		{
			return "t";
		}
		else if(name.equals("qb"))
		{
			return "qb";
		}
		else if(name.equals("well"))
		{
			return "well";
		}
		else if(name.equals("true"))
		{
			return "true";
		}
		else if(name.equals("i"))
		{
			return "i";
		}
		else if(name.equals("qb's"))
		{
			return "qb's";
		}
		else if(name.equals("houston"))
		{
			return "houston";
		}
		else if(name.equals("double-check"))
		{
			return "double-check";
		}
		else if(name.equals("super"))
		{
			return "super";
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
		holder.posts.addAll(Arrays.asList(parseForum(length, url)));
	}
	
	/**
	 * Actually parses the forum, returning the parsed text from
	 * across all of the pages
	 * @param length the number ofpages to be parsed
	 * @param url the url to parse
	 * @return the text from all the responses
	 * @throws IOException
	 */
	public static Post[] parseForum(int length, String url) throws IOException
	{
		StringBuilder parsedText = new StringBuilder(10000);
		StringBuilder parsedDates = new StringBuilder(10000);
		for(int i = 0; i < length; i++)
		{
			String newUrl = url + Integer.toString(i*20);
			Document doc = Jsoup.connect(newUrl).timeout(0).get();
			parsedText.append(HandleBasicQueries.handleListsMulti(doc, newUrl, "div.post.entry-content"));
			parsedDates.append(HandleBasicQueries.handleListsMulti(doc, newUrl, "abbr.published"));
		}
		String[] perPost = parsedText.toString().split("\n");
		String[] datesPost = parsedDates.toString().split("\n");
		Post[] posts = new Post[perPost.length];
		for(int i =0; i < posts.length; i++)
		{
			posts[i] = new Post(perPost[i], datesPost[i]);
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
		String text = HandleBasicQueries.handleLists(url, "td div div div ul.ipsList_inline.left.pages li a");
		String[] pageTotal = text.split("\n");
		String total = "";
		for(int i = 0; i < pageTotal.length; i++)
		{
			if(pageTotal[i].contains("Page"))
			{
				total = pageTotal[i];
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
