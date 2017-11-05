package com.thesis.app.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TestSetGenerator {
	private final static int itemsAmount = 100;

	private final static double width = 5;
	private final static double depth = 5;
	private final static double height = 5;

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("src/main/resources/input"
				+ itemsAmount + "-" + width + "-" + depth + "-" + height +".txt", "UTF-8");
		for (int i = 0; i < itemsAmount; i++) {
			writer.println("0.0 " + Math.random() * width + " " + Math.random()
					* depth + " " + Math.random() * height);
		}
		writer.close();
	}
}
