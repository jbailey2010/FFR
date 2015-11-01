package com.example.fantasyfootballrankings.ClassFiles.StorageClasses;

import java.util.ArrayList;
import java.util.List;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Roster;
import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Scoring;
import com.example.fantasyfootballrankings.ClassFiles.Utils.Constants;

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
		if (Constants.RBWR.equals(position)) {
			return doesLeagueAllowPosition(Constants.RB)
					&& doesLeagueAllowPosition(Constants.WR);
		}
		if (Constants.RBWRTE.equals(position)) {
			return doesLeagueAllowPosition(Constants.RB)
					&& doesLeagueAllowPosition(Constants.WR)
					&& doesLeagueAllowPosition(Constants.TE);
		}
		if (Constants.QBRBWRTE.equals(position)) {
			return doesLeagueAllowPosition(Constants.RB)
					&& doesLeagueAllowPosition(Constants.WR)
					&& doesLeagueAllowPosition(Constants.TE)
					&& doesLeagueAllowPosition(Constants.QB);
		}
		if (Constants.QB.equals(position)) {
			return (roster.qbs > 0 || (roster.flex != null && roster.flex.op > 0));
		} else if (Constants.RB.equals(position)) {
			return (roster.rbs > 0 || (roster.flex != null && (roster.flex.rbwr > 0
					|| roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if (Constants.WR.equals(position)) {
			return (roster.wrs > 0 || (roster.flex != null && (roster.flex.rbwr > 0
					|| roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if (Constants.TE.equals(position)) {
			return (roster.tes > 0 || (roster.flex != null && (roster.flex.rbwrte > 0 || roster.flex.op > 0)));
		} else if (Constants.DST.equals(position)) {
			return roster.def > 0;
		} else if (Constants.K.equals(position)) {
			return roster.k > 0;
		}
		return false;
	}
}
