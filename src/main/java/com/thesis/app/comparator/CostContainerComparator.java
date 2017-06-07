package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Container;

public class CostContainerComparator implements Comparator<Container> {

	public int compare(Container o1, Container o2) {
		return (int) Math.signum(o2.getCost() - o1.getCost());
	}

}
