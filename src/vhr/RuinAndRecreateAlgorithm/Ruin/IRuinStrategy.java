package vhr.RuinAndRecreateAlgorithm.Ruin;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

import java.util.Set;

/**
 * Created by quachv on 3/29/2017.
 */
public interface IRuinStrategy {
    VRPSolution ruin(VRPInstance vrpInstance, VRPSolution vrpSolution, double ruinRate);
    Set<Integer> getRemovedCustomerIds();
}
