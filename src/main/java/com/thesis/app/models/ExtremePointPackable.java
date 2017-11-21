package com.thesis.app.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thesis.app.utils.Configuration;

public class ExtremePointPackable implements Packable {

	private static final int POSSIBLE_ROTATIONS = 6;
	private Container container = null;
	private Map<Integer, List<Item>> itemsByCell = new HashMap<Integer, List<Item>>();
	private Set<ExtremePoint> extremePoints = null;

	public ExtremePointPackable(Container container) {
		this.container = container;
		this.extremePoints = new HashSet<ExtremePoint>();
		//this.extremePoints = new TreeSet<ExtremePoint>(
		//		new ExtremePointDistanceToTopFrontRightCornerComparator());
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
		add(item);

		// New Extreme Points are generated
		addNewExtremePoints(position, item, container);
		return true;
	}

	private void add(Item item) {
		Point3D minCoords = new Point3D(item.getPosition());
		Point3D maxCoords = new Point3D(minCoords.getX() + item.getWidth(),
				minCoords.getY() + item.getDepth(), minCoords.getZ()
						+ item.getHeight());
		minCoords.normalize(container.getWidth(), container.getDepth(), container.getHeight());
		maxCoords.normalize(container.getWidth(), container.getDepth(), container.getHeight());

		for (double i = minCoords.getX(); i <= maxCoords.getX(); i += 1) {
			for (double j = minCoords.getY(); j <= maxCoords.getY(); j += 1) {
				for (double k = minCoords.getZ(); k <= maxCoords.getZ(); k += 1) {
					double key = Math.pow(Configuration.CONTAINERS_CELL, 2) * k
							+ Configuration.CONTAINERS_CELL * j + i;
					if (itemsByCell.containsKey((int) key)) {
						itemsByCell.get((int) key).add(item);
					} else {
						List<Item> cellItems = new ArrayList<Item>();
						cellItems.add(item);
						itemsByCell.put((int) key, cellItems);
					}
				}
			}
		}
		container.add(item);
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
				if (itemsOverlapping(item)) {
					overlap = true;
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

	private boolean itemsOverlapping(Item item) {
		Point3D minCoords = new Point3D(item.getPosition());
		Point3D maxCoords = new Point3D(minCoords.getX() + item.getWidth(),
				minCoords.getY() + item.getDepth(), minCoords.getZ()
						+ item.getHeight());
		minCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());
		maxCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());

		for (double i = minCoords.getX(); i <= maxCoords.getX(); i += 1) {
			for (double j = minCoords.getY(); j <= maxCoords.getY(); j += 1) {
				for (double k = minCoords.getZ(); k <= maxCoords.getZ(); k += 1) {
					double key = Math.pow(Configuration.CONTAINERS_CELL, 2) * k
							+ Configuration.CONTAINERS_CELL * j + i;
					if (itemsByCell.containsKey((int)key)) {
						for (Item itemPacked : itemsByCell.get((int)key)) {
							if (item.overlaps(itemPacked)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
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
