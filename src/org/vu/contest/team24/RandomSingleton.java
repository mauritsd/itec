package org.vu.contest.team24;


import java.util.Random;

public class RandomSingleton {
	private static RandomSingleton instance = null;

	private Random random;

	private RandomSingleton() {
		// Exists only to defeat instantiation by external code.
	}

	public static RandomSingleton getInstance() {
		if (instance == null) {
			instance = new RandomSingleton();
		}
		return instance;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}
}
