package tool.cluster.representativechoose;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lzj on 2016/7/26.
 */
public interface IReferenceChoose {
    List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster);

    List<List<Integer>> getReferenceIndexs(List<Set<Integer>> routeindexsOfCluster, Map<Integer, Set<Integer>> coreAndReachable);
}
