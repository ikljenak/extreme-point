package com.thesis.app.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.thesis.app.comparator.MaximalSpaceVolumeComparator;

public class MaximalSpacePackable implements Packable {

	private Set<MaximalSpace> maximalSpaces = new TreeSet<MaximalSpace>(
			new MaximalSpaceVolumeComparator());
	private Container container;
	private static final int POSSIBLE_ROTATIONS = 6;

	public MaximalSpacePackable(Container container) {
		this.container = container;
	}

	public boolean pack(Item item) {
		Point3D position = null;

		if (maximalSpaces.isEmpty()) {
			maximalSpaces.add(new MaximalSpace(new Point3D(0, 0, 0),
					new Point3D(container.getWidth(), container.getDepth(),
							container.getHeight())));
		}

		for (int i = 0; i < POSSIBLE_ROTATIONS; i++) {
			item.rotate(i);
			MaximalSpace maximalSpace = checkFittingMaximalSpace(item);
			if (maximalSpace != null) {
				position = maximalSpace.getMinCoords();
				break;
			}
		}
		if (position == null) {
			return false;
		}

		addNewMaximalSpaces(item);
		return true;
	}

	private void addNewMaximalSpaces(Item item) {

		List<MaximalSpace> maximalSpacesToAdd = new ArrayList<MaximalSpace>();
		List<MaximalSpace> maximalSpacesToRemove = new ArrayList<MaximalSpace>();

		for (MaximalSpace maximalSpace : maximalSpaces) {
			Point3D minCoords = maximalSpace.getMinCoords();
			Point3D maxCoords = maximalSpace.getMaxCoords();
			Point3D position = item.getPosition();

			if (item.overlaps(maximalSpace)) {
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(minCoords
						.getX(), minCoords.getY(), minCoords.getZ()),
						new Point3D(position.getX(), maxCoords.getY(),
								maxCoords.getZ())));
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(minCoords
						.getX(), minCoords.getY(), minCoords.getZ()),
						new Point3D(maxCoords.getX(), position.getY(),
								maxCoords.getZ())));
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(minCoords
						.getX(), minCoords.getY(), minCoords.getZ()),
						new Point3D(maxCoords.getX(), maxCoords.getY(),
								position.getZ())));
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(position
						.getX() + item.getWidth(), minCoords
						.getY(), minCoords.getZ()), new Point3D(maxCoords
						.getX(), maxCoords.getY(), maxCoords.getZ())));
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(minCoords
						.getX(), position.getY()
						+ item.getDepth(), minCoords.getZ()),
						new Point3D(maxCoords.getX(), maxCoords.getY(),
								maxCoords.getZ())));
				maximalSpacesToAdd.add(new MaximalSpace(new Point3D(minCoords
						.getX(), minCoords.getY(), position.getZ()
						+ item.getHeight()), new Point3D(
						maxCoords.getX(), maxCoords.getY(), maxCoords.getZ())));

				maximalSpacesToRemove.add(maximalSpace);
			}
		}

		maximalSpaces.removeAll(maximalSpacesToRemove);

		for (MaximalSpace maximalSpaceToAdd : maximalSpacesToAdd) {
			boolean isContained = false;
			Point3D minCoords = maximalSpaceToAdd.getMinCoords();
			Point3D maxCoords = maximalSpaceToAdd.getMaxCoords();
			if (minCoords.getX() < maxCoords.getX()
					&& minCoords.getY() < maxCoords.getY()
					&& minCoords.getZ() < maxCoords.getZ()) {

				for (MaximalSpace maximalSpace : maximalSpaces) {
					isContained = isContained
							|| maximalSpace.contains(maximalSpaceToAdd);
				}
				if (!isContained) {
					maximalSpaces.add(maximalSpaceToAdd);
				}
			}
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
	private MaximalSpace checkFittingMaximalSpace(Item item) {
		for (MaximalSpace maximalSpace : maximalSpaces) {
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
