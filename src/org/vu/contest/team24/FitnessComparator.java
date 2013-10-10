package org.vu.contest.team24;

import java.util.Comparator;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.model.Individual;

public class FitnessComparator implements Comparator<Individual> {
	private ContestEvaluation evaluation;
	
	public FitnessComparator(ContestEvaluation evaluation) {
		this.evaluation = evaluation;
	}
	
	@Override
	public int compare(Individual i1, Individual i2) {
		try {
			double fitnessOne = i1.getFitness(this.evaluation);
			double fitnessTwo = i2.getFitness(this.evaluation);				

			if(fitnessOne == fitnessTwo) {
				return 0;
			} else if(fitnessOne < fitnessTwo) {
				return 1;
			} else {
				return -1;
			}
		} catch (MaximumEvaluationsExceededException e) {
			throw new IllegalStateException("exceeded maximum evaluations in a place where this shouldn't happen!");
		}
	}

}
