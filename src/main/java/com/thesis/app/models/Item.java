package com.thesis.app.models;

public class Item extends Box {
	private Point3D position = null;

	// CONSTRUCTORS
	public Item(double weight, double width, double depth, double height) {
		super(weight, width, depth, height);
	}

	public Item(Item item) {
		super(item.getWeight(), item.getWidth(), item.getDepth(), item
				.getHeight());
		this.position = item.position;
	}

	// END CONSTRUCTORS

	// --------------------------------------------------//

	// GETTERS AND SETTERS
	public Point3D getPosition() {
		return position;
	}

	public void setPosition(Point3D position) {
		this.position = position;
	}

	// END GETTERS AND SETTERS

	// --------------------------------------------------//

	// TO STRING
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ITEM: ").append("Width: ").append(getWidth())
				.append(", Depth: ").append(getDepth()).append(", Height: ")
				.append(getHeight()).append(", Position: ")
				.append(getPosition()).append("\n");
		return sb.toString();
	}

	// END TO STRING

	// --------------------------------------------------//

	/**
	 * Check if two items overlap
	 * 
	 * @param item
	 *            , the item to be compared with current instance
	 * @return true if items intersect at any point, false otherwise
	 */
	public boolean overlaps(Item item) {
		return position.getX() < item.getPosition().getX() + item.getWidth()
				&& position.getX() + getWidth() > item.getPosition().getX()
				&& position.getY() < item.getPosition().getY()
						+ item.getDepth()
				&& position.getY() + getDepth() > item.getPosition().getY()
				&& position.getZ() < item.getPosition().getZ()
						+ item.getHeight()
				&& position.getZ() + getHeight() > item.getPosition().getZ();

	}

	public boolean overlaps(MaximalSpace maximalSpace) {
		Point3D maximalSpaceMaxCoords = maximalSpace.getMaxCoords();
		Point3D maximalSpaceMinCoords = maximalSpace.getMinCoords();
		return position.getX() < maximalSpaceMaxCoords.getX()
				&& position.getX() + getWidth() > maximalSpaceMinCoords.getX()
				&& position.getY() < maximalSpaceMaxCoords.getY()
				&& position.getY() + getDepth() > maximalSpaceMinCoords.getY()
				&& position.getZ() < maximalSpaceMaxCoords.getZ()
				&& position.getZ() + getHeight() > maximalSpaceMinCoords.getZ();
	}

	/**
	 * Rotate the item in X axis by swapping height and depth
	 */
	private void rotateX() {
		double depth = getDepth();
		double height = getHeight();

		setDepth(height);
		setHeight(depth);
	}

	/**
	 * Rotate the item in Y axis by swapping height and width
	 */
	private void rotateY() {
		double width = getWidth();
		double height = getHeight();

		setWidth(height);
		setHeight(width);
	}

	/**
	 * Rotate the item in Z axis by swapping width and depth
	 */
	private void rotateZ() {
		double depth = getDepth();
		double width = getWidth();

		setDepth(width);
		setWidth(depth);
	}

	/**
	 * Rotate the item around one of the three axis
	 * 
	 * @param rotation
	 *            , integer defining the axis around which the item should
	 *            rotate. Module 3 is calculated to this parameter and according
	 *            to the result one of the axis is chosen
	 */
	public void rotate(int rotation) {
		switch (rotation % 3) {
		case 0: {
			rotateX();
			break;
		}
		case 1: {
			rotateY();
			break;
		}
		case 2: {
			rotateZ();
			break;
		}
		}
	}

}
