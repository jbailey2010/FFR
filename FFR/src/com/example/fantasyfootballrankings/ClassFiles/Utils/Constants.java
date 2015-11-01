package com.example.fantasyfootballrankings.ClassFiles.Utils;

public class Constants {
	// Positions
	public final static String QB = "QB";
	public final static String RB = "RB";
	public final static String WR = "WR";
	public final static String RBWR = "RB/WR";
	public final static String TE = "TE";
	public final static String RBWRTE = "RB/WR/TE";
	public final static String QBRBWRTE = "QB/RB/WR/TE";
	public final static String DST = "D/ST";
	public final static String K = "K";

	// My Leagues
	public final static String ML_QB = "Quarterbacks";
	public final static String ML_RB = "Running Backs";
	public final static String ML_WR = "Wide Receivers";
	public final static String ML_TE = "Tight Ends";
	public final static String ML_K = "Kickers";
	public final static String ML_QB_SINGULAR = ML_QB.substring(0,
			ML_QB.length() - 1);
	public final static String ML_RB_SINGULAR = ML_RB.substring(0,
			ML_RB.length() - 1);
	public final static String ML_WR_SINGULAR = ML_WR.substring(0,
			ML_WR.length() - 1);
	public final static String ML_TE_SINGULAR = ML_TE.substring(0,
			ML_TE.length() - 1);
	public final static String ML_K_SINGULAR = ML_K.substring(0,
			ML_K.length() - 1);
	public final static String ML_QB_HEADER = ML_QB + ": ";
	public final static String ML_RB_HEADER = ML_RB + ": ";
	public final static String ML_WR_HEADER = ML_WR + ": ";
	public final static String ML_TE_HEADER = ML_TE + ": ";
	public final static String ML_DEF_HEADER = DST + ": ";
	public final static String ML_K_HEADER = ML_K + ": ";

	// Player Data
	public final static String BYE_WEEK = "Bye Week";

	// Storage
	public final static String SP_KEY = "FFR";
	public final static String RANKINGS_DELIMITER = "&&";
	public final static String PLAYER_RANKINGS_KEY = "Player Values";
	public final static String DRAFT_STATUS_KEY = "Draft Information";
	public final static String RANKINGS_PARSE_COUNT_KEY = "Parse Count";

	// Internal
	public final static String HASH_DELIMITER = "/";
	public final static String NUMBER_FORMAT = "#.##";
	public final static String LINE_BREAK = "\n";
	public final static String ML_NONE_SELECTED = "None Selected";
}
