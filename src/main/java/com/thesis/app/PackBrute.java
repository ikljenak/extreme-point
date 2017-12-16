package com.thesis.app;

import java.util.LinkedList;
import java.util.List;

import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Solution;

public class PackBrute implements Runnable{
	private Solution solution;
	private List<Item> items;
	
	public PackBrute(Solution solution, List<Item> items) {
		this.solution = solution;
		this.items = items;
	}

	@Override
	public void run() {
		List<Item> itemsCopy = new LinkedList<Item>();
		for(Item item: items) {
			itemsCopy.add(new Item(item));
		}
		List<Container> containers = solution.getContainers();
		int itemsPacked = 0;
		for (Item item : itemsCopy) {
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
		solution.setItemsPacked(itemsPacked);
	}

}
