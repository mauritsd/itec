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
	
	public MultipleChoiceGene(String type, Object[] options) {
		this.type = type;
		this.options = new ArrayList<Object>();
		for (Object option : options) {
			this.options.add(option);
		}
		this.random = RandomSingleton.getInstance().getRandom();
		this.value = this.random.nextInt(this.options.size());
	}
	
	public MultipleChoiceGene(MultipleChoiceGene gene) {
		this.type = gene.type;
		this.options = gene.options;
		this.value = gene.value;
		this.random = RandomSingleton.getInstance().getRandom();
	}
	
	@Override
	public void mutate() {
		this.value = this.random.nextInt(this.options.size());
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
