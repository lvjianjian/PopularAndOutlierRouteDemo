package tool.cluster.distancefunction;

import model.Edge;
import model.ODPair;
import model.Route;
import model.Vertex;
import service.cmpl.RoutesService;

import java.util.List;

/**
 * Created by lzj on 2016/7/18.
 */
public class DistanceFunction {


    /**
     * @param r1 route1's vertexs
     * @param r2 route2's vertexs
     * @return
     */
    public static double edit_distance(List<Vertex> r1, List<Vertex> r2) {
        return edit_distance_norecursion(r1, r1.size(), r2, r2.size());
    }

    /**
     * @param r1 route1's edges
     * @param r2 route2's edges
     * @return
     */
    @Deprecated
    public static double edit_distanceByEdges(List<Edge> r1, List<Edge> r2) {
        return edit_distance_with_distance_norecursion(r1, r2);
    }

    public static double edit_distanceByEdges(Route r1, Route r2) {
        return Math.abs(r1.getAllDis() - r2.getAllDis());
    }


    /**
     * non-recursion method
     *
     * @param r1    route1's vertexs
     * @param rest1 rest point num of r1,init route1.length
     * @param r2    route2's vertexs
     * @param rest2 rest point num of r2,init route2.length
     * @return
     */
    public static double edit_distance_norecursion(List<Vertex> r1, int rest1, List<Vertex> r2, int rest2) {
        double[][] diss = new double[rest1 + 1][rest2 + 1];
        for (int i = 0; i <= rest2; i++) {
            diss[0][i] = i;
        }
        for (int i = 0; i <= rest1; i++) {
            diss[i][0] = i;
        }

        for (int i = 1; i <= rest1; i++) {
            for (int j = 1; j <= rest2; j++) {
                double d = 0;
                double temp = Math.min(diss[i - 1][j] + 1, diss[i][j - 1] + 1);
                if (GetPreciseDistance(r1.get(i - 1), r2.get(j - 1)) > 10)
                    d = 1;
                diss[i][j] = Math.min(temp, diss[i - 1][j - 1] + d);
            }
        }
        return diss[rest1][rest2];
    }

    private static double getWeight(Vertex v1, Vertex v2) {
        double r = 0;
        double dis = GetPreciseDistance(v1, v2);
        if (dis < 20) {
            r = 0;
        } else if (dis < 100) {
            r = 0.5;
        } else {
            r = 1;
        }
        return r;
    }

    /**
     * non-recursion method and the distance into consideration
     *
     * @param r1    route1's vertexs
     * @param rest1 rest point num of r1,init route1.length
     * @param r2    route2's vertexs
     * @param rest2 rest point num of r2,init route2.length
     * @return
     */

    public static double edit_distance_with_distance_norecursion(List<Vertex> r1, int rest1, List<Vertex> r2, int rest2) {
        double[][] diss = new double[rest1 + 1][rest2 + 1];
        for (int i = 0; i <= rest2; i++) {
            diss[0][i] = i;
        }
        for (int i = 0; i <= rest1; i++) {
            diss[i][0] = i;
        }

        for (int i = 1; i <= rest1; i++) {
            for (int j = 1; j <= rest2; j++) {
                Vertex v1 = r1.get(i - 1);
                Vertex v2 = r2.get(j - 1);
                double dis = getWeight(v1, v2);
                double temp = Math.min(diss[i - 1][j] + dis, diss[i][j - 1] + dis);
                diss[i][j] = Math.min(temp, diss[i - 1][j - 1] + dis);
            }
        }
        return diss[rest1][rest2];
    }


    @Deprecated
    public static double edit_distance_with_distance_norecursion(List<Edge> r1, List<Edge> r2) {
        double dis = 0;
        final double[] dis1 = {0};
        final double[] dis2 = {0};
        r1.stream().forEach(edge -> dis1[0] += edge.getDis());
        r2.stream().forEach(edge -> dis2[0] += edge.getDis());
        return Math.abs(dis1[0] - dis2[0]);
    }


