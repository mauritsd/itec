package org.vu.contest.team24.model;
import java.util.Random;

import org.vu.contest.team24.RandomSingleton;


public class Gene {
	private double value;
	private Random random;
	
	public Gene(double value) {
		setValue(value);
		this.random = RandomSingleton.getInstance().getRandom();
	}
	
	public Gene(Gene gene) {
		this(gene.getValue());
	}
	
	public void mutate(double standardDeviation) {
		double gaussian = this.random.nextGaussian();
		setValue(this.value + (gaussian * standardDeviation));
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		if(value > 5.0) {
			value = 5.0;
		}
		if(value < -5.0) {
			value = -5.0;
		}
		this.value = value;
	}
}
