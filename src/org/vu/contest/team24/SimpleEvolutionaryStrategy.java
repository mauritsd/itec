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
		if(this.currentPopulation == null) {
			this.currentPopulation = new Population(this.populationSize);
		}
		
		List<Individual> individuals = new ArrayList<Individual>(this.currentPopulation.getIndividualList());
		List<Individual> nextIndividuals = selectParents(individuals);
				
		if(nextIndividuals == null) {
			this.shouldTerminate = true;
			return;
		}
		
		List<Double> fitnesses = new ArrayList<Double>(nextIndividuals.size());
		for (Individual individual : nextIndividuals) {
			try {
				fitnesses.add(individual.getFitness(this.evaluation));
			} catch (MaximumEvaluationsExceededException e) {
				throw new IllegalStateException("exceeded maximum evaluations in a place where this shouldn't happen!");
			}
		}
		
		mutationOperator(nextIndividuals, fitnesses);
		crossoverOperator(nextIndividuals, fitnesses);
		
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
		double spacing = 1 / populationSize;
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
			// We need to implement wraparound since this is also the case in the (n-armed)
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
		return ((2 - expectedOffspring) / size) + (((2 * rank) * (expectedOffspring - 1)) / (size * (size - 1)));
	}
	
	private void mutationOperator(List<Individual> individuals, List<Double> fitnesses) {
		int populationSize = individuals.size();
		
		for(int i = 0; i < populationSize; i++) {			
			Individual individual = individuals.get(i);
			double mutationChance = getScaledMutationChance(fitnesses.get(i));
			double mutationStandardDeviation = getScaledMutationStandardDeviation(fitnesses.get(i));
			
			for(int j = 0; j < 10; j++) {
				Gene gene = individual.getGene(j);
				if(!(this.random.nextDouble() > mutationChance)) {
					gene.mutate(mutationStandardDeviation);
					individual.invalidateCachedFitness();
				}
				
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
