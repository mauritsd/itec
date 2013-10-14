package org.vu.contest.team24.tuner;

import java.util.Comparator;
import java.util.Map;

import org.vu.contest.team24.tuner.model.Individual;

public class FitnessComparator implements Comparator<Individual> {
	private Map<Individual, Double> fitnesses;
	
	public FitnessComparator(Map<Individual, Double> fitnesses) {
		this.fitnesses = fitnesses;
	}
	
	@Override
	public int compare(Individual i1, Individual i2) {
		double fitnessOne = this.fitnesses.get(i1);
		double fitnessTwo = this.fitnesses.get(i2);				

		if(fitnessOne == fitnessTwo) {
			return 0;
		} else if(fitnessOne < fitnessTwo) {
			return 1;
		} else {
			return -1;
		}
	}

}