package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseTwitterSearch;
import AsyncTasks.StorageAsyncTask;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;
import com.example.fantasyfootballrankings.ClassFiles.Utils.HandleBasicQueries;
import com.example.fantasyfootballrankings.ClassFiles.Utils.TwitterWork;
import com.example.fantasyfootballrankings.Pages.Home;

/**
 * A library to handle the parsing of news
 * 
 * @author Jeff
 * 
 */
public class ParseNews {
	/**
	 * Calls the actual searching function
	 * 
	 * @param cont
	 * @param search
	 * @param obj
	 */
	public static void startTwitterSearchAsync(Context cont, String search,
			String header, boolean flag, String query, TwitterWork obj) {
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		ParseTwitterSearch news = stupid.new ParseTwitterSearch(cont, query,
				obj);
		news.execute(cont, search, header, obj);
	}
}
