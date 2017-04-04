package vhr.SolutionAcceptor;

/**
 * Created by quachv on 4/4/2017.
 */
public interface ISolutionAcceptor {
    boolean acceptSolution(double prevCost, double newCost);
    void updateAcceptor();
    boolean canTerminateSearching();
}
