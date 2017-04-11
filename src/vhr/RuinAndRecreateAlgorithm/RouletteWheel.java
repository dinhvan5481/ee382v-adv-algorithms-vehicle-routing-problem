package vhr.RuinAndRecreateAlgorithm;

import java.util.Arrays;

/**
 * Created by quachv on 4/10/2017.
 */
public class RouletteWheel {
    public static enum SolutionType {
        REJECTED_SOLUTION(1), ACCEPTED_SOLUTION(2), BETTER_SOLUTION(4), BEST_SOLUTION(6);
        private final int value;

        SolutionType(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }

    private double sensitiveParametter;
    private double[] weights;
    public RouletteWheel(int noSlots, double sensitiveParametter) {
        this.sensitiveParametter = sensitiveParametter;
        weights = new double[noSlots];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1;
        }
    }

    public int roll() {
        double sum = Arrays.stream(weights).sum();
        int indexOfMax = -1;
        double max = 0;
        double prob = 0;
        for (int i = 0; i < weights.length; i++) {
            prob = weights[i] / sum;
            if(prob > max) {
                max = prob;
                indexOfMax = i;
            }
        }
        return indexOfMax;
    }

    public void updateWeight(int index, SolutionType solutionType) {
        if(index < 0 || index >= weights.length) {
            throw new IllegalArgumentException("Index out of range");
        }
        weights[index] = sensitiveParametter*weights[index] + (1 - sensitiveParametter)*solutionType.getValue();
    }

}
