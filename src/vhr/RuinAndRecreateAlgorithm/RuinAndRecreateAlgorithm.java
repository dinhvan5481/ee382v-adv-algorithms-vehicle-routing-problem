package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.LeastCustomerRuinStrategy;
import vhr.SolutionAcceptor.ISolutionAcceptor;
import vhr.SolutionAcceptor.SimulationAnnealingSolutionAcceptor;
import vhr.core.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by dinhvan5481 on 3/26/17.
 */
public class RuinAndRecreateAlgorithm implements IVRPAlgorithm {

    protected int numOfInitSolution;
    protected List<Double> initSolutionCosts;
    protected ICostCalculator costCalculator;
    protected IDistanceCalculator distanceCalculator;
    protected GenerateClusteringInitialSolutionStrategy generateInitialSolutionStrategy;
    protected ISolutionAcceptor solutionAcceptor;

    protected IRuinStrategy leastCustomerRuinStrategy;
    protected ArrayList<IRuinStrategy> ruinStrategies;
    protected ArrayList<IRecreateStrategy> recreateStrategies;
    protected RouletteWheel ruinedRouletteWheel;
    protected RouletteWheel recreateRouletteWheel;
    protected int maxRun;

    protected SolutionLogger logger;
    protected String logFileName;

    protected RuinAndRecreateAlgorithm() {
        maxRun = 0;
        numOfInitSolution = 100;
        ruinStrategies = new ArrayList<>();
        recreateStrategies = new ArrayList<>();
        initSolutionCosts = new ArrayList<>();
        leastCustomerRuinStrategy = new LeastCustomerRuinStrategy();
        ruinStrategies.add(leastCustomerRuinStrategy);
    }

    protected RuinAndRecreateAlgorithm(ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
        this();
        this.costCalculator = costCalculator;
        this.distanceCalculator = distanceCalculator;
    }

    protected void addRuiStrategy(IRuinStrategy ruinStrategy) {
        ruinStrategies.add(ruinStrategy);
    }

    protected void addRecreateStrategy(IRecreateStrategy recreateStrategy) {
        recreateStrategies.add(recreateStrategy);
    }

    @Override
    public VRPSolution solve(VRPInstance vrpInstance) throws Exception {
        List<VRPSolution> initialSolutions = new ArrayList<>();
        double ruinRate = 0.3;
        int runCounter = 0;
        boolean continueSearchSolutionFlag = true;
        Collections.shuffle(ruinStrategies);
        Collections.shuffle(recreateStrategies);

        double rouletteWheelParameterSensitive = 0.6;
        ruinedRouletteWheel = new RouletteWheel(ruinStrategies.size(), rouletteWheelParameterSensitive);
        recreateRouletteWheel = new RouletteWheel(recreateStrategies.size(), rouletteWheelParameterSensitive);
        RouletteWheel.SolutionType currentSolutionType;
        IRuinStrategy currentRuinStrategy;
        IRecreateStrategy currentRecreateStrategy;

        VRPSolution vrpSolutionK = null;
        double bestInitSolutionCost = Double.MAX_VALUE;
        double initSolutionCost = 0;
        VRPSolution initSolution;
        double originalTruckCapacity = vrpInstance.getCapacity();

        while (vrpSolutionK == null) {
            for (Customer customer :
                    vrpInstance.getCustomers()) {
                generateInitialSolutionStrategy.setBaseCustomer(customer);
                initSolution = generateInitialSolutionStrategy.generateSolution(vrpInstance);
                initSolutionCost = initSolution.getSolutionCost();
                if(initSolutionCost < bestInitSolutionCost && initSolution.isNumberOfRoutesValid()) {
                    vrpSolutionK = initSolution;
                    bestInitSolutionCost = initSolution.getSolutionCost();
                }
                if(initSolution.isNumberOfRoutesValid()) {
                    initSolutionCosts.add(initSolutionCost);
                    initialSolutions.add(initSolution);
                }
            }
            if(vrpSolutionK == null) {
                vrpInstance.setCapacity(vrpInstance.getCapacity() * 1.25);
            }
        }
        vrpInstance.setCapacity(originalTruckCapacity);
        double initCostAverage = initSolutionCosts.stream().mapToDouble(c -> c.doubleValue())
                .summaryStatistics().getAverage();
        double standardDeviationOfInitSolutionCost = Math.sqrt(initSolutionCosts.stream()
                .mapToDouble(c -> Math.pow(c.doubleValue() - initCostAverage, 2.0)).sum() / (initSolutionCosts.size() - 1));

        solutionAcceptor = new SimulationAnnealingSolutionAcceptor
                .Builder()
                .setInitialTemperatur(standardDeviationOfInitSolutionCost)
                .setAlpha(0.99)
                .setBeta(1.05)
                .setUpdateStep(5)
                .setTerminateTemp(0.001)
                .setMaxNoiseRange(bestInitSolutionCost)
                .build();


        VRPSolution vrpSolutionK_p1 = null;
        VRPSolution bestSolution = vrpSolutionK;
        double preSolutionCost = bestInitSolutionCost;
        double bestSolutionCost = bestInitSolutionCost;
        double currentSolutionCost;
        if(logger != null) {
            logger.addSolutionCost(vrpSolutionK.getSolutionCost());
        }

        while (continueSearchSolutionFlag) {
            currentRuinStrategy = ruinStrategies.get(ruinedRouletteWheel.roll());
            currentRecreateStrategy = recreateStrategies.get(recreateRouletteWheel.roll());
            vrpSolutionK_p1 = currentRuinStrategy.ruin(vrpInstance, vrpSolutionK, ruinRate);
            vrpSolutionK_p1 = currentRecreateStrategy.recreate(vrpSolutionK_p1, currentRuinStrategy.getRemovedCustomerIds());

            currentSolutionCost = vrpSolutionK_p1.getSolutionCost();
            if (currentSolutionCost < preSolutionCost
                    && vrpSolutionK_p1.isNumberOfRoutesValid()) {
                vrpSolutionK = vrpSolutionK_p1;
                preSolutionCost = currentSolutionCost;
                if (currentSolutionCost < bestSolutionCost) {
                    bestSolutionCost = currentSolutionCost;
                    bestSolution = vrpSolutionK_p1;
                    currentSolutionType = RouletteWheel.SolutionType.BEST_SOLUTION;
                } else {
                    currentSolutionType = RouletteWheel.SolutionType.BETTER_SOLUTION;
                }
            } else {
                if (solutionAcceptor.acceptSolution(preSolutionCost, currentSolutionCost)) {
                    vrpSolutionK = vrpSolutionK_p1;
                    preSolutionCost = currentSolutionCost;
                    currentSolutionType = RouletteWheel.SolutionType.ACCEPTED_SOLUTION;
                } else {
                    currentSolutionType = RouletteWheel.SolutionType.REJECTED_SOLUTION;
                }
            }
            solutionAcceptor.updateAcceptor();
            ruinedRouletteWheel.updateWeight(ruinStrategies.indexOf(currentRuinStrategy), currentSolutionType);
            recreateRouletteWheel.updateWeight(recreateStrategies.indexOf(currentRecreateStrategy), currentSolutionType);
            runCounter++;
            continueSearchSolutionFlag = continueToSearch(runCounter, solutionAcceptor.canTerminateSearching());
            if (logger != null) {
                logger.addSolutionCost(vrpSolutionK.getSolutionCost());
            }
        }

        if(logger != null) {
            logger.toCSV(logFileName);
        }
        return bestSolution;
    }

