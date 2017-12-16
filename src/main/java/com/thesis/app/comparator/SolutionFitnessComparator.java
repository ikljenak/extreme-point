package com.thesis.app.comparator;

import java.util.Comparator;

import com.thesis.app.models.Individual;

public class SolutionFitnessComparator implements Comparator<Individual> {

	public int compare(Individual solution1, Individual solution2) {
		return (int)Math.signum(solution2.getFitness() - solution1.getFitness());
	}
}
