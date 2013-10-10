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
		invalidateCachedFitness();
		individual.invalidateCachedFitness();
		
		Gene ourGene = this.genes.get(geneIndex);
		Gene theirGene = individual.genes.get(geneIndex);
		
		this.genes.set(geneIndex, theirGene);
		individual.genes.set(geneIndex, ourGene);
	}
	
	public void averageGene(Individual individual, int geneIndex) {
		invalidateCachedFitness();
		individual.invalidateCachedFitness();
		
		Gene ourGene = this.genes.get(geneIndex);
		Gene theirGene = individual.genes.get(geneIndex);
		
		double averageValue = (ourGene.getValue() + theirGene.getValue()) / 2.0;
		ourGene.setValue(averageValue);
		theirGene.setValue(averageValue);
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
		if(this.cachedFitness == null) {
			double[] geneArray = new double[10];
			int geneCount = this.genes.size();
			for (int i = 0; i < geneCount; i++) {
				Gene g = this.genes.get(i);
				geneArray[i] = g.getValue();
			}
			this.cachedFitness = (Double)evaluation.evaluate(geneArray);
			if(this.cachedFitness == null) {
				throw new MaximumEvaluationsExceededException();
			}
		} 
		
		return this.cachedFitness;
	}
	
	
}
