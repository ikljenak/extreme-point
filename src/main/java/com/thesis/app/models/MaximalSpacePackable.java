package com.thesis.app.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.thesis.app.comparator.MaximalSpaceVolumeComparator;
import com.thesis.app.utils.Configuration;

public class MaximalSpacePackable implements Packable {

	private Set<MaximalSpace> maximalSpaces = new TreeSet<MaximalSpace>(
			new MaximalSpaceVolumeComparator());
	private Map<Integer, Set<MaximalSpace>> maximalSpacesByCell = new HashMap<Integer, Set<MaximalSpace>>();
	private Container container;
	private static final int POSSIBLE_ROTATIONS = 6;

	public MaximalSpacePackable(Container container) {
		this.container = container;
	}

	/**
	 * Attempts to pack an item inside a container
	 * 
	 * @param item
	 * @return true if item could be packed, false otherwise
	 */
	public boolean pack(Item item) {
		Point3D position = null;

		// If the container has no items inside, the position (0, 0, 0) is
		// contemplated as the place for the first item
		if (maximalSpaces.isEmpty()) {
			addMaximalSpace(new MaximalSpace(new Point3D(0, 0, 0), new Point3D(
					container.getWidth() - 0.1, container.getDepth() - 0.1,
					container.getHeight() - 0.1)));
		}

		// A Maximal Space where the item could be placed is searched. If
		// there is no such Maximal Space, the item is rotated until a point
		// is found or every rotation is tried
		MaximalSpace maximalSpace;
		for (int i = 0; i < POSSIBLE_ROTATIONS; i++) {
			item.rotate(i);
			maximalSpace = checkFittingMaximalSpace(item);
			if (maximalSpace != null) {
				// When a suitable position and orientation is found,
				// remaining rotations are no longer tried
				position = maximalSpace.getMinCoords();
				break;
			}
		}
		
		if (position == null) {
			// if no suitable position is found, false is returned to indicate
			// that the item could not be packed
			return false;
		}

		// if there is a suitable position, it is used as the place of the item
		// inside the container
		// New Maximal Spaces are generated
		addNewMaximalSpaces(item);
		return true;
	}

	/**
	 * Process to subdivide in new Maximal Spaces the remaining empty space
	 * inside a Maximal Space after an item was placed or partially placed
	 * 
	 * @param item
	 * @param maximalSpace
	 * @return list of newly generated Maximal Spaces
	 */
	private List<MaximalSpace> differenceProcess(Item item,
			MaximalSpace maximalSpace) {
		List<MaximalSpace> newMaximalSpaces = new ArrayList<MaximalSpace>();
		Point3D minCoords = maximalSpace.getMinCoords();
		Point3D maxCoords = maximalSpace.getMaxCoords();
		Point3D position = item.getPosition();

		newMaximalSpaces.add(new MaximalSpace(new Point3D(minCoords.getX(),
				minCoords.getY(), minCoords.getZ()), new Point3D(position
				.getX(), maxCoords.getY(), maxCoords.getZ())));
		newMaximalSpaces.add(new MaximalSpace(new Point3D(minCoords.getX(),
				minCoords.getY(), minCoords.getZ()), new Point3D(maxCoords
				.getX(), position.getY(), maxCoords.getZ())));
		newMaximalSpaces.add(new MaximalSpace(new Point3D(minCoords.getX(),
				minCoords.getY(), minCoords.getZ()), new Point3D(maxCoords
				.getX(), maxCoords.getY(), position.getZ())));
		newMaximalSpaces.add(new MaximalSpace(new Point3D(position.getX()
				+ item.getWidth(), minCoords.getY(), minCoords.getZ()),
				new Point3D(maxCoords.getX(), maxCoords.getY(), maxCoords
						.getZ())));
		newMaximalSpaces.add(new MaximalSpace(new Point3D(minCoords.getX(),
				position.getY() + item.getDepth(), minCoords.getZ()),
				new Point3D(maxCoords.getX(), maxCoords.getY(), maxCoords
						.getZ())));
		newMaximalSpaces.add(new MaximalSpace(new Point3D(minCoords.getX(),
				minCoords.getY(), position.getZ() + item.getHeight()),
				new Point3D(maxCoords.getX(), maxCoords.getY(), maxCoords
						.getZ())));
		return newMaximalSpaces;
	}

	/**
	 * Checks if a recently positioned item overlaps any of the existing Maximal
	 * Spaces. If there is overlapping, difference process is conducted to
	 * subdivide the overlapped Maximal Space
	 * 
	 * @param item
	 */
	private void addNewMaximalSpaces(Item item) {
		// Every existing Maximal Space is verified and if any of them is
		// overlapped by the item, it is added to the removal list and maximal
		// spaces are defined in the space that has not been used
		itemsOverlapping(item);
	}

