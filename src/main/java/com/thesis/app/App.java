package com.thesis.app;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.app.comparator.CostContainerComparator;
import com.thesis.app.comparator.SetOfBoxesCostComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.utils.OutputWriter;

public class App {
	private static final int AMOUNT_OF_BOXES = 6;
	private static final double VOLUME_FACTOR = 1.5;
	private static final Set<Container> boxes = new TreeSet<Container>(
			new CostContainerComparator());
	private static final List<Item> items = new LinkedList<Item>();

	public static void main(String[] args) throws IOException {
		initializeBoxes();
		initializeItems();

		// Set<Item> items = new TreeSet<Item>(new AreaHeightItemComparator());
		// List<Item> items = new LinkedList<Item>();
		Set<List<Container>> possibleBoxes = getPossibleBoxes(getVolume(items));
		List<Container> solution = null;

		for (List<Container> containers : possibleBoxes) {
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
			if (itemsPacked == items.size()) {
				solution = containers;
				break;
			}
		}

		OutputWriter.ContainersToXyzFormat(solution);
		System.out.println(items.size());
		System.out.println(solution);
	}

	private static void initializeItems() {
		for (int i = 0; i < 100; i++) {
			items.add(new Item(0.0, Math.random() * 450 / 2,
					Math.random() * 400 / 2, Math.random() * 455 / 2));
		}
	}

	private static void initializeBoxes() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < AMOUNT_OF_BOXES; i++) {
			String json = IOUtils.toString(App.class.getClassLoader()
					.getResourceAsStream("box" + (i + 1) + ".json"), Charset
					.defaultCharset());
			boxes.add(mapper.readValue(json, Container.class));
		}
	}

	private static double getVolume(List<Item> items) {
		double volume = 0;
		for (Item item : items) {
			volume += item.getVolume();
		}
		return volume;
	}

	private static Set<List<Container>> getPossibleBoxes(double volume) {
		Set<List<Container>> ans = new TreeSet<List<Container>>(
				new SetOfBoxesCostComparator());
		getPossibleBoxesRecursive(ans, new ArrayList<Container>(), 0, volume);
		return ans;
	}

	private static void getPossibleBoxesRecursive(Set<List<Container>> ans,
			List<Container> containers, double volumeSoFar, double totalVolume) {
		if (volumeSoFar > totalVolume
				&& volumeSoFar < totalVolume * VOLUME_FACTOR) {
			List<Container> aux = new ArrayList<Container>();
			for (Container c : containers) {
				aux.add(c.copy());
			}
			ans.add(aux);
		}
		if (volumeSoFar >= totalVolume * VOLUME_FACTOR) {
			if (ans.size() == 0) {
				for (Container box : boxes) {
					List<Container> aux = new ArrayList<Container>();
					aux.add(box);
					ans.add(aux);
				}
			}
			return;
		}
		for (Container box : boxes) {
			List<Container> aux = new ArrayList<Container>();
			for (Container c : containers) {
				aux.add(c.copy());
			}
			aux.add(box);
			getPossibleBoxesRecursive(ans, aux, volumeSoFar + box.getVolume(),
					totalVolume);
		}
	}
}
