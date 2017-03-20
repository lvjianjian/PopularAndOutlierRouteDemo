package model;

/**
 * Created by JQ-Cao on 2016/6/6.
 */
public class Edge {
    private long id;
    private Vertex startVertex;
    private Vertex endVertex;
    private double dis;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public Vertex getEndVertex() {
        return endVertex;
    }

    public void setEndVertex(Vertex endVertex) {
        this.endVertex = endVertex;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }

    public Edge(long id, Vertex startVertex, Vertex endVertex, double dis) {
        this.id = id;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.dis = dis;
    }


    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", startVertex=" + startVertex +
                ", endVertex=" + endVertex +
                ", dis=" + dis +
                '}';
    }
}
