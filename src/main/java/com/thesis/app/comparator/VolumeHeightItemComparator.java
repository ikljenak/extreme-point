package com.thesis.app.comparator;

import java.math.BigDecimal;
import java.util.Comparator;

import com.thesis.app.models.Item;

public class VolumeHeightItemComparator implements Comparator<Item> {

	public int compare(Item o1, Item o2) {
		BigDecimal volume1 = new BigDecimal(o1.getWidth()).multiply(
				new BigDecimal(o1.getDepth())).multiply(
				new BigDecimal(o1.getHeight()));
		BigDecimal volume2 = new BigDecimal(o2.getWidth()).multiply(
				new BigDecimal(o2.getDepth())).multiply(
				new BigDecimal(o2.getHeight()));
		if (volume1.subtract(volume2).doubleValue() == 0) {
			return (int) Math.signum(o1.getHeight() - o2.getHeight());
		}
		return (int) Math.signum(volume1.subtract(volume2).doubleValue());
	}

}
