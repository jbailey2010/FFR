package com.example.fantasyfootballrankings.ClassFiles;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import org.htmlcleaner.XPatherException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import com.ffr.fantasyfootballrankings.R;
import FileIO.ReadFromFile;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.DropBoxManager.Entry;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Handles the .csv exporting to file
 * @author Jeff
 *
 */ 
public class HandleExport 
{
	/**
	 * Gets a priority queue of the players
	 * @return
	 */
	public static PriorityQueue<PlayerObject> orderPlayers(Storage holder, Context cont)
	{
		PriorityQueue<PlayerObject>totalList = null;
		boolean isAuction = ReadFromFile.readIsAuction(cont);
		if(isAuction)
		{
			totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
			{
				@Override
				public int compare(PlayerObject a, PlayerObject b) 
				{
					if (a.values.worth > b.values.worth)
					{
					    return -1;
					}
					if (a.values.worth < b.values.worth)
					{
						return 1;
					}
				    return 0;
				}
			});
		}
		else
		{ 
			totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
			{
				@Override
				public int compare(PlayerObject a, PlayerObject b) 
				{
					if(a.values.ecr == -1 && b.values.ecr != -1)
					{
						return 1;
					}
					if(a.values.ecr != -1 && b.values.ecr == -1)
					{
						return -1;
					}
					if(a.values.ecr == -1 && b.values.ecr == -1)
					{
						if(a.values.worth > b.values.worth)
						{
							return -1;
						}
						if(b.values.worth > a.values.worth)
						{
							return 1;
						}
						return 0;
					}
					if (a.values.ecr > b.values.ecr)
				    {
					        return 1;
				    }
				    if (a.values.ecr < b.values.ecr)
				    {
				    	return -1;
				    }
				    return 0;
				}
			});
		}
		for(PlayerObject player : holder.players)
		{
			totalList.add(player);
		}
		return totalList;
	}
	
	
	/**
	 * Writes each line of the .csv
	 */
	private static void writeCsvData(double worth, String name, String pos, String team, String age,
			String bye, int sos, String adp, String trend, double proj, double paa, double paapd, double ecr, double risk,
			FileWriter writer) throws IOException 
	{  
		  String line = String.format("%f,%s,%s,%s,%s,%s,%d,%s,%f,%f,%f,%f,%f,%s\n", 
				  					worth, name, pos, team, age, bye, sos, adp, ecr, proj, paa, paapd, risk, trend);
		  writer.write(line);
	}
	
	/**
	 * Writes the csv header
	 */
	private static void writeCsvHeader(FileWriter writer) throws IOException 
	{
		String line = String.format("Worth,Name,Position,Team,Age,Bye,SOS,ADP,ECR,Proj,PAA,PAApd,Risk,Trend\n");
		writer.write(line);
	}

	 
	
	/**
	 * Handles google drive initially
	 */
	public static void driveInit(PriorityQueue<PlayerObject> players, Dialog dialog, Context cont, Storage holder)
	{
		FileWriter writer; 
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/Fantasy Football Rankings");
		dir.mkdirs();		
		File output = new File(dir, "Rankings.csv");
		try {
	        writer = new FileWriter(output);
			writeCsvHeader(writer);
			while(!players.isEmpty())
			{
				PlayerObject player = players.poll();
				if(!player.info.team.equals("None") && !player.info.team.equals("---") && !player.info.team.equals("FA") && 
						player.info.team.length() > 0 && player.info.team.length() > 0 && !player.info.name.contains("NO MATCH FOUND"))
				{
					writeCsvData(player.values.secWorth, player.info.name, player.info.position, player.info.team,
							player.info.age, holder.bye.get(player.info.team), holder.sos.get(player.info.team + "," + player.info.position), 
							player.info.adp, player.info.trend, player.values.points, 
							player.values.paa, player.values.paapd, player.values.ecr, player.risk, writer);
				}
			}
			writer.flush();
			writer.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(output));
		cont.startActivity(Intent.createChooser(i, "Exported to the SD card, directory Fantasy Football Rankings. " +
				"Select below if you'd also like to send it elsewhere."));
	}	
}
