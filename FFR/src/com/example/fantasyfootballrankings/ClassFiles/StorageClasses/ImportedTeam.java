package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;

public class ImportedTeam {
	public List<TeamAnalysis> teams = new ArrayList<TeamAnalysis>();
	public String leagueName;
	public String leagueHost;
	public Roster roster;
	public Scoring scoring;

	public ImportedTeam(List<TeamAnalysis> teamSet, String name, String host) {
		teams = teamSet;
		leagueName = name;
		leagueHost = host;
	}
}
