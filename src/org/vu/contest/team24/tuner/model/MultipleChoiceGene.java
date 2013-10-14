package org.vu.contest.team24.tuner.model;

import java.util.List;
import java.util.Random;

import org.vu.contest.team24.RandomSingleton;

public class MultipleChoiceGene implements Gene {
	private String type;
	private int value;
	private List<String> options;
	private Random random;
	
	public MultipleChoiceGene(String type, List<String> options) {
		this.type = type;
		this.options = options;
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = this.random.nextInt(this.options.size());
	}
	
	
	@Override
	public void mutate() {
		this.value = this.random.nextInt(this.options.size());
	}
	
	public void crossover(MultipleChoiceGene otherGene) {
		if(!this.type.equals(otherGene.type)) {
			throw new RuntimeException("trying to crossover with MultipleChoiceGene of a different type!");
		}
		if(this.random.nextBoolean()) {
			int ourValue = this.value;
			int theirValue = otherGene.value;
			
			otherGene.setValue(ourValue);
			this.setValue(theirValue);
		}
	}

	@Override
	public Object getValue() {
		return this.options.get(this.value);
	}
	
	private void setValue(int value) {
		this.value = value;
	}

}
