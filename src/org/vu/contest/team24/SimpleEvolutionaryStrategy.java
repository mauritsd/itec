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
	private int populationSize;
	private boolean shouldTerminate;
	private Random random;
	private double mutationChance;
	private double mutationStandardDeviation;
	private double mutationChanceScalingFactor;
	private double mutationStandardDeviationScalingFactor;
	private double crossoverChance;
	private double crossoverChanceScalingFactor;
	private double fittestExpectedOffspring;
	
	public SimpleEvolutionaryStrategy(ContestEvaluation evaluation) {
		this.evaluation = evaluation;
		this.random = RandomSingleton.getInstance().getRandom();
	}
	
	public int getPopulationSize() {
		return this.populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public double getMutationChance() {
		return this.mutationChance;
	}

	public void setMutationChance(double mutationChance) {
		this.mutationChance = mutationChance;
	}

	public double getMutationStandardDeviation() {
		return this.mutationStandardDeviation;
	}

	public void setMutationStandardDeviation(double mutationStandardDeviation) {
		this.mutationStandardDeviation = mutationStandardDeviation;
	}
	
	
	public double getMutationStandardDeviationScalingFactor() {
		return this.mutationStandardDeviationScalingFactor;
	}

	public void setMutationStandardDeviationScalingFactor(
			double mutationStandardDeviationScalingFactor) {
		this.mutationStandardDeviationScalingFactor = mutationStandardDeviationScalingFactor;
	}

	public double getCrossoverChance() {
		return this.crossoverChance;
	}

	public double getMutationChanceScalingFactor() {
		return this.mutationChanceScalingFactor;
	}

	public void setMutationChanceScalingFactor(double mutationChanceScalingFactor) {
		this.mutationChanceScalingFactor = mutationChanceScalingFactor;
	}

	public void setCrossoverChance(double crossoverChance) {
		this.crossoverChance = crossoverChance;
	}

	public double getCrossoverChanceScalingFactor() {
		return this.crossoverChanceScalingFactor;
	}

	public void setCrossoverChanceScalingFactor(double crossoverChanceScalingFactor) {
		this.crossoverChanceScalingFactor = crossoverChanceScalingFactor;
	}
	
	public double getFittestExpectedOffspring() {
		return this.fittestExpectedOffspring;
	}

	public void setFittestExpectedOffspring(double fittestExpectedOffspring) {
		this.fittestExpectedOffspring = fittestExpectedOffspring;
	}

	public double getScaledMutationChance(double fitness) {
		if(fitness < 0.0) {
			return 1.0;
		} else {
			return Math.pow(this.mutationChanceScalingFactor, fitness) * this.mutationChance;
		}
	}
	
	public double getScaledMutationStandardDeviation(double fitness) {
		if(fitness < 0.0) {
			return 1.0;
		} else {
			return Math.pow(this.mutationStandardDeviationScalingFactor, fitness) * this.mutationStandardDeviation;
		}
	}
	
	public double getScaledCrossoverChance(double fitness) {
		if(fitness < 0.0) {
			return 1.0;
		} else {
			return Math.pow(this.crossoverChanceScalingFactor, fitness) * this.crossoverChance;
		}		
	}
	
	@Override
	public void evolveGeneration() {
		// If we're at the first generation then we need to create it.
		if(this.currentPopulation == null) {
			this.currentPopulation = new Population(this.populationSize);
		}
		
		// Copy the list of current individuals and apply parent selection to get the list of parents.
		List<Individual> individuals = new ArrayList<Individual>(this.currentPopulation.getIndividualList());
		List<Individual> nextIndividuals = selectParents(individuals);
		
		// If nextIndividuals is null this means selectParents() has burned through the evaluation budget.
		// This means we should signal the caller that it should terminate.
		if(nextIndividuals == null) {
			this.shouldTerminate = true;
			return;
		}
		
		// Get fitnesses for all individuals since we need them for parameter scaling.
		// Note that this does not burn through the evaluation budget because all the fitnesses
		// were generated and cached during the parent selection.
		List<Double> fitnesses = new ArrayList<Double>(nextIndividuals.size());
		for (Individual individual : nextIndividuals) {
			try {
				fitnesses.add(individual.getFitness(this.evaluation));
			} catch (MaximumEvaluationsExceededException e) {
				throw new IllegalStateException("exceeded maximum evaluations in a place where this shouldn't happen!");
			}
		}
		
		// Apply mutation and crossover operators to the parents.
		mutationOperator(nextIndividuals, fitnesses);
		crossoverOperator(nextIndividuals, fitnesses);
		
		// Copy the mutated/crossed parents to be the current iteration.
		Object[] objectArray = nextIndividuals.toArray();
		Individual[] individualArray = Arrays.copyOf(objectArray, objectArray.length, Individual[].class);
		this.currentPopulation = new Population(individualArray);
	}
	
	private List<Individual> selectParents(List<Individual> individuals) {
		// Make sure that if we get a MaximumEvaluationsExceededException that we get it
		// here rather than at some other unexpected point. This means we can safely treat
		// a MaximumEvaluationsExceededException in most other places as an error condition.
		for(Individual individual : individuals) {
			try {
				individual.getFitness(this.evaluation);
			} catch (MaximumEvaluationsExceededException e) {
				return null;
			}
		}
		
		// Sort individuals according to fitness to get the ranking.
		individuals = new ArrayList<Individual>(individuals);
		Collections.sort(individuals, new FitnessComparator(this.evaluation));
		
		
		// Apply the SUS algorithm to select parents based on the linear rank probability.
		int populationSize = individuals.size();
		double spacing = 1.0 / populationSize;
		double position = this.random.nextDouble() * spacing;
		List<Individual> parents = new ArrayList<Individual>(populationSize);
		
		int i = 0;
		int currentParent = 0;
		double currentCumulative = probabilityForRank(i, populationSize, this.fittestExpectedOffspring);
		while(currentParent < populationSize) {
			while(currentCumulative < position) {
				currentCumulative += probabilityForRank(++i, populationSize, this.fittestExpectedOffspring);
			}
			
			parents.add(new Individual(individuals.get(i)));
			currentParent++;

			position += spacing;
			// We need to wraparound here since this is also the case in the (n-armed)
			// roulette wheel...
			if(position > 1.0) {
				position -= 1.0;
				i = 0;
				currentCumulative = probabilityForRank(i, populationSize, this.fittestExpectedOffspring);
			}
		}
		
		return parents;
	}
	
	private double probabilityForRank(int rank, int size, double expectedOffspring) {		
		rank = size - rank - 1;

		// Linear ranking algorithm as in the book. expectedOffspring determines the
		// amount of expected selections for the fittest individual (ie, the individual with the highest rank).
		return ((2.0 - expectedOffspring) / (double)size) + (((2.0 * (double)rank) * (expectedOffspring - 1.0)) / ((double)size * ((double)size - 1.0)));
	}
	
	private void mutationOperator(List<Individual> individuals, List<Double> fitnesses) {
		int populationSize = individuals.size();
		
		for(int i = 0; i < populationSize; i++) {			
			Individual individual = individuals.get(i);
			double mutationChance = getScaledMutationChance(fitnesses.get(i));
			double mutationStandardDeviation = getScaledMutationStandardDeviation(fitnesses.get(i));
			if(!(this.random.nextDouble() > mutationChance)) {
				Gene gene = individual.getGene(this.random.nextInt(10));
				
				gene.mutate(mutationStandardDeviation);
				
				// Our mutation has caused the individuals' fitness to change, so invalidate
				// its cached fitness.
				individual.invalidateCachedFitness();
			}
		}
	}
	
	private void crossoverOperator(List<Individual> individuals, List<Double> fitnesses) {
		int populationSize = individuals.size();
		
		for(int i = 0; i < populationSize; i++) {
			Individual individual = individuals.get(i);
			double crossoverChance = getScaledCrossoverChance(fitnesses.get(i));
			
			if(!(this.random.nextDouble() > crossoverChance)) {
				int j;
				do {
					j = this.random.nextInt(populationSize);
				} while(j == i);
				Individual otherIndividual = individuals.get(j);
				
				int chromosomeSize = individual.getChromosomeSize();
				int crossoverPoint = this.random.nextInt(chromosomeSize + 1);
				
				for(int n = 0; n < chromosomeSize; n++) {
					if(n >= crossoverPoint) {
						double mixingRatio = this.random.nextDouble();
						
						
						// We have chosen to use mixing here instead of swapping.
						// This could be configurable by the tuner in the ideal case
						// but we chose not to go this way. The mixing operation will
						// invalidate the cached fitness for us so we don't have to
						// do that here explicitly.
						individual.mixGene(otherIndividual, n, mixingRatio);
						//individual.swapGene(otherIndividual, n);
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
