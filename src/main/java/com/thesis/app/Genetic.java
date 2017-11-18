package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thesis.app.comparator.SolutionFitnessComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Result;
import com.thesis.app.models.SolutionGenetic;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;
import com.thesis.app.utils.OutputWriter;

public class Genetic {

	private static double bestFitness = 0;
	private static double iterationBestFitness = 0;
	private static final List<Item> items = new LinkedList<Item>();

	public Result run() throws IOException, InterruptedException {
		initializeItems();
		long startTime = System.currentTimeMillis();
		List<SolutionGenetic> possibleBoxes = getPossibleBoxes(getVolume(items));
		int notImprovedGeneration = 0;
		while (notImprovedGeneration < Configuration.FINAL_CONDITION) {
			iterationBestFitness = 0;

			ExecutorService es = Executors.newCachedThreadPool();
			for (SolutionGenetic solution : possibleBoxes) {
				solution.setTotalItems(items.size());
				List<Item> aux = new LinkedList<Item>();
				for (Item item : items) {
					aux.add(new Item(item));
				}
				es.execute(new PackGenetic(solution, aux));
			}
			es.shutdown();
			boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
			if (finished) {
				for (SolutionGenetic solution : possibleBoxes) {
					double fitness = solution.getFitness();
					if (fitness > iterationBestFitness) {
						iterationBestFitness = fitness;
					}
				}
			}
			if (iterationBestFitness > bestFitness) {
				bestFitness = iterationBestFitness;
				notImprovedGeneration = 0;
			} else {
				notImprovedGeneration++;
			}
			possibleBoxes = newGenerationSolutions(possibleBoxes);
		}

		SolutionGenetic finalSolution = getBestSolution(possibleBoxes);
		long endTime = System.currentTimeMillis();
		pack(finalSolution, Configuration.VISUAL_OUTPUT);
		
		return new Result(endTime - startTime,
				finalSolution.calculateAmountOfBoxes(),
				finalSolution.calculateCost(), finalSolution.getUsedVolume(),
				finalSolution.getItemsPacked());
	}

	private double pack(SolutionGenetic solution, boolean print)
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

		BigDecimal remainingVolume = new BigDecimal(0);
		BigDecimal totalVolume = new BigDecimal(0);
		for (Container container : containers) {
			remainingVolume = remainingVolume.add(container
					.getRemainingVolume());
			totalVolume = totalVolume.add(container.getVolume());
		}
		solution.setRemainingVolume(remainingVolume);
		solution.setTotalVolume(totalVolume);

		solution.setItemsPacked(itemsPacked);

		if (print) {
			OutputWriter.ContainersToXyzFormat(containers,
					Configuration.OUTPUT_FILE);
		}
		return solution.getFitness();
	}

	private SolutionGenetic getBestSolution(List<SolutionGenetic> solutions) {
		Collections.sort(solutions, new SolutionFitnessComparator());
		return solutions.get(0);
	}

	private List<SolutionGenetic> newGenerationSolutions(
			List<SolutionGenetic> possibleBoxes) {
		Collections.sort(possibleBoxes, new SolutionFitnessComparator());

		List<SolutionGenetic> ans = new ArrayList<SolutionGenetic>();
		int eliteSize = (int) (Configuration.POPULATION_SIZE * Configuration.ELITE_PERCENTAGE);

		ans.addAll(possibleBoxes.subList(0, eliteSize));

		for (int i = 0; i < eliteSize; i++) {
			SolutionGenetic solution = possibleBoxes.get((new Random())
					.nextInt(possibleBoxes.size() - eliteSize) + eliteSize);
			for (int k = 0; k < eliteSize; k++) {
				solution = solution.cross(possibleBoxes.get(k));
				ans.add(solution);
			}
		}
		return ans;
	}

	private void initializeItems() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(
				Configuration.INPUT_FILE));
		for (int i = 0; i < Configuration.INPUT_SIZE; i++) {
			String[] values = in.readLine().split(" ");
			items.add(new Item(new Double(values[0]), new Double(values[1]),
					new Double(values[2]), new Double(values[3])));
		}
		in.close();
	}

	private BigDecimal getVolume(List<Item> items) {
		double volume = 0;
		for (Item item : items) {
			volume += item.getVolume().doubleValue();
		}
		return new BigDecimal(volume);
	}

	private List<SolutionGenetic> getPossibleBoxes(BigDecimal volume)
			throws JsonParseException, JsonMappingException, IOException {
		return generateInitialSolutions(volume);
	}

	private List<SolutionGenetic> generateInitialSolutions(BigDecimal volume) {
		List<SolutionGenetic> ans = new ArrayList<SolutionGenetic>();
		for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
			SolutionGenetic solution = new SolutionGenetic(volume);
			ans.add(solution);
		}
		return ans;
	}
}
