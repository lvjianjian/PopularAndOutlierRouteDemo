package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lzj on 2016/7/15.
 */
public class ODPair {
    private Long start_vertex_id;
    private Long end_vertex_id;
    @JsonIgnore
    private List<Route> routes = new LinkedList<>();
    private int route_num;
    /**
     * whether clean bad routes
     */
    private boolean isCleaned = false;

    private int min_edge_num = Integer.MAX_VALUE;

    public ODPair(Long start_vertex_id, Long end_vertex_id) {
        this.start_vertex_id = start_vertex_id;
        this.end_vertex_id = end_vertex_id;
    }

    public ODPair(Long start_vertex_id, Long end_vertex_id, int route_num) {
        this.start_vertex_id = start_vertex_id;
        this.end_vertex_id = end_vertex_id;
        this.route_num = route_num;
    }

    public int getRoute_num() {
        return route_num;
    }

    public void setRoute_num(int route_num) {
        this.route_num = route_num;
    }

    public List<Route> getRoutes() {
        if(!isCleaned){
            cleanBadRoutes();
        }
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public Long getStart_vertex_id() {
        return start_vertex_id;
    }

    public void setStart_vertex_id(Long start_vertex_id) {
        this.start_vertex_id = start_vertex_id;
    }

    public Long getEnd_vertex_id() {
        return end_vertex_id;
    }

    public void setEnd_vertex_id(Long end_vertex_id) {
        this.end_vertex_id = end_vertex_id;
    }

    public void addRoute(Route route){
        routes.add(route);
        if(route.getEdge_ids().size() < min_edge_num)
            min_edge_num = route.getEdge_ids().size();
    }


    public void cleanBadRoutes(){
        if(isCleaned)
            return;
        else {
            isCleaned = true;
            List<Route> cleaned_routes = new LinkedList<>();
            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                if (route.getEdge_ids().size() < min_edge_num * 3) {//filter routes which's edge_nums < min * 3
                    cleaned_routes.add(route);
                }
            }
            setRoutes(cleaned_routes);
            setRoute_num(cleaned_routes.size());
        }
    }


    @Override
    public String toString() {
        return "ODPair{" +
                "end_vertex_id=" + end_vertex_id +
                ", start_vertex_id=" + start_vertex_id +
                ", route_num=" + route_num +
                '}';
    }
}
