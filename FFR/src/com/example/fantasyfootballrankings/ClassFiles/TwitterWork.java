package com.example.fantasyfootballrankings.ClassFiles;

import java.util.ArrayList;

import java.util.List;
import com.ffr.fantasyfootballrankings.R;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseNews;
import com.example.fantasyfootballrankings.Pages.News;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import FileIO.ReadFromFile;
import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class TwitterWork 
{
	String validURL = "";
	int pin = -1;
	public Twitter twitter;
	public Twitter userTwitter;
	RequestToken requestToken;
	AccessToken accessToken = null;
	static AccessToken userToken = null;
	static ListView searchOutput;
	Dialog searchOutputDialog;
	/**
	 * Calls the validation URL asynctask
	 * @param cont
	 * @param flag 
	 * @param b 
	 */
	public void twitterInitial(Context cont, int flag, String search, boolean b)
	{
		long check = ReadFromFile.readUseID(cont);
		//Not yet set
		if(check == -1)
		{
			TwitterWork obj = new TwitterWork();
		    TwitterConnection task = obj.new TwitterConnection((Activity)cont, flag);
		    task.execute(cont);
		}
		else //it IS set, so call a function to 'log in' the user'
		{
			logInUser(cont, flag, search, b);
		}
	}

	/**
	 * Logs in the user and makes a pop up asking them what they'd like to do
	 */
	public void logInUser(final Context cont, int flag, String search, boolean saveFlag)
	{
		String token = ReadFromFile.readToken(cont);
		String tokenSecret = ReadFromFile.readTokenSecret(cont);
		userToken = new AccessToken(token, tokenSecret);
		userTwitter = TwitterFactory.getSingleton();
		try
		{
			userTwitter.setOAuthConsumer("BCARDaoZRV1VhOVh3Nxq4g",
	        		"u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg");
			userTwitter.setOAuthAccessToken(userToken);
		}
		catch(IllegalStateException e)
		{
		}
		if(flag == 1)
		{
			News.twitterFeedsDialog();
		}
		if(flag == 2)
		{
			News.twitterSearchDialog();
		}
		if(flag == 3)
		{
			ParseNews.startTwitterSearchAsync(cont, search, "Twitter Search: " + search, saveFlag, search, this);
		}
	}




	/**
	 * Gets the validation URL from twitter
	 * @author Jeff
	 *
	 */
	public class TwitterConnection extends AsyncTask<Object, Void, Twitter> 
	{
		ProgressDialog pdia;
		Activity act;
		Integer flag;
	    public TwitterConnection(Activity activity, int i) 
	    {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        act = activity;
	        flag = i;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, fetching the URL...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(Twitter result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   if(result != null)
		   {
			   handleURL(act, result, flag);
		   }
		   else
		   {
			   Toast.makeText(act, "Please kill the app and re-open it to re-attempt to connect to twitter", Toast.LENGTH_LONG).show();
		   }
		}

	    @Override
	    protected Twitter doInBackground(Object... data) 
	    {
	    	final Context cont = (Context)data[0];
	    	Twitter twitter = TwitterFactory.getSingleton();
	    	try{
		        twitter.setOAuthConsumer("BCARDaoZRV1VhOVh3Nxq4g",
		        		"u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg");
	    	}catch(IllegalStateException ise)
			{
	    		return null;
			}
	        try {
				requestToken = twitter.getOAuthRequestToken();
		        accessToken = null;
		        validURL = requestToken.getAuthorizationURL();
			} catch (TwitterException e) {
				e.printStackTrace();
			}
	        return twitter;
	    }
	}

	/**
	 * Creates a dialog to get the user to validate it, then enter the pin
	 * @param cont
	 * @param flag 
	 */
	public void handleURL(final Activity cont, Twitter twit, final Integer flag)
	{
		twitter = twit;
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.twitter_login);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button validate = (Button)dialog.findViewById(R.id.twitter_confirm_go);
	    validate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(validURL));
				cont.startActivity(i);
				dialog.dismiss();
				handlePin(cont, twitter, flag);
			}
	    });
	    dialog.setCancelable(false);
	}

	/**
	 * Gets and fetches the valid pin
	 * @param cont
	 * @param flag 
	 */
	public void handlePin(final Activity cont, final Twitter twitter, final Integer flag)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.twitter_validate_pin);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.setCancelable(false);
	    dialog.show();	
	    final EditText input = (EditText)dialog.findViewById(R.id.twitter_pin_field);
	    Button submit = (Button)dialog.findViewById(R.id.twitter_pin_go);
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String text = input.getText().toString();
				if(text.length() < 5)
				{ 
					Toast.makeText(cont, "Please Enter a Valid PIN", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try{
						pin = Integer.parseInt(text);
						dialog.dismiss();
						finalizeValidation(cont, twitter, flag, dialog);
					}
					catch (NumberFormatException e){
						Toast.makeText(cont, "Please Enter a PIN of Only Numbers", Toast.LENGTH_SHORT).show();
					}
				}
			}
	    });
	}

	/**
	 * Calls the authentication asynctask
	 * @param cont
	 * @param flag 
	 * @param dialog 
	 */
	public void finalizeValidation(Context cont, Twitter twitter, Integer flag, Dialog dialog)
	{
		TwitterWork obj = new TwitterWork();
	    TwitterValidate task = obj.new TwitterValidate((Activity)cont, flag, dialog, twitter);
	    task.execute(cont, twitter, requestToken, Integer.toString(pin));
	} 

	/**
	 * Gets the validation URL from twitter
	 * @author Jeff
	 *
	 */
	public class TwitterValidate extends AsyncTask<Object, Void, AccessToken> 
	{
		ProgressDialog pdia;
		Activity act;
		Integer flag;
		Dialog d;
		Twitter t;
	    public TwitterValidate(Activity activity, int i, Dialog dialog, Twitter twitter) 
	    {
	        pdia = new ProgressDialog(activity);
	        pdia.setCancelable(false);
	        act = activity;
	        flag = i;
	        t = twitter;
	        d = dialog;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();
		        pdia.setMessage("Please wait, validating your account...");
		        pdia.show();    
		}

		@Override
		protected void onPostExecute(AccessToken result){
		   super.onPostExecute(result);
		   pdia.dismiss();
		   if(result == null)
		   {
			   Toast.makeText(act, "Invalid pin.", Toast.LENGTH_SHORT).show();
			   d.dismiss();
		   }
		   else
		   {
			   d.dismiss();
			   handleAccessToken(act, result, flag);
		   }
		}

	    @Override
	    protected AccessToken doInBackground(Object... data) 
	    {
	    	Context cont = (Context)data[0];
	    	Twitter twit = (Twitter)data[1];
	    	RequestToken rt = (RequestToken)data[2];
	    	String pinStr = (String)data[3];
	    	AccessToken accessToken;
			try {
				accessToken = t.getOAuthAccessToken(rt, pinStr);
				WriteToFile.storeID(t.verifyCredentials().getId(), cont);
			} catch (TwitterException e) {
		        if(401 == e.getStatusCode()){
		        	  System.out.println("Error getting token");
			          return null;
			    }else{
			          System.out.println("Error validating token");
			          return null;
			    }
			}
			twit.setOAuthAccessToken(accessToken);
			return accessToken;
	    }
	}

	/**
	 * Saves the rest of it to file
	 * @param cont
	 * @param accessToken
	 * @param flag 
	 */
	public void handleAccessToken(Activity cont, AccessToken accessToken, Integer flag)
	{ 
		if(accessToken == null)
		{
			return;
		}
		WriteToFile.storeToken(accessToken, cont);
		Toast.makeText(cont, "Successfully set up your account! Please press the menu option again.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Handles twitter parsing for a list instead of a user
	 * @param obj 
	 * @return
	 */
	public static List<NewsObjects> parseTwitter4jList(String userName, String listName, TwitterWork obj)
	{
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
	    TwitterFactory factory = new TwitterFactory(cb.build());*/
	    Twitter twitter = obj.userTwitter;
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
	    Paging paging = new Paging(1, 25);
	    try {
	    	int id = -1;
			ResponseList<UserList> list = twitter.getUserLists(userName);
			for(UserList listIter : list)
			{
				if(listIter.getName().equals(listName))
				{
					id = listIter.getId();
					break;
				}
			}
			ResponseList<Status> listStatuses = twitter.getUserListStatuses(id, paging);
			for(Status status : listStatuses)
			{
				String header = status.getUser().getName() + ": " + status.getText();
				String date = status.getCreatedAt().toString();
				StringBuilder replySet = new StringBuilder(1000);
				int counter = 0;
				while(status.getInReplyToStatusId() != -1L && counter < 4)
				{
					status = twitter.showStatus(status.getInReplyToStatusId());
					replySet.append("In reply to:  " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
							+ status.getText() + "\n\n");
					counter++;
				}
				if(replySet.length() < 5)
				{
					replySet.append(" ");
				}
				NewsObjects news = new NewsObjects(header, replySet.toString(), date);
				newsSet.add(news);
			}
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    if(newsSet.size() == 0)
	    {
	    	newsSet.add(new NewsObjects("Rate limit exceeded, try again in a few minutes", " ", " "));
	    }
	    return newsSet;
	}

	/**
	 * Parses the tweets from the individual accounts
	 * @param obj 
	 */
	public static List<NewsObjects> parseTwitter4j(String accountName, TwitterWork obj)
	{
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
	    TwitterFactory factory = new TwitterFactory(cb.build());*/
	    Twitter twitter = obj.userTwitter;//factory.getInstance();
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
	    Paging paging = new Paging(1, 25);
	    List<Status> statuses = new ArrayList<Status>();
	    try {
			statuses = twitter.getUserTimeline(accountName, paging);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Status status : statuses)
		{
			String header = status.getUser().getName() + ": " + status.getText();
			String date = status.getCreatedAt().toString();
			StringBuilder replySet = new StringBuilder(1000);
			int counter = 0;
			while(status.getInReplyToStatusId() != -1L && counter < 4)
			{
				try {
					status = twitter.showStatus(status.getInReplyToStatusId());
				} catch (TwitterException e) {
					break;
				}
				replySet.append("In reply to: " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
						+ status.getText() + "\n\n");
				counter++;
			} 
			if(replySet.length() < 5)
			{
				replySet.append(" ");
			}
			NewsObjects news = new NewsObjects(header, replySet.toString(), date);
			newsSet.add(news);
		}
	    if(newsSet.size() < 10)
	    {
	    	newsSet.add(new NewsObjects("Rate limit exceeded, try again in a few minutes", " ", " "));
	    } 
	    return newsSet;
	}

	/**
	 * Parses twitter given a user's input query terms, returning relevant tweets
	 * @param userTwitter2 
	 */
	public static List<NewsObjects> searchTweets(String query, Twitter userTwitter2)
	{
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BCARDaoZRV1VhOVh3Nxq4g")
		  .setOAuthConsumerSecret("u84R7JlzTNtss0Tut61oSRKYpgo4uW8G1moOlrBOgSg")
		  .setOAuthAccessToken("734038682-8h2b63A8UM0UoMrlPHKZGa6RIzOZLpx5qsPeZ1Ma")
		  .setOAuthAccessTokenSecret("Zijqkk2GU4WINIQ67YCnBE6Yz2Ahzk6XIYckMv8zRY");
        TwitterFactory factory = new TwitterFactory(cb.build());*/
        Twitter twitter = userTwitter2;
		List<NewsObjects> newsSet = new ArrayList<NewsObjects>();
        try {
        	Query queryObj = new Query(query);
	    	queryObj.setCount(40);
	        QueryResult result;
			result = twitter.search(queryObj);
			List<Status> statuses = result.getTweets();
		    for(Status status: statuses)
		    {
				String header = status.getText();
				String date = status.getUser().getName() + "\n" +  status.getCreatedAt().toString();
				StringBuilder replySet = new StringBuilder(1000);
				int counter = 0;
				while(status.getInReplyToStatusId() != -1L && counter < 3)
				{
					status = twitter.showStatus(status.getInReplyToStatusId());
					replySet.append("In reply to:  " + status.getUser().getName() + " (" + status.getCreatedAt() + ")\n" 
							+ status.getText() + "\n\n");
					counter++;
				}
				if(replySet.length() < 5)
				{
					replySet.append(" ");
				}
				NewsObjects news = new NewsObjects(header, replySet.toString(), date);
				newsSet.add(news);
			}
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return newsSet;
	}
}