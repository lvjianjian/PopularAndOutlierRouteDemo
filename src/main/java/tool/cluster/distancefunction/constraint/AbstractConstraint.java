package tool.cluster.distancefunction.constraint;

import model.Route;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lzj on 2016/7/26.
 */
public abstract class AbstractConstraint implements IConstraint {

    protected static final double INVALID = -1;
    /**
     * limit value of constraint
     */
    protected double constraint_limit;

    /**
     * route list which is to clusterByKMedoids
     */
    private List<Route> routes;

    protected double[][] ratio;

    public AbstractConstraint(double constraint_limit,List<Route> routes){
        this.constraint_limit = constraint_limit;
        setRoutes(routes);
    }

    public List<Route> getRoutes() {
        if(routes == null)
            throw new NullPointerException("routes is null");
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        if(routes == null){
            throw new NullPointerException("routes should nou be null");
        }
        this.routes = routes;
        clean();
    }

    @Override
    public void clean() {
        if(this.routes != null){
            if(ratio == null)
                ratio = new double[getRoutes().size()][getRoutes().size()];
            for (int i = 0; i < getRoutes().size(); i++) {
                Arrays.fill(ratio[i], INVALID);
            }
        }
    }
}
