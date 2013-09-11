package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.util.ArrayList;
import java.util.List;

public class ImportedTeam 
{
	public List<TeamAnalysis> teams = new ArrayList<TeamAnalysis>();
	public String leagueName;
	public String leagueHost;
	public ImportedTeam(List<TeamAnalysis>teamSet, String name, String host)
	{
		teams = teamSet;
		leagueName = name;
		leagueHost = host;
	}
}
