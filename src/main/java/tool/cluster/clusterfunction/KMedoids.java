package tool.cluster.clusterfunction;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.ODPair;
import model.Route;
import model.Trajectory;
import service.cmpl.RoutesService;
import tool.cluster.distancefunction.DistanceFunction;
import tool.cluster.distancefunction.EditDistancePlusQuantityFunction;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/18.
 */
public class KMedoids extends AbstractClusterFunction {
    /**
     * clusterByKMedoids num
     */
    private int k = 3;
    /**
     * route list which is to clusterByKMedoids
     */
    private boolean finish = false;
    private List<List<Route>> result;
    private List<Integer> references;
    private List<List<Integer>> multiReference;


    public List<List<Integer>> getMultiReference() {
        return multiReference;
    }

    private double curr_total_dis = Integer.MAX_VALUE;
    private double new_total_dis = 0;

    public KMedoids(int k, List<Route> routes) {
        super(routes);
        this.k = k;
        if (k < 1)
            k = 3;

    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        if (this.getK() != k) {
            this.k = k;
            finish = false;
        }
    }


    @Override
    public List<List<Route>> cluster() {

        if (getK() >= getRoutes().size()) {
            setK(getRoutes().size());
        }

        if (finish && result != null) {
            return result;
        }
        if (result == null) {
            result = new LinkedList<>();
        } else {
            result.clear();
        }

        if (references == null) {
            references = new LinkedList<>();
        } else {
            references.clear();
        }
        //k-medoids algo
        int[] initK = initK();
        Map<Integer, List<Integer>> curr_allocates = allocate(initK);
        do {
            curr_total_dis = new_total_dis;
            curr_allocates = nextClusters(curr_allocates);//reallcate
            System.out.println("new_total_dis - curr_total_dis = " + (new_total_dis - curr_total_dis));
        } while (Math.abs(new_total_dis - curr_total_dis) > 2);//if result is better

        multiReference = new LinkedList<>();

        //save result
        curr_allocates.forEach(new BiConsumer<Integer, List<Integer>>() {
            @Override
            public void accept(Integer integer, List<Integer> integers) {
//                //print
//                System.out.println("reference id:" + getRoutes().get(integer).getId());
//                System.out.println("clusters:");
//                for (int i = 0; i < integers.size(); i++) {
//                    System.out.print(getRoutes().get(integers.get(i)).getId() + ",");
//                }
//                System.out.println();
                List<Route> r = new LinkedList<Route>();
                for (int i = 0; i < integers.size(); i++) {
                    r.add(getRoutes().get(integers.get(i)));
//                    System.out.println(integer + "," + integers.get(i));
                    if (integer.equals(integers.get(i))) {
                        LinkedList<Integer> integers1 = new LinkedList<>();
                        integers1.add(i);
                        multiReference.add(integers1);
                    }
                }
                result.add(r);
            }
        });


        System.out.println(result.size());
        System.out.println(multiReference.size());
        return result;
    }


    private Map<Integer, List<Integer>> nextClusters(Map<Integer, List<Integer>> clusters) {
        int[] new_k_routes = new int[getK()];
        final int[] j = {0};
        clusters.forEach(new BiConsumer<Integer, List<Integer>>() {
            @Override
            public void accept(Integer integer, List<Integer> integers) {
                new_k_routes[j[0]++] = nextBetterRouteIndex(integers);
            }
        });

        return allocate(new_k_routes);
    }

    private int nextBetterRouteIndex(List<Integer> cluster) {
        int better = 0;
        double min_total_dis = Integer.MAX_VALUE;
        for (int i = 0; i < cluster.size(); i++) {
            int route_index = cluster.get(i);
            double sum = 0;
            for (int j = 0; j < cluster.size(); j++) {
                sum += distance(route_index, cluster.get(j));
            }
            if (sum < min_total_dis) {
                sum = min_total_dis;
                better = route_index;
            }
        }
        return better;
    }


