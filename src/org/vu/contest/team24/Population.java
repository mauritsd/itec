package org.vu.contest.team24;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author asus
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Population {
    
       SphereEvaluation evaluation2_ = new SphereEvaluation();
       Random randomGenerator = new Random();
       
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
        
        public double[][][] parentSelectionTournament(int numberOfTournaments,int tournamentSize,double[][] orderedParents){
            double[][][] selectedParents = new double[tournamentSize][][];
            for (int i=0; i<numberOfTournaments; i++){
                selectedParents[i] = tournamentBattle(tournamentSize, orderedParents);
            }
            return selectedParents;
        }
        
        public double[][] tournamentBattle(int tournamentSize,double[][] orderedParents){
            int c1 = randomGenerator.nextInt(orderedParents.length);
            int c2 = randomGenerator.nextInt(orderedParents.length);
            int c3 = randomGenerator.nextInt(orderedParents.length);

            ArrayList<Integer> selected = new ArrayList<Integer>();
            selected.add(c1);
            selected.add(c2);
            selected.add(c3);
            
            Collections.sort(selected);
            double[] parent1 = orderedParents[selected.get(0)];
            double[] parent2 = orderedParents[selected.get(1)];
            
            double[][] parents = {parent1,parent2};
            
            return parents;         
        }
}
