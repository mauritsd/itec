package org.vu.contest.team24.tuner.model;

import java.util.Random;

import org.vu.contest.team24.RandomSingleton;

public class DoubleGene implements Gene {
	private double min;
	private double max;
	private double value;
	private Random random;
	
	public DoubleGene(double min, double max) {
		this.min = min;
		this.max = max;
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = (this.random.nextDouble() * (this.max - this.min)) + this.min;
	}
	
	public DoubleGene(DoubleGene gene) {
		this.min = gene.min;
		this.max = gene.max;
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = gene.value;
	}
	
	@Override
	public void mutate() {		
		double amount = this.random.nextGaussian() * (this.max - this.min);
		
		setValue(this.value + amount);
	}
	
	public void crossover(Gene otherGene) {
		// Mix the value of this gene with another gene with a random mixing ratio.
		DoubleGene otherDoubleGene = (DoubleGene)otherGene;
		double ratio = this.random.nextDouble();
		
		double ourValue = (Double) this.getValue();
		double theirValue = (Double) otherGene.getValue();
		double mixedValue = (ratio * ourValue) + ((1-ratio) * theirValue);
		
		this.setValue(mixedValue);
		otherDoubleGene.setValue(mixedValue);
	}
	
	@Override
	public Object getValue() {
		return this.value;
	}
	
	private void setValue(double value) {
		// If value falls outside the defined interval clamp it to the boundary.
		if(value > this.max) {
			value = this.max;
		} else if(value < this.min) {
			value = this.min;
		}
		this.value = value;
	}
}
