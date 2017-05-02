package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.Customer;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

/**
 * Created by quachv on 3/27/2017.
 */
public interface IGenerateInitialSolutionStrategy {
    void setBaseCustomer(Customer customer);
    VRPSolution generateSolution(VRPInstance vrpInstance);
}
