package com.example.fantasyfootballrankings.LeagueImports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class ESPNImport 
{

	
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

}
 