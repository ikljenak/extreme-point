package com.thesis.app.models;

public class Point3D {
	private double x;
	private double y;
	private double z;

	// CONSTRUCTORS
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// END CONSTRUCTORS

	// --------------------------------------------------//

	// GETTERS AND SETTERS
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	// END GETTERS AND SETTERS

	// --------------------------------------------------//

	// TO STRING
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("x: ").append(x).append(", y: ").append(y).append(", z: ")
				.append(z);
		return sb.toString();
	}

	// END TO STRING

	// --------------------------------------------------//
	
	/**
	 * compares two Point3D
	 * 
	 * @param point3d
	 * @return 1 if all coords of first point are greater than those of the
	 *         second point -1 if all coords of first point are lower than those
	 *         of the second point 0 otherwise
	 */
	public int compare(Point3D point3d) {
		if (this.x - point3d.getX() <= 0 && this.y - point3d.getY() <= 0
				&& this.z - point3d.getZ() <= 0) {
			return -1;
		} else if (this.x - point3d.getX() >= 0 && this.y - point3d.getY() >= 0
				&& this.z - point3d.getZ() >= 0) {
			return 1;
		}
		return 0;
	}

	public double distance(Point3D position) {
		return Math.sqrt(Math.pow((position.getX() - x), 2)
				+ Math.pow((position.getY() - y), 2)
				+ Math.pow((position.getZ() - z), 2));
	}
	
	public Point3D sum(Point3D point) {
		return new Point3D(point.getX() + x, point.getY() + y, point.getZ() + z);
	}
}
