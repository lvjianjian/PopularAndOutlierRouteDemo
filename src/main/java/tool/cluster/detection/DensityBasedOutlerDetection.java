package tool.cluster.detection;

import com.ada.routecount.main.model.RouteTrie;
import com.ada.routecount.main.model.TrieNode;
import model.Route;
import model.Trajectory;
import tool.cluster.clusterfunction.DensityBased;
import tool.cluster.distancefunction.DistanceFunction;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;
import tool.cluster.distancefunction.IDistanceFunction;
import tool.cluster.model.DensityBasedReferenceRoutes;
import tool.cluster.model.RoutesCluster;
import tool.cluster.representativechoose.AllCoreChoose;
import tool.cluster.representativechoose.IntuitiveReferenceChoose;
import tool.cluster.representativechoose.LongestReferenceChoose;

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
 * Created by lzj on 2016/7/26.
 */
public class DensityBasedOutlerDetection implements IOutlierDetection {
    List<RoutesCluster> routesClusters;
    IDistanceFunction distanceFunction = null;
    private int alpha = 20;

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public IDistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(IDistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public DensityBasedOutlerDetection(List<RoutesCluster> routesClusters) {
        this.routesClusters = routesClusters;
    }


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

        int[] alpha = new int[]{25};// 15, 20, 25, 30,35
        double[] theta = new double[]{0.03};//0.01, 0.02, 0.03, 0.04, 0.05

        for (int k = 0; k < alpha.length; k++) {
            for (int j = 0; j < theta.length; j++) {
                int alpha_temp = alpha[k];
                double minptsRate = theta[j];

                //载入数据
//        String pathname = "/home/zhujie/lvzhongjian/search/routetree/";

                String pathname = tool.Properties.trieFilePath;

                String newdir = pathname + "dbtod\\new\\" + alpha_temp + "_" + minptsRate + "\\";
                new File(newdir).mkdir();

                File dir = new File(pathname);
                String[] list = dir.list();
                for (int i = 0; i < list.length; i++) {
                    String filename = list[i];
                    System.out.println("filename is " + filename);
                    Pattern p = Pattern.compile("(\\d*)_(\\d*).xml");
                    Matcher matcher = p.matcher(filename);
                    if (!matcher.find())
                        continue;
                    long startid = Long.valueOf(matcher.group(1));
                    long endid  = Long.valueOf(matcher.group(2));
                    RouteTrie routeTrie = RouteTrie.readFromXml(pathname + filename);
                    Set<TrieNode> triNodesOfDesitination = routeTrie.getTriNodesOfDesitination(routeTrie.d_id());
                    java.util.Iterator<TrieNode> iterator1 = triNodesOfDesitination.iterator();
                    File file = new File(newdir + filename + ".dbtod.result");
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
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

                    DensityBased densityBased = new DensityBased(routes);

                    densityBased.setE(alpha_temp);
                    densityBased.setMINPTS((int) (all_freq * minptsRate));

                    EditDistanceWithWeightFunction distanceFunction = new EditDistanceWithWeightFunction(densityBased.getRoutes());
                    densityBased.setDistanceFunction(distanceFunction);
                    densityBased.setReferenceChoose(new IntuitiveReferenceChoose(densityBased));
                    List<List<Route>> clusters = densityBased.cluster();
                    List<List<Integer>> multiReferences = densityBased.getMultiReferences();
                    List<RoutesCluster> routesClusters = new LinkedList<>();
                    int referencesize = 0;
                    for (int i1 = 0; i1 < clusters.size(); i1++) {
                        List<Route> routes1 = clusters.get(i1);
                        List<Integer> referenceRouteIndex = multiReferences.get(i1);
                        referencesize += referenceRouteIndex.size();
                        for (int t = 0; t < referenceRouteIndex.size(); t++) {
                            System.out.print(routes1.get(referenceRouteIndex.get(t)).getId() + ",");
                        }
                        routesClusters.add(new RoutesCluster(routes1, referenceRouteIndex));
                    }

                    DensityBasedReferenceRoutes densityBasedReferenceRoutes = new DensityBasedReferenceRoutes(startid,endid);

                    for (int l = 0; l < routesClusters.size(); l++) {
                        RoutesCluster routesCluster = routesClusters.get(l);
                        for (int h = 0; h < routesCluster.getReferenceRouteIndexs().size(); h++) {
                            Route route = routesCluster.getRoutes().get(routesCluster.getReferenceRouteIndexs().get(h));
                            densityBasedReferenceRoutes.addReferenceRoute(route);
                        }
                    }
                    densityBasedReferenceRoutes.save(String.format("referenceRoutes/%d_%d_referenceroutes.txt", startid,endid));

//                    System.out.println();
//                    System.out.println(referencesize);
//                    DensityBasedOutlerDetection densityBasedOutlerDetection = new DensityBasedOutlerDetection(routesClusters);
//                    densityBasedOutlerDetection.setDistanceFunction(distanceFunction);
//                    densityBasedOutlerDetection.setAlpha(alpha_temp);
//
//                    for (int b = 0; b < routes.size(); b++) {
//                        long l = System.currentTimeMillis();
//                        Route detectionRoute = routes.get(b);
//                        boolean score = densityBasedOutlerDetection.isOutlier(detectionRoute);
//                        long time = System.currentTimeMillis() - l;
//                        try {
//                            bw.write(detectionRoute.getId() + "," + score + "," + time);
//                            bw.newLine();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if (bw != null) {
//                        try {
//                            bw.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                bw.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }

                }
            }
        }

    }
}
