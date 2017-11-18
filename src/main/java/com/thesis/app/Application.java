package com.thesis.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.thesis.app.models.Result;
import com.thesis.app.utils.Configuration;

public class Application {
	private static long time;
	private static int amountOfContainers;
	private static double cost;
	private static double usedVolume;
	private static int itemsPacked;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		initialConfiguration();
		for (int i = 0; i < Configuration.ITERATIONS_NUMBER; i++) {
			Result result = run();
			time += result.getTime();
			amountOfContainers += result.getAmountOfContainers();
			cost += result.getCost();
			usedVolume += result.getUsedVolume();
			itemsPacked += result.getItemsPacked();
		}

		System.out.println("TIME: " + time / Configuration.ITERATIONS_NUMBER);
		System.out.println("AMOUNT OF CONTAINERS: " + amountOfContainers
				/ Configuration.ITERATIONS_NUMBER);
		System.out.println("COST: " + cost / Configuration.ITERATIONS_NUMBER);
		System.out.println("USED VOLUME: " + usedVolume / Configuration.ITERATIONS_NUMBER);
		System.out.println("ITEMS PACKED: " + itemsPacked / Configuration.ITERATIONS_NUMBER);
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

	private static void initialConfiguration() throws IOException {
		Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("config.properties");
		properties.load(is);
		Configuration.setProperties(properties);
	}
}
