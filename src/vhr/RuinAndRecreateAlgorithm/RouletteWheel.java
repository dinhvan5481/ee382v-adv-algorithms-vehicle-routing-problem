package vhr.RuinAndRecreateAlgorithm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by quachv on 4/10/2017.
 */
public class RouletteWheel {
    public static enum SolutionType {
        REJECTED_SOLUTION(1), ACCEPTED_SOLUTION(3), BETTER_SOLUTION(5), BEST_SOLUTION(7);
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
        if(weights.length == 1) {
            return 0;
        }
        double sum = Arrays.stream(weights).sum();
        double rollValue = ThreadLocalRandom.current().nextDouble();
        List<Double> adjustedWeight = Arrays.stream(weights).map(w -> rollValue*sum - w).boxed().collect(Collectors.toList());
        double maxAdjustedWeight = adjustedWeight.stream().filter(w -> w <= 0).max(Double::compare).orElse(Double.valueOf(0));
        int index = adjustedWeight.indexOf(maxAdjustedWeight);
        if(index < 0) {
            return ThreadLocalRandom.current().nextInt(0, weights.length);
        }
        return index;

//        int index = 0;
//        double sum = Arrays.stream(weights).sum();
//        double rollValue = ThreadLocalRandom.current().nextDouble();
//        index  = Arrays.stream(weights).map(w -> w - w*sum).filter(w -> w <= 0).max().
//        return indexOfMax;

//        int[] largeArray     = {5,4,13,7,7,8,9,10,5};
//        int   largestElement = findLargest(largeArray);
//        int   index          = Arrays.asList(5,4,13,7,7,8,9,10,5).indexOf(largestElement);

/*
        if(weights.length == 1) {
            return 0;
        }
        return ThreadLocalRandom.current().nextInt(0, weights.length);
*/
    }

    public void updateWeight(int index, SolutionType solutionType) {
        if(index < 0 || index >= weights.length) {
            throw new IllegalArgumentException("Index out of range");
        }
        weights[index] = sensitiveParametter*weights[index] + (1 - sensitiveParametter)*solutionType.getValue();
    }

}
