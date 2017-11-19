package com.thesis.app.utils;

import java.util.Properties;

public class Configuration {
	public static int CONTAINERS_NUMBER;
	public static int POPULATION_SIZE;
	public static double ELITE_PERCENTAGE;
	public static double ELITE_PROBABILITY;
	public static double MUTATION_PROBABILITY;
	public static double MUTATION_FACTOR;
	public static int FINAL_CONDITION;
	public static String INPUT_FILE;
	public static int INPUT_SIZE;
	public static String OUTPUT_FILE;
	public static String PACKING_METHOD;
	public static double VOLUME_FACTOR;
	public static int BRUTE_FORCE_MAX_DEPTH;
	public static String CONTAINER_SELECTION_METHOD;
	public static int ITERATIONS_NUMBER;
	public static boolean VISUAL_OUTPUT;
	
	public static void setProperties(Properties properties){
		CONTAINERS_NUMBER = Integer.valueOf(properties.getProperty("containers.number"));
		FINAL_CONDITION = Integer.valueOf(properties.getProperty("final.condition"));
		POPULATION_SIZE = Integer.valueOf(properties.getProperty("population.size"));
		ELITE_PERCENTAGE = Double.valueOf(properties.getProperty("elite.percentage"));
		ELITE_PROBABILITY = Double.valueOf(properties.getProperty("elite.probability"));
		MUTATION_PROBABILITY = Double.valueOf(properties.getProperty("mutation.probability"));
		MUTATION_FACTOR = Double.valueOf(properties.getProperty("mutation.factor"));
		INPUT_FILE = properties.getProperty("input.file");
		INPUT_SIZE = Integer.valueOf(properties.getProperty("input.size"));
		OUTPUT_FILE = properties.getProperty("output.file");
		PACKING_METHOD = properties.getProperty("packing.method");
		VOLUME_FACTOR = Double.valueOf(properties.getProperty("volume.factor"));
		BRUTE_FORCE_MAX_DEPTH = Integer.valueOf(properties.getProperty("bruteforce.maxdepth"));
		CONTAINER_SELECTION_METHOD = properties.getProperty("container.selection.method");
		ITERATIONS_NUMBER = Integer.valueOf(properties.getProperty("iterations.number"));
		VISUAL_OUTPUT = Boolean.valueOf(properties.getProperty("visual.output"));
	}
}
