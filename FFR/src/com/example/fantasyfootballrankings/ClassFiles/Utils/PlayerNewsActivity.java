package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class PlayerNewsActivity {
	
	public void startNews(String playerName, Context cont){
		FantasyProsUtils obj = new FantasyProsUtils();
		String baseUrl = "http://www.fantasypros.com/nfl/news/" + obj.playerNameUrl(playerName) + ".php";
		ParsePlayerNews objParse = new ParsePlayerNews((Activity) cont, baseUrl);
		objParse.execute();
	}
	
	
	public class ParsePlayerNews extends AsyncTask<Object, String, List<NewsObjects>> 
	{
		Activity act;
		String urlNews;
	    public ParsePlayerNews(Activity activity, String url) 
	    {
	        act = activity;
	        urlNews = url;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();   
		}

		@Override
		protected void onPostExecute(List<NewsObjects> result){
			super.onPostExecute(result);
			if(result.size() > 0){
				HandleSentiment.sentimentInit(act, result);
			}
		}

	    @Override
	    protected List<NewsObjects> doInBackground(Object... data) 
	    {
	    	List<NewsObjects> newsList = new ArrayList<NewsObjects>();
	    	try {
	    		urlNews = urlNews.toLowerCase();
				Document doc = Jsoup.connect(urlNews).userAgent(HandleBasicQueries.ua).get();
				List<String> indivTweets = HandleBasicQueries.handleListsMulti(doc, urlNews, "div.tweets");
				List<String> indivNews = HandleBasicQueries.handleListsMulti(doc, urlNews, "div.notes div.pull-left div.news-title");
				List<String> indivNewsElems = HandleBasicQueries.handleListsMulti(doc, urlNews, "div.notes div.pull-left div.pull-left div");
				if(indivNews.size() > 0){
					for(int i = 0; i < indivNews.size(); i++){
						String title = indivNews.get(i);
						String newsElem = indivNewsElems.get(i);
						NewsObjects obj = new NewsObjects(newsElem, title, "");
						newsList.add(obj);
					}
				}
				if(indivTweets.size() > 0){
					for(String tweet : indivTweets){
						int lastInd = tweet.lastIndexOf('@');
						String before = tweet.substring(0, lastInd);
						String sub = tweet.substring(lastInd);
						NewsObjects obj = new NewsObjects(before, sub, "");
						newsList.add(obj);
					}
				}
			} catch (IOException e) {
				return newsList;
			} catch(ArrayIndexOutOfBoundsException e){
				return newsList;
			}
	    	return newsList;
	    }

	  }

}
