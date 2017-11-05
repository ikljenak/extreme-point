package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Item;

public class ClusteredHeightAreaItemComparator implements Comparator<Item> {
	private static final int DELTA = 50;
	private double height;

	public ClusteredHeightAreaItemComparator(double height) {
		this.height = height;
	}

	public int compare(Item o1, Item o2) {
		double area1 = o1.getWidth() * o1.getDepth();
		double area2 = o2.getWidth() * o2.getDepth();
		double height1 = o1.getHeight();
		double height2 = o2.getHeight();

		double higherBound1 = 0;
		double higherBound2 = 0;

		for (int j = 1; height1 > higherBound1; j++) {
			higherBound1 = j * height / 100 * DELTA;
		}
		for (int j = 1; height2 > higherBound2; j++) {
			higherBound2 = j * height / 100 * DELTA;
		}
		if (higherBound1 - higherBound2 == 0) {
			return (int) Math.signum(area1 - area2);
		}
		return (int) Math.signum(higherBound2 - higherBound1);
	}
}
