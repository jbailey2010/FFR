package com.example.fantasyfootballrankings.ClassFiles.LittleStorage;

/**
 * A super dummy object that will keep track of various flex structures a league
 * may have
 * 
 * @author Jeff
 * 
 */
public class Flex {
	public int rbwr;
	public int rbwrte;
	public int op;

	/**
	 * A dummy constructor setting arrything to 0
	 */
	public Flex() {
		rbwr = 0;
		rbwrte = 0;
		op = 0;
	}

	/**
	 * A constructor that'll actually set the values themselves
	 */
	public Flex(int f1, int f2, int f3) {
		rbwr = f1;
		rbwrte = f2;
		op = f3;
	}
}
