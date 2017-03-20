package web.rest;

import model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import service.cmpl.GetKMPR;
import service.cmpl.RoutesService;
import tool.Properties;
import tool.cluster.clusterfunction.DensityBased;
import tool.cluster.clusterfunction.KMedoids;
import tool.cluster.distancefunction.EditDistanceFunction;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;
import tool.cluster.distancefunction.RouteLengthDistanceFunction;
import tool.cluster.distancefunction.constraint.OverlapConstraint;
import tool.cluster.model.RoutesCluster;
import tool.cluster.representativechoose.MinimumVarianceChoose;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/15.
 */
@RestController
@RequestMapping(value = "/rest/routes")
public class RoutesRest {
    @Autowired
    private RoutesService routesService;

    @RequestMapping(value = "/getODPairForTable.json", method = RequestMethod.POST)
    @ResponseBody
    public List<ODPair> getPairForTable(HttpServletRequest request) {
        System.out.println("request getODPairForTable");
        List<ODPair> odPairList = routesService.getOdPairList();
        System.out.println(odPairList.size());
        return odPairList;
    }

    /**
     * @param startNo start_vertex_id
     * @param disNo   end_vertex_id
     * @param index   index in the odpairlist
     * @return List<[route.frequency,nodes]>
     */
    @RequestMapping(value = "/getAllRoutesByODPairIndex.json", method = RequestMethod.POST)
    public List<Route> getRoutes(long startNo, long disNo, int index) {
        ODPair odPair = routesService.getOdPairList().get(index);
//        List<Object[]> results = new LinkedList<>();
//        for (int i = 0; i < odPair.getRoute_num(); i++) {
//            Route route = odPair.getRoutes().get(i);
//            List<Vertex> vertexs = route.getVertexs();
//            //from vertexs to nodes
//            List<Node> nodes = new ArrayList<>();
//            for (Vertex vertex : vertexs) {
//                nodes.add(new Node(vertex.getId(), vertex.getLatitude(), vertex.getLongitude()));
//            }
//            results.add(new Object[]{route.getFrequency(), nodes});
//        }

        return odPair.getRoutes();
    }

    @RequestMapping(value = "/getVertex.json", method = RequestMethod.POST)
    public Node getVertex(long vertex_id) {
        Vertex vertex = routesService.getRoadNet().getVertex_map().get(vertex_id);
        return new Node(vertex_id, vertex.getLatitude(), vertex.getLongitude());
    }

    @RequestMapping(value = "/clusterByKMedoids.json", method = RequestMethod.POST)
    public List<RoutesCluster> clusterByKMedoids(int index, int k) {
        ODPair odPair = routesService.getOdPairList().get(index);
        KMedoids kMedoids = new KMedoids(k, odPair.getRoutes());
        kMedoids.setDistanceFunction(new EditDistanceFunction(kMedoids.getRoutes()));
        List<List<Route>> clusters = kMedoids.cluster();
        List<Integer> references = kMedoids.getReferences();
        List<RoutesCluster> r = new LinkedList<>();
        System.out.println(clusters.size() + "," + references.size());
        for (int i = 0; i < clusters.size(); i++) {
            List<Integer> list = new ArrayList<>();
            list.add(references.get(i));
            r.add(new RoutesCluster(clusters.get(i), list));
        }
        System.out.println("request clusterByKMedoids finish");
        return r;
    }


    DensityBased clusterFunction = null;

