package com.example.fantasyfootballrankings.ClassFiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.htmlcleaner.XPatherException;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.BasicInfo;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Post;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.PostedPlayer;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Values;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPN;
import com.example.fantasyfootballrankings.ClassFiles.ParseFiles.ParseESPNadv;

import AsyncTasks.AlteringDataAsyncTasks;
import AsyncTasks.AlteringDataAsyncTasks.OfflineHighLevel;
import AsyncTasks.ParsingAsyncTask;
import AsyncTasks.ParsingAsyncTask.ADPHighLevel;
import AsyncTasks.ParsingAsyncTask.ParseRanks;
import AsyncTasks.ParsingAsyncTask.StatsHighLevel;
import FileIO.ReadFromFile;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

/**
 * This is actually where the rankings are parsed
 * One function per web site to be parsed
 * @author Jeff
 *
 */
public class ParseRankings 
{
	static Context context;
	/** 
	 * This is where the magic happens. This sets the names/fetches them if they're
	 * stored (not based on user input as it won't change), then fetches the players, 
	 * and runs high-level stuff on it. Does NOT save, as that is up to user choice.
	 * @param cont the context used to work with the saving
	 * @throws IOException
	 * @throws XPatherException
	 */
	public static void runRankings(final Storage holder, final Context cont) throws IOException, XPatherException
	{  
		holder.players.clear();
		context = cont;
		ParsingAsyncTask stupid = new ParsingAsyncTask();
	    ParseRanks task = stupid.new ParseRanks((Activity)cont, holder);
	    task.execute(holder, cont);
	    
	}
	
	/**
	 * Calls the high level functions
	 * @param cont
	 * @param holder
	 */
	public static void highLevel(Activity cont, Storage holder)
	{
		
		ParsingAsyncTask stupid = new ParsingAsyncTask();
		AlteringDataAsyncTasks status = new AlteringDataAsyncTasks();
		
		StatsHighLevel stats = stupid.new StatsHighLevel(cont);
		stats.execute(holder, cont);
		
	    ADPHighLevel task = stupid.new ADPHighLevel(cont);
	    task.execute(holder, cont);
	    
	    //SOSHighLevel sos = stupid.new SOSHighLevel(cont);
	    //sos.execute(holder, cont);
	    
	    //ContractHighLevel contract = stupid.new ContractHighLevel(cont);
	    //contract.execute(holder, cont);
	    
	    OfflineHighLevel offline = status.new OfflineHighLevel(cont, holder);
	    offline.execute(holder, cont);

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
	 */
	public static void finalStretch(Storage holder, String playerName, int val, String team, String pos)
	{
		String validated = fixNames(playerName);
		String newName = Storage.nameExists(holder, validated);
		PlayerObject newPlayer = new PlayerObject(newName, team, pos, val);
		PlayerObject match =  Storage.pqExists(holder, newName);
		ParseRankings.handlePlayer(holder, newPlayer, match);
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
			holder.players.remove(match);
			BasicInfo.standardAll(newPlayer.info.team, newPlayer.info.position, match.info);
			Values.handleNewValue(match.values, newPlayer.values.worth);
			match.info.team = fixTeams(match.info.team);
			holder.players.add(match);
		}
		else
		{
			Values.isExtreme(newPlayer.values, newPlayer.values.worth);
			newPlayer.info.team = fixTeams(newPlayer.info.team);
			holder.players.add(newPlayer);
		}		
	}
	
