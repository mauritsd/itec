import java.util.Random;
import java.util.Vector;

import org.vu.contest.ContestEvaluation;


public class Individual {
	private Vector<Gene> genes;
	private Random random;
	
	public Individual() {
		this.random = RandomSingleton.getInstance().getRandom();
		this.genes = new Vector<Gene>(10);
		for(int i=0; i < 10; i++) {
			double value = (10.0 * this.random.nextDouble()) - 5.0;
			this.genes.add(new Gene(value));
		}
	}
	
	public Individual(Gene[] genes) {
		this.random = RandomSingleton.getInstance().getRandom();
		this.genes = new Vector<Gene>(10);
		for (Gene gene : genes) {
			this.genes.add(gene);
		}
	}
	
	public Individual(Individual individual) {
		this(individual.getGeneArray());
	}
	
	public Gene getGene(int geneIndex) {
		return this.genes.get(geneIndex);
	}
	
	public void swapGene(Individual individual, int geneIndex) {
		Gene ourGene = this.genes.get(geneIndex);
		Gene theirGene = individual.genes.get(geneIndex);
		
		this.genes.set(geneIndex, theirGene);
		individual.genes.set(geneIndex, ourGene);
	}
	
	public Gene[] getGeneArray() {
		Gene[] geneArray = new Gene[10];
		for (int i = 0; i < this.genes.size(); i++) {
			Gene g = this.genes.get(i);
			geneArray[i] = g;
		}
		return geneArray;
	}
	
	public double getFitness(ContestEvaluation evaluation) {
		double[] geneArray = new double[10];
		for (int i = 0; i < this.genes.size(); i++) {
			Gene g = this.genes.get(i);
			geneArray[i] = g.getValue();
		}
		return (Double)evaluation.evaluate(geneArray);
	}
	
	
}
