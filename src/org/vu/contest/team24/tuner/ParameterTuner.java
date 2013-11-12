package org.vu.contest.team24.tuner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.vu.contest.team24.tuner.FitnessComparator;
import org.vu.contest.team24.RandomSingleton;
import org.vu.contest.team24.SimpleEvolutionaryStrategy;
import org.vu.contest.team24.tuner.model.Gene;
import org.vu.contest.team24.tuner.model.Individual;

public class ParameterTuner {
	private static final int POPULATION_SIZE = 16;
	private static final double EXPECTED_OFFSPRING = 2.0;
	private static final int RUNS_PER_INDIVIDUAL = 10;
	private static final int GENERATIONS = 20;
	
	private Class<?> evaluationClass;
	private String evaluationClassName;
	private List<Individual> population;
	private TunerContestEvaluation evaluation;
	private Random random;

	public ParameterTuner() {
		RandomSingleton randomSingleton = RandomSingleton.getInstance();
		Random random = new Random();
		random.setSeed(new Date().getTime());
		randomSingleton.setRandom(random);
		this.random = random;
		
		this.population = new ArrayList<Individual>(POPULATION_SIZE);
		for(int n = 0; n < POPULATION_SIZE; n++) {
			this.population.add(new Individual());
		}
	}
	
	public static void main(String[] args) {
		new ParameterTuner().run(args);
	}
	
	public void run(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("-evaluation="))
				evaluationClassName = arg.split("=")[1];
			else
				System.out.println("Invalid flag: '" + arg + "' !");
		}

		if (evaluationClassName == null)
			throw new Error(
					"Evaluation ID was not specified! Cannot run...\n Use -evaluation=<classnamehere> to specify the name of the evaluation class.");
		
		try {
			evaluationClass = Class.forName(evaluationClassName);
		} catch (Throwable throwable) {
			System.err.println("Could not load evaluation class for evaluation '" + evaluationClassName + "'");
			throwable.printStackTrace();
			System.exit(1);
		}
		
		try {
			this.evaluation = (TunerContestEvaluation)this.evaluationClass.newInstance();
		} catch (Throwable throwable) {
			System.err.println("ExecutionError: Could not instantiate evaluation object for evaluation '" + evaluationClassName + "'");
			throwable.printStackTrace();
			System.exit(1);
		}
		
		for(int generation = 0; generation < GENERATIONS; generation++) {
			System.out.println("Generation "+ generation);
			
			evolveGeneration();
		}
		
		Map<Individual, Double> fitnesses = getFitnesses(this.population);		
		for(Individual individual : this.population) {
			individual.printSummary(fitnesses.get(individual));
		}
	}
	
	private Map<Individual, Double> getFitnesses(List<Individual> population) {
		TunerContestEvaluation evaluation = this.evaluation;
		evaluation.reset();
		Map<Individual, Double> fitnesses = new HashMap<Individual, Double>(population.size());
		for (Individual individual : population) {
			double fitnessSum = 0.0;
			for(int run = 0; run < RUNS_PER_INDIVIDUAL; run++) {
				SimpleEvolutionaryStrategy strategy = new SimpleEvolutionaryStrategy(evaluation);
				individual.configureStrategy(strategy);
				ParameterEvaluator parameterEvaluator = new ParameterEvaluator(evaluation, strategy);
				parameterEvaluator.run();
				evaluation.reset();
				fitnessSum += parameterEvaluator.getFitness();
			}

			fitnesses.put(individual, fitnessSum / RUNS_PER_INDIVIDUAL);
		}
		
		return fitnesses;
	}
	
	private void evolveGeneration() {
		Map<Individual, Double> fitnesses = getFitnesses(this.population);
		
		List<Individual> nextPopulation = selectParents(this.population, fitnesses);
		for (Individual individual : nextPopulation) {
			List<Gene> genes = individual.getGenes();
			if(!(this.random.nextDouble() > 0.5)) {
				Gene g = genes.get(this.random.nextInt(genes.size()));
				g.mutate();
			}
		}
		
		for(int i = 0; i < POPULATION_SIZE; i++) {
			Individual individual = nextPopulation.get(i);
			
			if(!(this.random.nextDouble() > 0.2)) {
				int j;
				do {
					j = this.random.nextInt(POPULATION_SIZE);
				} while(j == i);
				Individual otherIndividual = nextPopulation.get(j);
				
				int chromosomeSize = individual.getGenes().size();
				for(int n = 0; n < chromosomeSize; n++) {
					Gene gene = individual.getGene(n);
					Gene otherGene = otherIndividual.getGene(n);
					gene.crossover(otherGene);
				}
			}
		}
		
		this.population = nextPopulation;		
	}
	
	private List<Individual> selectParents(List<Individual> individuals, Map<Individual, Double> fitnesses) {
		// Sort parents to get the ordered list for ranking.
		individuals = new ArrayList<Individual>(individuals);
		Collections.sort(individuals, new FitnessComparator(fitnesses));
		
		// Print parameters of fittest individual.
		Individual fittestIndividual = individuals.get(0);
		double fittestFitness = fitnesses.get(fittestIndividual);
		System.out.println("Fittest individual has fitness: " + fittestFitness);
		if(fittestFitness >= 0.0) {
			fittestIndividual.printSummary(fittestFitness);
		}
		
		// Apply Stochastic Universal Sampling to select the parents.
		int populationSize = individuals.size();
		double spacing = 1.0 / populationSize;
		double position = this.random.nextDouble() * spacing;
		List<Individual> parents = new ArrayList<Individual>(populationSize);
		
		int i = 0;
		int currentParent = 0;
		double currentCumulative = probabilityForRank(i, populationSize, EXPECTED_OFFSPRING);
		while(currentParent < populationSize) {
			while(currentCumulative < position) {
				currentCumulative += probabilityForRank(++i, populationSize, EXPECTED_OFFSPRING);
			}
			
			parents.add(new Individual(individuals.get(i)));
			currentParent++;

			position += spacing;
			// We need to implement wraparound since this is also the case in the (n-armed)
			// roulette wheel...
			if(position > 1.0) {
				position -= 1.0;
				i = 0;
				currentCumulative = probabilityForRank(i, populationSize, EXPECTED_OFFSPRING);
			}
		}
		
		return parents;
	}
	
	private double probabilityForRank(int rank, int size, double expectedOffspring) {
		rank = size - rank - 1;
		// Linear ranking. expectedOffspring is the number of offspring the fittest (highest ranking)
		// individual should be expected to get.
		return ((2.0 - expectedOffspring) / (double)size) + (((2.0 * (double)rank) * (expectedOffspring - 1.0)) / ((double)size * ((double)size - 1.0)));
	}
	
}
