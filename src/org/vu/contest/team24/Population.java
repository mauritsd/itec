package org.vu.contest.team24;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author asus
 */

import java.util.Random;

public class Population {
    
       SphereEvaluation evaluation2_ = new SphereEvaluation();
       
       public double[] initiateMember(int genomeSize){
            double[] genome = new double[genomeSize];
            double start = -5;
            double end = 5;
            for (int i = 0; i < genomeSize; i++) {
                double random = new Random().nextDouble();
                double rVal = start + (random * (end - start));
                genome[i] = rVal;
            }
            return genome;
        }
        
        public double[][] initiatePopulation(int populationSize, int genomeSize){
            double[][] population = new double[populationSize][genomeSize];
            for (int i = 0; i < populationSize; i++) {
                population[i] = initiateMember(genomeSize);
            }
            return population;
        }
        
        public double getFitnessMember(double[] member){
            double score = Double.valueOf(evaluation2_.evaluate(member).toString());
            return score;}
        
        public selectParentsMethodx(){
            
        }
}
