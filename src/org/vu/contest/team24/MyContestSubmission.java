package org.vu.contest.team24;

import java.util.Properties;
import java.util.Random;

import org.vu.contest.ContestEvaluation;
import org.vu.contest.ContestSubmission;

public class MyContestSubmission implements ContestSubmission {
	private Random rnd;
	private ContestEvaluation evaluation;
	private boolean seperable;
	private boolean regular;

	private boolean multimodal;
	private int evaluations;
	
	public MyContestSubmission()
	{
		this.rnd = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		this.rnd.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		this.evaluation = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
	}
	
	public void run()
	{
		// Run your algorithm here

		// Getting data from evaluation problem (depends on the specific evaluation implementation)
		// E.g. getting a vector of numbers
		// Vector<Double> data = (Vector<Double>)evaluation_.getData("trainingset1");

		// Evaluating your results
		// E.g. evaluating a series of true/false predictions
		// boolean pred[] = ...
		// Double score = (Double)evaluation_.evaluate(pred);
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
