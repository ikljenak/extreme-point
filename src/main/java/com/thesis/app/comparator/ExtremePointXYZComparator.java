package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.ExtremePoint;

public class ExtremePointXYZComparator implements Comparator<ExtremePoint> {

	public int compare(ExtremePoint o1, ExtremePoint o2) {
		double zCoord = o1.getZ() - o2.getZ();
		double yCoord = o1.getY() - o2.getY();
		double xCoord = o1.getX() - o2.getX();
		
		if(zCoord == 0) {
			if(yCoord == 0){
				if(xCoord == 0) {
					return 0;
				}
				return (int) Math.signum(xCoord);
			}
			return (int) Math.signum(yCoord);
		}
		return (int) Math.signum(zCoord);
	}

}
