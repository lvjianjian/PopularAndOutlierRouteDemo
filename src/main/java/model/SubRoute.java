package model;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by lzj on 2016/8/12.
 */
public class SubRoute {

    private long edgeid = -1;

    private SubRoute privous;
    /**
     * 当前分数
     */
    private double score = 0;
    /**
     * 当前结束点id
     */
    private long current_end_vertex_id;

    //trajectory的开始比较位置
    private int compare_site = 0;
    //和trajectory的最小距离
    private double min_distance = 0;
    //和trajectory目前的确定距离
    private double current_distance = 0;
    //是否和trajectory分离
    private boolean separate = false;


    private BitSet now_bitset;

    public BitSet getNow_bitset() {
        return now_bitset;
    }

    public void setNow_bitset(BitSet now_bitset) {
        this.now_bitset = now_bitset;
    }

    public double getCurrent_distance() {
        return current_distance;
    }

    public void setCurrent_distance(double current_distance) {
        this.current_distance = current_distance;
    }

    public boolean isSeparate() {
        return separate;
    }

    public void setSeparate(boolean separate) {
        this.separate = separate;
    }

    public int getCompare_site() {
        return compare_site;
    }

    public void setCompare_site(int compare_site) {
        this.compare_site = compare_site;
    }

    public double getMin_distance() {
        return min_distance;
    }

    public void setMin_distance(double min_distance) {
        this.min_distance = min_distance;
    }

    public long getCurrent_end_vertex_id() {
        return current_end_vertex_id;
    }

    public void setCurrent_end_vertex_id(long current_end_vertex_id) {
        this.current_end_vertex_id = current_end_vertex_id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


    public long getEdgeid() {
        return edgeid;
    }

    public void setEdgeid(long edgeid) {
        this.edgeid = edgeid;
    }

    public SubRoute getPrivous() {
        return privous;
    }

    public void setPrivous(SubRoute privous) {
        this.privous = privous;
    }


    @Deprecated
    private void printEdgeids(SubRoute subRoute){//递归溢出
        if(subRoute.privous == null){
            System.out.print(subRoute.getEdgeid() + ",");
        }else {
            printEdgeids(subRoute.privous);
            System.out.print(subRoute.getEdgeid() + ",");
        }
    }

    private List<Long> getAllEdges(SubRoute subRoute){
        LinkedList<Long> edgeids = new LinkedList<>();
        while (subRoute != null) {
            edgeids.add(0, subRoute.getEdgeid());
            subRoute = subRoute.getPrivous();
        }
        return edgeids;
    }

    private void printEdgeids2(SubRoute subRoute){//非递归
        List<Long> allEdges = getAllEdges(subRoute);
        for (int i = 0; i < allEdges.size(); i++) {
            System.out.print(allEdges.get(i) + ",");
        }
    }

    public void printEdgeids(){
        printEdgeids2(this);
        System.out.println();
    }

    @Override
    public String toString() {
        return "SubRoute{" +
                "min_distance=" + min_distance +
                ", current_distance=" + current_distance +
                '}';
    }
}