    private boolean continueToSearch(int runCounter, boolean canStopSearching) {
        if(runCounter >= maxRun || canStopSearching) {
            return false;
        }
        return true;
    }

    protected static class SolutionLogger {
        private double[] costLog;
        private int logIndex;
        public SolutionLogger(int maxRun) {
            costLog = new double[maxRun];
            logIndex = 0;
            for(int i = 0; i < costLog.length; i++) {
                costLog[i] = -1;
            }
        }
        public void addSolutionCost(double cost) {
            if(logIndex >= costLog.length) {
                return;
            }
            costLog[logIndex++] = cost;

        }

        public void toCSV(String fileName) {
            FileWriter fileWriter = null;
            int runIndex = 0;
            try {
                fileWriter = new FileWriter(fileName);
                while (runIndex < costLog.length && costLog[runIndex] > 0) {
                    fileWriter.write(String.valueOf(costLog[runIndex]) + System.getProperty("line.separator"));
                    runIndex++;
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static class Builder {
        protected int numOfInitSolution;
        protected ICostCalculator costCalculator;
        protected IDistanceCalculator distanceCalculator;
        protected GenerateClusteringInitialSolutionStrategy generateInitialSolutionStrategy;
        protected ArrayList<IRuinStrategy> ruinStrategies;
        protected ArrayList<IRecreateStrategy> recreateStrategies;
        protected ISolutionAcceptor solutionAcceptor;
        protected int maxRun;

        protected boolean logSolution;
        protected String logFileName;



        public Builder(ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
            this.costCalculator = costCalculator;
            this.distanceCalculator = distanceCalculator;
            ruinStrategies = new ArrayList<>();
            recreateStrategies = new ArrayList<>();
            maxRun = 0;
            numOfInitSolution = 10;
            logSolution = false;
        }

        public Builder setNumberOfSolution(int numberOfSolution) {
            this.numOfInitSolution = numberOfSolution;
            return this;
        }

        public Builder setInitializeSolutionStrategy(GenerateClusteringInitialSolutionStrategy generateInitialSolution) {
            this.generateInitialSolutionStrategy = generateInitialSolution;
            return this;
        }

        public Builder addRuinStrategies(IRuinStrategy ruinStrategies) {
            this.ruinStrategies.add(ruinStrategies);
            return this;
        }

        public Builder addRecreateStrategies(IRecreateStrategy recreateStrategies) {
            this.recreateStrategies.add(recreateStrategies);
            return this;
        }

        public Builder setSolutionAcceptor(ISolutionAcceptor solutionAcceptor) {
            this.solutionAcceptor = solutionAcceptor;
            return this;
        }

        public Builder setMaxRun(int maxRun) {
            this.maxRun = maxRun;
            return this;
        }

        public Builder setLogSolution(String logFileName) {
            this.logFileName = logFileName;
            this.logSolution = true;
            return this;
        }

        public RuinAndRecreateAlgorithm build() {
            if(generateInitialSolutionStrategy == null || ruinStrategies == null || recreateStrategies == null
                    || maxRun <= 0) {
                throw new IllegalStateException("Not enough parameters to create RuinAndRecreate object");
            }
            RuinAndRecreateAlgorithm result = new RuinAndRecreateAlgorithm(costCalculator, distanceCalculator);
            result.generateInitialSolutionStrategy = generateInitialSolutionStrategy;
            ruinStrategies.forEach(result::addRuiStrategy);
            recreateStrategies.forEach(result::addRecreateStrategy);
            result.solutionAcceptor = solutionAcceptor;
            result.maxRun = maxRun;
            if(logSolution) {
                result.logger = new SolutionLogger(maxRun);
                result.logFileName = logFileName;
            }
            return result;
        }
    }

}