    /**
     * recursion method
     *
     * @param r1    route1's vertexs
     * @param rest1 rest point num of r1,init route1.length
     * @param r2    route2's vertexs
     * @param rest2 rest point num of r2,init route2.length
     * @return
     */
    public static double edit_distance_recursion(List<Vertex> r1, int rest1, List<Vertex> r2, int rest2) {
        //when one rest route is none,return points num of the other rest route
        System.out.println("" + rest1 + " ," + rest2);
        if (rest1 == 0)
            return rest2;
        if (rest2 == 0) {
//            System.out.println(rest2);
            return rest1;
        }
        Vertex v1 = r1.get(r1.size() - rest1);
        Vertex v2 = r2.get(r2.size() - rest2);
        if (GetPreciseDistance(v1, v2) < 10) {//if the two points' distance are small
            return edit_distance_recursion(r1, rest1 - 1, r2, rest2 - 1);
        } else {
            //choose the minimum
            double edit_distance1 = edit_distance_recursion(r1, rest1 - 1, r2, rest2) + 1;
            double edit_distance2 = edit_distance_recursion(r1, rest1, r2, rest2 - 1) + 1;
            double edit_distance3 = edit_distance_recursion(r1, rest1 - 1, r2, rest2 - 1) + 1;
            return Math.min(Math.min(edit_distance1, edit_distance2), edit_distance3);
        }
    }


    /**
     * calculate two gpsPoint distance
     */
    public static double GetPreciseDistance(Vertex pointA, Vertex pointB) {
        double dLat = (pointA.getLatitude() - pointB.getLatitude()) * Math.PI / 180;
        double dLng = (pointA.getLongitude() - pointB.getLongitude()) * Math.PI / 180;
        double R = 6378137;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(pointB.getLatitude() * Math.PI / 180) * Math.cos(pointA.getLatitude() * Math.PI / 180) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    /**
     * get ovarlap distance ration
     * @param r1 route1
     * @param r2 route2
     * @return
     */
    public static double getOverlapRatio(Route r1, Route r2) {
        double ratio = 0;
        List<Edge> r1_edges = r1.getEdges();
        List<Edge> r2_edges = r2.getEdges();
        double overlapDistance = 0;
        int j = 0;
        out:
        for (int i = 0; i < r1_edges.size(); i++) {//same edge
            if (r1_edges.get(i).getId() == r2_edges.get(j).getId()) {
                overlapDistance += r1_edges.get(i).getDis();
                ++j;
            } else {//not same edge
                for (int k = j; k < r2_edges.size(); k++) {//find next same edge
                    if (r1_edges.get(i).getId() == r2_edges.get(k).getId()) {//has found
                        overlapDistance += r1_edges.get(i).getDis();
                        j = k + 1;
                        continue out;
                    }
                }
            }
        }
        ratio = overlapDistance / Math.max(r1.getAllDis(),r2.getAllDis());
        return ratio;
    }

    public static void main(String[] args) {
//        System.out.println(GetPreciseDistance(new Vertex(1,39.905061,116.428885),new Vertex(1,39.905061,116.428763)));
        RoutesService routesService = new RoutesService();
        List<ODPair> odPairList = routesService.getOdPairList();
        ODPair odPair = null;
        for (int i = 0; i < odPairList.size(); i++) {
            ODPair temp = odPairList.get(i);
            if (temp.getRoute_num() == 36) {
                odPair = temp;
                break;
            }
        }
        List<Route> routes = odPair.getRoutes();
        Route route1 = routes.get(0);
        Route route2 = routes.get(1);
        System.out.println(route1);
        System.out.println(route1.getVertexs().size());
        System.out.println(route2);
        System.out.println(route2.getVertexs().size());

//        Route route1 = new Route(10,null);
//        List<Vertex> r1_vertexs = new LinkedList<>();
//        Set<Vertex> vertexSet = RoutesService.getVertexSet();
//        r1_vertexs.add(new Vertex(1,39.893218,116.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,115.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,114.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,113.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,116.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,115.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,114.319961));
//        r1_vertexs.add(new Vertex(1,39.893218,113.319961));
//        route1.setVertexs(r1_vertexs);
//
//
//        Route route2 = new Route(10,null);
//        List<Vertex> r2_vertexs = new LinkedList<>();
//        r2_vertexs.add(new Vertex(1,39.893218,115.319961));
//        r2_vertexs.add(new Vertex(1,39.893218,113.319961));
//        r2_vertexs.add(new Vertex(1,39.893218,112.319961));
//        r2_vertexs.add(new Vertex(1,39.893218,115.319961));
//        r2_vertexs.add(new Vertex(1,39.893218,113.319961));
//        r2_vertexs.add(new Vertex(1,39.893218,112.319961));
//        route2.setVertexs(r2_vertexs);
        double edit_distance = DistanceFunction.edit_distance(route1.getVertexs(), route2.getVertexs());
        System.out.println(edit_distance);
        System.out.println(DistanceFunction.getOverlapRatio(route1,route2));
    }
}
