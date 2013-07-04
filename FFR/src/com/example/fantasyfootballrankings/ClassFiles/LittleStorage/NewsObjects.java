package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

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
	
	/**
	 * The constructor. Easy peasy.
	 */
	public NewsObjects(String newsNew, String impactNew, String dateNew)
	{
		news = newsNew;
		impact = impactNew;
		date = dateNew;
	}
}
