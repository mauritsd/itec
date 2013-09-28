import java.util.Vector;


public class Population {
	private Vector<Individual> individuals;
	
	public Population(int initialSize) {
		this.individuals = new Vector<Individual>(initialSize);
		for(int i=0; i < initialSize; i++) {
			individuals.add(new Individual());
		}
	}
	
	public Population(Individual[] individuals) {
		this.individuals = new Vector<Individual>(individuals.length);
		for(Individual individual : individuals) {
			this.individuals.add(individual);
		}
	}
	
	
	public Population(Population population) {
		this(population.getIndividualArray());
	}
	
	public Individual[] getIndividualArray() {
		Individual[] individualArray = new Individual[this.individuals.size()];
		for(int i=0; i<this.individuals.size(); i++) {
			individualArray[i] = this.individuals.get(i);
		}
		
		return individualArray;
	}
}
