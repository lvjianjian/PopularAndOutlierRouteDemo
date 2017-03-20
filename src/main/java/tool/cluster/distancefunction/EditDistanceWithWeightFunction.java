package tool.cluster.distancefunction;

import model.Edge;
import model.Route;

import java.util.List;

/**
 * Created by lzj on 2016/8/5.
 */
public class EditDistanceWithWeightFunction extends AbstractDistanceFunction{
    public static int ADD_WEIGHT = 1;
    public static int DELETE_WEIGHT = 1;

    public EditDistanceWithWeightFunction(List<Route> routes) {
        super(routes);
    }

    @Override
    public double distance(Route route, Route route1) {
        List<Edge> edges1 = route.getEdges();
        List<Edge> edges2 = route1.getEdges();

        int rest1 = edges1.size();
        int rest2 = edges2.size();

        double[][] diss = new double[rest1 + 1][rest2 + 1];
        double weight1 = 0, weight2 = 0;

        diss[0][0] = 0;

        for (int i = 1; i <= rest2; i++) {
            weight2 += getWeightOfEdge(edges2.get(i-1)) * ADD_WEIGHT;
            diss[0][i] = weight2;
        }
        for (int i = 1; i <= rest1; i++) {
            weight1 += getWeightOfEdge(edges1.get(i-1)) * ADD_WEIGHT;
            diss[i][0] = weight1;
        }

        for (int i = 1; i <= rest1; i++) {
            for (int j = 1; j <= rest2; j++) {
                double d = 0;
                // add edge
                Edge edge1 = edges1.get(i - 1);
                Edge edge2 = edges2.get(j - 1);
                double temp = Math.min(diss[i - 1][j] + getWeightOfEdge(edge1) * ADD_WEIGHT, diss[i][j - 1] + getWeightOfEdge(edge2) * ADD_WEIGHT);
                // replace edge
                if(edge1.getId() == edge2.getId())
                    d = 0;
                else{
                    d = DELETE_WEIGHT * Math.min(edge1.getDis(),edge2.getDis()) + ADD_WEIGHT * Math.max(edge1.getDis(),edge2.getDis());
                }
                diss[i][j] = Math.min(temp, diss[i - 1][j - 1] + d);
            }
        }
        return diss[rest1][rest2];
    }


    public double getWeightOfEdge(Edge e){
        return 1;
    }//e.getDis()/100

}
