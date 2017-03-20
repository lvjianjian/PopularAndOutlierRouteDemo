package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import service.cmpl.RoutesService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/7/15.
 */
public class Route {

    private long id;

    @JsonIgnore
    protected List<Long> vertex_ids;
    @JsonIgnore
    protected List<Long> edge_ids;
    @JsonIgnore
    protected List<Vertex> vertexs ;
    @JsonIgnore
    protected List<Edge> edges;
    protected double allDis = 0;
    protected List<Node> nodes;//only include lng and lat
    private int frequency;

    private boolean isOutlier = true;

    private double score;

    private double ibat_score;
    private double search_score;


    public double getSearch_score() {
        return search_score;
    }

    public void setSearch_score(double search_score) {
        this.search_score = search_score;
    }

    public double getIbat_score() {
        return ibat_score;
    }

    public void setIbat_score(double ibat_score) {
        this.ibat_score = ibat_score;
    }

    public boolean isOutlier() {
        return isOutlier;
    }

    public boolean getIsOutlier() {
        return isOutlier;
    }

    public void setIsOutlier(boolean isOutlier) {
        this.isOutlier = isOutlier;
    }

    public Route(long id, int frequency) {
        this.id = id;
        this.frequency = frequency;
    }

    public Route(int frequency, List<Long> edge_ids) {
        this.frequency = frequency;
        this.edge_ids = edge_ids;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setVertex_ids(List<Long> vertex_ids) {
        this.vertex_ids = vertex_ids;
    }

    public List<Long> getVertex_ids() {
        if(vertex_ids == null){
            vertex_ids = new LinkedList<>();
            getVertexs().forEach(new Consumer<Vertex>() {
                @Override
                public void accept(Vertex vertex) {
                    vertex_ids.add(vertex.getId());
                }
            });
        }
        return vertex_ids;
    }

    public List<Node> getNodes() {
        if(nodes == null){
            List<Vertex> vertexs = getVertexs();
            nodes = new LinkedList<>();
            for (int i = 0; i < vertexs.size(); i++) {
                Vertex vertex = vertexs.get(i);
                nodes.add(new Node(vertex.getId(),vertex.getLatitude(),vertex.getLongitude()));
            }
        }
        return nodes;
    }


    public void setVertexs(List<Vertex> vertex_ids) {
        this.vertexs = vertex_ids;
    }

    public List<Vertex> getVertexs() {
        if(vertexs == null){
            vertexs = new LinkedList<>();
            Map<Long, Edge> edge_map = RoutesService.getRoadNet().getEdge_map();
            vertexs.add(edge_map.get(edge_ids.get(0)).getStartVertex());
            edge_ids.forEach(new Consumer<Long>() {
                @Override
                public void accept(Long edge_id) {
                    vertexs.add(edge_map.get(edge_id).getEndVertex());
                }
            });
        }
        return vertexs;
    }

    public double getAllDis() {
        if(allDis == 0){
            getEdgesAndAllDis();
        }
        return allDis;
    }

    public List<Edge> getEdges() {
        if(edges == null){
            getEdgesAndAllDis();
        }
        return edges;
    }

    private void getEdgesAndAllDis() {
        edges = new LinkedList<>();
        Map<Long, Edge> edge_map = RoutesService.getRoadNet().getEdge_map();
        for (int i = 0; i < edge_ids.size(); i++) {
            Edge e = edge_map.get(edge_ids.get(i));
            edges.add(e);
            allDis+=e.getDis();
        }
    }

    public List<Long> getEdge_ids() {
        return edge_ids;
    }

    public void setEdge_ids(List<Long> edge_ids) {
        this.edge_ids = edge_ids;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void printEdgeids(){
        getEdge_ids().forEach(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                System.out.print(aLong+",");
            }
        });
        System.out.println();
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ",frequency=" + frequency +
                ",edge size="+ edge_ids.size()+
                '}';
    }
}
