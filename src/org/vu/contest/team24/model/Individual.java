package org.vu.contest.team24.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.MaximumEvaluationsExceededException;
import org.vu.contest.team24.RandomSingleton;


public class Individual {
	private List<Gene> genes;
	private Random random;
	private Double cachedFitness;
	
	public Individual() {
		this.random = RandomSingleton.getInstance().getRandom();
		this.genes = new ArrayList<Gene>(10);
		for(int i=0; i < 10; i++) {
			double value = (10.0 * this.random.nextDouble()) - 5.0;
			this.genes.add(new Gene(value));
		}
	}
	
	public Individual(Gene[] genes) {
		this.random = RandomSingleton.getInstance().getRandom();
		this.genes = new ArrayList<Gene>(10);
		for (Gene gene : genes) {
			this.genes.add(new Gene(gene));
		}
	}
	
	public Individual(Individual individual) {
		this(individual.getGeneArray());
		this.cachedFitness = individual.cachedFitness;
	}
	
	public Gene getGene(int geneIndex) {
		return this.genes.get(geneIndex);
	}
	
	public int getChromosomeSize() {
		return this.genes.size();
	}
	
	public void swapGene(Individual individual, int geneIndex) {
		// Invalidate both our cached fitness as the cached fitness of the
		// individual whose gene we will be mixing with.
		invalidateCachedFitness();
		individual.invalidateCachedFitness();
		
		Gene ourGene = this.genes.get(geneIndex);
		Gene theirGene = individual.genes.get(geneIndex);
		
		// Note that because we are both instances of Individual we are able
		// to access the private instance variables of the other Gene as well.
		this.genes.set(geneIndex, theirGene);
		individual.genes.set(geneIndex, ourGene);
	}
	
	public void mixGene(Individual individual, int geneIndex) {
		this.mixGene(individual, geneIndex, 0.5);
	}
	
	public void mixGene(Individual individual, int geneIndex, double mixingRatio) {
		// Invalidate both our cached fitness as the cached fitness of the
		// individual whose gene we will be mixing with.
		invalidateCachedFitness();
		individual.invalidateCachedFitness();
		
		Gene ourGene = this.genes.get(geneIndex);
		Gene theirGene = individual.genes.get(geneIndex);
		
		// Mix the value of this gene and the other gene given the specified mixing
		// ratio. Note that because we are both instances of Individual we are able
		// to access the private instance variables of the other Gene as well.
		double mixedValue = ((ourGene.getValue() * mixingRatio) + (theirGene.getValue() * (1.0 - mixingRatio)));
		ourGene.setValue(mixedValue);
		theirGene.setValue(mixedValue);
	}
	
	public Gene[] getGeneArray() {
		Gene[] geneArray = new Gene[10];
		int geneCount = this.genes.size();
		for (int i = 0; i < geneCount; i++) {
			Gene g = this.genes.get(i);
			geneArray[i] = g;
		}
		return geneArray;
	}
	
	public void invalidateCachedFitness() {
		this.cachedFitness = null;
	}
	
	public double getFitness(ContestEvaluation evaluation) throws MaximumEvaluationsExceededException {
		// If the fitness is not cached try to generate it by
		// passing it to the ContestEvaluation.
		if(this.cachedFitness == null) {
			double[] geneArray = new double[10];
			int geneCount = this.genes.size();
			for (int i = 0; i < geneCount; i++) {
				Gene g = this.genes.get(i);
				geneArray[i] = g.getValue();
			}
			this.cachedFitness = (Double)evaluation.evaluate(geneArray);
			
			// Uh oh! The ContestEvaluation returned null, which means we've exceeded
			// our evaluation budget. Throw the MaximumEvaluationsExceededException, which
			// in turn will signal to the caller that he should terminate his EA.
			if(this.cachedFitness == null) {
				throw new MaximumEvaluationsExceededException();
			}
		} 
		
		return this.cachedFitness;
	}
	
	
}
