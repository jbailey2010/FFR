package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class PlayerNewsActivity {

	public void startNews(String playerName, String playerTeam, Context cont) {
		FantasyProsUtils obj = new FantasyProsUtils();
		String baseUrl = "http://www.fantasypros.com/nfl/news/"
				+ obj.playerNameUrl(playerName, playerTeam) + ".php";
		ParsePlayerNews objParse = new ParsePlayerNews((Activity) cont, baseUrl);
		objParse.execute();
	}

	public class ParsePlayerNews extends
			AsyncTask<Object, String, List<NewsObjects>> {
		Activity act;
		String urlNews;

		public ParsePlayerNews(Activity activity, String url) {
			act = activity;
			urlNews = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<NewsObjects> result) {
			super.onPostExecute(result);
			if (result.size() > 0) {
				PlayerInfo.populateNews(result);
			}
		}

		@Override
		protected List<NewsObjects> doInBackground(Object... data) {
			List<NewsObjects> newsList = new ArrayList<NewsObjects>();
			try {
				urlNews = urlNews.toLowerCase();
				Document doc = Jsoup.connect(urlNews)
						.userAgent(HandleBasicQueries.ua).get();
				List<String> indivTweets = HandleBasicQueries.handleListsMulti(
						doc, urlNews, "div.tweets");
				List<String> indivNews = HandleBasicQueries.handleListsMulti(
						doc, urlNews, "div.notes div.news-title");
				List<String> indivNewsElems = new ArrayList<String>();
				Elements elems = doc.select("div.notes");
				for (Element el : elems) {
					Element nonTitle = el.child(2);
					indivNewsElems.add(nonTitle.child(2).text());
				}
				if (indivNews.size() > 0) {
					for (int i = 0; i < indivNews.size(); i++) {
						String title = indivNews.get(i);
						String newsElem = indivNewsElems.get(i);
						NewsObjects obj = new NewsObjects(newsElem, title, "");
						newsList.add(obj);
					}
				}
				int counter = 0;
				if (indivTweets.size() > 0) {
					for (String tweet : indivTweets) {
						if (counter > 20) {
							break;
						}
						int lastInd = tweet.lastIndexOf('@');
						String before = tweet.substring(0, lastInd);
						String sub = tweet.substring(lastInd);
						NewsObjects obj = new NewsObjects(before, sub, "");
						newsList.add(obj);
						counter++;
					}
				}
			} catch (IOException e) {
				return newsList;
			} catch (ArrayIndexOutOfBoundsException e) {
				return newsList;
			}
			return newsList;
		}

	}

}
