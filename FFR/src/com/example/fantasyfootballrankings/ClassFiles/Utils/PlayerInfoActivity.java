package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.ClassFiles.ManageInput;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.ffr.fantasyfootballrankings.R;
import com.socialize.EntityUtils;
import com.socialize.EntityUtils.SortOrder;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException; 
import com.socialize.listener.entity.EntityListListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager; 
import android.widget.Button;
import android.widget.TextView;
/**
 * A little library to handle the rendering of the player view/like stats in a pop up
 * @author Jeff
 *
 */ 
public class PlayerInfoActivity 
{
	public static TextView output;
	public static StringBuilder sb;
	
	/**
	 * Does the real work, fetching the data asynchronously, sorting it appropriately, and 
	 * displays it in a simple way on the pop up
	 * @param cont
	 */
	public static void displayStats(Context cont)
	{
		final Dialog dialog = new Dialog(cont, R.style.RoundCornersFull);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.one_line_text);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    dialog.getWindow().setAttributes(lp);
	    dialog.show();
	    Button close = (Button)dialog.findViewById(R.id.player_stats_close);
	    close.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) { 
				dialog.dismiss();
			}
	    });
	    output = (TextView)dialog.findViewById(R.id.textView1);
	    EntityUtils.getEntities((Activity) cont, 0, 200, SortOrder.TOTAL_ACTIVITY, new EntityListListener() {

			@Override
			public void onList(ListResult<Entity> result) {
				List<Entity> results = result.getItems();
				StringBuilder data = new StringBuilder(1000);
				int i = 0;
				PriorityQueue<Entity> sorted = sortEntitiesViews(results);
				if(sorted.size() > 0)
				{
					data.append("Views:\n");
					while(!sorted.isEmpty() && i < 15)
					{
						Entity elem = sorted.poll();
						i++;
						data.append(i + ". "+ elem.getDisplayName() + " - " + elem.getEntityStats().getViews() + " views\n");
					}
				}
				PriorityQueue<Entity> sortedLikes = sortEntitiesLikes(results);
				i=0;
				if(sortedLikes.size() > 0)
				{
					data.append("\n");
				}
				while(!sortedLikes.isEmpty() && i < 15)
				{
					if(i == 0)
					{
						data.append("Likes:\n");
					}
					Entity elem = sortedLikes.poll();
					i++;
					data.append(i + ". "+ elem.getDisplayName() + " - " + elem.getEntityStats().getLikes() + " likes\n");
				}
				PriorityQueue<Entity> sortedComments = sortEntitiesComments(results);
				if(sortedComments.size() > 0)
				{
					data.append("\n");
				}
				i=0;
				while(!sortedComments.isEmpty() && i < 15)
				{
					if(i == 0)
					{
						data.append("Comments:\n");
					}
					Entity elem = sortedComments.poll();
					i++;
					data.append(i + ". "+ elem.getDisplayName() + " - " + elem.getEntityStats().getComments() + " likes\n");
				}
				sb = new StringBuilder(1000);
				sb.append(data.toString());
				//output.setText(data.toString());
			}

			@Override
			public void onError(SocializeException error) {
				System.out.println(error.getMessage());
				System.out.println("-------");
				error.printStackTrace();
				output.setText("An error occurred. Do you have an internet connection?");
			}
	    });
	    EntityUtils.getEntities((Activity) cont, 0, 200, SortOrder.TOTAL_ACTIVITY, new EntityListListener() {

	    	@Override
	    	public void onList(ListResult<Entity> result) {
	    		List<Entity> items = result.getItems();
	    		StringBuilder data = new StringBuilder(1000);
				int i = 0;
				PriorityQueue<Entity> sorted = sortEntitiesDraft(items);
				if(sorted.size() > 0)
				{
					data.append("\nTimes Added to Watch List:\n");
					while(!sorted.isEmpty() && i < 20)
					{
						Entity elem = sorted.poll();
						i++;
						data.append(i + ". "+ elem.getDisplayName() + " - " + elem.getMetaData() + " times added\n");
					}
				}
				sb.append(data.toString());
				if(sb.toString().length () > 4)
				{
					output.setText(sb.toString());
				}
				else
				{
					output.setText("There was no player data to look at.");
				}
	    	}
	    	
	    	@Override
	    	public void onError(SocializeException error) {
	    		System.out.println(error.getMessage());
	    		output.setText("An error occurred. Do you have an internet connection?");
	    	}
	    });
	}
	
	/**
	 * Sorts the results by views
	 * @param input
	 * @return
	 */
	public static PriorityQueue<Entity> sortEntitiesViews(List<Entity> input)
	{
		PriorityQueue<Entity> sorted = new PriorityQueue<Entity>(100, new Comparator<Entity>()
				{
					@Override
					public int compare(Entity a, Entity b)
					{
						if(a.getEntityStats().getViews() > b.getEntityStats().getViews())
						{
							return -1;
						}
						if(a.getEntityStats().getViews() < b.getEntityStats().getViews())
						{
							return 1;
						}
						return 0;
					}
				});
		for(Entity elem : input)
		{
			sorted.add(elem);
		}
		return sorted;
	}
	
	/**
	 * Sorts the results by likes
	 * @param input
	 * @return
	 */
	public static PriorityQueue<Entity> sortEntitiesLikes(List<Entity> input)
	{
		PriorityQueue<Entity> sorted = new PriorityQueue<Entity>(100, new Comparator<Entity>()
				{
					@Override
					public int compare(Entity a, Entity b)
					{
						if(a.getEntityStats().getLikes() > b.getEntityStats().getLikes())
						{
							return -1;
						}
						if(a.getEntityStats().getLikes() < b.getEntityStats().getLikes())
						{
							return 1;
						}
						return 0;
					}
				});
		for(Entity elem : input)
		{
			if(elem.getEntityStats().getLikes() > 0)
			{
				sorted.add(elem);
			}
		}
		return sorted;
	}
	
	/**
	 * Sorts the results by comments
	 * @param input
	 * @return
	 */
	public static PriorityQueue<Entity> sortEntitiesComments(List<Entity> input)
	{
		PriorityQueue<Entity> sorted = new PriorityQueue<Entity>(100, new Comparator<Entity>()
				{
					@Override
					public int compare(Entity a, Entity b)
					{
						if(a.getEntityStats().getComments() > b.getEntityStats().getComments())
						{
							return -1;
						}
						if(a.getEntityStats().getComments() < b.getEntityStats().getComments())
						{
							return 1;
						}
						return 0;
					}
				});
		for(Entity elem : input)
		{
			if(elem.getEntityStats().getComments() > 0)
			{
				sorted.add(elem);
			}
		}
		return sorted;
	}
	
	/**
	 * Sorts the input based on the amount of times drafted
	 * @param input
	 * @return
	 */
	public static PriorityQueue<Entity> sortEntitiesDraft(List<Entity> input)
	{
		PriorityQueue<Entity> sorted = new PriorityQueue<Entity>(100, new Comparator<Entity>()
				{
					@Override
					public int compare(Entity a, Entity b)
					{
						if(Integer.parseInt(a.getMetaData()) > Integer.parseInt(b.getMetaData()))
						{
							return -1;
						}
						if(Integer.parseInt(a.getMetaData()) < Integer.parseInt(b.getMetaData()))
						{
							return 1;
						}
						return 0;
					}
				});
		for(Entity elem : input)
		{
			if(elem.getMetaData()!= null && elem.getMetaData().length() > 0 && ManageInput.isInteger(elem.getMetaData()) && Integer.parseInt(elem.getMetaData()) > 0)
			{
				sorted.add(elem);
			}
		}
		return sorted;
	}
}
