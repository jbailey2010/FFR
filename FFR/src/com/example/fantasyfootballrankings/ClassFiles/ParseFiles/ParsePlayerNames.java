package com.example.fantasyfootballrankings.ClassFiles.ParseFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import FileIO.WriteToFile;
import android.content.Context;

import com.example.fantasyfootballrankings.ClassFiles.HandleBasicQueries;

/**
 * A small library to help manage parsing of player
 * names from cbs to local storage
 * @author Jeff
 *
 */
public class ParsePlayerNames {

	/**
	 * This fetches/parses the player names, then 
	 * calls another function to write it to file
	 * @param holder holds the array to be written to and written
	 * @param cont the context used to write to file in the called function
	 * @throws IOException 
	 */
	public static void fetchPlayerNames(final Context cont) throws IOException
	{
		//holder.playerNames.clear();
		List<String> names = new ArrayList<String>();
		String[] defenses = {"Bengals D/ST", "Steelers D/ST", "Browns D/ST", "Ravens D/ST", 
	   		"Patriots D/ST", "Dolphins D/ST", "Bills D/ST", "Jets D/ST", "Texans D/ST",
	   		"Colts D/ST", "Jaguars D/ST", "Titans D/ST", "Broncos D/ST", "Raiders D/ST",
	   		"Chiefs D/ST", "Chargers D/ST", "Vikings D/ST", "Lions D/ST", "Packers D/ST",
	   		"Bears D/ST", "Giants D/ST", "Eagles D/ST", "Cowboys D/ST", "Redskins D/ST",
	   		"Falcons D/ST", "Saints D/ST", "Panthers D/ST", "Buccaneers D/ST", "Seahawks D/ST",
	   		"49ers D/ST", "Rams D/ST", "Cardinals D/ST"	};
	   	for(int i = 0; i < defenses.length; i++)
	   	{
			names.add(defenses[i]);
	   	}
		//The baseline URL and the endings to be attached for the sake of getting all data easily
		String url = "http://www.cbssports.com/nfl/playersearch?POSITION=";
		String[] concat = {"QB&print_rows=9999", "RB&print_rows=9999", "FB&print_rows=9999",
				"WR&print_rows=9999", "TE&print_rows=9999", "K&print_rows=9999"};
		for(int i = 0; i < concat.length; i++)
		{
			final String full = url + concat[i];
			try {
				Document doc = Jsoup.connect(full).timeout(0).get();
				ParsePlayerNames.fetchPlayersHelp(doc, names, cont, full, "row2");
				ParsePlayerNames.fetchPlayersHelp(doc, names, cont, full, "row1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Left out...why the hell aren't they in the list?
		names.add("Robert Griffin III");
		names.add("Brandon Jacobs");
		names.add("Kellen Winslow");
		names.add("Tim Hightower");
		names.add("Terrell Owens");
		names.add("Latavius Murray");
		names.add("Xavier Rhodes");
		names.add("Ezekiel Ansah");
		names.add("Alec Ogletree");
		names.add("Bernard Pollard");
		names.add("John Potter");
		names.add("Neil Rackers");
		names.add("Vince Young");
		names.add("John Beck");
		names.add("Daniel Hrapmann");
		names.add("Darrel Young");
		names.add("Erik Lorig");
		names.add("Thaddeus Lewis");
		names.add("Greg Jones");
		names.add("Aaron Hernandez");
		names.add("David Garrard");
		names.add("Nate Kaeding");
		names.add("David Ruffer");
		names.add("Kris Brown");
		names.add("Patrick Edwards");
		names.add("Willis McGahee");
		names.add("Brandon Lloyd");
		names.add("Michael Turner");
		names.add("Dallas Clark");
		names.add("Beanie Wells");
		names.add("Cedric Benson");
		names.add("Peyton Hillis");
		names.add("David Thomas");
		names.add("Jason Hanson");
		names.add("Lawrence Tynes");
		names.add("Billy Cundiff");
		names.add("Olindo Mare");
		names.add("Ryan Longwell");
		names.add("Charlie Batch");
		names.add("Curtis Brinkley");
		names.add("Byron Leftwich");
		names.add("Tyler Thigpen");
		names.add("Matt Leinart");
		names.add("Trent Edwards");
		names.add("Max Hall");
		names.add("Javon Ringer");
		names.add("Taiwan Jones");
		names.add("Jamie Harper");
		names.add("Chris Rainey");
		names.add("Mewelde Moore");
		names.add("Danny Ware");
		names.add("Ryan Grant");
		names.add("Kahlil Bell");
		names.add("Jackie Battle");
		names.add("Early Doucet");
		names.add("Braylon Edwards");
		names.add("Josh Cribbs");
		names.add("Jason Hill");
		names.add("Michael Spurlock");
		names.add("Sammie Stroughter");
		names.add("Chaz Schilens");
		names.add("Laurent Robinson");
		names.add("Randy Moss");
		names.add("Ruvell Martin");
		names.add("Brandon Stokley");
		names.add("Legedu Naanee");
		names.add("Seyi Ajirotutu");
		names.add("Kevin Boss");
		names.add("Travis Beckum");
		names.add("Cameron Morrah");
		names.add("Chris Cooley");
		names.add("Leonard Pope");
		names.add("Todd Heap");
		names.add("Randy McMichael");
		names.add("Evan Moore");
		names.add("Will Heller");
		names.add("Justin Medlock");
		names.add("Kevin Smith");
		names.add("Steve Breaston");
		names.add("Deion Branch");
		names.add("Jabar Gaffney");
		names.add("Roscoe Parrish");
		names.add("Greg Camarillo");
		names.add("Brian Robiskie");
		names.add("Donald Jones");
		names.add("Jahvid Best");
		names.add("Brett Maher");
		names.add("Matt McGloin");
		names.add("John Parker Wilson");
		names.add("Ryan W. Griffin");
		names.add("Zac  Dysert");
		names.add("Zachary Brown");
		names.add("Ryan D'Imperio");
		names.add("Matt Slater");
		names.add("Marcus Sales");
		names.add("Tim Wright");
		names.add("Garrard Sheppard");
		names.add("Sederrick Cunningham");
		names.add("Tyrone Walker");
		names.add("Cole McKenzie");
		names.add("Rashod Ross");
		names.add("Joseph Collins");
		names.add("Keith Carlos");
		names.add("Michael Higgins");
		names.add("Dominique Curry");
		names.add("DeMarco Cosby");
		names.add("Bryce Davis");
		names.add("LeStar Jean");
		names.add("Mike Goodson");
		names.add("Ryan Swope");
		names.add("Chad Spann");
		names.add("Alex Silvestro");
		names.add("Jeff Demps");
		names.add("Nathan Enderle");
		names.add("Mardy Gilyard");
		names.add("David Buehler");
		names.add("Vonta Leach");
		names.add("Austin Collie");
		if(names.size() > 200)
		{
			WriteToFile.storePlayerNames(names, cont);
		}
	}

	/**
	 * Abstracted as cbs is stupid, same code twice
	 * @param holder the storage to write to
	 * @param cont the context because idk
	 * @param full the url to work with
	 * @param params the id to parse from
	 * @throws IOException
	 */
	public static void fetchPlayersHelp(Document doc, List<String> names, Context cont, String full, String params) throws IOException
	{
		String playerText = HandleBasicQueries.handleTablesMulti(doc, full, params);
		String[] perRow = playerText.split("\n");
		for(int j = 0; j < perRow.length; j++)
		{
			String[][] all = new String[perRow.length][];
			all[j] = perRow[j].split(" ");
			if(all[j][0].contains(","))
			{
				String lastName = all[j][0].substring(0, all[j][0].length() - 1);
				String name = all[j][1] + " " + lastName;
				names.add(name);
			}
		}
	}

}
