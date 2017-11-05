package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Item;

public class ClusteredAreaHeightItemComparator implements Comparator<Item> {
	private static final int DELTA = 50;
	private double width;
	private double depth;

	public ClusteredAreaHeightItemComparator(double width, double depth) {
		this.width = width;
		this.depth = depth;
	}

	public int compare(Item o1, Item o2) {
		double area1 = o1.getWidth() * o1.getDepth();
		double area2 = o2.getWidth() * o2.getDepth();

		double higherBound1 = 0;
		double higherBound2 = 0;

		for (int j = 1; area1 > higherBound1; j++) {
			higherBound1 = j * width * depth / 100 * DELTA;
		}
		for (int j = 1; area2 > higherBound2; j++) {
			higherBound2 = j * width * depth / 100 * DELTA;
		}
		if (higherBound1 - higherBound2 == 0) {
			return (int) Math.signum(o1.getHeight() - o2.getHeight());
		}
		return (int) Math.signum(higherBound2 - higherBound1);
	}
}
