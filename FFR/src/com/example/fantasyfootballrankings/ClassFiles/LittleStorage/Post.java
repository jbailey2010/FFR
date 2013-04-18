package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

/**
 * Holds all the data of a post.
 * This is parsed later to get postedplayers.
 * @author Jeff
 *
 */
public class Post 
{
	public String text;
	public String date;
	
	public Post(String post, String datePosted)
	{
		text = post;
		date = datePosted;
	}
}
