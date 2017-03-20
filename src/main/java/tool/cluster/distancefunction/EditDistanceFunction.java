package tool.cluster.distancefunction;

import model.Route;
import model.Vertex;

import java.util.List;

/**
 * Created by lzj on 2016/7/25.
 * get distance by edit distance
 */
public class EditDistanceFunction extends AbstractDistanceFunction {


    public EditDistanceFunction(List<Route> routes) {
        super(routes);
    }

    private double edit_distance(Route r1, Route r2) {
        return edit_distance_norecursion(r1.getVertexs(), r2.getVertexs());
    }


    /**
     * non-recursion method
     *
     * @param r1 route1's vertexs
     * @param r2 route2's vertexs
     * @return
     */
    private double edit_distance_norecursion(List<Vertex> r1, List<Vertex> r2) {
        int rest1 = r1.size();
        int rest2 = r2.size();
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
                if (DistanceFunction.GetPreciseDistance(r1.get(i - 1), r2.get(j - 1)) > 10)
                    d = 1;
                diss[i][j] = Math.min(temp, diss[i - 1][j - 1] + d);
            }
        }
        return diss[rest1][rest2];
    }

    @Override
    public double distance(Route route, Route route1) {
        return edit_distance(route,route1);
    }
}
