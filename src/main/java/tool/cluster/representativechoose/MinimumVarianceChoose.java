package tool.cluster.representativechoose;

import model.Route;
import tool.cluster.clusterfunction.IClusterFunction;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/26.
 * 每个类找使得方差最小的route作为reference route
 */
public class MinimumVarianceChoose extends AbstractReferenceChoose {

    private List<Route> routes = null;

    public MinimumVarianceChoose(IClusterFunction clusterFunction,List<Route> routes) {
        super(clusterFunction);
        this.routes = routes;
    }


    /**
     * choose reference by minimum variance
     * @param route_indexs
     * @return
     */
    private int getReferenceIndex(Set<Integer> route_indexs) {
        final int[] index = {-1};
        final double[] min_variance = {Integer.MAX_VALUE};
        route_indexs.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Iterator<Integer> iterator = route_indexs.iterator();
                //calculate mean
                double mean = 0;
                while (iterator.hasNext()) {
                    int i = iterator.next();
                    mean += clusterFunction.getDistanceFunction().distance(integer, i);
                }
                mean = mean / route_indexs.size();
                //calculate variance of each route based on it and it's distance
                int variance = 0;
                iterator = route_indexs.iterator();
                while (iterator.hasNext()) {
                    int i = iterator.next();
                    variance += Math.pow(clusterFunction.getDistanceFunction().distance(integer, i), 2);
                }
                variance = variance / route_indexs.size();
                //get min-variance and it's index
                if (variance < min_variance[0]) {
                    min_variance[0] = variance;
                    index[0] = integer;
                }
            }
        });
        return index[0];
    }

    @Override
    public List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster) {
        List<List<Integer>> referenceInfexs = new LinkedList<>();
        for (int i = 0; i < routeindexsOfCluster.size(); i++) {
            Set<Integer> route_indexs = routeindexsOfCluster.get(i);
            int referenceIndex = getReferenceIndex(route_indexs);
            List<Integer> referenceIndexs = new ArrayList<>();
            referenceIndexs.add(referenceIndex);
            referenceInfexs.add(referenceIndexs);
        }
        return referenceInfexs;
    }

    @Override
    public List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster, Map<Integer, Set<Integer>> coreAndReachable) {
        return getReferenceIndexs(routeindexsOfCluster);
    }
}
