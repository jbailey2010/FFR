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
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.example.fantasyfootballrankings.R;

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
	public static PriorityQueue<PlayerObject> orderPlayers(Storage holder)
	{
		PriorityQueue<PlayerObject>totalList = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
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
			String bye, String adp, String trend, double high, double low, FileWriter writer) throws IOException 
	{  
		  String line = String.format("%f,%s,%s,%s,%s,%s,%s,%s,%f,%f\n", worth, name, pos, team, age, bye, adp, trend, high, low);
		  writer.write(line);
	}
	
	/**
	 * Writes the csv header
	 */
	private static void writeCsvHeader(FileWriter writer) throws IOException 
	{
		String line = String.format("Worth,Name,Position,Team,Age,Bye,ADP,Trend,High,Low\n");
		writer.write(line);
	}

	
	
	/**
	 * Handles google drive initially
	 */
	public static void driveInit(PriorityQueue<PlayerObject> players, Dialog dialog, Context cont)
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
				writeCsvData(player.values.worth, player.info.name, player.info.position, player.info.team,
						player.info.age, player.info.bye, player.info.adp, player.info.trend, player.values.high,
						player.values.low, writer);
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
