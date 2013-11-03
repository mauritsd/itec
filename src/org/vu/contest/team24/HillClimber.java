package org.vu.contest.team24;

import java.util.List;

import org.vu.contest.ContestEvaluation;

public interface HillClimber {
	public List<Double> climb(List<Double> startingPoint, ContestEvaluation evaluation);
}
