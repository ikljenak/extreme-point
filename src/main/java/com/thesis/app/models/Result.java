package com.thesis.app.models;

public class Result {
	private long time;
	private int amountOfContainers;
	private double cost;
	private double usedVolume;
	private int itemsPacked;

	public Result(long time, int amountOfContainers, double cost,
			double usedVolume, int itemsPacked) {
		super();
		this.time = time;
		this.amountOfContainers = amountOfContainers;
		this.cost = cost;
		this.usedVolume = usedVolume;
		this.itemsPacked = itemsPacked;
	}

	public long getTime() {
		return time;
	}

	public int getAmountOfContainers() {
		return amountOfContainers;
	}

	public double getCost() {
		return cost;
	}

	public double getUsedVolume() {
		return usedVolume;
	}

	public int getItemsPacked() {
		return this.itemsPacked;
	}
}
