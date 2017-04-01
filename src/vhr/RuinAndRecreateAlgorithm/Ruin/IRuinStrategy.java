package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.Customer;
import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.List;

/**
 * Created by quachv on 3/29/2017.
 */
public interface IRuinStrategy {
    VRPSolution ruin(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate);
    List<Integer> getRemovedCustomerIds();
}
