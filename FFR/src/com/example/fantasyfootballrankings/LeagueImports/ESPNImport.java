package com.example.fantasyfootballrankings.LeagueImports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jeff.isawesome.fantasyfootballrankings.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.fantasyfootballrankings.ClassFiles.HighLevel;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.TeamAnalysis;

import FileIO.WriteToFile;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ESPNImport 
{
	public String url;
	public String username;
	public String password;
	public Document doc;
	public Context cont;
	
	/**
	 * Gets the process started with a trial query
	 * @param urlOrig
	 * @param c
	 * @throws IOException
	 */
	public void handleESPNParsing(String urlOrig, final Context c) throws IOException
	{
		cont = c;
		url = urlOrig;
		((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		GetTestDoc task1 = this.new GetTestDoc((Activity)cont, this);
		task1.execute(url);
	}
	
	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * @author Jeff
	 *
	 */
	public class GetTestDoc extends AsyncTask<Object, String, Document> 
	{
		Activity act;
		ESPNImport obj;
		ProgressDialog pda;
	    public GetTestDoc(Activity activity, ESPNImport espnImport) 
	    {
	        act = activity;
	        obj = espnImport;
	        pda = new ProgressDialog(act);
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute(); 
		   pda.setMessage("Please wait, querying the league...");
		}

		@Override
		protected void onPostExecute(Document result){
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		   super.onPostExecute(result);
		   pda.dismiss();
		   obj.handleTest(result);
		}

	    @Override
	    protected Document doInBackground(Object... data) 
	    {
	    	String url = (String)data[0];
	    	
			try {
				return Jsoup.connect(url).timeout(0).get();
			} catch (IOException e) {
				return null;
			}
	    }

	  }
	
	/**
	 * Uses the new test document to see if someone needs to sign in at all
	 * @param test
	 */
	public void handleTest(Document test)
	{
		if(isSignIn(test))
		{
			handleSignInNeed(test);
		}
		else if(!isRosters(test))
		{
			final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
		    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
			popUp.setContentView(R.layout.tweet_popup);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		    lp.copyFrom(popUp.getWindow().getAttributes());
		    lp.width = WindowManager.LayoutParams.FILL_PARENT;
		    popUp.getWindow().setAttributes(lp);
		    popUp.show(); 
		    TextView textView = (TextView)popUp.findViewById(R.id.tweet_field);
		    textView.setText("   The league ID you input was invalid.\n\n");
		    Button close = (Button)popUp.findViewById(R.id.tweet_popup_close);
		    close.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					popUp.dismiss();
					return;
				}
		    });
		}  
		else
		{
			doc = test;
			handleParsing();
		}
		
	}
	
	/**
	 * Handles signing in if need be
	 * @param test
	 */
	public void handleSignInNeed(Document test)
	{
		if(!isCredentialsSet(cont))
		{
			setLogIn();
		}
		else
		{
			readUnPw(cont);
			((Activity) cont).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			GetLogInFirst task = this.new GetLogInFirst((Activity)cont, this);
			task.execute();
		}
	}
	
	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * @author Jeff
	 *
	 */
	public class GetLogInFirst extends AsyncTask<Object, String, Document> 
	{
		Activity act;
		ESPNImport obj;
		ProgressDialog pda;
	    public GetLogInFirst(Activity activity, ESPNImport espnImport) 
	    {
	    	pda = new ProgressDialog(activity);
	        act = activity;
	        obj = espnImport;
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();   
		   pda.setMessage("Please wait, making a first attempt with your credentials...");
		}

		@Override
		protected void onPostExecute(Document result){
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		   super.onPostExecute(result);
		   pda.dismiss();
		   obj.handleFirstLogIn(result);
		}

	    @Override
	    protected Document doInBackground(Object... data) 
	    {
	    	try {
				Document test = parseESPNPassWord(url, username, password);
				return test;
			} catch (IOException e) {
				return null;
			}

	    }

	  }
	
	/**
	 * Handles the logic of the first sign in attempt
	 * @param test
	 */
	public void handleFirstLogIn(Document test)
	{
		if(isSignIn(test))
		{
			Toast.makeText(cont, "Log in failed with the stored information", Toast.LENGTH_SHORT).show();
			setLogIn();
		}
		else
		{
			doc = test;
			handleParsing();
		}
	}
	
	/**
	 * Handles the logging in
	 */
	public void setLogIn()
	{
		final Dialog popUp = new Dialog(cont, R.style.RoundCornersFull);
	    popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);       
		popUp.setContentView(R.layout.espn_unpw);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(popUp.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    popUp.getWindow().setAttributes(lp);
	    popUp.show(); 
	    Button close = (Button)popUp.findViewById(R.id.espn_unpw_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				popUp.dismiss();
				return;
			}
	    });
	    final EditText unField = (EditText)popUp.findViewById(R.id.username_input);
	    final EditText pwField = (EditText)popUp.findViewById(R.id.password_input);
	    Button submit = (Button)popUp.findViewById(R.id.espn_unpw_submit);
	    final ESPNImport obj = this;
	    submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				HandleLogIn task = obj.new HandleLogIn((Activity)cont, obj, popUp);
				String un = unField.getText().toString();
				String pw = pwField.getText().toString();
				if(un.length() > 0 && pw.length() > 0)
				{
					task.execute(un, pw);
					popUp.dismiss();
				}
				else
				{
					Toast.makeText(cont, "Please enter a username and a password", Toast.LENGTH_SHORT).show();
				}
			}
	    });
	}
	
	/**
	 * Gets the initial doc from the URL to see if any more work is necessary
	 * @author Jeff
	 *
	 */
	public class HandleLogIn extends AsyncTask<Object, String, Document> 
	{
		Activity act;
		ESPNImport obj;
		Dialog dialog;
		ProgressDialog pda;
	    public HandleLogIn(Activity activity, ESPNImport espnImport, Dialog popUp) 
	    {
	        act = activity;
	        obj = espnImport;
	        dialog = popUp;
	        pda = new ProgressDialog(act);
	    }

		@Override
		protected void onPreExecute(){ 
		   super.onPreExecute();  
		   pda.setMessage("Please wait, trying to log in...");
		}

		@Override
		protected void onPostExecute(Document result){
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		   super.onPostExecute(result);
		   pda.dismiss();
		   if(result == null)
		   {
			   Toast.makeText(cont, "Invalid username/password", Toast.LENGTH_SHORT).show();
			   obj.setLogIn();
		   }
		   else
		   {
			   obj.handleParsing();
		   }
		}

	    @Override
	    protected Document doInBackground(Object... data) 
	    {
	    	String un = (String)data[0];
	    	String pw = (String)data[1];
			try {
				Document testSignIn = parseESPNPassWord(url, un, pw);
				if(isRosters(testSignIn))
				{
					obj.doc = testSignIn;
					obj.username = un;
					obj.password = pw;
					storeUnPw(cont);
					return obj.doc;
				}
			} catch (IOException e) {
				System.out.println(e.toString());
			}
			return null;
	    }

	  }
	
	public void handleParsing()
	{

	}
	 
	/**
	 * Returns the html of the document when it for sure needs a password.
	 * Note: this does NOT validate that the url input is what it should be
	 */
	public Document parseESPNPassWord(String url, String username, String password) throws IOException
	{
		String base = "https://r.espn.go.com/espn/memberservices/pc/login";
		Connection.Response res = Jsoup
				.connect(base)
			    .data("SUBMIT", "1", "failedLocation", "", "aff_code", "espn_fantgames", "appRedirect", url, "cookieDomain", ".go.com", ".multipleDomains", 
			    		"true", "username", username, "password", password, "submit", "Sign+In")
			    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
			    .method(Method.POST)
			    .execute();
			Document doc2 = Jsoup.connect(url)
			    .cookies(res.cookies()) 
			    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
			    .get();	
			return doc2;
	}
	
	/**
	 * Checks if the document is currently a sign in page
	 * @param doc
	 * @return
	 */
	public boolean isSignIn(Document doc)
	{
		Elements elements = doc.select("title");
		for(Element elem : elements)
		{
			if(elem.text().contains("Sign In"))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the document is currently the league rosters page
	 * @param doc
	 * @return
	 */
	public boolean isRosters(Document doc)
	{
		Elements elements = doc.select("title");
		for(Element elem : elements)
		{
			if(elem.text().contains("League Rosters"))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns if the username/password are stored
	 */
	public boolean isCredentialsSet(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		return prefs.getBoolean("ESPN Credentials Stored", false);
	}
	
	/**
	 * Reads the stored username and password from file
	 * @param cont
	 */
	public void readUnPw(Context cont)
	{
		SharedPreferences prefs = cont.getSharedPreferences("FFR", 0); 
		username = prefs.getString("ESPN Username", "Not Set");
		password = prefs.getString("ESPN Password", "Not Set");
	}
	
	/**
	 * Stores the username and password to avoid unnecessary hassle later
	 * @param cont
	 */
	public void storeUnPw(Context cont)
	{
		SharedPreferences.Editor editor = cont.getSharedPreferences("FFR", 0).edit();
		editor.putString("ESPN Username", username);
		editor.putString("ESPN Password", password);
		editor.putBoolean("ESPN Credentials Stored", true);
		editor.commit();
	}
	
	

}
 