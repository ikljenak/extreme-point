package com.thesis.app.models;

import java.math.BigDecimal;

public class MaximalSpace {
	private Point3D minCoords;
	private Point3D maxCoords;
	
	//CONSTRUCTORS
		public MaximalSpace(Point3D minCoords, Point3D maxCoords) {
			this.minCoords = minCoords;
			this.maxCoords = maxCoords;
		}
	//END CONSTRUCTORS
		
	// --------------------------------------------------//
		
	//TO STRING
		@Override
		public String toString() {
			return "minCoords: " + minCoords + " maxCoords: " + maxCoords + "\n"; 
		}
	//END TO STRING
	
	// --------------------------------------------------//
		
	// GETTERS AND SETTERS
	public Point3D getMinCoords() {
		return minCoords;
	}
	public void setMinCoords(Point3D minCoords) {
		this.minCoords = minCoords;
	}
	public Point3D getMaxCoords() {
		return maxCoords;
	}
	public void setMaxCoords(Point3D maxCoords) {
		this.maxCoords = maxCoords;
	}
	
	// END GETTERS AND SETTERS

	// --------------------------------------------------//

	public boolean contains(MaximalSpace maximalSpace) {
		int minCoordsCompare = getMinCoords().compare(maximalSpace.getMinCoords());
		int maxCoordsCompare = getMaxCoords().compare(maximalSpace.getMaxCoords());
		
		return minCoordsCompare == -1 && maxCoordsCompare == 1;
	}
	
	public BigDecimal getArea() {
		BigDecimal width = new BigDecimal(maxCoords.getX() - minCoords.getX());
		BigDecimal depth = new BigDecimal(maxCoords.getY() - minCoords.getY());
		BigDecimal height = new BigDecimal(maxCoords.getZ() - minCoords.getZ());
		
		return width.multiply(depth).multiply(height);
	}
}
