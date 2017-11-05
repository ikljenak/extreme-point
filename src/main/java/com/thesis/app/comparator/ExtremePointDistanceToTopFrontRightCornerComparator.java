package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.ExtremePoint;

public class ExtremePointDistanceToTopFrontRightCornerComparator implements
		Comparator<ExtremePoint> {

	public int compare(ExtremePoint o1, ExtremePoint o2) {
		return (int) Math.signum(o1.getDistanceToTopFrontRightCorner()
				- o2.getDistanceToTopFrontRightCorner());
	}

}
