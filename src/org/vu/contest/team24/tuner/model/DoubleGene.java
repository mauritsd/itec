package org.vu.contest.team24.tuner.model;

import java.util.Random;

import org.vu.contest.team24.RandomSingleton;

public class DoubleGene implements Gene {
	private double min;
	private double max;
	private double value;
	private Random random;
	
	public DoubleGene(float min, float max) {
		this.min = min;
		this.max = max;
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = (this.random.nextDouble() * (this.max - this.min)) - this.min;
	}
	
	@Override
	public void mutate() {		
		float amount = (float)this.random.nextGaussian();
		
		setValue(this.value + amount);
	}
	
	public void crossover(DoubleGene otherGene) {
		double ourValue = (Double) this.getValue();
		double theirValue = (Double) otherGene.getValue();
		double averageValue = 0.5f * ourValue + 0.5f * theirValue;
		
		this.setValue(averageValue);
		otherGene.setValue(averageValue);
	}
	
	@Override
	public Object getValue() {
		return this.value;
	}
	
	private void setValue(double value) {
		if(value > this.max) {
			value = this.max;
		} else if(value < this.min) {
			value = this.min;
		}
		this.value = value;
	}
}
