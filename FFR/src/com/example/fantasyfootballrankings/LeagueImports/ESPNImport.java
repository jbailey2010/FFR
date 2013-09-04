package com.example.fantasyfootballrankings.LeagueImports;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ESPNImport 
{
	public static String ua = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.2 Safari/537.36"; 
	public static String name = "FFL_LM_COOKIE";
	
	public void parseESPNHome(String url) throws IOException
	{
		Connection.Response res2 = Jsoup
				.connect(url)
			    .data("username", "jbailey2010", "password", "vfcq2010")
			    .userAgent(ua)
			    .method(Method.POST)
			    //.followRedirects(false)
			    .execute();
		Connection.Response res = Jsoup
				.connect(res2.url().toString())
				.data("username", "jbailey2010", "password", "vfcq2010")
			    .userAgent(ua)
			    .method(Method.POST)
			    //.followRedirects(false)
			    .execute();
			System.out.println(res.statusCode());
			System.out.println(res.cookies().size());
			Document doc2 = Jsoup.connect(url)
			    .cookies(res.cookies())
			    .get();	
			System.out.println(Jsoup.connect(url).cookies(res.cookies()).response().statusCode());
			System.out.println(doc2.html());
	}

}
 