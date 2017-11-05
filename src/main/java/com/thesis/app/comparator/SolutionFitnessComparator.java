package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Solution;

public class SolutionFitnessComparator implements Comparator<Solution> {

	public int compare(Solution solution1, Solution solution2) {
		return (int)Math.signum(solution2.fitness() - solution1.fitness());
	}

}
