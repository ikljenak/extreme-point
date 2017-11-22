package com.thesis.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.SolutionGenetic;
import com.thesis.app.utils.BoxHelper;

public class PackGenetic implements Runnable {
	private SolutionGenetic solution;
	private List<Item> items = new LinkedList<Item>();

	public PackGenetic(SolutionGenetic solution, List<Item> items) {
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
			System.out.println(Thread.currentThread() + " " + itemsPacked);
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
