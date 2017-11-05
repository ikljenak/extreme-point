package com.thesis.app.models;

public class ExtremePoint extends Point3D {

	private double distanceToTopFrontRightCorner;

	public ExtremePoint(double x, double y, double z,
			Point3D topFrontRightCorner) {
		super(x, y, z);
		this.distanceToTopFrontRightCorner = topFrontRightCorner
				.distance(new Point3D(x, y, z));
	}

	public double getDistanceToTopFrontRightCorner() {
		return distanceToTopFrontRightCorner;
	}
	
	public String toString() {
		return getX() + " " + getY() + " " + getZ();
	}
}
