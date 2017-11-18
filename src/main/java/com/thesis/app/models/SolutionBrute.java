package com.thesis.app.models;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class SolutionBrute {
	private List<Container> containers;
	private int itemsPacked;
	
	public SolutionBrute(List<Container> containers) {
		this.containers = containers;
	}
	
	public List<Container> getContainers() {
		return containers;
	}
	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	public int getItemsPacked() {
		return itemsPacked;
	}
	public void setItemsPacked(int itemsPacked) {
		this.itemsPacked = itemsPacked;
	}
	
	public double getCost(){
		double cost = 0;
		for(Container container:containers) {
			cost += container.getCost();
		}
		return cost;
	}
	
	public double getUsedVolume() {
		BigDecimal usedVolume = new BigDecimal(0);
		BigDecimal totalVolume = new BigDecimal(0);
		for(Container container: containers) {
			usedVolume = usedVolume.add(container.getUsedVolume());
			totalVolume = totalVolume.add(container.getVolume());
		}
		return usedVolume.divide(totalVolume, MathContext.DECIMAL128).doubleValue();
	}
}
