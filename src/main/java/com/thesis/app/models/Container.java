package com.thesis.app.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thesis.app.utils.Configuration;

public class Container extends Box {

	private List<Item> items = new ArrayList<Item>();
	private double loadedWeight = 0;
	private double cost = 0;
	private int id;
	private int itemsPacked = 0;
	private BigDecimal remainingVolume;
	private Packable packable = null;

	// CONSTRUCTORS
	@JsonCreator
	public Container(@JsonProperty("id") int id,
			@JsonProperty("weight") double weight,
			@JsonProperty("width") double width,
			@JsonProperty("depth") double depth,
			@JsonProperty("height") double height,
			@JsonProperty("cost") double cost) {
		super(weight, width, depth, height);
		this.cost = cost;
		this.id = id;

		switch (Configuration.PACKING_METHOD) {
		case "EP": {
			this.packable = new ExtremePointPackable(this);
			break;
		}
		case "MS":
		default: {
			this.packable = new MaximalSpacePackable(this);
			break;
		}
		}
		
		this.remainingVolume = new BigDecimal(getWidth()).multiply(
				new BigDecimal(getDepth())).multiply(
				new BigDecimal(getHeight()));
	}

	public Container(Container container) {
		super(container.getWeight(), container.getWidth(),
				container.getDepth(), container.getHeight());
		this.cost = container.getCost();
		this.remainingVolume = new BigDecimal(getWidth()).multiply(
				new BigDecimal(getDepth())).multiply(
				new BigDecimal(getHeight()));
		;
	}

	// END CONSTRUCTORS

	// --------------------------------------------------//

	// GETTERS AND SETTERS

	public int getId() {
		return id;
	}

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

	public int getItemsPacked() {
		return itemsPacked;
	}

	public BigDecimal getUsedVolume() {
		BigDecimal usedVolume = new BigDecimal(0);
		for (Item item : items) {
			usedVolume = usedVolume.add(item.getVolume());
		}
		return usedVolume;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public BigDecimal getRemainingVolume() {
		return this.remainingVolume;
	}

	public Packable getPackable() {
		return this.packable;
	}

	public void setPackable(Packable packable) {
		this.packable = packable;
	}

	// END GETTERS AND SETTERS

	// --------------------------------------------------//

	// TO STRING
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------------------------\n");
		sb.append("CONTAINER\n");
		sb.append("Weight: ").append(loadedWeight).append("\n");
		sb.append("Width: ").append(getWidth()).append("\n");
		sb.append("Depth: ").append(getDepth()).append("\n");
		sb.append("Height: ").append(getHeight()).append("\n");
		sb.append("Used space: ").append(getUsedVolume()).append("\n");
		sb.append("Items: " + this.getItems().size() + "\n");
		sb.append("-----------------------------\n");
		for (Item item : items) {
			sb.append(item.toString());
		}
		return sb.toString();
	}

	// END TO STRING

	// --------------------------------------------------//

	/**
	 * Creates a copy of current container
	 * 
	 * @return the copy of the container
	 */
	public Container copy() {
		Container container = new Container(this);
	
		if("EP".equals(Configuration.PACKING_METHOD)){
			container.setPackable(new ExtremePointPackable(container));
		} else {
			container.setPackable(new MaximalSpacePackable(container));
		}
		
		for(Item item: this.getItems()){
			container.add(new Item(item));
		}
		return container;
	}

	/**
	 * get the coords of the front top right corner of the container
	 * 
	 * @return Point3D with the coords
	 */
	public Point3D getFrontTopRightCorner() {
		return new Point3D(this.getWidth(), this.getDepth(), this.getHeight());
	}

	/**
	 * Adds an item to the container
	 * 
	 * @param item
	 */
	public void add(Item item) {
		items.add(item);
		itemsPacked++;
		this.loadedWeight = loadedWeight + item.getWeight();
		this.remainingVolume = this.remainingVolume.subtract(item.getVolume());
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
		if (!checkIfFits(item)) {
			return false;
		}

		return packable.pack(item);
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
		return !(item.getDepth() > this.getDepth()
				|| item.getWidth() > this.getWidth()
				|| item.getHeight() > this.getHeight()
				|| item.getWeight() > this.getWeight() || item.getWeight()
				+ loadedWeight > this.getWeight());
	}
}
