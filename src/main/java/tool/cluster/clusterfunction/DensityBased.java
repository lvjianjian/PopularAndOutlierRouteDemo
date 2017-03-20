package tool.cluster.clusterfunction;

import model.Route;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/19.
 */
public class DensityBased extends AbstractClusterFunction {

    private double E = 15;//e-domain size
    private int MINPTS = 10;//min traj num
    private List<List<Route>> result;

    private List<List<Integer>> multiReference;


    public List<List<Integer>> getMultiReferences(){
        return multiReference;
    }


    public double getE() {
        return E;
    }

    public void setE(double e) {
        E = e;
    }

    public int getMINPTS() {
        return MINPTS;
    }

    public void setMINPTS(int MINPTS) {
        this.MINPTS = MINPTS;
    }

    public DensityBased(List<Route> routes) {
        super(routes);
    }


    @Override
    public List<List<Route>> cluster() {
        //clear or create datastruct
        if (result == null)
            result = new LinkedList<>();
        else {
            result.clear();
        }
        if (multiReference == null) {
            multiReference = new LinkedList<>();
        } else {
            multiReference.clear();
        }

        Map<Integer, Set<Integer>> coreAndDensity = new HashMap<>();
        //find core object and it's directly density-reachable
        List<Route> routes = getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            //judge the i object is core
            int pts = 0;
            Set<Integer> ddr = new HashSet<>();
            for (int j = 0; j < routes.size(); j++) {
                if (distance(i, j) < E) {//in e-domain
                    //constraint filter
                    if (filter(i, j)) {
                        pts += routes.get(j).getFrequency();
                        ddr.add(j);
                    }
                }
            }
            if (pts >= MINPTS)//satisfy minpts,it is core object
                coreAndDensity.put(i, ddr);
        }
        System.out.println("find core object and it's directly density-reachabl e finish");
        List<Set<Integer>> r = new LinkedList<>();

        //get clusterByKMedoids according to core object and directly density-reachable
        Set<Integer> coreindexs = coreAndDensity.keySet();
        Set<Integer> coreFound = new HashSet<>();//the core object which has been found
        Set<Integer> visied = new HashSet<>();
        final int[] k = {0};
        coreindexs.forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                if (!coreFound.contains(integer)) {//the core has not been found
//                    System.out.println("start clusterByKMedoids core :" + integer);
                    //store clusterByKMedoids's object
                    Set<Integer> set = new HashSet<Integer>();
                    //cores queue which need to traverse
                    LinkedList<Integer> queue = new LinkedList<Integer>();
                    queue.add(integer);
                    while (!queue.isEmpty()) {
                        Integer core = queue.remove();
//                        System.out.println("from queue get "+core);
                        coreFound.add(core);
                        coreAndDensity.get(core).forEach(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) {
                                set.add(integer);
                                if (!coreFound.contains(integer) && coreindexs.contains(integer))//if not traverse and is core
                                {
                                    if (!visied.contains(integer)) {
                                        queue.add(integer);
                                    }
                                }
                                visied.add(integer);
                            }
                        });
                    }
                    r.add(set);
                }
            }
        });
        System.out.println("find reference routes");
        List<List<Integer>> referenceIndexsSet = referenceChoose.getReferenceIndexs(r,coreAndDensity);
        //save result
        for (int i = 0; i < r.size(); i++) {
//            System.out.println("start the " + i);
            LinkedList<Route> rs = new LinkedList<>();
            Iterator<Integer> iterator = r.get(i).iterator();
            List<Integer> lists = new ArrayList<>();
            int j = 0;
            while (iterator.hasNext()) {
                Integer index = iterator.next();
                rs.add(routes.get(index));
                for (int i1 = 0; i1 < referenceIndexsSet.get(i).size(); i1++) {
                    if (index.equals(referenceIndexsSet.get(i).get(i1))) {
                        lists.add(j);
                        break;
                    }
                }
                ++j;
            }
            multiReference.add(lists);
            result.add(rs);
        }
        return result;
    }

}
