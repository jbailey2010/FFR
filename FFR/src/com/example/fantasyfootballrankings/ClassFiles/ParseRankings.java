package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;






import java.util.HashMap;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ParseRanks;
import android.app.Activity;
import android.content.Context;

/**
 * This is actually where the rankings are parsed
 * One function per web site to be parsed
 * @author Jeff
 *
 */
public class ParseRankings 
{
	public static HashMap<String, String> fixes = new HashMap<String, String>();
	public static HashMap<String, String> teams = new HashMap<String, String>();
	public static HashMap<String, String> defenses = new HashMap<String, String>();
	
	static Context context;
	/** 
	 * This is where the magic happens. This sets the names/fetches them if they're
	 * stored (not based on user input as it won't change), then fetches the players, 
	 * and runs high-level stuff on it. 
	 * @param cont the context used to work with the saving
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void runRankings(final Storage holder, final Context cont) throws IOException, XPatherException
	{  
		handleHashes();
		context = cont;
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		
	    ParseRanks task = stupid.new ParseRanks((Activity)cont, holder);
	    task.execute(holder, cont);
	}
	
	/**
	 * Populates the fix hashes
	 */
	public static void handleHashes()
	{
		fixes.clear();
		fixes.put("Chris D. Johnson", "Chris Johnson");
		fixes.put("Charles D. Johnson", "Charles Johnson");
		fixes.put("Zach J. Miller", "Zach Miller");
		fixes.put("Leveon Bell", "Le'Veon Bell");
		fixes.put("LeVeon Bell", "Le'Veon Bell");
		fixes.put("QB Eagles No. 7", "Michael Vick");
		fixes.put("Robert Griffin", "Robert Griffin III");
		fixes.put("Jonathan Baldwin", "Jon Baldwin");
		fixes.put("Chris Wells", "Beanie Wells");
		fixes.put("Steve L. Smith", "Steve Smith");
		fixes.put("Mike A. Williams", "Mike Williams"); 
		fixes.put("Alex D. Smith", "Alex Smith");
		fixes.put("Adrian L. Peterson", "Adrian Peterson");
		fixes.put("Stevie Johnson", "Steve Johnson");
		fixes.put("Robert Housler", "Rob Housler");
		fixes.put("Christopher Ivory", "Chris Ivory");
		fixes.put("R. Mendenhall", "Rashard Mendenhall");
		fixes.put("B. Green-Ellis", "BenJarvus Green-Ellis");
		fixes.put("BJGE", "BenJarvus Green-Ellis");
		fixes.put("Joseph Morgan", "Joe Morgan");
		fixes.put("E.j. Manuel", "EJ Manuel");
		fixes.put("TY Hilton", "T.Y. Hilton");
		fixes.put("Ty Hilton", "T.Y. Hilton");
		fixes.put("Reuben Randle", "Rueben Randle");
		fixes.put("E.J. Manuel", "EJ Manuel"); 
		fixes.put("Steve L Smith", "Steve Smith");
		fixes.put("Michael Crabtree*", "Michael Crabtree");
		fixes.put("Malcolm Floyd", "Malcom Floyd");
		fixes.put("Wesley Welker", "Wes Welker"); 
		fixes.put("LaVon Brazil", "LaVon Brazill");
		fixes.put("Steve Hauschka", "Steven Hauschka");
		fixes.put("Ben Watson", "Benjamin Watson");
		fixes.put("Deangelo Williams", "DeAngelo Williams");
		fixes.put("Demarco Murray", "DeMarco Murray");
		fixes.put("Laron Byrd", "LaRon Byrd");
		fixes.put("Ted Ginn Jr.", "Ted Ginn");
		fixes.put("D.J. Williams Jr.", "D.J. Williams");
		fixes.put("Greg Jones II", "Greg Jones");
		fixes.put("Matt Stafford", "Matthew Stafford");
		fixes.put("Mike Vick", "Michael Vick");
		fixes.put("Jamal Charles", "Jamaal Charles");
		fixes.put("CJ Spiller", "C.J. Spiller");
		fixes.put("Maurice Jones Drew", "Maurice Jones-Drew");
		fixes.put("Jon Stewart", "Jonathan Stewart");
		fixes.put("Jonathan Franklin", "Johnathan Franklin");
		fixes.put("Mikel LeShoure", "Mikel Leshoure");
		fixes.put("Jaquizz Rodgers", "Jacquizz Rodgers");
		fixes.put("AJ Green", "A.J. Green");
		fixes.put("Delaine Walker", "Delanie Walker");
		fixes.put("Steven Gostkowski", "Stephen Gostkowski");
		fixes.put("Cecil Shorts III", "Cecil Shorts");
		fixes.put("T Y Hilton", "T.Y. Hilton");
		fixes.put("Joshua Cribbs", "Josh Cribbs");
		fixes.put("Devier Posey", "DeVier Posey");
		fixes.put("Darren Mcfadden", "Darren McFadden");
		fixes.put("Robert Griffin, III", "Robert Griffin III");
		fixes.put("Rashad Mendenhall", "Rashard Mendenhall");
		fixes.put("Le`Veon Bell", "Le'Veon Bell");
		fixes.put("Benjarvus Green-Ellis", "BenJarvus Green-Ellis");
		fixes.put("Michael Crabtre", "Michael Crabtree");
		fixes.put("Zach K. Brown", "Zach Brown");
		fixes.put("Gio Bernard", "Giovani Bernard");
		fixes.put("Timothy Wright", "Tim Wright");
		
		teams.clear();
		teams.put("cin", "Cincinnati Bengals");
		teams.put("cincinnati", "Cincinnati Bengals");
		teams.put("bengals", "Cincinnati Bengals");
		teams.put("pit", "Pittsburgh Steelers");
		teams.put("pittsburgh", "Pittsburgh Steelers");
		teams.put("steelers", "Pittsburgh Steelers");
		teams.put("cle", "Cleveland Browns");
		teams.put("clv", "Cleveland Browns");
		teams.put("cleveland", "Cleveland Browns");
		teams.put("browns", "Cleveland Browns");
		teams.put("bal", "Baltimore Ravens");
		teams.put("blt", "Baltimore Ravens");
		teams.put("baltimore", "Baltimore Ravens");
		teams.put("ravens", "Baltimore Ravens");
		teams.put("mia", "Miami Dolphins");
		teams.put("miami", "Miami Dolphins");
		teams.put("dolphins", "Miami Dolphins");
		teams.put("nwe", "New England Patriots");
		teams.put("ne", "New England Patriots");
		teams.put("new england", "New England Patriots");
		teams.put("england", "New England Patriots");
		teams.put("patriots", "New England Patriots");
		teams.put("pats", "New England Patriots");
		teams.put("nyj", "New York Jets");
		teams.put("jets", "New York Jets");
		teams.put("ny jets", "New York Jets");
		teams.put("n.y. jets", "New York Jets");
		teams.put("buf", "Buffalo Bills");
		teams.put("buffalo", "Buffalo Bills");
		teams.put("bills", "Buffalo Bills");
		teams.put("ind", "Indianapolis Colts");
		teams.put("indianapolis", "Indianapolis Colts");
		teams.put("colts", "Indianapolis Colts");
		teams.put("jac", "Jacksonville Jaguars");
		teams.put(" jac", "Jacksonville Jaguars");
		teams.put("jac ", "Jacksonville Jaguars");
		teams.put("jax", "Jacksonville Jaguars");
		teams.put("jacksonville", "Jacksonville Jaguars");
		teams.put("jaguars", "Jacksonville Jaguars");
		teams.put("hou", "Houston Texans");
		teams.put("houston", "Houston Texans");
		teams.put("hst", "Houston Texans");
		teams.put("texans", "Houston Texans");
		teams.put("ten", "Tennessee Titans");
		teams.put("tennessee", "Tennessee Titans");
		teams.put("titans", "Tennessee Titans");
		teams.put("kc", "Kansas City Chiefs");
		teams.put("kansas", "Kansas City Chiefs");
		teams.put("kansas city", "Kansas City Chiefs");
		teams.put("chiefs", "Kansas City Chiefs");
		teams.put("oak", "Oakland Raiders");
		teams.put("oakland", "Oakland Raiders");
		teams.put("raiders", "Oakland Raiders");
		teams.put("den", "Denver Broncos");
		teams.put("denver", "Denver Broncos");
		teams.put("broncos", "Denver Broncos");
		teams.put("sd", "San Diego Chargers");
		teams.put("san diego", "San Diego Chargers");
		teams.put("chargers", "San Diego Chargers");
		teams.put("chi", "Chicago Bears");
		teams.put("chicago", "Chicago Bears");
		teams.put("bears", "Chicago Bears");
		teams.put("min", "Minnesota Vikings");
		teams.put("minnesota", "Minnesota Vikings");
		teams.put("vikings", "Minnesota Vikings");
		teams.put("det", "Detroit Lions");
		teams.put("detroit", "Detroit Lions");
		teams.put("lions", "Detroit Lions");
		teams.put("gb", "Green Bay Packers");
		teams.put("gb", "Green Bay Packers");
		teams.put("gb ", "Green Bay Packers");
		teams.put(" gb", "Green Bay Packers");
		teams.put("green bay", "Green Bay Packers");
		teams.put("packers", "Green Bay Packers");
		teams.put("nyg", "New York Giants");
		teams.put("n.y. giants", "New York Giants");
		teams.put("giants", "New York Giants");
		teams.put("ny giants", "New York Giants");
		teams.put("phi", "Philadelphia Eagles");
		teams.put("philadelphia", "Philadelphia Eagles");
		teams.put("eagles", "Philadelphia Eagles");
		teams.put("dal", "Dallas Cowboys");
		teams.put("dallas", "Dallas Cowboys");
		teams.put("cowboys", "Dallas Cowboys");
		teams.put("was", "Washington Redskins");
		teams.put("washington", "Washington Redskins");
		teams.put("redskins", "Washington Redskins");
		teams.put("atl", "Atlanta Falcons");
		teams.put("atlanta", "Atlanta Falcons");
		teams.put("falcons", "Atlanta Falcons");
		teams.put("car", "Carolina Panthers");
		teams.put("carolina", "Carolina Panthers");
		teams.put("panthers", "Carolina Panthers");
		teams.put("no", "New Orleans Saints");
		teams.put("new orleans", "New Orleans Saints");
		teams.put("saints", "New Orleans Saints");
		teams.put("tb", "Tampa Bay Buccaneers");
		teams.put("tampa bay", "Tampa Bay Buccaneers");
		teams.put("buccaneers", "Tampa Bay Buccaneers");
		teams.put("tampa", "Tampa Bay Buccaneers");
		teams.put("sea", "Seattle Seahawks");
		teams.put("seattle", "Seattle Seahawks");
		teams.put("seahawks", "Seattle Seahawks");
		teams.put("sf", "San Francisco 49ers");
		teams.put("san francisco", "San Francisco 49ers");
		teams.put("ers", "San Francisco 49ers");
		teams.put("49ers", "San Francisco 49ers");
		teams.put("stl", "St. Louis Rams");
		teams.put("st. louis", "St. Louis Rams");
		teams.put("st louis", "St. Louis Rams");
		teams.put("rams", "St. Louis Rams");
		teams.put("sl", "St. Louis Rams");
		teams.put("ari", "Arizona Cardinals");
		teams.put("arz", "Arizona Cardinals");
		teams.put("arizona", "Arizona Cardinals");
		teams.put("cardinals", "Arizona Cardinals");
	}

	
	/**
	 * Super basic function. Gets a normalized name, makes a player with it. If
	 * it exists in the pq or not, it calls handlePlayer to do what's appropriate.
	 * Abstracted as it was called in all parsers.
	 * @param holder the storage written to
	 * @param playerName the name to be checked
	 * @param val the parsed worth
	 * @param team the team parsed
	 * @param pos the postition parsed
	 * @param counter 
	 */
	public static void finalStretch(Storage holder, String playerName, int val, String team, String pos)
	{
		if(playerName.equals("Adrian Peterson")){
			System.out.println(val);
		}
		String validated = fixNames(playerName);
		String newName = Storage.nameExists(holder, validated);
		PlayerObject newPlayer = new PlayerObject(newName, team, pos, val);
		PlayerObject match =  Storage.pqExists(holder, newName);
		handlePlayer(holder, newPlayer, match);
	}
	
