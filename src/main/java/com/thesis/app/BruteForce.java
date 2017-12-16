package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thesis.app.comparator.SetOfBoxesCostComparator;
import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Result;
import com.thesis.app.models.Solution;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;
import com.thesis.app.utils.OutputWriter;

public class BruteForce {
	private final List<Item> items = new LinkedList<Item>();
	private Collection<Container> boxes;
	private final int THREADS = 20;
	private Solution bestSolution = null;
	private double bestFitness = Double.MAX_VALUE;
	private List<Solution> solutions = new ArrayList<Solution>();

	public Result run() throws IOException, InterruptedException {
		initializeItems();
		boxes = BoxHelper.getInstance().getAllBoxes();
		long startTime = System.currentTimeMillis();
		Set<List<Integer>> possibleCombinationsOfContainers = getPossibleBoxes(getVolume(items));

		// Generate pools of threads with a fixed number of threads
		ExecutorService es = Executors.newFixedThreadPool(THREADS);

		// Iterate over a set of possible combinations of containers. Every
		// combination is a list of numbers representing one container.
		// Assign a thread to every individual and command the execution of
		// the packing routine with a copy of the set of items
		for (List<Integer> containerIndexes : possibleCombinationsOfContainers) {
			Solution containers = modelContainers(containerIndexes);		
			es.execute(new PackBrute(containers, items));
		}

		// Prevent the launching of new threads and wait for termination of
		// running ones
		es.shutdown();
		boolean finished = es.awaitTermination(10, TimeUnit.MINUTES);

		// Find best solution
		if (finished) {
			bestSolution(solutions);
		}

		long endTime = System.currentTimeMillis();

		if (Configuration.VISUAL_OUTPUT) {
			OutputWriter.ContainersToXyzFormat(bestSolution.getContainers(),
					Configuration.OUTPUT_FILE);
		}

		return new Result(endTime - startTime, bestSolution.getContainers()
				.size(), bestFitness, bestSolution.getUsedVolume(),
				bestSolution.getItemsPacked());
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
	
	private Solution modelContainers(List<Integer> containersIndexes) {
		List<Container> containers = new ArrayList<Container>();
		for (Integer i : containersIndexes) {
			containers.add(BoxHelper.getInstance().getBox(i).copy());
		}
		Solution aux = new Solution(containers);
		solutions.add(aux);
		return aux;
	}
	
	private void bestSolution(List<Solution> solutions) {
		for (Solution solution : solutions) {
			if (solution.getItemsPacked() == items.size()
					&& solution.getCost() < bestFitness) {
				bestSolution = solution;
				bestFitness = solution.getCost();
			}
		}
	}

	private BigDecimal getVolume(List<Item> items) {
		double volume = 0;
		for (Item item : items) {
			volume += item.getVolume().doubleValue();
		}
		return new BigDecimal(volume);
	}

	private Set<List<Integer>> getPossibleBoxes(BigDecimal volume)
			throws JsonParseException, JsonMappingException, IOException {
		Set<List<Integer>> ans = new TreeSet<List<Integer>>(
				new SetOfBoxesCostComparator());

		getPossibleBoxesRecursive(ans, new ArrayList<Integer>(),
				new BigDecimal(0), volume, 0);
		return ans;
	}

	private void getPossibleBoxesRecursive(Set<List<Integer>> ans,
			List<Integer> containers, BigDecimal volumeSoFar,
			BigDecimal totalVolume, int iteration) {
		if (volumeSoFar.compareTo(totalVolume) > 0
				&& volumeSoFar.compareTo(totalVolume.multiply(new BigDecimal(
						Configuration.VOLUME_FACTOR))) < 0) {
			List<Integer> aux = new ArrayList<Integer>();

			for (Integer c : containers) {
				aux.add(Integer.valueOf(c));
			}

			ans.add(aux);
		}

		if (volumeSoFar.compareTo(totalVolume.multiply(new BigDecimal(
				Configuration.VOLUME_FACTOR))) >= 0) {
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