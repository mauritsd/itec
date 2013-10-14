package org.vu.contest.team24.tuner;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.EvolutionaryStrategy;

public class ParameterEvaluator implements Runnable {
	private ContestEvaluation evaluation;
	private EvolutionaryStrategy strategy;
	private double fitness;
	private boolean done;
	
	public ParameterEvaluator(ContestEvaluation evaluation, EvolutionaryStrategy strategy) {
		this.evaluation = evaluation;
		this.strategy = strategy;
	}
	
	@Override
	public void run() {
		while(!(this.strategy.shouldTerminate())) {
			this.strategy.evolveGeneration();
		}
		
		this.fitness = this.evaluation.getFinalResult();
		this.done = true;
	}
	
	public double getFitness() {
		return this.fitness;
	}
	
	public boolean isDone() {
		return this.done;
	}
}
