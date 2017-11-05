package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.MaximalSpace;

public class MaximalSpaceVolumeComparator implements Comparator<MaximalSpace> {

	public int compare(MaximalSpace o1, MaximalSpace o2) {
		return o2.getArea().compareTo(o1.getArea());
	}

}
