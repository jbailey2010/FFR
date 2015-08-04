package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import FileIO.ReadFromFile;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

/**
 * Handles the .csv exporting to file
 * 
 * @author Jeff
 * 
 */
public class HandleExport {
	/**
	 * Gets a priority queue of the players
	 * 
	 * @return
	 */
	public static PriorityQueue<PlayerObject> orderPlayers(Storage holder,
			Context cont) {
		PriorityQueue<PlayerObject> totalList = null;
		boolean isAuction = ReadFromFile.readIsAuction(cont);
		if (isAuction) {
			totalList = new PriorityQueue<PlayerObject>(300,
					new Comparator<PlayerObject>() {
						@Override
						public int compare(PlayerObject a, PlayerObject b) {
							if (a.values.worth > b.values.worth) {
								return -1;
							}
							if (a.values.worth < b.values.worth) {
								return 1;
							}
							return 0;
						}
					});
		} else {
			totalList = new PriorityQueue<PlayerObject>(300,
					new Comparator<PlayerObject>() {
						@Override
						public int compare(PlayerObject a, PlayerObject b) {
							if (a.values.ecr == -1 && b.values.ecr != -1) {
								return 1;
							}
							if (a.values.ecr != -1 && b.values.ecr == -1) {
								return -1;
							}
							if (a.values.ecr == -1 && b.values.ecr == -1) {
								if (a.values.worth > b.values.worth) {
									return -1;
								}
								if (b.values.worth > a.values.worth) {
									return 1;
								}
								return 0;
							}
							if (a.values.ecr > b.values.ecr) {
								return 1;
							}
							if (a.values.ecr < b.values.ecr) {
								return -1;
							}
							return 0;
						}
					});
		}
		for (PlayerObject player : holder.players) {
			totalList.add(player);
		}
		return totalList;
	}

	private static void writeCsvData(String name, double secWorth,
			String position, String team, String age, String string,
			Integer integer, String adp, double points, double paa, Double ecr,
			double risk, String note, FileWriter writer) throws IOException {
		String line = String.format("%s,%f,%s,%s,%s,%s,%d,%s,%f,%f,%f,%f,%s\n",
				name, secWorth, position, team, age, string, integer, adp, ecr,
				points, paa, risk, note);
		writer.write(line);

	}

	/**
	 * Writes the csv header
	 */
	private static void writeCsvHeader(FileWriter writer) throws IOException {
		String line = String
				.format("Name,Worth,Position,Team,Age,Bye,SOS,ADP,ECR,Proj,PAA,PAApd,Risk,Note\n");
		writer.write(line);
	}

	/**
	 * Handles google drive initially
	 */
	public static void driveInit(PriorityQueue<PlayerObject> players,
			Dialog dialog, Context cont, Storage holder) {
		FileWriter writer;
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath()
				+ "/Fantasy Football Rankings");
		dir.mkdirs();
		File output = new File(dir, "Rankings.csv");
		try {
			writer = new FileWriter(output);
			writeCsvHeader(writer);
			while (!players.isEmpty()) {
				PlayerObject player = players.poll();
				if (!player.info.team.equals("None")
						&& !player.info.team.equals("---")
						&& !player.info.team.equals("FA")
						&& player.info.team.length() > 0
						&& player.info.team.length() > 0
						&& !player.info.name.contains("NO MATCH FOUND")) {
					String note = " ";
					if (holder.notes.containsKey(player.info.name
							+ player.info.position)) {
						note = holder.notes.get(player.info.name
								+ player.info.position);
					}
					writeCsvData(
							player.info.name,
							player.values.secWorth,
							player.info.position,
							player.info.team,
							player.info.age,
							holder.bye.get(player.info.team),
							holder.sos.get(player.info.team + ","
									+ player.info.position), player.info.adp,
							player.values.points, player.values.paa,
							player.values.ecr, player.risk, note, writer);
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
		cont.startActivity(Intent
				.createChooser(
						i,
						"Exported to the SD card, directory Fantasy Football Rankings. "
								+ "Select below if you'd also like to send it elsewhere."));
	}

}
