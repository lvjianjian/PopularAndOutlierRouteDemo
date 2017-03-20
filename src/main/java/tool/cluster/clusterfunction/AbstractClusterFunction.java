package tool.cluster.clusterfunction;

import model.Route;
import tool.cluster.distancefunction.IDistanceFunction;
import tool.cluster.distancefunction.constraint.IConstraint;
import tool.cluster.representativechoose.IReferenceChoose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lzj on 2016/7/19.
 */
public abstract class AbstractClusterFunction implements IClusterFunction {

    /**
     * route list which is to clusterByKMedoids
     */
    private List<Route> routes;

    public AbstractClusterFunction(List<Route> routes) {
        this.routes = routes;
        afterSetRoutes();
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        if (this.getRoutes() != routes) {
            this.routes = routes;
            afterSetRoutes();
        }
    }

    protected void afterSetRoutes() {
        if (distanceFunction != null)
            distanceFunction.clean();
        for (int i = 0; i < constraints.size(); i++) {
            constraints.get(i).clean();
        }
//        ratio = new double[getRoutes().size()][getRoutes().size()];
//        for (int i = 0; i < getRoutes().size(); i++) {
//            Arrays.fill(ratio[i], -1);
//        }
    }

    /**
     * measure function between routes
     */
    private IDistanceFunction distanceFunction = null;

    @Override
    public void setDistanceFunction(IDistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
        distanceFunction.clean();
        constraints.clear();
    }

    @Override
    public IDistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    /**
     * compute distance between r1 and r2
     *
     * @param r1_index
     * @param r2_index
     * @return
     */
    public double distance(int r1_index, int r2_index) {
        return distanceFunction.distance(r1_index, r2_index);
    }

    /**
     * some constraint between two routes,if satisfy constraint,they are similar
     */
    private List<IConstraint> constraints = new ArrayList<>();

    @Override
    public void addConstraint(IConstraint constraint){
        constraints.add(constraint);
    }

    protected boolean filter(int r1_index, int r2_index ){
        boolean ok = true;
        for (int i = 0; i < constraints.size(); i++) {
            if(constraints.get(i).similar(r1_index, r2_index))
                continue;
            else{
                ok = false;
                break;
            }
        }
        return ok;
    }

    protected IReferenceChoose referenceChoose = null;

    @Override
    public void setReferenceChoose(IReferenceChoose referenceChoose) {
        this.referenceChoose = referenceChoose;
    }
}
