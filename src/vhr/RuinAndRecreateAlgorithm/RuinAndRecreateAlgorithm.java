package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.SolutionAcceptor.ISolutionAcceptor;
import vhr.core.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static vhr.utils.StringUtil.appendStringLine;

/**
 * Created by dinhvan5481 on 3/26/17.
 */
public class RuinAndRecreateAlgorithm implements IVRPAlgorithm {

    protected ICostCalculator costCalulator;
    protected IDistanceCalculator distanceCalculator;
    protected IGenerateInitialSolutionStrategy generateInitialSolutionStrategy;
    protected IRuinStrategy ruinStrategy;
    protected IRecreateStrategy recreateStrategy;
    protected ISolutionAcceptor solutionAcceptor;

    protected ArrayList<IRuinStrategy> ruinStrategies;
    protected ArrayList<IRecreateStrategy> recreateStrategies;
    protected RouletteWheel ruinedRouletteWheel;
    protected RouletteWheel recreateRouletteWheel;
    protected int maxRun;

    protected SolutionLogger logger;
    protected String logFileName;

    protected RuinAndRecreateAlgorithm() {
        maxRun = 0;
        ruinStrategies = new ArrayList<>();
        recreateStrategies = new ArrayList<>();
    }

    protected RuinAndRecreateAlgorithm(ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
        this();
        this.costCalulator = costCalculator;
        this.distanceCalculator = distanceCalculator;
    }

    public void addRuiStrategy(IRuinStrategy ruinStrategy) {
        ruinStrategies.add(ruinStrategy);
    }

    public void addRecreateStrategy(IRecreateStrategy recreateStrategy) {
        recreateStrategies.add(recreateStrategy);
    }

    @Override
    public VRPSolution solve(VRPInstance vrpInstance) throws Exception {
        double ruinRate = 0.3;
        int runCounter = 0;
        boolean continueSearchSolutionFlag = true;

        double rouletteWheelParametterSensitive = 0.9;
        ruinedRouletteWheel = new RouletteWheel(ruinStrategies.size(), rouletteWheelParametterSensitive);
        recreateRouletteWheel = new RouletteWheel(recreateStrategies.size(), rouletteWheelParametterSensitive);
        RouletteWheel.SolutionType currentSolutionType;
        IRuinStrategy currentRuinStrategy;
        IRecreateStrategy currentRecreateStrategy;

        VRPSolution vrpSolutionK = generateInitialSolutionStrategy.generateSolution(vrpInstance);
        VRPSolution vrpSolutionK_p1 = null;
        VRPSolution bestSolution = null;
        double preSolutionCost = vrpSolutionK.getSolutionCost();
        double bestSolutionCost = vrpSolutionK.getSolutionCost();
        double currentSolutionCost = 0;
        if(logger != null) {
            logger.addSolutionCost(vrpSolutionK.getSolutionCost());
        }
        while (continueSearchSolutionFlag) {
            currentRuinStrategy = ruinStrategies.get(ruinedRouletteWheel.roll());
            currentRecreateStrategy = recreateStrategies.get(recreateRouletteWheel.roll());

            vrpSolutionK_p1 = currentRuinStrategy.ruin(vrpInstance, vrpSolutionK, ruinRate);
            vrpSolutionK_p1 = currentRecreateStrategy.recreate(vrpSolutionK_p1, ruinStrategy.getRemovedCustomerIds());

            if(!vrpSolutionK_p1.isSolutionValid()) {
                StringBuilder sb = new StringBuilder();
                appendStringLine(sb, "Recreate solution is invalid: run counter: " + runCounter);
                appendStringLine(sb, vrpSolutionK_p1.toString());
                throw new Exception(sb.toString());
            }

            currentSolutionCost = vrpSolutionK_p1.getSolutionCost();
            if(currentSolutionCost < preSolutionCost) {
                vrpSolutionK = vrpSolutionK_p1;
                preSolutionCost = currentSolutionCost;
                if(currentSolutionCost < bestSolutionCost) {
                    bestSolutionCost = currentSolutionCost;
                    bestSolution = vrpSolutionK_p1;
                    currentSolutionType = RouletteWheel.SolutionType.BEST_SOLUTION;
                } else {
                    currentSolutionType = RouletteWheel.SolutionType.BETTER_SOLUTION;
                }
            } else {
                if(solutionAcceptor.acceptSolution(preSolutionCost, currentSolutionCost)) {
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
            if(logger != null) {
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
        protected ICostCalculator costCalulator;
        protected IDistanceCalculator distanceCalculator;
        protected IGenerateInitialSolutionStrategy generateInitialSolutionStrategy;
        protected IRuinStrategy ruinStrategy;
        protected IRecreateStrategy recreateStrategy;
        protected ISolutionAcceptor solutionAcceptor;
        protected int maxRun;

        protected boolean logSolution;
        protected String logFileName;



        public Builder(ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
            this.costCalulator = costCalculator;
            this.distanceCalculator = distanceCalculator;
            maxRun = 0;
            logSolution = false;
        }

        public Builder setInitializeSolutionStrategy(IGenerateInitialSolutionStrategy generateInitialSolution) {
            this.generateInitialSolutionStrategy = generateInitialSolution;
            return this;
        }

        public Builder setRuinStrategy(IRuinStrategy ruinStrategy) {
            this.ruinStrategy = ruinStrategy;
            return this;
        }

        public Builder setRecreateStrategy(IRecreateStrategy recreateStrategy) {
            this.recreateStrategy = recreateStrategy;
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
            if(generateInitialSolutionStrategy == null || ruinStrategy == null || recreateStrategy == null
                    || solutionAcceptor == null|| maxRun <= 0) {
                throw new IllegalStateException("Not enough parameters to create RuinAndRecreate object");
            }
            RuinAndRecreateAlgorithm result = new RuinAndRecreateAlgorithm(costCalulator, distanceCalculator);
            result.generateInitialSolutionStrategy = generateInitialSolutionStrategy;
            result.ruinStrategy = ruinStrategy;
            result.recreateStrategy = recreateStrategy;
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
