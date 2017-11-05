package com.thesis.app.models;

import java.math.BigDecimal;

import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;

public class Solution {
	private int[] genes = new int[Configuration.CONTAINERS_NUMBER];
	private int itemsPacked = 0;
	//private BigDecimal remainingVolume = new BigDecimal(0);
	private BoxHelper boxHelper = BoxHelper.getInstance();

	public Solution() {

	}

	public Solution(BigDecimal volume) {
		for (int i = 0; i < genes.length; i++) {
			BigDecimal containerVolume = boxHelper.getBox(i + 1).getVolume();
			genes[i] = (int) Math.rint(Math.random() * volume.doubleValue() / containerVolume.doubleValue());
		}
	}

	public double fitness() {
		int amountOfBoxes = 0;
		for (int i = 0; i < genes.length; i++) {
			amountOfBoxes += genes[i] * i;
		}
		
		if (amountOfBoxes == 0) {
			amountOfBoxes = -1;
		}
		return 100 / (5 * calculateCost()) + 0.1 * itemsPacked;
	}

	private double calculateCost() {
		double cost = 0;
		for (int i = 0; i < genes.length; i++) {
			cost += (genes[i] * boxHelper.getBoxCost(i + 1));
		}
		return cost;
	}

	public void setItemsPacked(int itemsPacked) {
		this.itemsPacked = itemsPacked;
	}

	/*public void setRemainingVolume(BigDecimal remainingVolume) {
		this.remainingVolume = remainingVolume;
	}*/

	public int[] getGenes() {
		return genes;
	}

	public Solution cross(Solution solution) {
		Solution ans = new Solution();
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
