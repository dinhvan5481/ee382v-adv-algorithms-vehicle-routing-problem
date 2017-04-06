package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.core.VRPSolution;

import java.util.List;
import java.util.Set;

/**
 * Created by dinhvan5481 on 4/1/17.
 */
public interface IRecreateStrategy {
    VRPSolution recreate(VRPSolution ruinedSolution, Set<Integer> removedCustomerIds);
}
