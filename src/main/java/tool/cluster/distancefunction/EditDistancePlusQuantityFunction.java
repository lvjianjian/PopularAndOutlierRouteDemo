package tool.cluster.distancefunction;

import model.Edge;
import model.Route;

import java.util.List;

/**
 * Created by lzj on 2016/10/22.
 */
public class EditDistancePlusQuantityFunction extends EditDistanceWithWeightFunction {

    public EditDistancePlusQuantityFunction(List<Route> routes) {
        super(routes);
    }

    @Override
    public double distance(Route route, Route route1) {
       return super.distance(route,route1)/(route.getFrequency() + route1.getFrequency());
    }


}
