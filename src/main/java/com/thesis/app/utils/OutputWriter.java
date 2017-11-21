package com.thesis.app.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

import com.thesis.app.models.Container;
import com.thesis.app.models.Item;

public class OutputWriter {
	private static final double STEP = 0.1;

	public static void ContainersToXyzFormat(List<Container> containers,
			String file) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		for (Container container : containers) {
			int count = 8;
			for (Item item : container.getItems()) {
				double width = item.getWidth();
				double depth = item.getDepth();
				double height = item.getHeight();
				for (double i = 0.0; i < width; i += STEP) {
					for (double j = 0.0; j < depth; j += STEP) {
						for (double k = 0.0; k < height; k += STEP) {
							count++;
						}
					}
				}
			}

			writer.println(count);
			writer.println();
			// for every item print: x y z colorR colorG colorB
			for (Item item : container.getItems()) {
				double x = item.getPosition().getX();
				double y = item.getPosition().getY();
				double z = item.getPosition().getZ();
				double r = Math.random();
				double g = Math.random();
				double b = Math.random();
				double width = item.getWidth();
				double depth = item.getDepth();
				double height = item.getHeight();
				for (double i = 0.0; i < width; i += STEP) {
					for (double j = 0.0; j < depth; j += STEP) {
						for (double k = 0.0; k < height; k += STEP) {
							DecimalFormat df = new DecimalFormat("####0.00");
							writer.println(df.format(x + i) + " " + df.format(y + j) + " "
									+ df.format(z + k) + " " + r + " " + g + " " + b);
						}
					}
				}
			}

			writer.println("0 0 0 0 0 0");
			writer.println("0 " + container.getDepth() + " 0 0 0 0");
			writer.println(container.getWidth() + " 0 0 0 0 0");
			writer.println("0 0 " + container.getHeight() + " 0 0 0");
			writer.println("0 " + container.getDepth() + " "
					+ container.getHeight() + " 0 0 0");
			writer.println(container.getWidth() + " 0 "
					+ container.getHeight() + " 0 0 0");
			writer.println(container.getWidth() + " " + container.getDepth()
					+ " 0 0 0 0");
			writer.println(container.getWidth() + " " + container.getDepth() + " "
					+ container.getHeight() + " 0 0 0");
		}
		writer.close();
	}

}
