package com.thesis.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Individual;
import com.thesis.app.utils.BoxHelper;

public class PackEvolutionary implements Runnable {
	private Individual solution;
	private List<Item> items = new LinkedList<Item>();

	public PackEvolutionary(Individual solution, List<Item> items) {
		this.solution = solution;
		this.items = items;
	}

	@Override
	public void run() {
		List<Container> containers = new ArrayList<Container>();
		int[] genes = solution.getGenes();
		for (int i = 0; i < genes.length; i++) {
			for (int k = 0; k < genes[i]; k++) {
				containers.add(BoxHelper.getInstance().getBox(i + 1).copy());
			}
		}
		int itemsPacked = 0;
		for (Item item : items) {
			int c = 0;
			boolean packed = false;
			while (c < containers.size() && !packed) {
				Container container = containers.get(c);
				packed = container.pack(item);
				c++;
			}
			if (packed) {
				itemsPacked++;
			}
		}

		BigDecimal remainingVolume = new BigDecimal(0);
		BigDecimal totalVolume = new BigDecimal(0);
		for (Container container : containers) {
			remainingVolume = remainingVolume.add(container.getRemainingVolume());
			totalVolume = totalVolume.add(container.getVolume());
		}
		solution.setRemainingVolume(remainingVolume);
		solution.setTotalVolume(totalVolume);

		solution.setItemsPacked(itemsPacked);
		solution.calculateFitness();
	}
}
