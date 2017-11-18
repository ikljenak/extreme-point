package com.thesis.app.models;

import java.math.BigDecimal;
import java.math.MathContext;

import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;

public class SolutionGenetic {
	private int[] genes = new int[Configuration.CONTAINERS_NUMBER];
	private int itemsPacked = 0;
	private int totalItems = 0;
	private BigDecimal remainingVolume = new BigDecimal(0);
	private BigDecimal totalVolume = new BigDecimal(0);
	private BoxHelper boxHelper = BoxHelper.getInstance();
	private double fitness;
	private double maxCost;

	public SolutionGenetic() {

	}

	public SolutionGenetic(BigDecimal volume) {
		double higherCost = 0;
		int amountOfBoxes = 0;
		for (int i = 0; i < genes.length; i++) {
			Container container = boxHelper.getBox(i + 1);
			BigDecimal containerVolume = container.getVolume();
			double maxAmountOfBoxesOfAClass = volume.divide(containerVolume,
					MathContext.DECIMAL128).doubleValue() + 1;
			genes[i] = (int) Math.rint(Math.random()
					* maxAmountOfBoxesOfAClass);

			double cost = container.getCost();
			if (cost > higherCost) {
				higherCost = cost;
				amountOfBoxes = (int) Math.rint(volume.divide(containerVolume,
						MathContext.DECIMAL128).doubleValue());
			}
		}
		maxCost = higherCost * amountOfBoxes;
	}

	public void calculateFitness() {
		int amountOfBoxes = calculateAmountOfBoxes();

		if (amountOfBoxes == 0) {
			fitness = -Double.MAX_VALUE;
			return;
		}

		itemsPacked = itemsPacked != totalItems ? -1 : 1;
		fitness = ((maxCost)
				/ calculateCost()
				+ getUsedVolume() + 5 / amountOfBoxes)
				* itemsPacked;
	}
	
	public double getUsedVolume() {
		double usedVolume;
		try {
			usedVolume = 1 - remainingVolume.divide(totalVolume,
					MathContext.DECIMAL128).doubleValue();
		} catch(ArithmeticException e){
			usedVolume = 0;
		}
		
		return usedVolume;
	}

	public int calculateAmountOfBoxes() {
		int amountOfBoxes = 0;
		for (int i = 0; i < genes.length; i++) {
			amountOfBoxes += genes[i];
		}
		return amountOfBoxes;
	}

	public double getFitness() {
		return fitness;
	}

	public double calculateCost() {
		double cost = 0;
		for (int i = 0; i < genes.length; i++) {
			cost += (genes[i] * boxHelper.getBoxCost(i + 1));
		}
		return cost;
	}

	public void setItemsPacked(int itemsPacked) {
		this.itemsPacked = itemsPacked;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public void setRemainingVolume(BigDecimal remainingVolume) {
		this.remainingVolume = remainingVolume;
	}

	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}

	public BigDecimal getRemainingVolume() {
		return remainingVolume;
	}

	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

	public int[] getGenes() {
		return genes;
	}

	public int getItemsPacked() {
		return this.itemsPacked;
	}

	public SolutionGenetic cross(SolutionGenetic solution) {
		SolutionGenetic ans = new SolutionGenetic();
		int[] solutionGenes = getGenes();
		int[] eliteGenes = solution.getGenes();
		int[] ansGenes = new int[Configuration.CONTAINERS_NUMBER];

		for (int i = 0; i < Configuration.CONTAINERS_NUMBER; i++) {
			if (Math.random() > Configuration.ELITE_PROBABILITY) {
				ansGenes[i] = solutionGenes[i];
			} else {
				ansGenes[i] = eliteGenes[i];
			}

			if (Math.random() < Configuration.MUTATION_PROBABILITY) {
				ansGenes[i] *= 3;
			}
		}

		ans.setGenes(ansGenes);
		return ans;
	}

	private void setGenes(int[] genes) {
		this.genes = genes;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < genes.length; i++) {
			sb.append("BOX: ").append(i).append(" amount: ").append(genes[i])
					.append("\n");
		}
		return sb.toString();
	}
}
