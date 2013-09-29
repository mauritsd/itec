package org.vu.contest.team24;

import org.vu.contest.team24.model.Population;

public interface EvolutionStrategy {
	public void evolveGeneration();
	public boolean shouldTerminate();
	public Population getCurrentPopulation();
	public double getBestFitness();
}
