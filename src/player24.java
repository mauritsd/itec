

import java.util.Properties;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;
import org.vu.contest.team24.RandomSingleton;
import org.vu.contest.team24.SimpleEvolutionaryStrategy;

public class player24 implements ContestSubmission {
	private ContestEvaluation evaluation;
	private SimpleEvolutionaryStrategy strategy;
	
	private boolean seperable;
	private boolean regular;
	private boolean multimodal;
	private int evaluations;
	
	public player24() {

	}
	
	public void setSeed(long seed) {
		RandomSingleton randomSingleton = RandomSingleton.getInstance();
		Random random = new Random();
		random.setSeed(seed);
		randomSingleton.setRandom(random);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
		// Set evaluation problem used in the run.
		this.evaluation = evaluation;
		
		// Get evaluation properties.
		Properties props = evaluation.getProperties();
		this.evaluations = Integer.parseInt(props.getProperty("Evaluations"));
		this.multimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
		this.regular = Boolean.parseBoolean(props.getProperty("Regular"));
		this.seperable = Boolean.parseBoolean(props.getProperty("Separable"));
		
		// We only have one strategy, but set its parameters based on function properties.
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
			this.strategy.setPopulationSize(15);
	
			this.strategy.setMutationChance(0.7492548311153375);
			this.strategy.setMutationChanceScalingFactor(0.923398298198534);
			
			this.strategy.setMutationStandardDeviation(0.5564530509643655);
			this.strategy.setMutationStandardDeviationScalingFactor(0.829543388853112);
			
			this.strategy.setCrossoverChance(0.6094967337645261);
			this.strategy.setCrossoverChanceScalingFactor(0.571875872795713);
			
			this.strategy.setFittestExpectedOffspring(1.8557366483321078);		
		} else {
			this.strategy.setPopulationSize(25);

			this.strategy.setMutationChance(0.7898969937068365);
			this.strategy.setMutationChanceScalingFactor(0.5125191642552177);

			this.strategy.setMutationStandardDeviation(0.26283377870469576);
			this.strategy.setMutationStandardDeviationScalingFactor(0.6540430306465737);

			this.strategy.setCrossoverChance(0.6290377681692815);
			this.strategy.setCrossoverChanceScalingFactor(0.7017059355954637);
			
			this.strategy.setFittestExpectedOffspring(1.997776640156771);
		}
	}
	
	public void run() {
		// Run the algorithm until it signals we have run out of our iteration budget.
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
