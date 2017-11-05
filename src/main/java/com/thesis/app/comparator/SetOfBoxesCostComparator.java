package com.thesis.app.comparator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.app.App;
import com.thesis.app.models.Container;

public class SetOfBoxesCostComparator implements Comparator<List<Integer>> {
	
	private Map<Integer, Double> costs = new HashMap<Integer, Double>();
	private static final int AMOUNT_OF_BOXES = 6;
	
	public SetOfBoxesCostComparator() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < AMOUNT_OF_BOXES; i++) {
			String json = IOUtils.toString(App.class.getClassLoader()
					.getResourceAsStream("box" + (i + 1) + ".json"), Charset
					.defaultCharset());
			costs.put(i+1, mapper.readValue(json, Container.class).getCost());
		}
	}

	public int compare(List<Integer> o1, List<Integer> o2) {
		return (int)Math.signum(getCost(o1) - getCost(o2));
	}
	
	private double getCost(List<Integer> containers) {
		double cost = 0;
		for(Integer c:containers) {
			cost += costs.get(c);
		}
		return cost;
	}

}
