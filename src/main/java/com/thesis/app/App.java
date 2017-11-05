package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.app.comparator.SetOfBoxesCostComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;

public class App {
	private static final int AMOUNT_OF_BOXES = 6;
	private static final BigDecimal VOLUME_FACTOR = new BigDecimal(1.1);
	private static final Map<Integer, Container> boxes = new HashMap<Integer, Container>();
	private static List<Item> items;
	private static double bestCost = Double.MAX_VALUE;

	public static void main(String[] args) throws IOException {
		initializeBoxes();

		Set<List<Integer>> possibleBoxes;
		List<Container> solution = new ArrayList<Container>();

		String line;
		BufferedReader br = new BufferedReader(new FileReader(
				"src/main/resources/table.csv"));
		long currentOrderId = 0;
		items = new LinkedList<Item>();
		PrintWriter writer = new PrintWriter("src/main/resources/output.csv",
				"UTF-8");

		int k = 1;
		while ((line = br.readLine()) != null) {
			String[] itemInfo = line.split(",");
			long orderId;
			int quantity;
			double weight;
			double width;
			double depth;
			double height;
			try {
				orderId = Long.parseLong(itemInfo[0]);
				quantity = Integer.parseInt(itemInfo[1]);
				weight = Double.parseDouble(itemInfo[2]);
				width = Double.parseDouble(itemInfo[3]);
				depth = Double.parseDouble(itemInfo[4]);
				height = Double.parseDouble(itemInfo[5]);
			} catch (NumberFormatException e) {
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}

			if (orderId != currentOrderId) {
				if (items.size() > 0) {
					long startTime = System.currentTimeMillis();
					possibleBoxes = getPossibleBoxes(getVolume(items));
					Iterator<List<Integer>> it = possibleBoxes.iterator();
					while (it.hasNext()) {
						List<Integer> containersIndexes = it.next();
						List<Container> containers = new ArrayList<Container>();
						for (Integer index : containersIndexes) {
							containers.add(boxes.get(index).copy());
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

						if (itemsPacked == items.size()) {
							double cost = 0;

							for (Container c : containers) {
								cost += c.getCost();
							}

							if (cost < bestCost) {
								bestCost = cost;
								solution = containers;
							}
						}
						containers = null;
						it.remove();
					}
					long endTime = System.currentTimeMillis();
					if (solution != null) {
						BigDecimal usedVolume = new BigDecimal(0);
						BigDecimal totalVolume = new BigDecimal(0);
						int itemsCount = 0;
						for (Container c : solution) {
							usedVolume = usedVolume.add(c.getUsedVolume());
							totalVolume = totalVolume.add(c.getVolume());
							itemsCount += c.getItems().size();
						}
						writer.print(k + "," + currentOrderId + "," + bestCost
								+ "," + usedVolume.doubleValue()
								/ totalVolume.doubleValue() + ","
								+ ((double) (endTime - startTime)) / itemsCount);
						writer.println();
						writer.flush();
						k++;
						bestCost = Double.MAX_VALUE;
						solution = null;
					}
				}

				currentOrderId = orderId;
				items = new LinkedList<Item>();

				for (int i = 0; i < quantity; i++) {
					items.add(new Item(weight, 
							width, depth, 
							height));
				}
			} else {
				for (int i = 0; i < quantity; i++) {
					items.add(new Item(weight, width, depth, height));
				}
			}

		}
		br.close();
		writer.close();
	}

	private static void initializeBoxes() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < AMOUNT_OF_BOXES; i++) {
			String json = IOUtils.toString(App.class.getClassLoader()
					.getResourceAsStream("box" + (i + 1) + ".json"), Charset
					.defaultCharset());
			boxes.put(i + 1, mapper.readValue(json, Container.class));
		}
	}

	private static BigDecimal getVolume(List<Item> items) {
		BigDecimal volume = new BigDecimal(0);
		for (Item item : items) {
			volume = volume.add(item.getVolume());
		}
		return volume;
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
				&& volumeSoFar.compareTo(totalVolume.multiply(VOLUME_FACTOR)) < 0) {
			List<Integer> aux = new ArrayList<Integer>();

			for (Integer c : containers) {
				aux.add(Integer.valueOf(c));
			}

			ans.add(aux);
		}

		if (volumeSoFar.compareTo(totalVolume.multiply(VOLUME_FACTOR)) >= 0) {
			if (ans.size() == 0) {
				for (Container box : boxes.values()) {
					List<Integer> aux = new ArrayList<Integer>();
					aux.add(box.getId());
					ans.add(aux);
				}
			}
			return;
		}
		if (iteration > 20) {
			return;
		}
		

		for (Container box : boxes.values()) {
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
