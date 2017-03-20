package service.cmpl;

import model.Edge;
import model.ODPair;
import model.RoadNet;
import model.Vertex;
import tool.Properties;
import tool.cmpl.LoadSource;

import java.util.List;
import java.util.Set;

/**
 * Created by lzj on 2016/7/27.
 */
public class RoutesServiceOfZJ {

    public RoutesServiceOfZJ() {
    }



    private static LoadSource loadSource = new LoadSource();

    private static Set<Vertex> vertexSet = loadSource.loadVertex2("D:\\吕中剑\\OutlierDetection\\data\\wise2015_data\\lzj\\roadnetwork\\vertices.txt");


    private static Set<Edge> edgeSet = loadSource.loadEdge("D:\\吕中剑\\OutlierDetection\\data\\wise2015_data\\lzj\\roadnetwork\\edges.txt",vertexSet);


    private static RoadNet roadNet = new RoadNet(edgeSet,vertexSet);

    public static Set<Vertex> getVertexSet() {
        return vertexSet;
    }


    public static RoadNet getRoadNet() {
        return roadNet;
    }

    private static List<ODPair> odPairList = loadSource.loadODPairs("");

    public static List<ODPair> getOdPairList() {
        return odPairList;
    }

    public static void main(String[] args) {
        System.out.println(vertexSet.size());
        System.out.println(edgeSet.size());
    }
}
