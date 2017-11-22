package com.thesis.app.models;

import java.math.BigDecimal;
import java.math.MathContext;

import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;

public class SolutionGenetic {
	private int[] genes = new int[Configuration.CONTAINERS_NUMBER];
	private int itemsPacked = 0;
	private int totalItems = 0;
	private BigDecimal itemsVolume = new BigDecimal(0);
	private BigDecimal remainingVolume = new BigDecimal(0);
	private BigDecimal totalVolume = new BigDecimal(0);
	private BoxHelper boxHelper = BoxHelper.getInstance();
	private double fitness;
	private double refCost;
	private int refNumBoxes;

	public SolutionGenetic() {

	}

	public SolutionGenetic(BigDecimal volume) {
		itemsVolume = volume;
		initialPopulation(volume);
	}

	/**
	 * Generates an initial population for the genetic algorithm
	 * 
	 * @param volume
	 *            , total volume of items to be packed
	 */
	private void initialPopulation(BigDecimal volume) {
		double higherCost = 0;
		int amountOfBoxes = 0;

		// Loop to complete every gene
		for (int i = 0; i < genes.length; i++) {
			// Get container associated to gene
			Container container = boxHelper.getBox(i + 1);

			// Calculate volume of container
			BigDecimal containerVolume = container.getVolume();

			// calculate amount of containers that would be necessary to hold
			// all the items
			double maxAmountOfBoxesOfAClass = volume.divide(containerVolume,
					MathContext.DECIMAL128).doubleValue() + 1;

			// Set gene with a random value between 0 and previously calculated
			// maximum
			genes[i] = (int) Math
					.rint(Math.random() * maxAmountOfBoxesOfAClass);
			refNumBoxes += genes[i];
			double cost = container.getCost();
			if (cost > higherCost) {
				higherCost = cost;
				amountOfBoxes = (int) Math.rint(volume.divide(containerVolume,
						MathContext.DECIMAL128).doubleValue() + 1);
			}
		}
		refNumBoxes /= genes.length;
		refCost = higherCost * amountOfBoxes;
	}

	public void calculateFitness() {
		int amountOfBoxes = calculateAmountOfBoxes();

		if (amountOfBoxes == 0) {
			fitness = -Double.MAX_VALUE;
			return;
		}
		itemsPacked = itemsPacked != totalItems ? -1 : 1;
		System.out.println(refCost / calculateCost() + " " + getUsedVolume() + " " + ((double)refNumBoxes) / amountOfBoxes);
		fitness = (Configuration.FITNESS_A * refCost / calculateCost()
				+ Configuration.FITNESS_B * getUsedVolume() + Configuration.FITNESS_C
				* refNumBoxes / amountOfBoxes)
				* itemsPacked;
	}

	public double getUsedVolume() {
		double usedVolume;
		try {
			usedVolume = itemsVolume
					.divide(totalVolume, MathContext.DECIMAL128).doubleValue();
		} catch (ArithmeticException e) {
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

	public BigDecimal getItemsVolume() {
		return itemsVolume;
	}

	public void setItemsVolume(BigDecimal itemsVolume) {
		this.itemsVolume = itemsVolume;
	}

	/**
	 * Takes two chromosomes and perform a crossover to generate offspring
	 * 
	 * @param solution
	 *            , chromosome that will be crossed with the current instance
	 * @return New chromosome originated from two chromosome parents from the
	 *         previous generation
	 */
	public SolutionGenetic cross(SolutionGenetic eliteProgenitor) {
		SolutionGenetic offspring = new SolutionGenetic();
		offspring.setItemsVolume(itemsVolume);
		int[] progenitorGenes = getGenes();
		int[] eliteGenes = eliteProgenitor.getGenes();
		int[] offspringGenes = new int[Configuration.CONTAINERS_NUMBER];

		for (int i = 0; i < offspringGenes.length; i++) {
			// With a given probability the offspring has the gene of one
			// progenitor or the other
			if (Math.random() > Configuration.ELITE_PROBABILITY) {
				offspringGenes[i] = progenitorGenes[i];
			} else {
				offspringGenes[i] = eliteGenes[i];
			}

			// With a given probability the offspring will experience a mutation
			// that will multiply the received gene by a factor
			if (Math.random() < Configuration.MUTATION_PROBABILITY) {
				offspringGenes[i] *= Configuration.MUTATION_FACTOR;
			}
		}

		offspring.setGenes(offspringGenes);
		return offspring;
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
