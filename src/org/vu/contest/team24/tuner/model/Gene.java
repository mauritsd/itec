package org.vu.contest.team24.tuner.model;

public interface Gene {
	public void mutate();
	public void crossover(Gene otherGene);
	public Object getValue();
	
}
