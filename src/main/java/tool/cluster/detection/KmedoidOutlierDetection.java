package tool.cluster.detection;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.Route;
import model.Trajectory;
import tool.cluster.clusterfunction.KMedoids;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;
import tool.cluster.distancefunction.IDistanceFunction;
import tool.cluster.model.RoutesCluster;
import tool.cluster.representativechoose.IntuitiveReferenceChoose;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzj on 2016/10/24.
 */
public class KmedoidOutlierDetection implements IOutlierDetection {

    List<RoutesCluster> routesClusters;
    IDistanceFunction distanceFunction = null;
    private int alpha = 20;

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public KmedoidOutlierDetection(List<RoutesCluster> routesClusters) {

        this.routesClusters = routesClusters;
    }

    public void setDistanceFunction(IDistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    @Override
    public boolean isOutlier(Route detectionRoute) {
        for (int i = 0; i < routesClusters.size(); i++) {
            RoutesCluster routesCluster = routesClusters.get(i);
            for (int j = 0; j < routesCluster.getReferenceRouteIndexs().size(); j++) {
                Route route = routesCluster.getRoutes().get(routesCluster.getReferenceRouteIndexs().get(j));
                if (distanceFunction.distance(detectionRoute, route) < alpha)
                    return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        int[] ks = new int[]{2, 4, 6, 8, 10, 12};
        int[] alphas = new int[]{15, 20, 25, 30, 35};

        for (int k = 0; k < ks.length; k++) {
            int K = ks[k];

            //载入数据
//        String pathname = "/home/zhujie/lvzhongjian/search/routetree/";
            String pathname = "D:\\lzj_spark_test\\data\\trie\\";


            File dir = new File(pathname);
            String[] list = dir.list();
            for (int i = 0; i < list.length; i++) {
                String filename = list[i];
                System.out.println("filename is " + filename);
                Pattern p = Pattern.compile("(\\d*)_(\\d*).xml");
                Matcher matcher = p.matcher(filename);
                if (!matcher.find())
                    continue;
                RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);
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

                KMedoids kMedoids = new KMedoids(K, routes);


                EditDistanceWithWeightFunction distanceFunction = new EditDistanceWithWeightFunction(kMedoids.getRoutes());
                kMedoids.setDistanceFunction(distanceFunction);
                List<List<Route>> clusters = kMedoids.cluster();
                List<List<Integer>> multiReferences = kMedoids.getMultiReference();


                List<RoutesCluster> routesClusters = new LinkedList<>();
                for (int i1 = 0; i1 < clusters.size(); i1++) {
                    List<Route> routes1 = clusters.get(i1);
                    List<Integer> referenceRouteIndex = multiReferences.get(i1);
                    for (int t = 0; t < referenceRouteIndex.size(); t++) {
                        System.out.print(routes1.get(referenceRouteIndex.get(t)).getId() + ",");
                    }
                    routesClusters.add(new RoutesCluster(routes1, referenceRouteIndex));
                }
                KmedoidOutlierDetection kmedoidOutlierDetection = new KmedoidOutlierDetection(routesClusters);
                kmedoidOutlierDetection.setDistanceFunction(distanceFunction);

                for (int i1 = 0; i1 < alphas.length; i1++) {
                    int alpha = alphas[i1];
                    kmedoidOutlierDetection.setAlpha(alpha);
                    String newdir = pathname + "kmtod\\new\\" + K + "_" + alpha + "\\";
                    File file1 = new File(newdir);
                    if(!file1.exists())
                        file1.mkdir();
                    File file = new File(newdir + filename + ".kmtod.result");
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    for (int b = 0; b < routes.size(); b++) {
                        long l = System.currentTimeMillis();
                        Route detectionRoute = routes.get(b);
                        boolean score = kmedoidOutlierDetection.isOutlier(detectionRoute);
                        long time = System.currentTimeMillis() - l;
                        try {
                            bw.write(detectionRoute.getId() + "," + score + "," + time);
                            bw.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (bw != null) {
                        try {
                            bw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                bw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }


            }
        }
    }
}
