package vhr.RuinAndRecreateAlgorithm.Recreate;

import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.core.VRPSolution;

import java.util.List;

/**
 * Created by dinhvan5481 on 4/1/17.
 */
public interface IRecreateStrategy {
    VRPSolution recreate(VRPSolution ruinedSolution, List<Integer> removedCustomerIds);
}
