package tool;

import model.Route;
import model.SubRoute;
import model.Trajectory;
import model.TransferMatrixNode;
import service.cmpl.RoutesService;
import tool.cluster.distancefunction.EditDistanceWithWeightFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/9/19.
 */
public abstract class Search {
    private EditDistanceWithWeightFunction distanceWithWeightFunction = new EditDistanceWithWeightFunction(null);
    private int MIN_ED_DISTANCE = 20;
    long definition_id = -1;
    public void setDefinition_id(long definition_id) {
        this.definition_id = definition_id;
    }
    public int getMIN_ED_DISTANCE() {
        return MIN_ED_DISTANCE;
    }

    public void setMIN_ED_DISTANCE(int MIN_ED_DISTANCE) {
        this.MIN_ED_DISTANCE = MIN_ED_DISTANCE;
    }

    /**
     * 判断是否可以search，基本只有当不存在转移矩阵时才为false
     * @return
     */
    protected abstract boolean canSearch();

    protected long getDeftination_id(){
        return definition_id;
    }

    /**
     * 找到并计算所有与trajectory相似的路径权值之和
     *
     * @param trajectory
     * @return
     */
    public double score(Trajectory trajectory) {
        double score = 0;
        if (! canSearch()) {
            System.err.println("can't search");
            return 0;
        }
        List<Long> edge_ids = trajectory.getEdge_ids();
        long start_vertex_id = RoutesService.getRoadNet().getEdge_map().get(edge_ids.get(0)).getStartVertex().getId();
        long end_vertex_id = RoutesService.getRoadNet().getEdge_map().get(edge_ids.get(edge_ids.size() - 1)).getEndVertex().getId();
        if (end_vertex_id != getDeftination_id()) {
            System.out.println("end_vertex_id:" + end_vertex_id );
            System.err.println("end_verte_id not equal definition id");
            return 0;
        }

        LinkedList<SubRoute> stack = new LinkedList<>();

        addSubRoutes(start_vertex_id, stack, null, trajectory);
//        System.out.println("stack_size = " + stack.size());
        while (!stack.isEmpty()) {
            SubRoute pop = stack.pop();
//            pop.printEdgeids();
            if (pop.getCurrent_end_vertex_id() == end_vertex_id) {//找到一条完整路径
                double scoreOfOneRoute = getScoreOfOneRoute(pop);
                System.out.println("find route,it's score is " + scoreOfOneRoute);
                score += scoreOfOneRoute;
                continue;
            }
            addSubRoutes(pop.getCurrent_end_vertex_id(), stack, pop, trajectory);
//            System.out.println("stack_size = "+stack.size());
        }

        return score;
    }


    public abstract double[] scoreAndUseTime(Trajectory trajectory);

    protected abstract double getScoreOfOneRoute(SubRoute r);

    protected abstract void addSubRoutes(long current_end_vertex_id, LinkedList<SubRoute> stack, SubRoute pop, Trajectory trajectory);


    /**
     * 剪枝操作,通过部分轨迹和全轨迹计算最小可能编辑距离剪枝
     *
     * @param route
     * @param trajectory
     * @return true，剪枝。false，不剪枝
     */
    protected boolean prun(SubRoute route, Trajectory trajectory) {

//        System.out.println("============================================ compute min distance start ============================================");

        boolean prun = false;
        int comparesite = 0;
        double min_distance = 0;
        double current_distance = 0;
        boolean seperate = false;
        if (route.getPrivous() != null) {
            comparesite = route.getPrivous().getCompare_site();
            min_distance = route.getPrivous().getMin_distance();
            seperate = route.getPrivous().isSeparate();
            current_distance = route.getPrivous().getCurrent_distance();
        }
//        System.out.println("we get compare site is "+comparesite);
        long current_end_vertex_id = route.getCurrent_end_vertex_id();
//        System.out.println("current_end_vertex_id is "+current_end_vertex_id);
        int end_site = trajectory.getVertex_ids().indexOf(current_end_vertex_id);
        if (end_site == -1) {//新增的边的终点不在trajectory上
            route.setSeparate(true);
            route.setCompare_site(comparesite);
//            System.out.println("the end vertex of new edge is not in trajectory");
            route.setCurrent_distance(current_distance);
            route.setMin_distance(min_distance +
                    distanceWithWeightFunction.getWeightOfEdge(RoutesService.getRoadNet().getEdge_map().get(route.getEdgeid())) * distanceWithWeightFunction.ADD_WEIGHT);
        } else if (end_site == comparesite + 1 && !seperate) {//新增的一条边在trajectory上
//            System.out.println("the new edge is all in trajectory, compare site is set as "+end_site);
            route.setSeparate(false);
            route.setCompare_site(end_site);
            route.setMin_distance(min_distance);
            route.setCurrent_distance(current_distance);
        } else {
//            System.out.println("the end vertex of new edge is in trajectory");
            route.setSeparate(true);
            List<Long> edge_ids = trajectory.getEdge_ids();
            List<Long> sub_traj = new LinkedList<>();
            for (int i = comparesite; i < end_site; i++) {
                sub_traj.add(edge_ids.get(i));
            }
            Route route1 = new Route(1, sub_traj);
//            System.out.println("route1:");
//            route1.printEdgeids();
            route.setCompare_site(comparesite);
            Route route2 = new Route(1, getCompareEdgeidsFromSubRoute(route));
//            System.out.println("route2");
//            route2.printEdgeids();
            double new_distance = current_distance + distanceWithWeightFunction.distance(route1, route2);
            route.setCompare_site(end_site);
            route.setSeparate(false);
            route.setMin_distance(new_distance);
            route.setCurrent_distance(new_distance);
        }
//        route.printEdgeids();
//        System.out.println("min_distance:" + route.getMin_distance());
//        System.out.println("route:" + route);
        if (route.getMin_distance() > MIN_ED_DISTANCE) {
            prun = true;
        }

//        System.out.println("prun is " + prun);
//        System.out.println("============================================ compute min distance end ============================================");
        return prun;
    }


    private List<Long> getCompareEdgeidsFromSubRoute(SubRoute subRoute) {
//        System.out.println("in getCompareEdgeidsFromSubRoute method");
        List<Long> edgeids = new LinkedList<>();
        SubRoute r = subRoute;
        int start_site = r.getCompare_site();
//        System.out.println("start_site is" + start_site);
//        System.out.println("sub route compare site is "+subRoute.getCompare_site());
//        System.out.println("sub route is seperate " + subRoute.isSeparate());
        while (subRoute != null && subRoute.isSeparate()) {
            if (start_site != subRoute.getCompare_site())
                break;
            edgeids.add(0, subRoute.getEdgeid());
            subRoute = subRoute.getPrivous();
//            System.out.println("sub route compare site is "+subRoute.getCompare_site());
//            System.out.println("sub route is seperate " + subRoute.isSeparate());
        }
        return edgeids;
    }

}