	/**
	 * A generally abstracted function that has two cases -- if 
	 * it exists in the priority queue already and if it doesn't.
	 * If so, it removes it from the pq, standardizes stuff, adjusts the
	 * values, and re-adds it, if it isn't there, it sets extremes and adds it.
	 * @param holder the storage to be added to
	 * @param newPlayer the newly created player to be worked with
	 * @param match the already existent match to use if need be
	 */
	public static void handlePlayer(Storage holder, PlayerObject newPlayer, PlayerObject match)
	{
		if(match != null)
		{
			int flag = 	BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
			if(flag == -1)
			{
				for(PlayerObject iter : holder.players)
				{
					if(iter.info.name.equals(newPlayer.info.name) && ! iter.info.position.equals(match.info.position))
					{
						handlePlayer(holder, newPlayer, iter);
						return;
					}
				}
				newPlayer.info.team = fixTeams(newPlayer.info.team);
				holder.players.add(newPlayer);
				holder.parsedPlayers.add(newPlayer.info.name);
			}
			Values.handleNewValue(match.values, newPlayer.values.worth);
			if(newPlayer.info.name.equals("Adrian Peterson"))
			{
				System.out.println(newPlayer.values.worth + ", for a new total of " + match.values.worth);
			}
			match.info.team = fixTeams(match.info.team);
		}
		else
		{
			newPlayer.info.team = fixTeams(newPlayer.info.team);
			if(newPlayer.info.name.equals("Adrian Peterson"))
			{
				System.out.println(newPlayer.values.worth);
			}
			holder.players.add(newPlayer);
			holder.parsedPlayers.add(newPlayer.info.name);
		}		
	}
	
