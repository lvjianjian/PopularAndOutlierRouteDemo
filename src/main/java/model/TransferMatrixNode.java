package model;

/**
 * Created by lzj on 2016/8/12.
 */
public class TransferMatrixNode {
    private Long end_vertex_id;
    private Long edge_id;
    private Long count;


    public Long getEnd_vertex_id() {
        return end_vertex_id;
    }

    public void setEnd_vertex_id(Long end_vertex_id) {
        this.end_vertex_id = end_vertex_id;
    }

    public Long getEdge_id() {
        return edge_id;
    }

    public void setEdge_id(Long edge_id) {
        this.edge_id = edge_id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public TransferMatrixNode(Long end_vertex_id, Long edge_id, Long count) {
        this.end_vertex_id = end_vertex_id;
        this.edge_id = edge_id;
        this.count = count;
    }

    public TransferMatrixNode(Long end_vertex_id, Long edge_id) {
        this.end_vertex_id = end_vertex_id;
        this.edge_id = edge_id;
    }

    public TransferMatrixNode(Long end_vertex_id) {
        this.end_vertex_id = end_vertex_id;
    }

    @Override
    public String toString() {
        return "TransferMatrixNode{" +
                "end_vertex_id=" + end_vertex_id +
                ", edge_id=" + edge_id +
                ", count=" + count +
                '}';
    }
}
