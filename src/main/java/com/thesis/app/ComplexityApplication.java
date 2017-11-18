package com.thesis.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.thesis.app.models.Container;
import com.thesis.app.models.Item;
import com.thesis.app.models.Result;
import com.thesis.app.models.SolutionBrute;
import com.thesis.app.utils.BoxHelper;
import com.thesis.app.utils.Configuration;

public class ComplexityApplication {

	private static final List<Item> items = new LinkedList<Item>();
	private static long[] time;
	private static int itemsPacked;

	public static void main(String[] args) throws IOException {
		initialConfiguration(args[0]);
		time = new long[Configuration.ITERATIONS_NUMBER];
		for (int i = 0; i < Configuration.ITERATIONS_NUMBER; i++) {
			Result result = run();
			time[i] = result.getTime();
			itemsPacked += result.getItemsPacked();
			System.out.println("TIME " + i + ": " + time[i]);
		}
		System.out.println("ITEMS PACKED: " + itemsPacked
				/ Configuration.ITERATIONS_NUMBER);
	}

	public static Result run() throws IOException {
		initializeItems();
		long startTime = System.currentTimeMillis();

		List<Container> containers = new ArrayList<Container>();
		Container c = BoxHelper.getInstance().getBox(
				Configuration.CONTAINERS_NUMBER - 1);
		containers.add(c.copy());
		SolutionBrute solution = new SolutionBrute(containers);

		for (Item item : items) {
			int index = 0;
			while (index <= containers.size()) {
				if (index == containers.size()) {
					containers.add(c.copy());
				}

				Container container = containers.get(index);
				if (container.pack(item)) {
					solution.setItemsPacked(solution.getItemsPacked() + 1);
					break;
				} else {
					index++;
				}
			}
		}

		long endTime = System.currentTimeMillis();
		return new Result(endTime - startTime, 0, 0, 0,
				solution.getItemsPacked());
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

	private static void initialConfiguration(String propertiesFile) throws IOException {
		Properties properties = new Properties();
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(propertiesFile);
		properties.load(is);
		Configuration.setProperties(properties);
	}
}
