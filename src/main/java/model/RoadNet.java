package model;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public class RoadNet {
    private Map<Long, Edge> edge_map;
    private Map<Long, Vertex> vertex_map;

    private Map<Long, Long[]> edgeToSEVertex;

    private Map<Long, Set<Edge>> transfers; //key ：start vertex id , value: next edges


    public Map<Long, Set<Edge>> getTransfers() {
        if (transfers == null) {
            transfers = new HashMap<>();
            getEdge_map().values().forEach(new Consumer<Edge>() {
                @Override
                public void accept(Edge edge) {
                    Set<Edge> set = transfers.get(edge.getStartVertex().getId());
                    if (set == null) {
                        set = new HashSet<Edge>();
                        transfers.put(edge.getStartVertex().getId(),set);
                    }
                    set.add(edge);
                }
            });
        }
        return transfers;
    }

    public RoadNet(Set<Edge> edges, Set<Vertex> vertices) {
        edge_map = new HashMap<>();
        vertex_map = new HashMap<>();
        for (Edge edge : edges) {
            edge_map.put(edge.getId(), edge);
        }
        for (Vertex vertex : vertices) {
            vertex_map.put(vertex.getId(), vertex);
        }
    }

    public Map<Long, Edge> getEdge_map() {
        return edge_map;
    }

    public void setEdge_map(Map<Long, Edge> edge_map) {
        this.edge_map = edge_map;
    }

    public Map<Long, Vertex> getVertex_map() {
        return vertex_map;
    }

}
