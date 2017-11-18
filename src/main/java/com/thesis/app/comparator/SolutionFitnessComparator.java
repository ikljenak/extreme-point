package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.SolutionGenetic;

public class SolutionFitnessComparator implements Comparator<SolutionGenetic> {

	public int compare(SolutionGenetic solution1, SolutionGenetic solution2) {
		return (int)Math.signum(solution2.getFitness() - solution1.getFitness());
	}
}
