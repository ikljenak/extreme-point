package com.thesis.app.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thesis.app.comparator.ExtremePointComparator;

public class Container extends Box {

	private static final int POSSIBLE_ROTATIONS = 6;

	private List<Item> items = new ArrayList<Item>();
	private Set<Point3D> extremePoints = new TreeSet<Point3D>(
			new ExtremePointComparator());
	private double loadedWeight;
	private double cost;

	//CONSTRUCTORS
	@JsonCreator
	public Container(@JsonProperty("weight") double weight,
			@JsonProperty("width") double width,
			@JsonProperty("depth") double depth,
			@JsonProperty("height") double height,
			@JsonProperty("cost") double cost) {
		super(weight, width, depth, height);
		this.cost = cost;
	}
	
	public Container(Container container) {
		super(container.getWeight(), container.getWidth(), container.getDepth(), container.getHeight());
		this.cost = container.getCost();
	}
	// END CONSTRUCTORS

	// --------------------------------------------------//
	
	// GETTERS AND SETTERS
		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}

		public double getLoadedWeight() {
			return loadedWeight;
		}

		public void setLoadedWeight(double loadedWeight) {
			this.loadedWeight = loadedWeight;
		}

		public List<Item> getItems() {
			return items;
		}

		public void setItems(List<Item> items) {
			this.items = items;
		}

		// END GETTERS AND SETTERS

		// --------------------------------------------------//

		// TO STRING
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("-----------------------------\n");
			sb.append("CONTAINER\n");
			sb.append("Width: ").append(getWidth()).append("\n");
			sb.append("Depth: ").append(getDepth()).append("\n");
			sb.append("Height: ").append(getHeight()).append("\n");
			sb.append("\n");
			sb.append("Items: " + this.getItems().size() + "\n");
			for (Item item : this.getItems()) {
				sb.append(item.toString());
			}
			sb.append("-----------------------------\n");
			return sb.toString();
		}
		// END TO STRING

		// --------------------------------------------------//
	
	/**
	 * Creates a copy of current container
	 * @return the copy of the container
	 */
	public Container copy() {
		return new Container(this);
	}

	/**
	 * Receives an item and if it fits in this container it is packed. A
	 * position is set to the item so that the position within the box of the
	 * left-back-bottom corner of the item
	 * 
	 * @param item
	 * @return true if the item could be packed, false otherwise
	 */
	public boolean pack(Item item) {
		Point3D position = null;

		if (!checkIfFits(item)) {
			return false;
		}

		if (items.isEmpty()) {
			position = new Point3D(0, 0, 0);
		} else {
			// An extreme point where the item could be placed is searched. If
			// there is no such Extreme Point, the item is rotated until a point
			// is found or every rotation is tried
			for (int i = 0; i < POSSIBLE_ROTATIONS; i++) {
				item.rotate(i);
				position = checkFittingExtremePoint(item);
				if (position != null) {
					break;
				}
			}
			if (position == null) {
				return false;
			}
		}

		item.setPosition(position);
		addNewExtremePoints(position, item);
		items.add(item);
		this.loadedWeight = loadedWeight + item.getWeight();
		return true;
	}

	/**
	 * Check if any of the dimensions of the item or its weight are larger than
	 * the container's
	 * 
	 * @param item
	 * @return false if there is a dimension that is larger than the same
	 *         dimension of the container
	 */
	private boolean checkIfFits(Item item) {
		if (item.getDepth() > this.getDepth()
				|| item.getWidth() > this.getWidth()
				|| item.getHeight() > this.getHeight()
				|| item.getWeight() > this.getWeight()
				|| item.getWeight() + loadedWeight > this.getWeight()) {
			return false;
		}
		return true;
	}

	/**
	 * When a package is added new Extreme Points where more items could be
	 * packed are calculated. Given a position (x, y, z) and the dimensions of
	 * an item (w, d, h), the new Extreme Points are (x + w, y, z), (x, y + d,
	 * z) and (x, y, z + h)
	 * 
	 * @param position
	 *            , the coordinates of the left-back-bottom corner of the item
	 *            relative to the left-back-bottom corner of the container
	 * @param item
	 */
	private void addNewExtremePoints(Point3D position, Item item) {
		double x = position.getX();
		double y = position.getY();
		double z = position.getZ();

		Point3D xAxis = new Point3D(x + item.getWidth(), y, z);
		Point3D yAxis = new Point3D(x, y + item.getDepth(), z);
		Point3D zAxis = new Point3D(x, y, z + item.getHeight());

		if (xAxis.getX() < this.getWidth()) {
			extremePoints.add(xAxis);
		}
		if (xAxis.getY() < this.getDepth()) {
			extremePoints.add(yAxis);
		}
		if (xAxis.getZ() < this.getHeight()) {
			extremePoints.add(zAxis);
		}
	}

	/**
	 * check if an item can be positioned in any of the Extreme points without
	 * overlapping with previously packed items.
	 * 
	 * @param item
	 * @return the coordinates of the Extreme Point where the item could be
	 *         packed
	 */
	private Point3D checkFittingExtremePoint(Item item) {
		for (Point3D extremePoint : extremePoints) {
			item.setPosition(extremePoint);
			// get maximum coordinate in every axis
			double x = extremePoint.getX() + item.getWidth();
			double y = extremePoint.getY() + item.getDepth();
			double z = extremePoint.getZ() + item.getHeight();

			boolean overlap = false;

			// check if item fits within the bounds of the container
			if (x > this.getWidth() || y > this.getDepth()
					|| z > this.getHeight()) {
				overlap = true;
			}

			// for every item check if there is overlapping
			if (!overlap) {
				for (Item itemPacked : items) {
					if (item.overlaps(itemPacked)) {
						overlap = true;
						break;
					}
				}
			}

			// if there is not overlapping the item could be placed
			// in this extreme point
			if (!overlap) {
				extremePoints.remove(extremePoint);
				return extremePoint;
			}
		}
		return null;
	}
}