    @RequestMapping(value = "/clusterByDB2.json", method = RequestMethod.POST)
    public List<RoutesCluster> clusterByDB2(int index, double eRatio, double minptsRatio, double overlap_ratio) {
//        int type = DensityBased.TYPE_DIS_ONLY;
        ODPair odPair = routesService.getOdPairList().get(index);
        //set e and minpts
        List<Route> routes = odPair.getRoutes();
        int traj_num = 0;
        double e = 0;
        double minDis = Integer.MAX_VALUE;
        int minedge = Integer.MAX_VALUE;
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            if (route.getAllDis() < minDis)
                minDis = route.getAllDis();
            int size = route.getEdge_ids().size();
            if (size < minedge)
                minedge = size;
            traj_num += route.getFrequency();
        }
        e = eRatio;
//        e = minedge * eRatio;
        if (clusterFunction == null) {
            clusterFunction = new DensityBased(routes);
        } else {
            clusterFunction.setRoutes(routes);
        }
//        clusterFunction.setDistanceFunction(new RouteLengthDistanceFunction(clusterFunction.getRoutes()));
        clusterFunction.setDistanceFunction(new EditDistanceWithWeightFunction(clusterFunction.getRoutes()));
        clusterFunction.setE(e);
        clusterFunction.setMINPTS((int) (traj_num * minptsRatio));
//        clusterFunction.addConstraint(new OverlapConstraint(overlap_ratio, clusterFunction.getRoutes()));
        clusterFunction.setReferenceChoose(new MinimumVarianceChoose(clusterFunction, clusterFunction.getRoutes()));
        List<List<Route>> clusters = clusterFunction.cluster();
        List<List<Integer>> references = clusterFunction.getMultiReferences();
        List<RoutesCluster> r = new LinkedList<>();
        System.out.println(clusters.size() + "," + references.size());
        for (int i = 0; i < clusters.size(); i++) {
            r.add(new RoutesCluster(clusters.get(i), references.get(i)));
        }
        System.out.println("request clusterByDB2 finish");
        return r;
    }


    @RequestMapping(value = "/clusterByDB.json", method = RequestMethod.POST)
    @Deprecated
    public List<List<List<Node>>> clusterByDB(int index, double eRatio, double minptsRatio) {
        ODPair odPair = routesService.getOdPairList().get(index);
        //set e and minpts
        List<Route> routes = odPair.getRoutes();
        int minedge = Integer.MAX_VALUE;
        int traj_num = 0;
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            int size = route.getEdge_ids().size();
            if (size < minedge)
                minedge = size;
            traj_num += route.getFrequency();
        }

        double e = minedge * eRatio;
        int minpts = (int) (traj_num * minptsRatio);
        if (clusterFunction == null) {
            clusterFunction = new DensityBased(routes);
        } else {
            clusterFunction.setRoutes(routes);
        }
        clusterFunction.setDistanceFunction(new RouteLengthDistanceFunction(clusterFunction.getRoutes()));

        clusterFunction.setE(e);
        clusterFunction.setMINPTS(minpts);
        List<List<Route>> cluster = clusterFunction.cluster();
        List<List<List<Node>>> r = new LinkedList<>();
        cluster.forEach(new Consumer<List<Route>>() {
            @Override
            public void accept(List<Route> routes) {
                List<List<Node>> routenode = new LinkedList<List<Node>>();
                routes.forEach(new Consumer<Route>() {
                    @Override
                    public void accept(Route route) {
                        List<Node> nodes = new LinkedList<Node>();
                        List<Vertex> vertexs = route.getVertexs();
                        for (int i = 0; i < vertexs.size(); i++) {
                            Vertex vertex = vertexs.get(i);
                            nodes.add(new Node(vertex.getId(), vertex.getLatitude(), vertex.getLongitude()));
                        }
                        routenode.add(nodes);
                    }
                });
                r.add(routenode);
            }
        });
        return r;
    }


    @RequestMapping(value = "/getEdge.json", method = RequestMethod.POST)
    public Node[] getEdge(long edge_id) {
        Edge edge = RoutesService.getRoadNet().getEdge_map().get(edge_id);
        Vertex startVertex = edge.getStartVertex();
        Node s_n = new Node(startVertex.getId(), startVertex.getLatitude(), startVertex.getLongitude());
        Vertex endVertex = edge.getEndVertex();
        Node e_n = new Node(endVertex.getId(), endVertex.getLatitude(), endVertex.getLongitude());
        return new Node[]{s_n, e_n};
    }


    @RequestMapping(value = "/updateOutlier.json", method = RequestMethod.POST)
    public List<Route> update(int index, long routeid, boolean isOutlier) {
        List<Route> routes = routesService.getOdPairList().get(index).getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            if (route.getId() == routeid) {
//                System.out.println(routeid+","+isOutlier);
                route.setIsOutlier(isOutlier);
            }
        }
        return routes;

    }


    @RequestMapping(value = "/saveOutlier.json", method = RequestMethod.POST)
    public void saveOutlier(int index, long oid, long did) {
        List<Route> routes = routesService.getOdPairList().get(index).getRoutes();
        File f = new File(Properties.trieFilePath + "actual\\" + oid + "_" + did + ".xml" + ".actual");
        if (f.exists())
            f.delete();
        try {
            f.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                bufferedWriter.write(route.getId() + "," + route.getIsOutlier());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
