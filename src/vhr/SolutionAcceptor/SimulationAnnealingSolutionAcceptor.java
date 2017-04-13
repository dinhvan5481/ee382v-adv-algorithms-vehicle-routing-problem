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
        protected double initialTemp;
        protected double termninateTemp;
        protected double alpha;
        protected double beta;
        protected int updateStep;


        public Builder() {
        }

        public Builder setInitialTemperatur(double initialTemperature) {
            //TODO: make sure initial temperature > 0
            this.initialTemp = initialTemperature;
            return this;
        }

        public Builder setTerminateTemp(double terminateTemp) {
            this.termninateTemp = terminateTemp;
            return this;
        }

        public Builder setAlpha(double alpha) {
            // TODO: make sure alpha in range: (0, 1)
            this.alpha = alpha;
            return this;
        }

        public Builder setBeta(double beta) {
            // TODO: make sure beta (1, +inf)
            this.beta = beta;
            return this;
        }

        public Builder setUpdateStep(int updateStep) {
            this.updateStep = updateStep;
            return this;
        }

        public ISolutionAcceptor build() {
            SimulationAnnealingSolutionAcceptor result = new SimulationAnnealingSolutionAcceptor();
            result.initialTemp = initialTemp;
            result.currentTemp = initialTemp;
            result.termninateTemp = termninateTemp;
            result.updateStep = updateStep;
            result.beta = beta;
            result.alpha = alpha;
            return result;
        }
    }
}
