package vhr.RuinAndRecreateAlgorithm.InitializeSolution;

import vhr.core.VRPInstance;
import vhr.core.VRPSolution;

/**
 * Created by quachv on 3/27/2017.
 */
public interface IGenerateInitialSolutionStrategy {
    VRPSolution generateSolution(VRPInstance vrpInstance);
}
