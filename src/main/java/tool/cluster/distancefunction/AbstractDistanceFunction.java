package tool.cluster.distancefunction;

import model.Route;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lzj on 2016/7/25.
 */
public abstract class AbstractDistanceFunction implements IDistanceFunction{

    private static final int INVALID = -1;
    protected double [][] ds = null;

    /**
     * route list which is to clusterByKMedoids
     */
    private List<Route> routes;

    public AbstractDistanceFunction(List<Route> routes){
        setRoutes(routes);
    }


    public List<Route> getRoutes() {
        if(routes == null)
            throw new NullPointerException("routes is null");
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
        clean();
    }

    @Override
    public void clean() {
        if(this.routes != null){
            if(ds == null)
                ds = new double[getRoutes().size()][getRoutes().size()];
            for (int i = 0; i < getRoutes().size(); i++) {
                Arrays.fill(ds[i], INVALID);
            }
        }
    }

    @Override
    public double distance(int r1_index, int r2_index) {
        if (ds[r1_index][r2_index] == INVALID) {
            ds[r1_index][r2_index] = ds[r2_index][r1_index] = distance(getRoutes().get(r1_index), getRoutes().get(r2_index));
        }
        return ds[r1_index][r2_index];
    }

    /**
     * compute difference between route and route1
     * @param route
     * @param route1
     * @return
     */
    public abstract double distance(Route route, Route route1);


}
