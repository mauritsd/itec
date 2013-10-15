

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
		this.regular = Boolean.parseBoolean(props.getProperty("Regular"));
		this.seperable = Boolean.parseBoolean(props.getProperty("Separable"));
		
		this.strategy = new SimpleEvolutionaryStrategy(this.evaluation);
		if(!isRegular()) {
			this.strategy.setPopulationSize(10);
			
			this.strategy.setMutationChance(0.5772911241652854);
			this.strategy.setMutationChanceScalingFactor(0.75);

			this.strategy.setMutationStandardDeviation(0.1);
			this.strategy.setMutationStandardDeviationScalingFactor(1.0);
			
			this.strategy.setCrossoverChance(0.26723820835407497);
			this.strategy.setCrossoverChanceScalingFactor(1.0);
			
			this.strategy.setFittestExpectedOffspring(2.0);
		} else if(isMultimodal()) {
			this.strategy.setPopulationSize(10);
	
			this.strategy.setMutationChance(0.8779395186817696);
			this.strategy.setMutationChanceScalingFactor(0.842977651105258);
			
			this.strategy.setMutationStandardDeviation(0.18730531424742353);
			this.strategy.setMutationStandardDeviationScalingFactor(0.82999578894443);
			
			this.strategy.setCrossoverChance(0.941428291433915);
			this.strategy.setCrossoverChanceScalingFactor(1.0);
			
			this.strategy.setFittestExpectedOffspring(2.0);
		} else {
			this.strategy.setPopulationSize(30);

			this.strategy.setMutationChance(0.6847133102995688);
			this.strategy.setMutationChanceScalingFactor(0.766738566406783);

			this.strategy.setMutationStandardDeviation(0.8785429401699031);
			this.strategy.setMutationStandardDeviationScalingFactor(0.8125);

			this.strategy.setCrossoverChance(0.9484597996543878);
			this.strategy.setCrossoverChanceScalingFactor(0.9370016775417757);
			
			this.strategy.setFittestExpectedOffspring(1.9018196841721338);
		}
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
