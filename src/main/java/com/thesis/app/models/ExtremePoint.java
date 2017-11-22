package com.thesis.app.models;

public class ExtremePoint extends Point3D {

	private double distanceToTopFrontRightCorner;

	// CONSTRUCTORS
	public ExtremePoint(double x, double y, double z,
			Point3D topFrontRightCorner) {
		super(x, y, z);
		this.distanceToTopFrontRightCorner = topFrontRightCorner
				.distance(new Point3D(x, y, z));
	}
	// END CONSTRUCTORS

	// --------------------------------------------------//

	// GETTERS AND SETTERS
	public double getDistanceToTopFrontRightCorner() {
		return distanceToTopFrontRightCorner;
	}
	// END GETTERS AND SETTERS

	// --------------------------------------------------//

	// TO STRING
	public String toString() {
		return getX() + " " + getY() + " " + getZ();
	}
	// END TO STRING

	// --------------------------------------------------//
}
