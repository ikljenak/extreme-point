package com.thesis.app.models;

import java.util.Set;
import java.util.TreeSet;

import com.thesis.app.comparator.ExtremePointDistanceToTopFrontRightCornerComparator;

public class ExtremePointPackable implements Packable {

	private static final int POSSIBLE_ROTATIONS = 6;
	private Container container = null;
	private Set<ExtremePoint> extremePoints = null;

	public ExtremePointPackable(Container container) {
		this.container = container;
		//this.extremePoints = new HashSet<ExtremePoint>();
		 this.extremePoints = new TreeSet<ExtremePoint>(
		 new ExtremePointDistanceToTopFrontRightCornerComparator());
	}

	/**
	 * Attempts to pack an item inside a container
	 * 
	 * @param item
	 * @return true if item could be packed, false otherwise
	 */
	public boolean pack(Item item) {
		Point3D position = null;

		if (container.getItemsPacked() == 0) {
			// If the container has no items inside, the first one is packed in
			// position (0, 0, 0)
			position = new Point3D(0, 0, 0);
		} else {
			// An Extreme Point where the item could be placed is searched. If
			// there is no such Extreme Point, the item is rotated until a point
			// is found or every rotation is tried
			for (int i = 0; i < POSSIBLE_ROTATIONS; i++) {
				item.rotate(i);
				position = checkFittingExtremePoint(item, container);
				if (position != null) {
					// When a suitable position and orientation is found,
					// remaining rotations are no longer tried
					break;
				}
			}
		}

		if (position == null) {
			// if no suitable position is found, false is returned to indicate
			// that the item could not be packed
			return false;
		}

		// if there is a suitable position, it is used as the place of the item
		// inside the container
		item.setPosition(position);
		container.add(item);

		// New Extreme Points are generated
		addNewExtremePoints(position, item, container);
		return true;
	}

	/**
	 * Checks if an item fits on any of the Extreme Points defined inside a
	 * container
	 * 
	 * @param item
	 * @param container
	 * @return the coordinates of a Extreme Point where an item could be placed
	 */
	private Point3D checkFittingExtremePoint(Item item, Container container) {
		Point3D chosenExtremePoint = null;
		for (Point3D extremePoint : extremePoints) {
			item.setPosition(extremePoint);
			// Get maximum coordinate in every axis
			double x = extremePoint.getX() + item.getWidth();
			double y = extremePoint.getY() + item.getDepth();
			double z = extremePoint.getZ() + item.getHeight();

			boolean overlap = false;
			// If the item exceeds the dimensions of the container when placed
			// on the evaluated Extreme Point, a flag is set to proceed with
			// analysis of next Extreme Point
			if (x > container.getWidth() || y > container.getDepth()
					|| z > container.getHeight()) {
				overlap = true;
			}

			// For every item already packed, check if there is overlapping
			if (!overlap) {
				for (Item itemPacked : container.getItems()) {
					if (item.overlaps(itemPacked)) {
						overlap = true;
						break;
					}
				}
			}

			// If there is not overlapping the item could be placed
			// in this extreme point
			if (!overlap) {
				chosenExtremePoint = extremePoint;
				break;
			}
		}

		// If a Extreme Point is selected it is removed from the list of Extreme
		// Points
		if (chosenExtremePoint != null) {
			extremePoints.remove(chosenExtremePoint);
		}
		return chosenExtremePoint;
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
	private void addNewExtremePoints(Point3D position, Item item,
			Container container) {

		Point3D frontTopRightCorner = container.getFrontTopRightCorner();
		double x = position.getX();
		double y = position.getY();
		double z = position.getZ();

		ExtremePoint xAxis = new ExtremePoint(x + item.getWidth(), y, z,
				frontTopRightCorner);
		ExtremePoint yAxis = new ExtremePoint(x, y + item.getDepth(), z,
				frontTopRightCorner);
		ExtremePoint zAxis = new ExtremePoint(x, y, z + item.getHeight(),
				frontTopRightCorner);

		if (xAxis.getX() < container.getWidth()) {
			extremePoints.add(xAxis);
		}
		if (yAxis.getY() < container.getDepth()) {
			extremePoints.add(yAxis);
		}
		if (zAxis.getZ() < container.getHeight()) {
			extremePoints.add(zAxis);
		}
	}

	public Packable resetCopy(Container container) {
		return new ExtremePointPackable(container);
	}
}
