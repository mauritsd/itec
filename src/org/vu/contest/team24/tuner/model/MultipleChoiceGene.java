package org.vu.contest.team24.tuner.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.vu.contest.team24.RandomSingleton;

public class MultipleChoiceGene implements Gene {
	private String type;
	private int value;
	private List<Object> options;
	private Random random;
	private boolean ordered;
	
	
	public MultipleChoiceGene(String type, Object[] options, boolean ordered) {
		this.type = type;
		this.options = new ArrayList<Object>();
		for (Object option : options) {
			this.options.add(option);
		}
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = this.random.nextInt(this.options.size());
		this.ordered = ordered;
	}
	
	public MultipleChoiceGene(MultipleChoiceGene gene) {
		this.type = gene.type;
		this.options = gene.options;
		this.value = gene.value;
		this.random = RandomSingleton.getInstance().getRandom();
		this.ordered = gene.ordered;
	}
	
	@Override
	public void mutate() {
		if(this.ordered) {
			this.value += this.random.nextBoolean() ? 1 : -1;
			
			if(this.value >= this.options.size()) {
				this.value = this.options.size() - 2;
			}
			if(this.value < 0) {
				this.value = 1;
			}
		} else {
			this.value = this.random.nextInt(this.options.size());
		}
	}
	
	public void crossover(Gene otherGene) {
		MultipleChoiceGene otherMultipleChoiceGene = (MultipleChoiceGene)otherGene;
		
		if(!this.type.equals(otherMultipleChoiceGene.type)) {
			throw new RuntimeException("trying to crossover with MultipleChoiceGene of a different type!");
		}
		if(this.random.nextBoolean()) {
			int ourValue = this.value;
			int theirValue = otherMultipleChoiceGene.value;
			
			otherMultipleChoiceGene.setValue(ourValue);
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
