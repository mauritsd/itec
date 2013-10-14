

import java.util.Properties;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;
import org.vu.contest.team24.RandomSingleton;
import org.vu.contest.team24.SimpleEvolutionaryStrategy;

public class MyContestSubmission implements ContestSubmission {
	private ContestEvaluation evaluation;
	private SimpleEvolutionaryStrategy strategy;
	
	private boolean seperable;
	private boolean regular;
	private boolean multimodal;
	private int evaluations;
	
	public MyContestSubmission() {

	}
	
	public void setSeed(long seed) {
		RandomSingleton randomSingleton = RandomSingleton.getInstance();
		Random random = new Random();
		random.setSeed(seed);
		randomSingleton.setRandom(random);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
		// Set evaluation problem used in the run
		this.evaluation = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
		this.evaluations = Integer.parseInt(props.getProperty("Evaluations"));
		this.multimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
		this.regular = Boolean.parseBoolean(props.getProperty("GlobalStructure"));
		this.seperable = Boolean.parseBoolean(props.getProperty("Separable"));
		
		this.strategy = new SimpleEvolutionaryStrategy(this.evaluation);
		this.strategy.setCrossoverChance(0.2);
		this.strategy.setCrossoverChanceScalingFactor(0.9);
		this.strategy.setMutationChance(1.0);
		this.strategy.setMutationStandardDeviation(0.3);
		this.strategy.setMutationStandardDeviationScalingFactor(0.9);
		this.strategy.setMutationChanceScalingFactor(0.9);
		this.strategy.setFittestExpectedOffspring(2.0);
		this.strategy.setPopulationSize(30);
	}
	
	public void run() {
		while(!this.strategy.shouldTerminate()) {
			this.strategy.evolveGeneration();
		}
	}
	
	public boolean isSeperable() {
		return this.seperable;
	}

	public boolean isRegular() {
		return this.regular;
	}

	public boolean isMultimodal() {
		return this.multimodal;
	}

	public int getEvaluations() {
		return this.evaluations;
	}

}
