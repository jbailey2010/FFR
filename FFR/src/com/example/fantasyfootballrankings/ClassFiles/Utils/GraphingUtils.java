package com.example.fantasyfootballrankings.ClassFiles.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.example.fantasyfootballrankings.ClassFiles.StorageClasses.ImportedTeam;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;

public class GraphingUtils {

	public static GraphView generateGraphView(Context context) {
		GraphViewStyle gvs = new GraphViewStyle();
		gvs.setTextSize(13);
		gvs.setVerticalLabelsColor(Color.BLACK);
		gvs.setHorizontalLabelsColor(Color.BLACK);
		gvs.setGridColor(Color.GRAY);
		GraphView graphView = new LineGraphView(context, "");
		graphView.setGraphViewStyle(gvs);
		return graphView;
	}

	public static void configureLegend(GraphView graphView) {
		graphView.setScrollable(true);
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.TOP);
		graphView.setLegendWidth(250);
	}

	public static void configureAxes(GraphView graphView, String[] horizLabels,
			String[] vertLabels, double yMin, double yMax) {
		graphView.setHorizontalLabels(horizLabels);
		graphView.setVerticalLabels(vertLabels);
		graphView.setManualYAxisBounds(yMax, yMin);
	}

	public static void addSeries(GraphView graphView, String title,
			GraphViewSeriesStyle style, GraphViewDataInterface[] data) {
		GraphViewSeries series = new GraphViewSeries(title, style, data);
		graphView.addSeries(series);
	}

	public static GraphViewSeriesStyle getGraphSeriesStyle(Integer color,
			Integer thickness) {
		GraphViewSeriesStyle style = new GraphViewSeriesStyle();
		if (color != null) {
			style.color = color;
		}
		if (thickness != null) {
			style.thickness = thickness;
		}
		return style;
	}

	public static String[] filterHorizontalAxis(String[] input,
			ImportedTeam team) {
		List<String> axis = new LinkedList<String>(Arrays.asList(input));
		filterPositionFromAxis(axis, "QB", team);
		filterPositionFromAxis(axis, "RB", team);
		filterPositionFromAxis(axis, "WR", team);
		filterPositionFromAxis(axis, "TE", team);
		filterPositionFromAxis(axis, "D/ST", team);
		filterPositionFromAxis(axis, "K", team);
		return axis.toArray(new String[axis.size()]);
	}

	private static List<String> filterPositionFromAxis(List<String> input,
			String position, ImportedTeam team) {
		if (!team.doesLeagueAllowPosition(position)) {
			input.remove(position);
		}
		return input;
	}
}
