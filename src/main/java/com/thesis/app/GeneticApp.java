package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thesis.app.comparator.SolutionFitnessComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Solution;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;
import com.thesis.app.utils.OutputWriter;

public class GeneticApp {

	private static double bestFitness = 0;
	private static double iterationBestFitness = 0;
	private static final List<Item> items = new LinkedList<Item>();

	public static void main(String[] args) throws IOException {
		initialConfiguration();
		initializeItems();
		List<Solution> possibleBoxes = getPossibleBoxes(getVolume(items));
		int notImprovedGeneration = 0;
		while (notImprovedGeneration < Configuration.FINAL_CONDITION) {
			iterationBestFitness = 0;

			for (Solution solution : possibleBoxes) {
				double fitness = pack(solution, false);
				if (fitness > iterationBestFitness) {
					iterationBestFitness = fitness;
				}
			}
			possibleBoxes = newGenerationSolutions(possibleBoxes);
			if (iterationBestFitness > bestFitness) {
				bestFitness = iterationBestFitness;
				notImprovedGeneration = 0;
			} else {
				notImprovedGeneration++;
			}
		}

		Solution finalSolution = getBestSolution(possibleBoxes);
		pack(finalSolution, true);
	}

	private static void initialConfiguration() throws IOException {
		Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		properties.load(is);
		Configuration.setProperties(properties);
	}

	private static double pack(Solution solution, boolean print)
			throws FileNotFoundException, UnsupportedEncodingException {
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
		/*
		 * double remainingVolume = 0; for (Container container : containers) {
		 * remainingVolume += container .getRemainingVolume().doubleValue(); }
		 * solution.setRemainingVolume(new BigDecimal(remainingVolume));
		 */
		solution.setItemsPacked(itemsPacked);

		if (print) {
			OutputWriter.ContainersToXyzFormat(containers,
					Configuration.OUTPUT_FILE);
			System.out.println(getCost(containers));
			System.out.println(getItemsPacked(containers));
			System.out.println(containers);
		}
		return solution.fitness();
	}

	private static Solution getBestSolution(List<Solution> solutions) {
		Collections.sort(solutions, new SolutionFitnessComparator());
		return solutions.get(0);
	}

	private static double getCost(List<Container> containers) {
		double cost = 0;
		for (Container container : containers) {
			cost += container.getCost();
		}
		return cost;
	}

	private static int getItemsPacked(List<Container> containers) {
		int itemsPacked = 0;
		for (Container container : containers) {
			itemsPacked += container.getItems().size();
		}
		return itemsPacked;
	}

	private static List<Solution> newGenerationSolutions(
			List<Solution> possibleBoxes) {
		Collections.sort(possibleBoxes, new SolutionFitnessComparator());

		List<Solution> ans = new ArrayList<Solution>();
		int eliteSize = (int) (Configuration.POPULATION_SIZE * Configuration.ELITE_PERCENTAGE);

		ans.addAll(possibleBoxes.subList(0, eliteSize));

		for (int i = 0; i < eliteSize; i++) {
			Solution solution = possibleBoxes.get((new Random())
					.nextInt(possibleBoxes.size() - eliteSize) + eliteSize);
			for (int k = 0; k < eliteSize; k++) {
				solution = solution.cross(possibleBoxes.get(k));
				ans.add(solution);
			}
		}
		return ans;
	}

	private static void initializeItems() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(
				Configuration.INPUT_FILE));
		for (int i = 0; i < Configuration.INPUT_SIZE; i++) {
			String[] values = in.readLine().split(" ");
			items.add(new Item(new Double(values[0]), new Double(values[1]),
					new Double(values[2]), new Double(values[3])));
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

	private static List<Solution> getPossibleBoxes(BigDecimal volume)
			throws JsonParseException, JsonMappingException, IOException {
		return generateInitialSolutions(volume);
	}

	private static List<Solution> generateInitialSolutions(
			BigDecimal volume) {
		List<Solution> ans = new ArrayList<Solution>();
		for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
			Solution solution = new Solution(volume);
			ans.add(solution);
		}
		return ans;
	}
}