	/**
	 * Some noticed name issues, abstracted here for the sake of use later
	 * @param playerName the name to check
	 * @return the fixed name
	 */
	public static String fixNames(String playerName)
	{
		if(playerName.contains("D/ST"))
		{
			playerName = fixDefenses(playerName);
		}
		else if(playerName.equals("QB Eagles No. 7"))
		{
			playerName = "Michael Vick";
		}
		else if(playerName.equals("Robert Griffin"))
		{
			playerName = "Robert Griffin III";
		}
		else if(playerName.equals("Jonathan Baldwin"))
		{
			playerName = "Jon Baldwin";
		}
		else if(playerName.equals("Chris Wells") || playerName.contains("Beanie"))
		{
			playerName = "Beanie Wells";
		}		
		else if(playerName.equals("Steve L. Smith"))
		{
			playerName = "Steve Smith";
		}
		else if(playerName.equals("Mike A. Williams"))
		{
			playerName = "Mike Williams";
		}
		else if(playerName.equals("Alex D. Smith"))
		{
			playerName = "Alex Smith";
		}
		else if(playerName.equals("Adrian L. Peterson"))
		{
			playerName = "Adrian Peterson";
		}
		else if(playerName.equals("Stevie Johnson"))
		{
			playerName = "Steve Johnson";
		}
		else if(playerName.equals("Robert Housler"))
		{
			playerName = "Rob Housler";
		}
		else if(playerName.equals("Christopher Ivory"))
		{
			playerName = "Chris Ivory";
		}
		else if(playerName.toLowerCase().equals("robert griffiniii"))
		{
			playerName = "Robert Griffin III";
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
		else if(name.contains("tamba bay"))
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
		String low = team.toLowerCase().trim().replaceAll("[^a-zA-Z]+","");
		if(low.equals("cin") || low.equals("cincinnati") || low.equals("bengals"))
		{
			return "Cincinnati Bengals";
		}
		if(low.equals("pit") || low.equals("pittsburgh") || low.equals("steelers"))
		{
			return "Pittsburgh Steelers";
		}
		if(low.equals("cle") || low.equals("clv") || low.equals("cleveland") || low.equals("browns"))
		{
			return "Cleveland Browns";
		}
		if(low.equals("bal") || low.equals("blt") || low.equals("baltimore") || low.equals("ravens"))
		{
			return "Baltimore Ravens";
		}
		if(low.equals("mia") || low.equals("miami") || low.equals("dolphins"))
		{
			return "Miami Dolphins";
		}
		if(low.equals("nwe") || low.equals("ne") || low.equals("new england") || low.equals("patriots"))
		{
			return "New England Patriots";
		}
		if(low.equals("nyj") || low.equals("jets"))
		{
			return "New York Jets";
		}
		if(low.equals("buf") || low.equals("buffalo") || low.equals("bills"))
		{
			return "Buffalo Bills";
		}
		if(low.equals("ind") || low.equals("indianapolis") || low.equals("colts"))
		{
			return "Indianapolis Colts";
		}
		if(low.equals("jac") || low.equals(" jac") || low.equals("jac ") ||
				low.equals("jax") || low.equals("jacksonville") || low.equals("jaguars"))
		{
			return "Jacksonville Jaguars";
		}
		if(low.equals("hou") || low.equals("hst") || low.equals("houston") || low.equals("texans"))
		{
			return "Houston Texans";
		}
		if(low.equals("ten") || low.equals("tennessee") || low.equals("titans"))
		{
			return "Tennessee Titans";
		}
		if(low.equals("kc") || low.equals("kansas city") || low.equals("chiefs"))
		{
			return "Kansas City Chiefs";
		}
		if(low.equals("oak") || low.equals("oakland") || low.equals("raiders"))
		{
			return "Oakland Raiders";
		}
		if(low.equals("den") || low.equals("denver") || low.equals("broncos"))
		{
			return "Denver Broncos";
		}
		if(low.equals("sd") || low.equals("san diego") || low.equals("chargers"))
		{
			return "San Diego Chargers";
		}
		if(low.equals("chi") || low.equals("chicago") || low.equals("bears"))
		{
			return "Chicago Bears";
		}
		if(low.equals("min") || low.equals("minnesota") || low.equals("vikings"))
		{
			return "Minnesota Vikings";
		}
		if(low.equals("det") || low.equals("detroit") || low.equals("lions"))
		{
			return "Detroit Lions";
		}
		if(low.equals("gb") || low.equals(" gb") || low.equals("gb ") ||
				low.equals("green bay") || low.equals("packers"))
		{
			return "Green Bay Packers";
		}
		if(low.equals("nyg") || low.equals("giants"))
		{
			return "New York Giants";
		}
		if(low.equals("phi") || low.equals("philadelphia") || low.equals("eagles"))
		{
			return "Philadelphia Eagles";
		}
		if(low.equals("dal") || low.equals("dallas") || low.equals("cowboys"))
		{
			return "Dallas Cowboys";
		}
		if(low.equals("was") || low.equals("washington") || low.equals("redskins"))
		{
			return "Washington Redskins";
		}
		if(low.equals("atl") || low.equals("atlanta") || low.equals("falcons"))
		{
			return "Atlanta Falcons";
		}
		if(low.equals("car") || low.equals("carolina") || low.equals("panthers"))
		{
			return "Carolina Panthers";
		}
		if(low.equals("no") || low.equals("new orleans") || low.equals("saints"))
		{
			return "New Orleans Saints";
		}
		if(low.equals("tb") || low.equals("tampa bay") || low.equals("buccaneers"))
		{
			return "Tampa Bay Buccaneers";
		}
		if(low.equals("sea") || low.equals("seattle") || low.equals("seahawks"))
		{
			return "Seattle Seahawks";
		}
		if(low.equals("sf") || low.equals("san francisco") || low.contains("49ers") || low.equals("ers"))
		{
			return "San Francisco 49ers";
		}
		if(low.equals("stl") || low.equals("st. louis") || low.equals("st louis") || 
				low.equals("rams") || low.equals("sl"))
		{
			return "St. Louis Rams";
		}
		if(low.equals("ari") || low.equals("arizona") || low.equals("cardinals") || low.equals("arz"))
		{
			return "Arizona Cardinals";
		}
		return team;
	}

}
