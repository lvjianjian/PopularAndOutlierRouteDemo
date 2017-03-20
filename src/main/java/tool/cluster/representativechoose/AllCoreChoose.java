package tool.cluster.representativechoose;

import tool.cluster.clusterfunction.IClusterFunction;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/10/22.
 */
public class AllCoreChoose extends AbstractReferenceChoose {

    public AllCoreChoose(IClusterFunction clusterFunction) {
        super(clusterFunction);
    }

    @Override
    public List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster) {
        return null;
    }

    @Override
    public List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster, Map<Integer, Set<Integer>> coreAndReachable) {
        List<List<Integer>> referenceInfexs = new LinkedList<>();
        Set<Integer> cores = coreAndReachable.keySet();
        routeindexsOfCluster.forEach(new Consumer<Set<Integer>>() {
            @Override
            public void accept(Set<Integer> integers) {
                List<Integer> references = new ArrayList<Integer>();
                integers.forEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        if(cores.contains(integer))
                            references.add(integer);
                    }
                });
                referenceInfexs.add(references);
            }
        });
        return referenceInfexs;
    }


}
