package com.example.fantasyfootballrankings.ClassFiles;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import android.widget.TextView;

import com.example.fantasyfootballrankings.ClassFiles.LittleStorage.Draft;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.PlayerObject;
import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.Storage;

/**
 * A library of widget-oriented information
 * @author Jeff
 *
 */
public class WidgetLibrary 
{
	/**
	 * Does something of a suggested picks system
	 * @param widgOutput
	 */
	public static void suggestedPicks(TextView widgOutput, final Storage holder, final List<String> watchList)
	{
		String paaBackQB = paaDiff("QB", holder);
		final double qb3 = Double.parseDouble(paaBackQB.split(": ")[1].split("/")[0]);
		String paaBackRB = paaDiff("RB", holder);
		final double rb3 = Double.parseDouble(paaBackRB.split(": ")[1].split("/")[0]);
		String paaBackWR = paaDiff("WR", holder);
		final double wr3 = Double.parseDouble(paaBackWR.split(": ")[1].split("/")[0]);
		String paaBackTE = paaDiff("TE", holder);
		final double te3 = Double.parseDouble(paaBackTE.split(": ")[1].split("/")[0]);
		int qbTotal = Draft.posDraftedQuantity(holder.draft.qb);
		final double qbVal = (50.0*qbTotal)/100.0;
		int rbTotal = Draft.posDraftedQuantity(holder.draft.rb); 
		final double rbVal = (20.0*rbTotal)/100.0;
		int wrTotal = Draft.posDraftedQuantity(holder.draft.wr);
		final double wrVal = (20.0*wrTotal)/100.0;
		int teTotal = Draft.posDraftedQuantity(holder.draft.te);
		final double teVal = (60.0*teTotal)/100.0;
		int defTotal = Draft.posDraftedQuantity(holder.draft.def);
		final double defVal = (75.0 * defTotal)/100.0;
		int kTotal = Draft.posDraftedQuantity(holder.draft.k);
		final double kVal = (75.0*kTotal)/100.0;
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
		{
			@Override
			public int compare(PlayerObject a, PlayerObject b)  
			{
				double aVal = 0;
				double bVal = 0;
				if(watchList.contains(a.info.name))
				{
					aVal -= 10;
				}
				if(watchList.contains(b.info.name))
				{
					bVal -= 10;
				}
				aVal -= a.values.worth;
				if(!a.info.adp.equals("Not set"))
				{
					if(ManageInput.isInteger(a.info.adp))
					{
						aVal += Integer.parseInt(a.info.adp)*25;
					}
					else
					{
						aVal += Double.parseDouble(a.info.adp)*25;
					}
				} 
				else
				{       
					aVal += 7500;
				}
				if(a.values.ecr != -1.0)
				{
					aVal += 50* a.values.ecr;
				}
				else
				{
					aVal += 15000;
				}
				if(a.values.paa > 0)
				{
					aVal -= 1.5*a.values.paa;
				}
				aVal += 2.5*holder.sos.get(a.info.team + "," + a.info.position);
				
				if(a.info.position.equals("QB"))
				{ 
					aVal += qb3/2;
				}
				if(a.info.position.equals("RB"))
				{
					aVal += rb3/2;
				}
				if(a.info.position.equals("WR"))
				{
					aVal += wr3/2;
				}
				if(a.info.position.equals("TE"))
				{
					aVal += te3/2;
				}
				if(ComparatorHandling.samePos(a, holder))
				{
					aVal += 5;
				}
				if(ComparatorHandling.teamBye(holder, a))
				{
					aVal += 5;
				}
				if(ComparatorHandling.samePos(b, holder))
				{
					bVal += 5;
				}
				if(ComparatorHandling.teamBye(holder, b))
				{
					bVal += 5;
				}
				if(b.info.position.equals("QB"))
				{
					bVal += qb3/2;
				}
				if(b.info.position.equals("RB"))
				{
					bVal += rb3/2;
				}
				if(b.info.position.equals("WR"))
				{
					bVal += wr3/2;
				}
				if(b.info.position.equals("TE"))
				{
					bVal += te3/2;
				}
				bVal -= b.values.worth;
				if(!b.info.adp.equals("Not set"))
				{
					if(ManageInput.isInteger(b.info.adp))
					{
						bVal += Integer.parseInt(b.info.adp)*25;
					}
					else
					{
						bVal += Double.parseDouble(b.info.adp)*25;
					}
				}
				else
				{
					bVal += 7500;
				}
				if(b.values.ecr != -1.0)
				{
					bVal += 50* b.values.ecr;
				}
				else
				{
					bVal += 15000;
				}
				if(b.values.paa > 0)
				{
					bVal -= 1.5*b.values.paa;
				}
				bVal += 2.5*holder.sos.get(b.info.team + "," + b.info.position);
				if(a.info.position.equals("D/ST"))
				{
					aVal -= 1000;
				}
				if(a.info.position.equals("K"))
				{
					aVal -= 1750;
				}
				if(b.info.position.equals("D/ST"))
				{
					bVal -= 1000;
				}
				if(b.info.position.equals("K"))
				{
					bVal -= 1750;
				}
				if(a.info.position.equals("QB"))
				{
					aVal += aVal*qbVal;
				}
				if(a.info.position.equals("RB"))
				{
					aVal += aVal * rbVal;
				}
				if(a.info.position.equals("WR"))
				{
					aVal += aVal * wrVal;
				}
				if(a.info.position.equals("TE"))
				{
					aVal += aVal * teVal;
				}
				if(a.info.position.equals("D/ST"))
				{
					aVal += aVal * defVal;
				}
				if(a.info.position.equals("K"))
				{
					aVal += aVal * kVal;
				}
				if(b.info.position.equals("QB"))
				{
					bVal += bVal*qbVal;
				}
				if(b.info.position.equals("RB"))
				{
					bVal += bVal * rbVal;
				}
				if(b.info.position.equals("WR"))
				{
					bVal += bVal * wrVal;
				}
				if(b.info.position.equals("TE"))
				{
					bVal += bVal * teVal;
				}
				if(b.info.position.equals("D/ST"))
				{
					bVal += bVal * defVal;
				}
				if(b.info.position.equals("K"))
				{
					bVal += bVal * kVal;
				}
				//X*ECR + ADP - AUCTION
				if (aVal > bVal)
			    {
			        return 1;
			    }
			    if (aVal < bVal)
			    {
			    	return -1;
			    }
			    return 0;
			}
		});
		for(PlayerObject player : holder.players)
		{
			if(!Draft.isDrafted(player.info.name, holder.draft)&& player.info.team.length() > 1 && player.info.position.length() > 0
					&& !player.info.team.equals("None") && !player.info.team.equals("---") && !player.info.team.equals("FA"))
			{
				inter.add(player);
			}
		}
		List<PlayerObject> set = new ArrayList<PlayerObject>();
		List<Double> vals = new ArrayList<Double>();
		for(int i = 0; i < 10; i++)
		{
			PlayerObject a = inter.poll();
			double aVal = 0;
			aVal -= a.values.worth;
			if(!a.info.adp.equals("Not set"))
			{ 
				if(ManageInput.isInteger(a.info.adp))
				{
					aVal += Integer.parseInt(a.info.adp)*25;
				}
				else
				{
					aVal += Double.parseDouble(a.info.adp)*25;
				}
			} 
			else 
			{  
				aVal += 7500; 
			}
			if(a.values.ecr != -1.0)
			{
				aVal += 50* a.values.ecr; 
			}
			else
			{ 
				aVal += 15000;
			}
			if(a.values.paa > 0)
			{
				aVal -= 1.5*a.values.paa;
			}
			aVal += 2.5*holder.sos.get(a.info.team + "," + a.info.position);
			if(a.info.position.equals("QB"))
			{
				aVal += qb3/2;
			}
			if(a.info.position.equals("RB"))
			{
				aVal += rb3/2;
			}
			if(a.info.position.equals("WR"))
			{
				aVal += wr3/2;
			} 
			if(a.info.position.equals("TE"))
			{
				aVal += te3/2;
			}
			if(ComparatorHandling.samePos(a, holder))
			{
				aVal += 5;
			}
			if(ComparatorHandling.teamBye(holder, a))
			{
				aVal += 5;
			}
			if(a.info.position.equals("D/ST"))
			{
				aVal -= 1000;
			} 
			if(a.info.position.equals("K"))
			{
				aVal -= 1750;
			}
			if(a.info.position.equals("QB"))
			{
				aVal += aVal*qbVal;
			}
			if(a.info.position.equals("RB")) 
			{
				aVal += aVal * rbVal;
			}
			if(a.info.position.equals("WR"))
			{  
				aVal += aVal * wrVal;
			}
			if(a.info.position.equals("TE"))
			{ 
				aVal += aVal * teVal;
			}
			if(a.info.position.equals("D/ST"))
			{
				aVal += aVal * defVal;
			}
			if(a.info.position.equals("K"))
			{
				aVal += aVal * kVal;
			}
			if(watchList.contains(a.info.name))
			{
				aVal -= 15;
			}
			DecimalFormat df = new DecimalFormat("#.##");
			vals.add(Double.parseDouble(df.format(aVal)));
			set.add(a);
		}
		List<Double> normVals = new ArrayList<Double>();
		double total = 0;
		if(vals.get(0) < 0)
		{
			for(int i = 0; i < vals.size(); i++)
			{
				normVals.add(i, vals.get(i) - vals.get(0) + (i+1)*(i+1));
			}
		}
		else
		{
			for(int i = 0; i < vals.size(); i++)
			{
				normVals.add(i, vals.get(i) + 1);
			}
		}
		for(int i = 0; i < normVals.size(); i++)
		{
			total += normVals.get(i);
		}
		StringBuilder output = new StringBuilder(1000);
		output.append("   ");
		DecimalFormat df = new DecimalFormat("#.#");
		double valNorm = normVals.get(0);
		for(int i = 0; i < normVals.size(); i++)
		{
			PlayerObject name = set.get(i);
			double val = normVals.get(i);
			double difference = val - valNorm;
			String setStr = "-" + df.format(difference);
			output.append((i+1) + ": " + name.info.name + " " + (setStr) + ", ");
		}
		String result = output.toString().substring(0, output.toString().length()-2);
		result = result + "     ";
		widgOutput.setText(result);
	}
	
	
	/**
	 * Gets general draft info
	 * @return
	 */
	public static String basicInfo(Storage holder)
	{
		String result = "  ";
		int qbTotal = Draft.posDraftedQuantity(holder.draft.qb);
		int rbTotal = Draft.posDraftedQuantity(holder.draft.rb);
		int wrTotal = Draft.posDraftedQuantity(holder.draft.wr);
		int teTotal = Draft.posDraftedQuantity(holder.draft.te);
		int defTotal = Draft.posDraftedQuantity(holder.draft.def);
		int kTotal = Draft.posDraftedQuantity(holder.draft.k);
		DecimalFormat df = new DecimalFormat("#.#");
		if(qbTotal + rbTotal + wrTotal + teTotal + defTotal + kTotal == 0)
		{
			return "No players drafted";
		}
		result = "  ";
		if(qbTotal != 0)
		{
			String val = "QBs";
			if(qbTotal == 1)
			{
				val = "QB";
			}
			result += qbTotal + " " + val + ", ";
		}
		if(rbTotal != 0)
		{
			String val = "RBs";
			if(rbTotal == 1)
			{
				val = "RB";
			}
			result += rbTotal + " " + val + ", ";
		}
		if(wrTotal != 0)
		{
			String val = "WRs";
			if(wrTotal == 1)
			{
				val = "WR";
			}
			result += wrTotal + " " + val + ", ";
		}
		if(teTotal != 0)
		{
			String val = "TEs";
			if(teTotal == 1)
			{
				val = "TE";
			}
			result += teTotal + " " + val + ", ";
		}
		if(defTotal != 0)
		{
			String val = "D/STs";
			if(defTotal == 1)
			{
				val = "D/ST";
			}
			result += defTotal + " " + val + ", ";
		}
		if(kTotal != 0)
		{
			String val = "Ks";
			if(kTotal == 1)
			{
				val = "K";
			}
			result += kTotal + " " + val + ", ";
		}
		result +=  df.format(Draft.paaTotal(holder.draft)) + " total PAA";
		return result;
	}
	

	/**
	 * Gets the drafted players at a position
	 * @param pos
	 * @return
	 */
	public static String draftThusFar(String pos, Storage holder)
	{
		String result = "";
		double paa = 0.0;
		DecimalFormat df = new DecimalFormat("#.#");
		
		if(pos.equals("QB"))
		{
			for(PlayerObject player : holder.draft.qb)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(pos.equals("RB"))
		{
			for(PlayerObject player : holder.draft.rb)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(pos.equals("WR"))
		{
			for(PlayerObject player : holder.draft.wr)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(pos.equals("TE"))
		{
			for(PlayerObject player : holder.draft.te)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(pos.equals("D/ST"))
		{
			for(PlayerObject player : holder.draft.def)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(pos.equals("K"))
		{
			for(PlayerObject player : holder.draft.k)
			{
				result += player.info.name + ", ";
				paa += player.values.paa;
			}
		}
		if(!result.contains(","))
		{
			result = "None drafted";
		}
		else
		{
			result = result.substring(0, result.length()-2);
			result += " (" + df.format(paa) + " PAA)";
		}
		return result;
	}
	

	/**
	 * Calculates the PAA left at a position
	 * @param pos
	 * @return
	 */
	public static String paaDiff(String pos, Storage holder)
	{
		DecimalFormat df = new DecimalFormat("#.#");
		String result = "3/5/10 back: ";
		double paaLeft = 0.0;
		int counter = 0;
		PriorityQueue<PlayerObject> inter = new PriorityQueue<PlayerObject>(300, new Comparator<PlayerObject>() 
				{
			@Override
			public int compare(PlayerObject a, PlayerObject b) 
			{
				if (a.values.worth > b.values.worth)
			    {
			        return -1;
			    }
			    if (a.values.worth < b.values.worth)
			    {
			    	return 1;
			    } 
			    return 0;
			}
		});
		for(PlayerObject player: holder.players)
		{
			if(!Draft.isDrafted(player.info.name, holder.draft) && player.info.position.equals(pos))
			{
				inter.add(player);
			}
		}
		while(!inter.isEmpty())
		{
			PlayerObject player = inter.poll();
			paaLeft += player.values.paa;
			counter++;
			if(counter > 10)
			{
				result += df.format(paaLeft);
				break;
			}
			if(counter == 4)
			{
				result += df.format(paaLeft) + "/";
			}
			if(counter == 6)
			{
				result += df.format(paaLeft) + "/";
			}
		}
		return result;
	}
}
