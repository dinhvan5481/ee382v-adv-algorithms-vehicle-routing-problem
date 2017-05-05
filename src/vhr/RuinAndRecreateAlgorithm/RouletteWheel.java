package vhr.RuinAndRecreateAlgorithm;

import java.util.*;
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
        double prevSum = 0;
        List<Double> adjustedWeight = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            prevSum += weights[i] / sum;
            adjustedWeight.add(i, prevSum);
        }
        double rollValue = ThreadLocalRandom.current().nextDouble();
        for (int i = 0; i < adjustedWeight.size(); i++) {
            if(rollValue - adjustedWeight.get(i) <= 0) {
                return i;
            }
        }
        return 0;
    }

    public void updateWeight(int index, SolutionType solutionType) {
        if(index < 0 || index >= weights.length) {
            throw new IllegalArgumentException("Index out of range");
        }
        weights[index] = sensitiveParametter*weights[index] + (1 - sensitiveParametter)*solutionType.getValue();
    }

}
