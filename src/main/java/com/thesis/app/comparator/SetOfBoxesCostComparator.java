package com.thesis.app.comparator;

import java.util.Comparator;
import java.util.List;

import com.thesis.app.models.Container;

public class SetOfBoxesCostComparator implements Comparator<List<Container>> {

	public int compare(List<Container> o1, List<Container> o2) {
		return (int)Math.signum(getCost(o1) - getCost(o2));
	}
	
	private double getCost(List<Container> containers) {
		double cost = 0;
		for(Container c:containers) {
			cost += c.getCost();
		}
		return cost;
	}

}
