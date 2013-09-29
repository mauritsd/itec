package org.vu.contest.team24;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.model.Gene;
import org.vu.contest.team24.model.Individual;
import org.vu.contest.team24.model.Population;


public class SimpleEvolutionStrategy implements EvolutionStrategy {
	private Population currentPopulation;
	private ContestEvaluation evaluation;
	private boolean shouldTerminate;
	private Random random;
	private float mutationChance;
	
	
	public SimpleEvolutionStrategy(ContestEvaluation evaluation, float mutationChance) {
		this.currentPopulation = new Population(10);
		this.evaluation = evaluation;
		this.random = RandomSingleton.getInstance().getRandom();
		this.mutationChance = mutationChance;
		
	}
	
	@Override
	public void evolveGeneration() {
		List<Individual> individuals = new Vector<Individual>(this.currentPopulation.getIndividualList());
		List<Individual> nextIndividuals = selectParents(individuals);
		
		mutateIndividuals(nextIndividuals);
		//crossoverIndividuals(nextIndividuals)
		
		this.currentPopulation = new Population((Individual[]) nextIndividuals.toArray());

	}
	
	private List<Individual> selectParents(List<Individual> individuals) {
		// Have to copy the vector here since we don't want it to sort the original individual vector in place.
		// Sort list to get the ranking.
		Collections.sort(individuals, new Comparator<Individual>() {
			@Override
			public int compare(Individual i1, Individual i2) {
				try {
					double fitnessOne = i1.getFitness(SimpleEvolutionStrategy.this.evaluation);
					double fitnessTwo = i2.getFitness(SimpleEvolutionStrategy.this.evaluation);				

					if(fitnessOne == fitnessTwo) {
						return 0;
					} else if(fitnessOne < fitnessTwo) {
						return -1;
					} else {
						return 1;
					}
				} catch (MaximumEvaluationsExceededException e) {
					throw new IllegalStateException("exceeded maximum evaluations in a place where this shouldn't happen!");
				}
			}
		});
		
		int populationSize = individuals.size();
		float spacing = 1 / populationSize;
		float position = this.random.nextFloat() * spacing;
		List<Individual> parents = new Vector<Individual>(populationSize);
		
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
		return ((2 - expectedOffspring) / size) + (((2 * rank)*(expectedOffspring - 1)) / (size * (size - 1)));
	}
	
	private void mutateIndividuals(List<Individual> individuals) {
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
