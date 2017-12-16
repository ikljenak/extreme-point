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
import com.thesis.app.models.Individual;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;
import com.thesis.app.utils.OutputWriter;

public class Genetic {

	private double bestFitness = 0;
	private double iterationBestFitness = 0;
	private final List<Item> items = new LinkedList<Item>();

	public Result run() throws IOException, InterruptedException {
		initializeItems();
		long startTime = System.currentTimeMillis();
		List<Individual> population = getPossibleBoxes(getVolume(items));
		int generationsNotImproved = 0;

		// Run evolutionary algorithm until a given number of generations do not
		// show an increase in the best fitness value of the population
		while (generationsNotImproved < Configuration.FINAL_CONDITION) {
			iterationBestFitness = 0;

			// Generate pools of threads with as much threads as individuals in
			// the population
			ExecutorService es = Executors
					.newFixedThreadPool(Configuration.POPULATION_SIZE);

			// Assign a thread to every individual and command the execution of
			// the packing routine with a copy of the set of items
			for (Individual individual : population) {
				List<Item> aux = new LinkedList<Item>();
				for (Item item : items) {
					aux.add(new Item(item));
				}
				es.execute(new PackEvolutionary(individual, aux));
			}

			// Prevent the launching of new threads and wait for termination of
			// running ones
			es.shutdown();
			boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);

			// Calculate best fitness of the current generation and compare it
			// to global best fitness to verify if there was any improvement
			if (finished) {
				calculateIterationBestFitness(population);
			}
			if (iterationBestFitness > bestFitness) {
				bestFitness = iterationBestFitness;
				generationsNotImproved = 0;
			} else {
				generationsNotImproved++;
			}
			
			// Create new generation
			population = newGeneration(population);
		}

		long endTime = System.currentTimeMillis();
		Individual finalSolution = getBestSolution(population);
		pack(finalSolution, Configuration.VISUAL_OUTPUT);

		return new Result(endTime - startTime,
				finalSolution.calculateAmountOfBoxes(),
				finalSolution.calculateCost(), finalSolution.getUsedVolume(),
				finalSolution.getItemsPacked());
	}

	private void calculateIterationBestFitness(List<Individual> population) {
		for (Individual solution : population) {
			double fitness = solution.getFitness();
			if (fitness > iterationBestFitness) {
				iterationBestFitness = fitness;
			}
		}

	}

	private double pack(Individual solution, boolean print)
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

		BigDecimal totalVolume = new BigDecimal(0);
		List<Container> emptyContainers = new ArrayList<Container>();
		for (Container container : containers) {
			if (container.getItemsPacked() == 0) {
				emptyContainers.add(container);
			} else {
				totalVolume = totalVolume.add(container.getVolume());
			}
		}
		containers.removeAll(emptyContainers);

		solution.setTotalVolume(totalVolume);
		solution.setItemsPacked(itemsPacked);

		if (print) {
			OutputWriter.ContainersToXyzFormat(containers,
					Configuration.OUTPUT_FILE);
		}
		return solution.getFitness();
	}

	private Individual getBestSolution(List<Individual> solutions) {
		Collections.sort(solutions, new SolutionFitnessComparator());
		return solutions.get(0);
	}

	/**
	 * Evolves a population into the next generation
	 * 
	 * @param oldGeneration
	 * @return list of members of the population in the next generation
	 */
	private List<Individual> newGeneration(
			List<Individual> oldGeneration) {
		// Members of the population are sorted according to their fitness
		Collections.sort(oldGeneration, new SolutionFitnessComparator());

		List<Individual> newGeneration = new ArrayList<Individual>();

		// Elite size is calculated
		int eliteSize = (int) (Configuration.POPULATION_SIZE * Configuration.ELITE_PERCENTAGE);

		// Elite members of the old generation directly pass to the new one
		newGeneration.addAll(oldGeneration.subList(0, eliteSize));

		for (int i = 0; i < eliteSize; i++) {
			// Random members of the rest of the population are selected
			Individual solution = oldGeneration.get((new Random())
					.nextInt(oldGeneration.size() - eliteSize) + eliteSize);

			// Every randomly selected member is crossed with each elite member
			for (int k = 0; k < eliteSize; k++) {
				solution = solution.cross(oldGeneration.get(k));
				// the offspring is included in the next generation
				newGeneration.add(solution);
			}
		}
		return newGeneration;
	}

	/**
	 * Reads from a csv file the specifications of a set of items and generates
	 * the models
	 * 
	 * @throws IOException
	 */
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

	/**
	 * Calculates the total volume of a list of items by adding up the volumes
	 * of every individual item
	 * 
	 * @param items
	 * @return BigDecimal total volume
	 */
	private BigDecimal getVolume(List<Item> items) {
		BigDecimal volume = new BigDecimal(0);

		for (Item item : items) {
			volume = volume.add(item.getVolume());
		}

		return volume;
	}

	/**
	 * Generates a list of possible combinations of boxes to perform a packing
	 * of items that add up to a given volume
	 * 
	 * @param volume
	 *            of the list of items
	 * @return List<SolutionGenetic> list of possible solutions
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private List<Individual> getPossibleBoxes(BigDecimal volume)
			throws JsonParseException, JsonMappingException, IOException {
		return generateInitialSolutions(volume);
	}

	private List<Individual> generateInitialSolutions(BigDecimal volume) {
		List<Individual> ans = new ArrayList<Individual>();

		for (int i = 0; i < Configuration.POPULATION_SIZE; i++) {
			Individual solution = new Individual(volume);
			ans.add(solution);
		}

		return ans;
	}
}