	/**
	 * Some noticed name issues, abstracted here for the sake of use later
	 * @param playerName the name to check
	 * @return the fixed name
	 */
	public static String fixNames(String playerName)
	{
		if(fixes.containsKey(playerName))
		{
			playerName = fixes.get(playerName);
		}
		else if(playerName.contains("D/ST"))
		{
			playerName = fixDefenses(playerName);
		}
		else if(playerName.contains("Beanie"))
		{
			playerName = "Beanie Wells";
		}		
		else if(playerName.toLowerCase().equals("robert griffiniii"))
		{
			playerName = "Robert Griffin III";
		}
		else if(playerName.contains("Veon") && playerName.contains("Bell"))
		{
			playerName = "Le'Veon Bell";
		}
		return playerName;
	}
	
	/**
	 * Maps a team name to a defense with mascot
	 * @param uName
	 * @return
	 */
	public static String fixDefenses(String uName)
	{
		String name = uName.toLowerCase();
		if(name.contains("cincinnati"))
		{
			uName = "Bengals D/ST";
		}
		else if(name.contains("cleveland"))
		{
			uName = "Browns D/ST";
		}
		else if(name.contains("pittsburgh"))
		{
			uName = "Steelers D/ST";
		}
		else if(name.contains("baltimore"))
		{
			uName = "Ravens D/ST";
		}
		else if(name.contains("new england"))
		{
			uName = "Patriots D/ST";
		}
		else if(name.contains("miami"))
		{
			uName = "Dolphins D/ST";
		}
		else if(name.contains("buffalo"))
		{
			uName = "Bills D/ST";
		}
		else if(name.contains("new york jets") || name.contains("ny jets"))
		{
			uName = "Jets D/ST";
		}
		else if(name.contains("indianapolis"))
		{
			uName = "Colts D/ST";
		}
		else if(name.contains("jacksonville"))
		{
			uName = "Jaguars D/ST";
		}
		else if(name.contains("houston"))
		{
			uName = "Texans D/ST";
		}
		else if(name.contains("tennessee"))
		{
			uName = "Titans D/ST";
		}
		else if(name.contains("cleveland"))
		{
			uName = "Browns D/ST";
		}
		else if(name.contains("san diego"))
		{
			uName = "Chargers D/ST";
		}
		else if(name.contains("kansas city"))
		{
			uName = "Chiefs D/ST";
		}
		else if(name.contains("oakland"))
		{
			uName = "Raiders D/ST";
		}
		else if(name.contains("denver"))
		{
			uName = "Broncos D/ST";
		}
		else if(name.contains("chicago"))
		{
			uName = "Bears D/ST";
		}
		else if(name.contains("minnesota"))
		{
			uName = "Vikings D/ST";
		}
		else if(name.contains("detroit"))
		{
			uName = "Lions D/ST";
		}
		else if(name.contains("green bay"))
		{
			uName = "Packers D/ST";
		}
		else if(name.contains("new york giants") || name.contains("ny giants"))
		{
			uName = "Giants D/ST";
		}
		else if(name.contains("philadelphia"))
		{
			uName = "Eagles D/ST";
		}
		else if(name.contains("dallas"))
		{
			uName = "Cowboys D/ST";
		}
		else if(name.contains("washington"))
		{
			uName = "Redskins D/ST";
		}
		else if(name.contains("new orleans"))
		{
			uName = "Saints D/ST";
		}
		else if(name.contains("atlanta"))
		{
			uName = "Falcons D/ST";
		}
		else if(name.contains("carolina"))
		{
			uName = "Panthers D/ST";
		}
		else if(name.contains("tamba bay") || name.contains("tampa bay"))
		{
			uName = "Buccaneers D/ST";
		}
		else if(name.contains("san fran") || name.contains("san francisco"))
		{
			uName = "49ers D/ST";
		}
		else if(name.contains("st. louis") || name.contains("st louis"))
		{
			uName = "Rams D/ST";
		}
		else if(name.contains("arizona"))
		{
			uName = "Cardinals D/ST";
		}
		else if(name.contains("seattle"))
		{
			uName = "Seahawks D/ST";
		}
		return uName;
	}
	
