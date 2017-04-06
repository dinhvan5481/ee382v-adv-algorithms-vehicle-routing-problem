package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.SolutionAcceptor.ISolutionAcceptor;
import vhr.core.*;

import java.io.FileWriter;
import java.io.IOException;

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
    protected int maxRun;

    protected SolutionLogger logger;
    protected String logFileName;

    protected RuinAndRecreateAlgorithm() {
        maxRun = 0;
    }

    protected RuinAndRecreateAlgorithm(ICostCalculator costCalculator, IDistanceCalculator distanceCalculator) {
        this();
        this.costCalulator = costCalculator;
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public VRPSolution solve(VRPInstance vrpInstance) {
        double ruinRate = 0.3;
        int numberOfNodesWillBeRuined = 0;
        int runCounter = 0;
        boolean continueSearchSolutionFlag = true;
//        VRPSolution vrpSolutionK = generateInitialSolution(vrpInstance);
//        VRPSolution vrpSolutionK_p1 = null;
//        RuinStragegy ruinStragegy = null;
//        HashSet<Customer> unServerdCustomers = new HashSet<>();
//
//        // TODO: need to change for loop to while loop which checks on running flag
//        while (continueSearchSolutionFlag) {
//            ruinStragegy = chooseRuinStragegy();
//            ruinRate = selectRuinRate();
//            vrpSolutionK_p1 = ruinStragegy(vrpInstance, vrpSolutionK, ruinRate);
//            recreate(vrpSolutionK_p1, unServerdCustomers);
//            vrpSolutionK = decideWhatSolutionWillBeUsedForNextLoop(vrpSolutionK, vrpSolutionK_p1);
//            runCounter++;
//            checkIfNeedToRunMoreSearch(vrpSolutionK, vrpSolutionK_p1, runCounter);
//        }


//        return vrpSolutionK;

        VRPSolution vrpSolutionK = generateInitialSolutionStrategy.generateSolution(vrpInstance);
        VRPSolution vrpSolutionK_p1 = null;
        if(logger != null) {
            logger.addSolutionCost(vrpSolutionK.getSolutionCost());
        }
        while (continueSearchSolutionFlag) {
            vrpSolutionK_p1 = ruinStrategy.ruin(vrpInstance, vrpSolutionK, ruinRate);
            recreateStrategy.recreate(vrpSolutionK_p1, ruinStrategy.getRemovedCustomerIds());
            if(solutionAcceptor.acceptSolution(vrpSolutionK.getSolutionCost(), vrpSolutionK_p1.getSolutionCost())) {
                vrpSolutionK = vrpSolutionK_p1;
            }
            solutionAcceptor.updateAcceptor();
            runCounter++;
            continueSearchSolutionFlag = continueToSearch(runCounter, solutionAcceptor.canTerminateSearching());
            if(logger != null) {
                logger.addSolutionCost(vrpSolutionK.getSolutionCost());
            }
        }
        if(logger != null) {
            logger.toCSV(logFileName);
        }
        return vrpSolutionK;
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
            costLog[logIndex] = cost;
        }

        public void toCSV(String fileName) {
            FileWriter fileWriter = null;
            int runIndex = 0;
            try {
                fileWriter = new FileWriter(fileName);
                while (costLog[runIndex] > 0) {
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
