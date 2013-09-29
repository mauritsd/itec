package org.vu.contest.team24.model;
import java.util.List;
import java.util.Vector;


public class Population {
	private List<Individual> individuals;
	
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
		int individualCount = this.individuals.size();
		for(int i=0; i < individualCount; i++) {
			individualArray[i] = this.individuals.get(i);
		}
		
		return individualArray;
	}
	
	public List<Individual> getIndividualList() {
		return this.individuals;
	}
	
	public Individual getIndividual(int individualIndex) {
		return this.individuals.get(individualIndex);
	}
	
	public int getSize() {
		return this.individuals.size();
	}
}
