package com.thesis.app.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.app.Application;
import com.thesis.app.models.Container;

public class BoxHelper {
	private Map<Integer, Container> boxes = new HashMap<Integer, Container>();
	private static BoxHelper instance = new BoxHelper();
	
	private BoxHelper() {
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < Configuration.CONTAINERS_NUMBER; i++) {
			String json;
			try {
				json = IOUtils.toString(Application.class.getClassLoader()
						.getResourceAsStream("box" + (i + 1) + ".json"), Charset
						.defaultCharset());
				this.addBox(i + 1, mapper.readValue(json, Container.class));
			} catch (IOException e) {
				System.out.println("There was an error initializing boxes");
			}
		}
	}
	
	public static BoxHelper getInstance() {
		return instance;
	}
	
	private void addBox(int number, Container box) {
		this.boxes.put(number, box);
	}
	
	public Container getBox(int number) {
		return this.boxes.get(number);
	}
	
	public Collection<Container> getAllBoxes() {
		return boxes.values();
	}
	
	public double getBoxCost(int number) {
		return boxes.get(number).getCost();
	}

}
