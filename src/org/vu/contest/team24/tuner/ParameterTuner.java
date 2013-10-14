package org.vu.contest.team24.tuner;

import java.util.Date;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.team24.RandomSingleton;
import org.vu.contest.team24.SimpleEvolutionaryStrategy;

public class ParameterTuner {
	private Class<?> evaluationClass;
	private String evaluationClassName;
	

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
		
		ContestEvaluation evaluation = getEvaluationInstance();
		
		RandomSingleton randomSingleton = RandomSingleton.getInstance();
		Random random = new Random();
		random.setSeed(new Date().getTime());
		randomSingleton.setRandom(random);
		
		SimpleEvolutionaryStrategy strategy = new SimpleEvolutionaryStrategy(evaluation);
		strategy.setCrossoverChance(0.2);
		strategy.setMutationChance(1.0);
		strategy.setMutationStandardDeviation(0.3);
		strategy.setFittestExpectedOffspring(2.0);
		strategy.setPopulationSize(30);
		
		ParameterEvaluator parameterEvaluator = new ParameterEvaluator(evaluation, strategy);
		Thread thread = new Thread(parameterEvaluator);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(parameterEvaluator.getFitness());
	}
	
	private ContestEvaluation getEvaluationInstance() {
		ContestEvaluation evaluation = null;
		try {
			evaluation = (ContestEvaluation)this.evaluationClass.newInstance();
		} catch (Throwable throwable) {
			System.err.println("ExecutionError: Could not instantiate evaluation object for evaluation '" + evaluationClassName + "'");
			throwable.printStackTrace();
			System.exit(1);
		}
		
		return evaluation;
	}
}
