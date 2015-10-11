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

	public boolean doesLeagueAllowPosition(String position) {
		if ("RB/WR".equals(position)) {
			return doesLeagueAllowPosition("RB")
					&& doesLeagueAllowPosition("WR");
		}
		if ("RB/WR/TE".equals(position)) {
			return doesLeagueAllowPosition("RB")
					&& doesLeagueAllowPosition("WR")
					&& doesLeagueAllowPosition("TE");
		}
		if ("QB/RB/WR/TE".equals(position)) {
			return doesLeagueAllowPosition("RB")
					&& doesLeagueAllowPosition("WR")
					&& doesLeagueAllowPosition("TE")
					&& doesLeagueAllowPosition("QB");
		}
		if ("QB".equals(position)) {
			return (roster.qbs > 0 || (roster.flex != null && roster.flex.op > 0));
		} else if ("RB".equals(position)) {
			return (roster.rbs > 0 || (roster.flex != null && (roster.flex.rbwr > 0
					|| roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if ("WR".equals(position)) {
			return (roster.wrs > 0 || (roster.flex != null && (roster.flex.rbwr > 0
					|| roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if ("TE".equals(position)) {
			return (roster.tes > 0 || (roster.flex != null && (roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if ("D/ST".equals(position)) {
			return roster.def > 0;
		} else if ("K".equals(position)) {
			return roster.k > 0;
		}
		return false;
	}
}
