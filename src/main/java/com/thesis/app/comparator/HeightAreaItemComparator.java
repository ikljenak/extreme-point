package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Item;

public class HeightAreaItemComparator implements Comparator<Item> {
	public int compare(Item o1, Item o2) {
		double area1 = o1.getWidth() * o1.getDepth();
		double area2 = o2.getWidth() * o2.getDepth();
		if (o1.getHeight() - o2.getHeight() == 0) {
			return (int) Math.signum(area1 - area2);
		}
		return (int) Math.signum(o1.getHeight() - o2.getHeight());
	}
}
