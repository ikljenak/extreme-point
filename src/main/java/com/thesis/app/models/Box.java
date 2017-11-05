package com.thesis.app.models;

import java.math.BigDecimal;

public abstract class Box {
	private double weight;
	private double width;
	private double depth;
	private double height;
	
	//CONSTRUCTORS
	public Box(double weight, double width, double depth, double height) {
		super();
		this.weight = weight;
		this.width = width;
		this.depth = depth;
		this.height = height;
	}
	//END CONSTRUCTORS
	
	//--------------------------------------------------//
	
	//GETTERS AND SETTERS
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getDepth() {
		return depth;
	}
	public void setDepth(double depth) {
		this.depth = depth;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	//END GETTERS AND SETTERS
	
	//--------------------------------------------------//
	
	/**
	 * calculates the volume of the box as width * depth * height
	 * @return volume of the box
	 */
	public BigDecimal getVolume() {
		return new BigDecimal(width).multiply(new BigDecimal(depth)).multiply(new BigDecimal(height));
	}
}
