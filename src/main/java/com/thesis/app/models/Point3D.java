package com.thesis.app.models;

public class Point3D {
	private double x;
	private double y;
	private double z;

	//CONSTRUCTORS
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	//END CONSTRUCTORS
	
	//--------------------------------------------------//

	//GETTERS AND SETTERS
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
	//END GETTERS AND SETTERS
	
	//--------------------------------------------------//
	
	//TO STRING
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("x: ").append(x).append(", y: ").append(y).append(", z: ").append(z);
		return sb.toString();
	}
	//END TO STRING
	
	//--------------------------------------------------//
}
