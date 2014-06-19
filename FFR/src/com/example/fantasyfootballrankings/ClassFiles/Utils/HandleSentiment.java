package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.fantasyfootballrankings.ClassFiles.PlayerInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.NewsObjects;

public class HandleSentiment {
	
	public static String sentimentParsing(String text){
		URL url = null;
		StringBuilder response = new StringBuilder();
		 try{
		  url = new URL("http://text-processing.com/api/sentiment/");
		 }
		 catch (MalformedURLException M){
		   url = null;
		 }
		 HttpURLConnection conn = null;
		 try {
		   conn = (HttpURLConnection) url.openConnection();
		   try {
		     conn.setRequestMethod("POST"); //use post method
		     conn.setDoOutput(true); //we will send stuff
		     conn.setDoInput(true); //we want feedback
		     conn.setUseCaches(false); //no caches
		     conn.setAllowUserInteraction(false);
		     conn.setRequestProperty("Content-Type","text/xml");
		   }
		   catch (ProtocolException e) {
		   }
		   OutputStream out = conn.getOutputStream();
		   OutputStreamWriter wr = null;
		   try {
		     wr = new OutputStreamWriter(out);
		     wr.write("text=" + text); //ezm is my JSON object containing the api commands
		   }
		   catch (IOException e) {
		   }
		   finally { //in this case, we are ensured to close the output stream
		     if (wr != null)
		       wr.close();
		   }
		   InputStream in = conn.getInputStream();
		   BufferedReader rd = null;
		   try {
		     rd = new BufferedReader(new InputStreamReader(in));
		     String responseSingle = null;
		     while ((responseSingle = rd.readLine()) != null) {
		       response.append(responseSingle);
		     }
		  }
		   catch (IOException e) {
		   }
		   finally {  //in this case, we are ensured to close the input stream
		     if (rd != null)
		       rd.close();
		   }
		 }
		 catch (IOException e) {
		 }
		 finally {  //in this case, we are ensured to close the connection itself
		   if (conn != null)
		     conn.disconnect();
		 }
		 return response.toString();
	}
	
	public class ParsePlayerNewsSentiment extends AsyncTask<Object, Void, List<NewsObjects>> 
	{
		Activity act;
		List<NewsObjects> newsList;
	    public ParsePlayerNewsSentiment(Activity activity, List<NewsObjects> news) 
	    {
	        act = activity;
	        newsList = news;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();   
		}

		@Override
		protected void onPostExecute(List<NewsObjects> result){
			super.onPostExecute(result);
			PlayerInfo.populateNews(result);
		}

	    @Override
	    protected List<NewsObjects> doInBackground(Object... data) 
	    {
	    	StringBuilder str = new StringBuilder(1000);
	    	for(NewsObjects news : newsList){
	    		
	    		str.append(news.news + " ");
	    	}
			String jSon = (sentimentParsing(str.toString()));
			String posStr = jSon.split("\"pos\": ")[1].split("\\}")[0].split(",")[0];
			String negStr = jSon.split("\"neg\": ")[1].split("\\}")[0].split(",")[0];
			DecimalFormat df = new DecimalFormat ("##.##");
			double pos = Double.parseDouble(df.format(Double.parseDouble(("" + posStr)) * 100.0));
			double neg = Double.parseDouble(df.format(Double.parseDouble(("" + negStr)) * 100.0));
			String imp = "News is " + pos + "% positive, " + neg + "% negative";
			String tot = "";
			if(pos > neg){
				tot = "News on this player is mostly positive";
			}
			else{
				tot = "News on this player is mostly negative";
			}
			NewsObjects sentiment = new NewsObjects(tot, imp, "");
			newsList.add(0, sentiment); 
	    	return newsList;
	    }

	  }
	
	public static void sentimentInit(Context cont, List<NewsObjects> news){
		HandleSentiment stupid = new HandleSentiment();
		ParsePlayerNewsSentiment obj = stupid.new ParsePlayerNewsSentiment((Activity) cont, news);
		obj.execute();
	}
}
