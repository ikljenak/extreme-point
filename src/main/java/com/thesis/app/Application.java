package com.thesis.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.thesis.app.models.Result;
import com.thesis.app.utils.Configuration;

public class Application {
	private static long[] time;
	private static int[] amountOfContainers;
	private static double[] cost;
	private static double[] usedVolume;
	private static int[] itemsPacked;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		initialConfiguration(args[0]);
		time = new long[Configuration.ITERATIONS_NUMBER];
		amountOfContainers = new int[Configuration.ITERATIONS_NUMBER];
		cost = new double[Configuration.ITERATIONS_NUMBER];
		usedVolume = new double[Configuration.ITERATIONS_NUMBER];
		itemsPacked = new int[Configuration.ITERATIONS_NUMBER];
		for (double n = 0; n < 3; n += .25) {
			for (double j = 0; j < 3; j += .25) {
				for (double k = 0; k < 3; k += .25) {
					System.out.println(n + " " + j + " " + k);
					Configuration.FITNESS_A = n;
					Configuration.FITNESS_B = j;
					Configuration.FITNESS_C = k;
					for (int i = 0; i < Configuration.ITERATIONS_NUMBER; i++) {
						Result result = run();
						time[i] = result.getTime();
						amountOfContainers[i] = result.getAmountOfContainers();
						cost[i] = result.getCost();
						usedVolume[i] = result.getUsedVolume();
						itemsPacked[i] = result.getItemsPacked();

						System.out.println(time[i] + ","
								+ amountOfContainers[i] + "," + cost[i] + ","
								+ usedVolume[i] + "," + itemsPacked[i]);
					}
					System.out.println();
				}
			}
		}
		for (int i = 0; i < Configuration.ITERATIONS_NUMBER; i++) {
			Result result = run();
			time[i] = result.getTime();
			amountOfContainers[i] = result.getAmountOfContainers();
			cost[i] = result.getCost();
			usedVolume[i] = result.getUsedVolume();
			itemsPacked[i] = result.getItemsPacked();

			System.out.println(time[i] + "," + amountOfContainers[i] + ","
					+ cost[i] + "," + usedVolume[i] + "," + itemsPacked[i]);
		}
	}

	private static Result run() throws IOException, InterruptedException {
		String containerSelectionMethod = Configuration.CONTAINER_SELECTION_METHOD;
		switch (containerSelectionMethod) {
		case "brute": {
			BruteForce bf = new BruteForce();
			return bf.run();
		}
		case "genetic":
		default: {
			Genetic g = new Genetic();
			return g.run();
		}
		}
	}

	private static void initialConfiguration(String propertiesFile)
			throws IOException {
		Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(propertiesFile);
		properties.load(is);
		Configuration.setProperties(properties);
	}
}
