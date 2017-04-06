package vhr.SolutionAcceptor;


/**
 * Created by quachv on 4/4/2017.
 */
public class SimulationAnnealingSolutionAcceptor implements ISolutionAcceptor {
    private double initialTemp;
    private double alpha;
    private double beta;
    private double updateStep;
    private double currentTemp;
    private double stepCounter;
    private double termninateTemp;

    public SimulationAnnealingSolutionAcceptor() {
        initialTemp = 5000;
        currentTemp = initialTemp;
        termninateTemp = 0.0001;
        alpha = 0.99;
        beta = 1.05;
        updateStep = 5;
        stepCounter = updateStep;
    }

    @Override
    public boolean acceptSolution(double prevCost, double newCost) {
        double costDelta = newCost - prevCost;
        if(costDelta < 0) {
            return true;
        }

        if(Math.exp(-costDelta / currentTemp) >= 0.5) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateAcceptor() {
        if(--stepCounter < 0) {
            stepCounter = updateStep;
            currentTemp *= alpha;
            updateStep *= beta;
        }
    }

    @Override
    public boolean canTerminateSearching() {
        if(currentTemp < termninateTemp) {
            return true;
        }
        return false;
    }

    public static class Builder {
        SimulationAnnealingSolutionAcceptor result;

        public Builder() {
            result = new SimulationAnnealingSolutionAcceptor();
        }

        public Builder setInitialTemperatur(double initialTemperature) {
            //TODO: make sure initial temperature > 0
            result.initialTemp = initialTemperature;
            return this;
        }

        public Builder setTerminateTemp(double terminateTemp) {
            result.termninateTemp = terminateTemp;
            return this;
        }

        public Builder setAlpha(double alpha) {
            // TODO: make sure alpha in range: (0, 1)
            result.alpha = alpha;
            return this;
        }

        public Builder setBeta(double beta) {
            // TODO: make sure beta (1, +inf)
            result.beta = beta;
            return this;
        }

        public Builder setUpdateStep(int updateStep) {
            result.updateStep = updateStep;
            return this;
        }

        public ISolutionAcceptor build() {
            return result;
        }
    }
}
