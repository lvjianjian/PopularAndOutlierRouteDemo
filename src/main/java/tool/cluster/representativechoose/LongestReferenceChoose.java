package tool.cluster.representativechoose;

import tool.cluster.clusterfunction.IClusterFunction;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/9/28.
 * 每个类先随机找一个core route作为reference route，然后找离它最远的core route作为reference route,反复直到无core route
 */
public class LongestReferenceChoose extends AbstractReferenceChoose {

    public LongestReferenceChoose(IClusterFunction clusterFunction) {
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
                Set<Integer> copy = new HashSet<Integer>();
                copy.addAll(cores);
                List<Integer> references = new ArrayList<Integer>();
                Integer first = randomCore(integers, cores);
                do {
                    references.add(first);
                    coreAndReachable.get(first).forEach(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) {
                            copy.remove(integer);
                        }
                    });
                } while ((first = nextFarthestCore(copy, first)) != -1);
                referenceInfexs.add(references);
            }
        });
        return referenceInfexs;
    }

    private Integer nextFarthestCore(Set<Integer> copy, Integer current) {
        final double[] maxDist = {0};
        final int[] next = {-1};
        copy.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                double distance = clusterFunction.getDistanceFunction().distance(current, integer);
                if (distance > maxDist[0]) {
                    maxDist[0] = distance;
                    next[0] = integer;
                }
            }
        });
        return next[0];
    }


    private int randomCore(Set<Integer> routeindexes, Set<Integer> coreindexes) {
        int random = -1;
        Random random1 = new Random();
        int i = random1.nextInt(routeindexes.size());
        Iterator<Integer> iterator = routeindexes.iterator();
        while (i > -1) {
            random = iterator.next();
            --i;
        }
        if (coreindexes.contains(random))
            return random;
        return randomCore(routeindexes, coreindexes);
    }

}
