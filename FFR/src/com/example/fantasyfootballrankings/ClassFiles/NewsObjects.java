package com.example.fantasyfootballrankings.ClassFiles;

/**
 * A class object to hold all the data
 * relevant to posts
 * @author Jeff
 *
 */
public class NewsObjects 
{
	public String news;
	public String impact;
	public String date;
	public String source;
	
	/**
	 * The constructor. Easy peasy.
	 */
	public NewsObjects(String newsNew, String impactNew, String dateNew, String sourceNew)
	{
		news = newsNew;
		impact = impactNew;
		date = dateNew;
		source = sourceNew;
	}
}
