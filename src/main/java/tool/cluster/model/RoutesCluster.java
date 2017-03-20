package tool.cluster.model;
import model.Node;
import model.Route;

import java.util.List;

/**
 * Created by lzj on 2016/7/20.
 * a cluster including routes
 */
public class  RoutesCluster  {
    List<Route> routes;
    List<Integer> referenceRouteIndexs;
    int all_frequency = -1;


    public int getAll_frequency() {
        if(all_frequency == -1)
        {
            all_frequency = 0;
            for (int i = 0; i < routes.size(); i++) {
                all_frequency+=routes.get(i).getFrequency();
            }
        }
        return all_frequency;
    }

    public void setAll_frequency(int all_frequency) {
        this.all_frequency = all_frequency;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }


    public List<Integer> getReferenceRouteIndexs() {
        return referenceRouteIndexs;
    }

    public void setReferenceRouteIndexs(List<Integer> referenceRouteIndexs) {
        this.referenceRouteIndexs = referenceRouteIndexs;
    }

    public RoutesCluster(List<Route> routes, List<Integer>  referenceRouteIndex) {
        this.routes = routes;
        this.referenceRouteIndexs = referenceRouteIndex;
    }
}