    private Map<Integer, List<Integer>> allocate(int[] ks) {
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < ks.length; i++) {
            clusters.put(ks[i], new LinkedList<>());
        }
        new_total_dis = 0;
        for (int i = 0; i < getRoutes().size(); i++) {//allocate r to clusterByKMedoids
            //the route is medoids
            double min = Integer.MAX_VALUE;
            int index = -1;
            for (int j = 0; j < getK(); j++) {
                //find minimum
                double edit_distance = distance(i, ks[j]);
//                System.out.println(i + " and " + ks[j] + " edit distance is " + edit_distance);
                if (edit_distance < min) {//find min than now
                    min = edit_distance;
                    index = ks[j];
                }
            }
            new_total_dis += min;
            //allocate r to minimum dis clusterByKMedoids
            clusters.get(index).add(i);
//            System.out.println("new total dis:" + new_total_dis);
        }
//        System.out.println("allocate end");
        return clusters;
    }

    private int[] initK() {
        return computeK();
    }

    /**
     * by equation 5 in paper
     *
     * @return
     */
    private int[] computeK() {
        int[] init_k = new int[getK()];
        double[] scores = new double[getRoutes().size()];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = scoreJ(i);
        }
        Set<Integer> hasChoosen = new HashSet<>();
        for (int i = 0; i < getK(); i++) {
            int index = -1;
            double min = Integer.MAX_VALUE;
            for (int j = 0; j < scores.length; j++) {
                if (scores[j] < min && !hasChoosen.contains(j)) {
                    index = j;
                    min = scores[j];
                }
            }
            init_k[i] = index;
            hasChoosen.add(index);
        }
        return init_k;
    }

    private double scoreJ(int index) {
        double r = 0;
        for (int i = 0; i < getRoutes().size(); i++) {
            r += (distance(index, i) / addJ(i));
        }
        return r;
    }

    private double addJ(int index) {
        double r = 0;
        for (int i = 0; i < getRoutes().size(); i++) {
            r += distance(index, i);
        }
        return r;
    }

    private int[] randomK() {
        int[] init_k = new int[getK()];
        Random random = new Random();
        for (int i = 0; i < init_k.length; i++) {
            out:
            while (true) {// be sure not choose before this time
                int index = random.nextInt(getRoutes().size());
                for (int j = 0; j < i; j++) {
                    if (index == init_k[j])
                        continue out;
                }
                init_k[i] = index;
                break out;
            }

        }
        return init_k;
    }

    @Override
    public void afterSetRoutes() {
        super.afterSetRoutes();
        finish = false;
    }

    public List<Integer> getReferences() {

        return references;
    }


    public static void main(String[] args) {
        RouteTrie routeTrie = RouteTrie.readFromXml("D:\\lzj_spark_test\\data\\trie\\59566201082_59566201416.xml");
        Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
        java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();

        List<Route> routes = new LinkedList<>();
        int all_freq = 0;
        while (iterator1.hasNext()) {
            TrieNode next = iterator1.next();
            int route_id = next.route_id();
            scala.collection.immutable.List<Object> objectList = next.vertex_ids();
            List<Long> edgeids = new LinkedList<>();
            scala.collection.Iterator<Object> iterator = objectList.iterator();
            while (iterator.hasNext()) {
                Long next1 = (Long) iterator.next();
                edgeids.add(next1);
            }
            Trajectory trajectory = new Trajectory();
            trajectory.setEdge_ids(edgeids);
            trajectory.setId(route_id);
            trajectory.setFrequency(next.freq());
            all_freq += trajectory.getFrequency();
            routes.add(trajectory);
        }

        KMedoids kMedoids = new KMedoids(2, routes);


        EditDistanceWithWeightFunction distanceFunction = new EditDistanceWithWeightFunction(kMedoids.getRoutes());
        kMedoids.setDistanceFunction(distanceFunction);
        List<List<Route>> clusters = kMedoids.cluster();
    }
}
