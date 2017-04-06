package vhr.core;

/**
 * Created by dinhvan5481 on 3/26/17.
 */
public interface IVRPAlgorithm {
    VRPSolution solve(VRPInstance vrpInstance) throws Exception;
}
