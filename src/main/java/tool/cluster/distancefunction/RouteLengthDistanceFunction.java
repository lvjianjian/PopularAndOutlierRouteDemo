package tool.cluster.distancefunction;

import model.Route;

import java.util.List;

/**
 * Created by lzj on 2016/7/26.
 * get distance by  difference between the two routes' long
 */
public class RouteLengthDistanceFunction extends AbstractDistanceFunction{

    public RouteLengthDistanceFunction(List<Route> routes) {
        super(routes);
    }

    @Override
    public double distance(Route route, Route route1) {
        return Math.abs(route.getAllDis() - route1.getAllDis());
    }


}