	private void itemsOverlapping(Item item) {
		Point3D minCoords = new Point3D(item.getPosition());
		Point3D maxCoords = new Point3D(minCoords.getX() + item.getWidth(),
				minCoords.getY() + item.getDepth(), minCoords.getZ()
						+ item.getHeight());
		minCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());
		maxCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());

		List<MaximalSpace> newMaximalSpaces = new ArrayList<MaximalSpace>();
		Set<MaximalSpace> maximalSpacesToCheck = new HashSet<MaximalSpace>();
		for (double i = minCoords.getX(); i <= maxCoords.getX(); i += 1) {
			for (double j = minCoords.getY(); j <= maxCoords.getY(); j += 1) {
				for (double k = minCoords.getZ(); k <= maxCoords.getZ(); k += 1) {
					double key = Math.pow(Configuration.CONTAINERS_CELL, 2) * k
							+ Configuration.CONTAINERS_CELL * j + i;
					if (maximalSpacesByCell.containsKey((int) key)) {
						for (MaximalSpace maximalSpace : maximalSpacesByCell
								.get((int) key)) {
							if (!maximalSpace.isUsed()){
								maximalSpacesToCheck.add(maximalSpace);
							}
						}
					}
				}
			}
		}
		for (MaximalSpace maximalSpaceToCheck : maximalSpacesToCheck) {
			if (item.overlaps(maximalSpaceToCheck)) {
				List<MaximalSpace> maximalSpacesToAdd = differenceProcess(item,
						maximalSpaceToCheck);
				for (MaximalSpace maximalSpaceToAdd : maximalSpacesToAdd) {
					if (dimensionsNotNegligible(maximalSpaceToAdd)
							&& !isContained(maximalSpaceToAdd)) {
						newMaximalSpaces.add(maximalSpaceToAdd);
						maximalSpaceToCheck.setUsed(true);
					}
				}
			}
		}
		for (MaximalSpace maximalSpace : newMaximalSpaces) {
			addMaximalSpace(maximalSpace);
		}
	}

	private void addMaximalSpace(MaximalSpace maximalSpace) {
		Point3D minCoords = new Point3D(maximalSpace.getMinCoords());
		Point3D maxCoords = new Point3D(maximalSpace.getMaxCoords());
		minCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());
		maxCoords.normalize(container.getWidth(), container.getDepth(),
				container.getHeight());
		for (double i = minCoords.getX(); i <= maxCoords.getX(); i += 1) {
			for (double j = minCoords.getY(); j <= maxCoords.getY(); j += 1) {
				for (double k = minCoords.getZ(); k <= maxCoords.getZ(); k += 1) {
					double key = Math.pow(Configuration.CONTAINERS_CELL, 2) * k
							+ Configuration.CONTAINERS_CELL * j + i;
					if (maximalSpacesByCell.containsKey((int) key)) {
						maximalSpacesByCell.get((int) key).add(maximalSpace);
					} else {
						Set<MaximalSpace> cellItems = new HashSet<MaximalSpace>();
						cellItems.add(maximalSpace);
						maximalSpacesByCell.put(((int) key), cellItems);
					}
				}
			}
		}
		maximalSpaces.add(maximalSpace);
	}

	private boolean isContained(MaximalSpace maximalSpaceToAdd) {
		boolean isContained = false;
		for (MaximalSpace maximalSpace : maximalSpaces) {
			isContained = isContained
					|| maximalSpace.contains(maximalSpaceToAdd);
		}
		return isContained;
	}

	private boolean dimensionsNotNegligible(MaximalSpace maximalSpace) {
		Point3D minCoords = maximalSpace.getMinCoords();
		Point3D maxCoords = maximalSpace.getMaxCoords();
		return minCoords.getX() < maxCoords.getX()
				&& minCoords.getY() < maxCoords.getY()
				&& minCoords.getZ() < maxCoords.getZ();
	}

	/**
	 * check if an item can be positioned in any of the Extreme points without
	 * overlapping with previously packed items.
	 * 
	 * @param item
	 * @return the coordinates of the Extreme Point where the item could be
	 *         packed
	 */
	private MaximalSpace checkFittingMaximalSpace(Item item) {
		for (MaximalSpace maximalSpace : maximalSpaces) {
			if (maximalSpace.isUsed()) {
				continue;
			}
			item.setPosition(maximalSpace.getMinCoords());
			// get maximum coordinate in every axis
			Point3D position = item.getPosition();
			double x = position.getX() + item.getWidth();
			double y = position.getY() + item.getDepth();
			double z = position.getZ() + item.getHeight();

			boolean overlap = false;

			// check if item fits within the bounds of the maximalSpace
			Point3D bounds = maximalSpace.getMaxCoords();
			if (x > bounds.getX() || y > bounds.getY() || z > bounds.getZ()) {
				overlap = true;
			}

			// if there is not overlapping the item could be placed
			// in this maximal space
			if (!overlap) {
				return maximalSpace;
			}
		}
		return null;
	}

	public Packable resetCopy(Container container) {
		return new MaximalSpacePackable(container);
	}
}
