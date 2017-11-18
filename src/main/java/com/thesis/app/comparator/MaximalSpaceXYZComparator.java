package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.MaximalSpace;
import com.thesis.app.models.Point3D;

public class MaximalSpaceXYZComparator implements Comparator<MaximalSpace> {

	@Override
	public int compare(MaximalSpace o1, MaximalSpace o2) {
		Point3D minCoords1 = o1.getMinCoords();
		Point3D minCoords2 = o2.getMinCoords();
		double zCoord = minCoords1.getZ() - minCoords2.getZ();
		double yCoord = minCoords1.getY() - minCoords2.getY();
		double xCoord = minCoords1.getX() - minCoords2.getX();
		
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
