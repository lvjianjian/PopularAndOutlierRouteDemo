package tool.cluster.representativechoose;

import tool.cluster.clusterfunction.IClusterFunction;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/9/28.
 * 每个类找能包含新route的core route
 */
public class IntuitiveReferenceChoose extends AbstractReferenceChoose {
    public IntuitiveReferenceChoose(IClusterFunction clusterFunction) {
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
                copy.addAll(integers);
                List<Integer> references = new ArrayList<Integer>();
                Integer first = randomCore(integers, cores);
                List<Integer> waiting_cores = new LinkedList<Integer>();
                waiting_cores.add(first);
                while (!waiting_cores.isEmpty()) {
                    Integer waiting = waiting_cores.remove(0);
                    final boolean[] waiting_add = {false};
                    coreAndReachable.get(waiting).forEach(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) {
                            if (integer == waiting) {
                                copy.remove(waiting);
                            } else {
                                if (cores.contains(integer) && copy.contains(integer))
                                    waiting_cores.add(integer);
                                if (copy.contains(integer)) {
                                    if (!waiting_add[0]) {
                                        references.add(waiting);
                                        waiting_add[0] = true;
                                    }
                                    copy.remove(integer);
                                }
                            }
                        }

                    });
                }
                referenceInfexs.add(references);
            }
        });
        return referenceInfexs;
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
