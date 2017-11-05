package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thesis.app.comparator.SetOfBoxesCostComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;
import com.thesis.app.utils.OutputWriter;

public class AnotherApp {
	private static final List<Item> items = new LinkedList<Item>();
	private static Collection<Container> boxes;

	public static void main(String[] args) throws IOException {
		initialConfiguration();
		initializeItems();
		boxes = BoxHelper.getInstance()
				.getAllBoxes();
		Set<List<Integer>> possibleBoxes = getPossibleBoxes(getVolume(items));
		List<Container> solution = null;
		double bestFitness = Double.MAX_VALUE;
		double cost = 0;

		for (List<Integer> containersIndexes : possibleBoxes) {
			List<Container> containers = new ArrayList<Container>();
			for (Integer i : containersIndexes) {
				containers.add(BoxHelper.getInstance().getBox(i).copy());
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
			
			cost = getCost(containers);
			if (itemsPacked == items.size() && cost < bestFitness) {
				solution = new ArrayList<Container>();
				for(Container container:containers) {
					solution.add(container.copy());
				}
				bestFitness = cost;
			}
		}

		OutputWriter.ContainersToXyzFormat(solution, Configuration.OUTPUT_FILE);
		System.out.println(bestFitness);
	}
	
	private static void initialConfiguration() throws IOException {
		Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.bruteforce.properties");
		properties.load(is);
		Configuration.setProperties(properties);
	}

	private static void initializeItems() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(
				Configuration.INPUT_FILE));
		for (int i = 0; i < Configuration.INPUT_SIZE; i++) {
			String[] values = in.readLine().split(" ");
			items.add(new Item(new Double(values[0]), new Double(
					values[1]), new Double(values[2]), new Double(
					values[3])));
		}
		in.close();
	}

	private static BigDecimal getVolume(List<Item> items) {
		double volume = 0;
		for (Item item : items) {
			volume += item.getVolume().doubleValue();
		}
		return new BigDecimal(volume);
	}
	
	private static double getCost(List<Container> containers){
		double cost = 0;
		for(Container container:containers) {
			cost += container.getCost();
		}
		return cost;
	}

	private static Set<List<Integer>> getPossibleBoxes(BigDecimal volume)
			throws JsonParseException, JsonMappingException, IOException {
		Set<List<Integer>> ans = new TreeSet<List<Integer>>(
				new SetOfBoxesCostComparator());

		getPossibleBoxesRecursive(ans, new ArrayList<Integer>(),
				new BigDecimal(0), volume, 0);
		return ans;
	}

	private static void getPossibleBoxesRecursive(Set<List<Integer>> ans,
			List<Integer> containers, BigDecimal volumeSoFar,
			BigDecimal totalVolume, int iteration) {
		if (volumeSoFar.compareTo(totalVolume) > 0
				&& volumeSoFar.compareTo(totalVolume.multiply(new BigDecimal(Configuration.VOLUME_FACTOR))) < 0) {
			List<Integer> aux = new ArrayList<Integer>();

			for (Integer c : containers) {
				aux.add(Integer.valueOf(c));
			}

			ans.add(aux);
		}

		if (volumeSoFar.compareTo(totalVolume.multiply(new BigDecimal(Configuration.VOLUME_FACTOR))) >= 0) {
			if (ans.size() == 0) {
				for (Container box : boxes) {
					List<Integer> aux = new ArrayList<Integer>();
					aux.add(box.getId());
					ans.add(aux);
				}
			}
			return;
		}
		if (iteration > Configuration.BRUTE_FORCE_MAX_DEPTH) {
			return;
		}
		for (Container box : boxes) {
			if (containers.size() == 0
					|| containers.get(containers.size() - 1) <= box.getId()) {
				Integer newBox = box.getId();
				containers.add(newBox);

				getPossibleBoxesRecursive(ans, containers,
						volumeSoFar.add(box.getVolume()), totalVolume,
						iteration + 1);

				containers.remove(newBox);
			}
		}
	}
}