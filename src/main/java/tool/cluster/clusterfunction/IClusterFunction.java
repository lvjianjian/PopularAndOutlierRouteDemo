package tool.cluster.clusterfunction;

import model.Route;
import tool.cluster.distancefunction.IDistanceFunction;
import tool.cluster.distancefunction.constraint.IConstraint;
import tool.cluster.model.RoutesCluster;
import tool.cluster.representativechoose.IReferenceChoose;

import java.util.List;

/**
 * Created by lzj on 2016/7/19.
 */
public interface IClusterFunction {

    /**
     * cluster
     * @return cluster list ,each list includes routes which are in this cluster
     */
    List<List<Route>> cluster();


    /**
     * add constraint class,this must be after set distancefunction
     * @param constraint
     */
    void addConstraint(IConstraint constraint);

    /**
     * set distanceFunction class
     * after set new distanceFuntion,all constraints will remove
     * @param distanceFunction
     */
    void setDistanceFunction(IDistanceFunction distanceFunction);

    IDistanceFunction getDistanceFunction();


    void setReferenceChoose(IReferenceChoose referenceChoose);
}