	/**
	 * This adjusts teams to a standard value. MAY NEED ADJUSTING AS NEW PARSERS ADDED
	 * @param team the team to check
	 * @return the adjusted new team
	 */
	public static String fixTeams(String team)
	{
		String low = team.toLowerCase().replaceAll("[^\\x20-\\x7e]","");
		if(low.split(" ").length > 1 && (low.split(" ")[1].equals("p") || low.split(" ")[1].equals("q")))
		{
			low = low.split(" ")[0];
		}
		if(teams.containsKey(low))
		{
			return teams.get(low);
		}
		else if(low.contains("kansas"))
		{
			return "Kansas City Chiefs";
		}
		else if(low.contains("diego"))
		{
			return "San Diego Chargers";
		}
		else if(low.contains("green"))
		{ 
			return "Green Bay Packers";
		}
		else if(low.contains("tampa"))
		{
			return "Tampa Bay Buccaneers";
		}
		else if(low.contains("orleans"))
		{
			return "New Orleans Saints";
		}
		else if(low.contains("louis"))
		{
			return "St. Louis Rams";
		}
		else if(low.contains("francisco"))
		{
			return "San Francisco 49ers";
		}
		else if(low.contains("england"))
		{
			return "New England Patriots";
		}
		else if(low.contains("nyj"))
		{
			return "New York Jets";
		}
		else if(low.contains("tb"))
		{
			return "Tampa Bay Buccaneers";
		}
		else if(low.contains("mia"))
		{
			return "Miami Dolphins";
		}
		return team;
	}

}
