package org.vu.contest.team24;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.model.Gene;
import org.vu.contest.team24.model.Individual;
import org.vu.contest.team24.model.Population;


public class SimpleEvolutionaryStrategy implements EvolutionaryStrategy {
	private Population currentPopulation;
	private ContestEvaluation evaluation;
	private boolean shouldTerminate;
	private Random random;
	private float mutationChance;
	private float crossoverChance;
	
	
	public SimpleEvolutionaryStrategy(ContestEvaluation evaluation, float mutationChance, float crossoverChance) {
		this.currentPopulation = new Population(10);
		this.evaluation = evaluation;
		this.random = RandomSingleton.getInstance().getRandom();
		this.mutationChance = mutationChance;
		this.crossoverChance = crossoverChance;
	}
	
	@Override
	public void evolveGeneration() {
		List<Individual> individuals = new ArrayList<Individual>(this.currentPopulation.getIndividualList());
		List<Individual> nextIndividuals = selectParents(individuals);
				
		if(nextIndividuals == null) {
			this.shouldTerminate = true;
			return;
		}
				
		mutationOperator(nextIndividuals);
		crossoverOperator(nextIndividuals);
		
		Object[] objectArray = nextIndividuals.toArray();
		Individual[] individualArray = Arrays.copyOf(objectArray, objectArray.length, Individual[].class);
		this.currentPopulation = new Population(individualArray);
	}
	
	private List<Individual> selectParents(List<Individual> individuals) {
		for(Individual individual : individuals) {
			try {
				individual.getFitness(this.evaluation);
			} catch (MaximumEvaluationsExceededException e) {
				return null;
			}
		}
		
		
		// Sort list to get the ranking.
		individuals = new ArrayList<Individual>(individuals);
		Collections.sort(individuals, new FitnessComparator(this.evaluation));
		
		int populationSize = individuals.size();
		float spacing = 1 / populationSize;
		float position = this.random.nextFloat() * spacing;
		List<Individual> parents = new ArrayList<Individual>(populationSize);
		
		int i = 0;
		int currentParent = 0;
		float currentCumulative = probabilityForRank(i, populationSize, 2);
		while(currentParent < populationSize) {
			while(currentCumulative < position) {
				currentCumulative += probabilityForRank(++i, populationSize, 2);
			}
			
			parents.add(new Individual(individuals.get(i)));
			currentParent++;

			position += spacing;
			// We need to implement wraparound since this is also the case in the (n-armed)
			// roulette wheel...
			if(position > 1.0) {
				position -= 1.0;
				i = 0;
				currentCumulative = probabilityForRank(i, populationSize, 2);
			}
		}
		
		return parents;
	}
	
	private float probabilityForRank(int rank, int size, int expectedOffspring) {
		return ((2 - expectedOffspring) / size) + (((2 * rank) * (expectedOffspring - 1)) / (size * (size - 1)));
	}
	
	private void mutationOperator(List<Individual> individuals) {
		int populationSize = individuals.size();
		
		for(int i = 0; i < populationSize; i++) {
			Individual individual = individuals.get(i);
			for(int j = 0; j < 10; j++) {
				Gene gene = individual.getGene(j);
				if(!(this.random.nextFloat() > this.mutationChance)) {
					gene.mutate(1.0);
					individual.invalidateCachedFitness();
				}
				
			}
		}
	}
	
	private void crossoverOperator(List<Individual> individuals) {
		int populationSize = individuals.size();
		
		for(int i = 0; i < populationSize; i++) {
			Individual individual = individuals.get(i);
			if(!(this.random.nextFloat() > this.crossoverChance)) {
				int j;
				do {
					j = this.random.nextInt(populationSize);
				} while(j == i);
				Individual otherIndividual = individuals.get(j);
				
				int chromosomeSize = individual.getChromosomeSize();
				int crossoverPoint = this.random.nextInt(chromosomeSize + 1);
				
				for(int n = 0; n < chromosomeSize; n++) {
					if(n >= crossoverPoint) {
						individual.swapGene(otherIndividual, n);
					}
				}
			}
		}
		
	}
	
	@Override
	public boolean shouldTerminate() {
		return this.shouldTerminate;
	}
	
	@Override
	public Population getCurrentPopulation() {
		return this.currentPopulation;
	}

	@Override
	public double getBestFitness() {
		double bestFitness = 0.0;
		int populationSize = this.currentPopulation.getSize();
		
		for(int i = 0; i < populationSize; i++) {
			Individual individual = this.currentPopulation.getIndividual(i);
			double fitness;
			try {
				fitness = individual.getFitness(evaluation);
				if(fitness > bestFitness) {
					bestFitness = fitness;
				}
			} catch (MaximumEvaluationsExceededException e) {
				throw new IllegalStateException("exceeded maximum evaluations in a place where this shouldn't happen!");
			}
		}
		return bestFitness;
	}
}
