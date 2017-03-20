package tool.cluster.distancefunction.constraint;

import model.Route;
import tool.cluster.distancefunction.DistanceFunction;

import java.util.List;

/**
 * Created by lzj on 2016/7/26.
 */
public class OverlapConstraint extends AbstractConstraint {


    public OverlapConstraint(double constraint_limit, List<Route> routes) {
        super(constraint_limit, routes);
    }

    /**
     * ovarlap ratio must beyond the limit
     * @param r1_index
     * @param r2_index
     * @return
     */
    @Override
    public boolean similar(int r1_index, int r2_index) {
        return getOverlapRatio(r1_index,r2_index) >= constraint_limit;
    }

    private double getOverlapRatio(int coreRouteIndex, int compareRouteIndex) {
        if (ratio[coreRouteIndex][compareRouteIndex] == INVALID) {
            ratio[coreRouteIndex][compareRouteIndex] = ratio[compareRouteIndex][coreRouteIndex]
                    = DistanceFunction.getOverlapRatio(getRoutes().get(coreRouteIndex), getRoutes().get(compareRouteIndex));
        }
        return ratio[coreRouteIndex][compareRouteIndex];
    }
}
