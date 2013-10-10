

import java.util.Properties;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;
import org.vu.contest.team24.EvolutionaryStrategy;
import org.vu.contest.team24.RandomSingleton;
import org.vu.contest.team24.SimpleEvolutionaryStrategy;

public class MyContestSubmission implements ContestSubmission {
	private ContestEvaluation evaluation;
	private EvolutionaryStrategy strategy;
	
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
		
		this.strategy = new SimpleEvolutionaryStrategy(this.evaluation, 0.1f, 0.9f);
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
